package com.example.EyeCareHubDB.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.EyeCareHubDB.dto.PromotionCreateRequest;
import com.example.EyeCareHubDB.dto.PromotionDTO;
import com.example.EyeCareHubDB.dto.PromotionValidateRequest;
import com.example.EyeCareHubDB.dto.PromotionValidateResponse;
import com.example.EyeCareHubDB.entity.AuditLog;
import com.example.EyeCareHubDB.entity.Promotion;
import com.example.EyeCareHubDB.mapper.PromotionMapper;
import com.example.EyeCareHubDB.repository.PromotionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class PromotionService {

    private final PromotionRepository promotionRepository;
    private final AuditLogService auditLogService;
    private final PromotionMapper promotionMapper;

    public List<PromotionDTO> getAllPromotions() {
        return promotionRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<PromotionDTO> getActivePromotions() {
        return promotionRepository.findActivePromotions(LocalDateTime.now()).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public PromotionDTO getPromotionById(Long id) {
        return promotionRepository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new RuntimeException("Promotion not found with id: " + id));
    }

    public PromotionValidateResponse validatePromotion(PromotionValidateRequest request) {
        Promotion promo = promotionRepository
                .findValidPromotion(request.getCode(), LocalDateTime.now())
                .orElse(null);

        if (promo == null) {
            return PromotionValidateResponse.builder()
                    .valid(false)
                    .code(request.getCode())
                    .message("Mã giảm giá không hợp lệ hoặc đã hết hạn")
                    .build();
        }

        if (promo.getUsageLimit() > 0 && promo.getUsedCount() >= promo.getUsageLimit()) {
            return PromotionValidateResponse.builder()
                    .valid(false)
                    .code(request.getCode())
                    .name(promo.getName())
                    .message("Mã giảm giá đã hết lượt sử dụng")
                    .build();
        }

        if (promo.getMinimumOrderValue() != null
                && request.getOrderTotal().compareTo(promo.getMinimumOrderValue()) < 0) {
            return PromotionValidateResponse.builder()
                    .valid(false)
                    .code(request.getCode())
                    .name(promo.getName())
                    .message("Đơn hàng tối thiểu " + promo.getMinimumOrderValue() + " để sử dụng mã này")
                    .build();
        }

        BigDecimal discount = calculateDiscount(promo, request.getOrderTotal());

        return PromotionValidateResponse.builder()
                .valid(true)
                .code(promo.getCode())
                .name(promo.getName())
                .discountType(promo.getDiscountType().name())
                .discountValue(promo.getDiscountValue())
                .calculatedDiscount(discount)
                .orderTotalAfterDiscount(request.getOrderTotal().subtract(discount))
                .message("Áp dụng thành công: giảm " + discount)
                .build();
    }

    public BigDecimal calculateDiscount(Promotion promo, BigDecimal orderTotal) {
        BigDecimal discount;

        switch (promo.getDiscountType()) {
            case PERCENTAGE:
                discount = orderTotal.multiply(promo.getDiscountValue())
                        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                if (promo.getMaximumDiscount() != null
                        && discount.compareTo(promo.getMaximumDiscount()) > 0) {
                    discount = promo.getMaximumDiscount();
                }
                break;
            case FIXED_AMOUNT:
                discount = promo.getDiscountValue();
                if (discount.compareTo(orderTotal) > 0) {
                    discount = orderTotal;
                }
                break;
            case FREE_SHIPPING:
                discount = BigDecimal.ZERO;
                break;
            default:
                discount = BigDecimal.ZERO;
        }

        return discount;
    }

    public PromotionDTO createPromotion(PromotionCreateRequest request) {
        if (promotionRepository.existsByCode(request.getCode())) {
            throw new RuntimeException("Promotion code already exists: " + request.getCode());
        }

        Promotion promo = promotionMapper.toEntity(request);
        promo.setCode(request.getCode().toUpperCase());
        promo.setDiscountType(Promotion.DiscountType.valueOf(request.getDiscountType()));
        if (request.getUsageLimit() != null) {
            promo.setUsageLimit(request.getUsageLimit());
        }

        Promotion saved = promotionRepository.save(promo);
        auditLogService.log("Promotion", saved.getId(), AuditLog.AuditAction.CREATE, null, saved.getCode());
        return toDTO(saved);
    }

    public PromotionDTO updatePromotion(Long id, PromotionCreateRequest request) {
        Promotion promo = promotionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Promotion not found with id: " + id));

        promotionMapper.updateEntity(promo, request);
        if (request.getDiscountType() != null) {
            promo.setDiscountType(Promotion.DiscountType.valueOf(request.getDiscountType()));
        }

        Promotion updated = promotionRepository.save(promo);
        auditLogService.log("Promotion", updated.getId(), AuditLog.AuditAction.UPDATE, null, updated.getCode());
        return toDTO(updated);
    }

    public void deletePromotion(Long id) {
        Promotion promo = promotionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Promotion not found with id: " + id));
        promo.setIsActive(false);
        promotionRepository.save(promo);
        auditLogService.log("Promotion", id, AuditLog.AuditAction.DELETE, promo.getCode(), null);
    }

    public void incrementUsage(String code) {
        Promotion promo = promotionRepository.findByCode(code)
                .orElseThrow(() -> new RuntimeException("Promotion not found: " + code));
        promo.setUsedCount(promo.getUsedCount() + 1);
        promotionRepository.save(promo);
    }

    private PromotionDTO toDTO(Promotion promo) {
        PromotionDTO dto = promotionMapper.toDTO(promo);
        LocalDateTime now = LocalDateTime.now();
        boolean valid = promo.getIsActive()
                && now.isAfter(promo.getStartDate())
                && now.isBefore(promo.getEndDate())
                && (promo.getUsageLimit() == 0 || promo.getUsedCount() < promo.getUsageLimit());
        dto.setIsValid(valid);
        return dto;
    }
}

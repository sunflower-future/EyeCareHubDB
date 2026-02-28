package com.example.EyeCareHubDB.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.EyeCareHubDB.dto.FeedbackCreateRequest;
import com.example.EyeCareHubDB.dto.FeedbackDTO;
import com.example.EyeCareHubDB.dto.FeedbackSummaryDTO;
import com.example.EyeCareHubDB.entity.Account;
import com.example.EyeCareHubDB.entity.AuditLog;
import com.example.EyeCareHubDB.entity.Feedback;
import com.example.EyeCareHubDB.entity.Order;
import com.example.EyeCareHubDB.entity.OrderItem;
import com.example.EyeCareHubDB.entity.Product;
import com.example.EyeCareHubDB.mapper.FeedbackMapper;
import com.example.EyeCareHubDB.repository.FeedbackRepository;
import com.example.EyeCareHubDB.repository.OrderItemRepository;
import com.example.EyeCareHubDB.repository.OrderRepository;
import com.example.EyeCareHubDB.repository.ProductRepository;
import com.example.EyeCareHubDB.util.SecurityUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final AuditLogService auditLogService;
    private final FeedbackMapper feedbackMapper;

    public FeedbackDTO createFeedback(FeedbackCreateRequest request) {
        Account currentUser = SecurityUtils.getCurrentAccount();
        if (currentUser == null)
            throw new RuntimeException("Not authenticated");

        if (request.getRating() < 1 || request.getRating() > 5) {
            throw new RuntimeException("Rating must be between 1 and 5");
        }

        if (request.getOrderItemId() != null
                && feedbackRepository.existsByAccountIdAndOrderItemId(currentUser.getId(), request.getOrderItemId())) {
            throw new RuntimeException("You have already reviewed this item");
        }

        Order order = null;
        OrderItem orderItem = null;
        Product product = null;
        boolean verifiedPurchase = false;

        if (request.getOrderId() != null) {
            order = orderRepository.findById(request.getOrderId()).orElse(null);
            verifiedPurchase = order != null
                    && order.getAccount().getId().equals(currentUser.getId())
                    && order.getStatus() == com.example.EyeCareHubDB.entity.Order.OrderStatus.DELIVERED;
        }
        if (request.getOrderItemId() != null) {
            orderItem = orderItemRepository.findById(request.getOrderItemId()).orElse(null);
        }
        if (request.getProductId() != null) {
            product = productRepository.findById(request.getProductId()).orElse(null);
        }

        Feedback feedback = feedbackMapper.toEntity(request);
        feedback.setOrder(order);
        feedback.setOrderItem(orderItem);
        feedback.setAccount(currentUser);
        feedback.setProduct(product);
        feedback.setIsVerifiedPurchase(verifiedPurchase);

        Feedback saved = feedbackRepository.save(feedback);
        auditLogService.log("Feedback", saved.getId(), AuditLog.AuditAction.CREATE, null,
                "Rating: " + request.getRating());
        return feedbackMapper.toDTO(saved);
    }

    public FeedbackDTO getFeedbackById(Long id) {
        return feedbackRepository.findById(id)
                .map(feedbackMapper::toDTO)
                .orElseThrow(() -> new RuntimeException("Feedback not found: " + id));
    }

    public Page<FeedbackDTO> getFeedbacksByProductId(Long productId, Pageable pageable) {
        return feedbackRepository.findByProductIdAndStatus(productId, Feedback.FeedbackStatus.APPROVED, pageable)
                .map(feedbackMapper::toDTO);
    }

    public FeedbackSummaryDTO getProductFeedbackSummary(Long productId) {
        Double avgRating = feedbackRepository.getAverageRatingByProductId(productId);
        Long totalReviews = feedbackRepository.countApprovedByProductId(productId);
        List<Object[]> distribution = feedbackRepository.getRatingDistribution(productId);

        Map<Integer, Long> ratingMap = new HashMap<>();
        for (int i = 1; i <= 5; i++)
            ratingMap.put(i, 0L);
        for (Object[] row : distribution) {
            ratingMap.put((Integer) row[0], (Long) row[1]);
        }

        return FeedbackSummaryDTO.builder()
                .productId(productId)
                .averageRating(avgRating != null ? Math.round(avgRating * 10.0) / 10.0 : 0.0)
                .totalReviews(totalReviews != null ? totalReviews : 0L)
                .ratingDistribution(ratingMap)
                .build();
    }

    public FeedbackDTO updateFeedback(Long id, FeedbackCreateRequest request) {
        Feedback fb = feedbackRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Feedback not found: " + id));

        Account currentUser = SecurityUtils.getCurrentAccount();
        if (currentUser == null || !fb.getAccount().getId().equals(currentUser.getId())) {
            throw new RuntimeException("You can only edit your own feedback");
        }

        feedbackMapper.updateEntity(fb, request);

        return feedbackMapper.toDTO(feedbackRepository.save(fb));
    }

    public void deleteFeedback(Long id) {
        Account currentUser = SecurityUtils.getCurrentAccount();
        Feedback fb = feedbackRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Feedback not found: " + id));
        if (currentUser == null || !fb.getAccount().getId().equals(currentUser.getId())) {
            throw new RuntimeException("You can only delete your own feedback");
        }
        feedbackRepository.deleteById(id);
    }

    public FeedbackDTO staffReply(Long id, String reply) {
        Feedback fb = feedbackRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Feedback not found: " + id));

        Account staff = SecurityUtils.getCurrentAccount();
        fb.setStaffReply(reply);
        fb.setStaffReplyAt(LocalDateTime.now());
        fb.setRepliedByStaff(staff);

        return feedbackMapper.toDTO(feedbackRepository.save(fb));
    }

    public FeedbackDTO moderateFeedback(Long id, String newStatus) {
        Feedback fb = feedbackRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Feedback not found: " + id));

        String oldStatus = fb.getStatus().name();
        fb.setStatus(Feedback.FeedbackStatus.valueOf(newStatus));

        Feedback updated = feedbackRepository.save(fb);
        auditLogService.log("Feedback", id, AuditLog.AuditAction.STATUS_CHANGE, oldStatus, newStatus);
        return feedbackMapper.toDTO(updated);
    }

    public Page<FeedbackDTO> getAllFeedbacks(String query, String status, Integer rating, Long productId,
            Pageable pageable) {
        Feedback.FeedbackStatus feedbackStatus = null;
        if (status != null && !status.isEmpty()) {
            try {
                feedbackStatus = Feedback.FeedbackStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException ignored) {
            }
        }
        return feedbackRepository.searchFeedbacks(query, feedbackStatus, rating, productId, pageable)
                .map(feedbackMapper::toDTO);
    }
}

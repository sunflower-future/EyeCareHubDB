package com.example.EyeCareHubDB.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.EyeCareHubDB.dto.InventoryLocationDTO;
import com.example.EyeCareHubDB.dto.InventoryStockDTO;
import com.example.EyeCareHubDB.dto.StockAdjustRequest;
import com.example.EyeCareHubDB.entity.AuditLog;
import com.example.EyeCareHubDB.entity.InventoryLocation;
import com.example.EyeCareHubDB.entity.InventoryStock;
import com.example.EyeCareHubDB.entity.ProductVariant;
import com.example.EyeCareHubDB.mapper.InventoryMapper;
import com.example.EyeCareHubDB.repository.InventoryLocationRepository;
import com.example.EyeCareHubDB.repository.InventoryStockRepository;
import com.example.EyeCareHubDB.repository.ProductVariantRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class InventoryService {

    private final InventoryLocationRepository locationRepository;
    private final InventoryStockRepository stockRepository;
    private final ProductVariantRepository variantRepository;
    private final AuditLogService auditLogService;
    private final InventoryMapper inventoryMapper;

    public List<InventoryLocationDTO> getAllLocations() {
        return locationRepository.findByIsActiveTrue().stream()
                .map(inventoryMapper::toLocationDTO)
                .collect(Collectors.toList());
    }

    public InventoryLocationDTO createLocation(InventoryLocationDTO request) {
        if (locationRepository.existsByCode(request.getCode())) {
            throw new RuntimeException("Location code already exists: " + request.getCode());
        }

        InventoryLocation loc = inventoryMapper.toLocationEntity(request);
        loc.setType(InventoryLocation.LocationType.valueOf(request.getType()));

        InventoryLocation saved = locationRepository.save(loc);
        return inventoryMapper.toLocationDTO(saved);
    }

    public InventoryLocationDTO updateLocation(Long id, InventoryLocationDTO request) {
        InventoryLocation loc = locationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Location not found: " + id));

        inventoryMapper.updateLocationEntity(loc, request);
        if (request.getType() != null) {
            loc.setType(InventoryLocation.LocationType.valueOf(request.getType()));
        }

        return inventoryMapper.toLocationDTO(locationRepository.save(loc));
    }

    public List<InventoryStockDTO> getStocksByVariantId(Long variantId) {
        return stockRepository.findByProductVariantId(variantId).stream()
                .map(inventoryMapper::toStockDTO)
                .collect(Collectors.toList());
    }

    public List<InventoryStockDTO> getStocksByLocationId(Long locationId) {
        return stockRepository.findByLocationId(locationId).stream()
                .map(inventoryMapper::toStockDTO)
                .collect(Collectors.toList());
    }

    public InventoryStockDTO adjustStock(StockAdjustRequest request) {
        InventoryStock stock = stockRepository
                .findByProductVariantIdAndLocationId(request.getProductVariantId(), request.getLocationId())
                .orElseGet(() -> {
                    ProductVariant variant = variantRepository.findById(request.getProductVariantId())
                            .orElseThrow(
                                    () -> new RuntimeException("Variant not found: " + request.getProductVariantId()));
                    InventoryLocation location = locationRepository.findById(request.getLocationId())
                            .orElseThrow(() -> new RuntimeException("Location not found: " + request.getLocationId()));
                    return InventoryStock.builder()
                            .productVariant(variant)
                            .location(location)
                            .build();
                });

        String oldValue = "onHand=" + stock.getOnHandQuantity() + ",reserved=" + stock.getReservedQuantity();

        if (request.getOnHandQuantity() != null)
            stock.setOnHandQuantity(request.getOnHandQuantity());
        if (request.getReservedQuantity() != null)
            stock.setReservedQuantity(request.getReservedQuantity());

        InventoryStock saved = stockRepository.save(stock);
        String newValue = "onHand=" + saved.getOnHandQuantity() + ",reserved=" + saved.getReservedQuantity();
        auditLogService.log("InventoryStock", saved.getId(), AuditLog.AuditAction.UPDATE, oldValue, newValue);
        return inventoryMapper.toStockDTO(saved);
    }

    public void reserveStock(Long variantId, Long locationId, int quantity) {
        InventoryStock stock = stockRepository.findByProductVariantIdAndLocationId(variantId, locationId)
                .orElseThrow(() -> new RuntimeException(
                        "No stock record for variant " + variantId + " at location " + locationId));

        if (stock.getAvailableQuantity() < quantity) {
            throw new RuntimeException(
                    "Insufficient stock for variant " + variantId + ". Available: " + stock.getAvailableQuantity());
        }
        stock.setReservedQuantity(stock.getReservedQuantity() + quantity);
        stockRepository.save(stock);
    }

    public void releaseStock(Long variantId, Long locationId, int quantity) {
        InventoryStock stock = stockRepository.findByProductVariantIdAndLocationId(variantId, locationId)
                .orElseThrow(() -> new RuntimeException(
                        "No stock record for variant " + variantId + " at location " + locationId));
        stock.setReservedQuantity(Math.max(0, stock.getReservedQuantity() - quantity));
        stockRepository.save(stock);
    }

    public List<InventoryStockDTO> getLowStock(int threshold) {
        return stockRepository.findLowStock(threshold).stream()
                .map(inventoryMapper::toStockDTO)
                .collect(Collectors.toList());
    }
}

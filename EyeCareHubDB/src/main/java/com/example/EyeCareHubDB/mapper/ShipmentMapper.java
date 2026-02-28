package com.example.EyeCareHubDB.mapper;

import com.example.EyeCareHubDB.dto.ShipmentCreateRequest;
import com.example.EyeCareHubDB.dto.ShipmentDTO;
import com.example.EyeCareHubDB.entity.Shipment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ShipmentMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "order", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "actualDeliveryDate", ignore = true)
    @Mapping(target = "recipientName", ignore = true)
    @Mapping(target = "recipientPhone", ignore = true)
    @Mapping(target = "shippingAddress", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Shipment toEntity(ShipmentCreateRequest request);

    @Mapping(source = "order.id", target = "orderId")
    @Mapping(source = "order.orderNumber", target = "orderNumber")
    ShipmentDTO toDTO(Shipment entity);
}

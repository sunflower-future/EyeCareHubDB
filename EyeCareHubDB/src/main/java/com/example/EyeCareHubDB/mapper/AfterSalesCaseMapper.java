package com.example.EyeCareHubDB.mapper;

import com.example.EyeCareHubDB.dto.AfterSalesCaseCreateRequest;
import com.example.EyeCareHubDB.dto.AfterSalesCaseDTO;
import com.example.EyeCareHubDB.entity.AfterSalesCase;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AfterSalesCaseMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "order", ignore = true)
    @Mapping(target = "orderItem", ignore = true)
    @Mapping(target = "account", ignore = true)
    @Mapping(target = "resolution", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "assignedStaff", ignore = true)
    @Mapping(target = "resolvedAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    AfterSalesCase toEntity(AfterSalesCaseCreateRequest request);

    @Mapping(source = "order.id", target = "orderId")
    @Mapping(source = "order.orderNumber", target = "orderNumber")
    @Mapping(source = "orderItem.id", target = "orderItemId")
    @Mapping(target = "productName", expression = "java(entity.getOrderItem() != null && entity.getOrderItem().getProductVariant() != null && entity.getOrderItem().getProductVariant().getProduct() != null ? entity.getOrderItem().getProductVariant().getProduct().getName() : null)")
    @Mapping(source = "account.id", target = "accountId")
    @Mapping(source = "account.email", target = "accountEmail")
    @Mapping(source = "assignedStaff.id", target = "assignedStaffId")
    @Mapping(source = "assignedStaff.email", target = "assignedStaffEmail")
    AfterSalesCaseDTO toDTO(AfterSalesCase entity);
}

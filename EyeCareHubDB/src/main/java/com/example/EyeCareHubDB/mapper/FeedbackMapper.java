package com.example.EyeCareHubDB.mapper;

import com.example.EyeCareHubDB.dto.FeedbackCreateRequest;
import com.example.EyeCareHubDB.dto.FeedbackDTO;
import com.example.EyeCareHubDB.entity.Feedback;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface FeedbackMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "order", ignore = true)
    @Mapping(target = "orderItem", ignore = true)
    @Mapping(target = "account", ignore = true)
    @Mapping(target = "product", ignore = true)
    @Mapping(target = "isVerifiedPurchase", ignore = true)
    @Mapping(target = "staffReply", ignore = true)
    @Mapping(target = "staffReplyAt", ignore = true)
    @Mapping(target = "repliedByStaff", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Feedback toEntity(FeedbackCreateRequest request);

    @Mapping(source = "order.id", target = "orderId")
    @Mapping(source = "order.orderNumber", target = "orderNumber")
    @Mapping(source = "orderItem.id", target = "orderItemId")
    @Mapping(source = "account.id", target = "accountId")
    @Mapping(source = "account.email", target = "accountEmail")
    @Mapping(target = "customerName", expression = "java(entity.getAccount() != null && entity.getAccount().getCustomer() != null ? entity.getAccount().getCustomer().getFirstName() + \" \" + entity.getAccount().getCustomer().getLastName() : null)")
    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "product.name", target = "productName")
    @Mapping(source = "product.slug", target = "productSlug")
    @Mapping(source = "repliedByStaff.email", target = "repliedByStaffEmail")
    FeedbackDTO toDTO(Feedback entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "order", ignore = true)
    @Mapping(target = "orderItem", ignore = true)
    @Mapping(target = "account", ignore = true)
    @Mapping(target = "product", ignore = true)
    @Mapping(target = "isVerifiedPurchase", ignore = true)
    @Mapping(target = "staffReply", ignore = true)
    @Mapping(target = "staffReplyAt", ignore = true)
    @Mapping(target = "repliedByStaff", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(@MappingTarget Feedback entity, FeedbackCreateRequest request);
}

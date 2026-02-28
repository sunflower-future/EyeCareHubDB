package com.example.EyeCareHubDB.mapper;

import com.example.EyeCareHubDB.dto.PrescriptionCreateRequest;
import com.example.EyeCareHubDB.dto.PrescriptionDTO;
import com.example.EyeCareHubDB.entity.Prescription;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PrescriptionMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "orderItem", ignore = true)
    @Mapping(target = "verifiedByStaff", ignore = true)
    @Mapping(target = "verifiedAt", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Prescription toEntity(PrescriptionCreateRequest request);

    @Mapping(source = "orderItem.id", target = "orderItemId")
    @Mapping(target = "productName", expression = "java(entity.getOrderItem() != null && entity.getOrderItem().getProductVariant() != null && entity.getOrderItem().getProductVariant().getProduct() != null ? entity.getOrderItem().getProductVariant().getProduct().getName() : null)")
    @Mapping(target = "variantSku", expression = "java(entity.getOrderItem() != null && entity.getOrderItem().getProductVariant() != null ? entity.getOrderItem().getProductVariant().getSku() : null)")
    @Mapping(target = "verifiedByStaffEmail", expression = "java(entity.getVerifiedByStaff() != null ? entity.getVerifiedByStaff().getEmail() : null)")
    @Mapping(source = "status", target = "status")
    PrescriptionDTO toDTO(Prescription entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "orderItem", ignore = true)
    @Mapping(target = "verifiedByStaff", ignore = true)
    @Mapping(target = "verifiedAt", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(@MappingTarget Prescription entity, PrescriptionCreateRequest request);
}

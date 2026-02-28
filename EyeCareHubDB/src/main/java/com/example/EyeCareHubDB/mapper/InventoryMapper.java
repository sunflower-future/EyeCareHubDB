package com.example.EyeCareHubDB.mapper;

import com.example.EyeCareHubDB.dto.InventoryLocationDTO;
import com.example.EyeCareHubDB.dto.InventoryStockDTO;
import com.example.EyeCareHubDB.entity.InventoryLocation;
import com.example.EyeCareHubDB.entity.InventoryStock;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface InventoryMapper {



    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    InventoryLocation toLocationEntity(InventoryLocationDTO dto);

    InventoryLocationDTO toLocationDTO(InventoryLocation entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "code", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateLocationEntity(@MappingTarget InventoryLocation entity, InventoryLocationDTO dto);



    @Mapping(source = "productVariant.id", target = "productVariantId")
    @Mapping(source = "productVariant.sku", target = "variantSku")
    @Mapping(target = "productName", expression = "java(entity.getProductVariant() != null && entity.getProductVariant().getProduct() != null ? entity.getProductVariant().getProduct().getName() : null)")
    @Mapping(source = "location.id", target = "locationId")
    @Mapping(source = "location.name", target = "locationName")
    @Mapping(source = "location.code", target = "locationCode")
    InventoryStockDTO toStockDTO(InventoryStock entity);
}

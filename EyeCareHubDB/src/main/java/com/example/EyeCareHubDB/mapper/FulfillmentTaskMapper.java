package com.example.EyeCareHubDB.mapper;

import com.example.EyeCareHubDB.dto.FulfillmentTaskDTO;
import com.example.EyeCareHubDB.entity.FulfillmentTask;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FulfillmentTaskMapper {

    @Mapping(source = "order.id", target = "orderId")
    @Mapping(source = "order.orderNumber", target = "orderNumber")
    @Mapping(source = "assignee.id", target = "assigneeId")
    @Mapping(source = "assignee.email", target = "assigneeEmail")
    FulfillmentTaskDTO toDTO(FulfillmentTask entity);
}

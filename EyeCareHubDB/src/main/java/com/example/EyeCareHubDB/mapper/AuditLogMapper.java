package com.example.EyeCareHubDB.mapper;

import com.example.EyeCareHubDB.entity.AuditLog;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AuditLogMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "changedBy", ignore = true)
    @Mapping(target = "changedAt", ignore = true)
    @Mapping(target = "ipAddress", ignore = true)
    AuditLog toEntity(String entityType, Long entityId, AuditLog.AuditAction action,
            String oldValue, String newValue);
}

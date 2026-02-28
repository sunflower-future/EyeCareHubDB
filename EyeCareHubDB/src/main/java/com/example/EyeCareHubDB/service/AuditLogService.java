package com.example.EyeCareHubDB.service;

import com.example.EyeCareHubDB.entity.Account;
import com.example.EyeCareHubDB.entity.AuditLog;
import com.example.EyeCareHubDB.mapper.AuditLogMapper;
import com.example.EyeCareHubDB.repository.AuditLogRepository;
import com.example.EyeCareHubDB.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;
    private final AuditLogMapper auditLogMapper;

    public void log(String entityType, Long entityId, AuditLog.AuditAction action,
            String oldValue, String newValue) {
        Account currentUser = SecurityUtils.getCurrentAccount();

        AuditLog auditLog = auditLogMapper.toEntity(entityType, entityId, action, oldValue, newValue);
        auditLog.setChangedBy(currentUser);

        auditLogRepository.save(auditLog);
    }
}

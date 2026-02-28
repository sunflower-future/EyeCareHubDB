package com.example.EyeCareHubDB.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.EyeCareHubDB.dto.FulfillmentTaskDTO;
import com.example.EyeCareHubDB.entity.Account;
import com.example.EyeCareHubDB.entity.AuditLog;
import com.example.EyeCareHubDB.entity.FulfillmentTask;
import com.example.EyeCareHubDB.entity.Order;
import com.example.EyeCareHubDB.mapper.FulfillmentTaskMapper;
import com.example.EyeCareHubDB.repository.AccountRepository;
import com.example.EyeCareHubDB.repository.FulfillmentTaskRepository;
import com.example.EyeCareHubDB.repository.OrderRepository;
import com.example.EyeCareHubDB.util.SecurityUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class FulfillmentService {

    private final FulfillmentTaskRepository taskRepository;
    private final OrderRepository orderRepository;
    private final AccountRepository accountRepository;
    private final AuditLogService auditLogService;
    private final FulfillmentTaskMapper fulfillmentTaskMapper;

    public List<FulfillmentTaskDTO> generateTasks(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));

        List<FulfillmentTask.TaskType> taskTypes;
        switch (order.getOrderType()) {
            case PREORDER:
                taskTypes = Arrays.asList(
                        FulfillmentTask.TaskType.RECEIVE_PREORDER,
                        FulfillmentTask.TaskType.QC,
                        FulfillmentTask.TaskType.PACK,
                        FulfillmentTask.TaskType.SHIP);
                break;
            case PRESCRIPTION:
                taskTypes = Arrays.asList(
                        FulfillmentTask.TaskType.CUT_LENS,
                        FulfillmentTask.TaskType.ASSEMBLE,
                        FulfillmentTask.TaskType.QC,
                        FulfillmentTask.TaskType.PACK,
                        FulfillmentTask.TaskType.SHIP);
                break;
            default:
                taskTypes = Arrays.asList(
                        FulfillmentTask.TaskType.QC,
                        FulfillmentTask.TaskType.PACK,
                        FulfillmentTask.TaskType.SHIP);
                break;
        }

        List<FulfillmentTask> tasks = new ArrayList<>();
        for (int i = 0; i < taskTypes.size(); i++) {
            FulfillmentTask task = FulfillmentTask.builder()
                    .order(order)
                    .taskType(taskTypes.get(i))
                    .priority(i)
                    .displayOrder(i)
                    .build();
            tasks.add(task);
        }

        List<FulfillmentTask> saved = taskRepository.saveAll(tasks);
        auditLogService.log("FulfillmentTask", orderId, AuditLog.AuditAction.CREATE, null,
                "Generated " + saved.size() + " tasks for " + order.getOrderType());

        return saved.stream().map(fulfillmentTaskMapper::toDTO).collect(Collectors.toList());
    }

    public List<FulfillmentTaskDTO> getTasksByOrderId(Long orderId) {
        return taskRepository.findByOrderIdOrderByDisplayOrder(orderId).stream()
                .map(fulfillmentTaskMapper::toDTO)
                .collect(Collectors.toList());
    }

    public FulfillmentTaskDTO updateTaskStatus(Long taskId, String newStatus) {
        FulfillmentTask task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found: " + taskId));

        String oldStatus = task.getStatus().name();
        FulfillmentTask.TaskStatus status = FulfillmentTask.TaskStatus.valueOf(newStatus);
        task.setStatus(status);

        if (status == FulfillmentTask.TaskStatus.IN_PROGRESS && task.getStartedAt() == null) {
            task.setStartedAt(LocalDateTime.now());
        }
        if (status == FulfillmentTask.TaskStatus.DONE || status == FulfillmentTask.TaskStatus.SKIPPED) {
            task.setCompletedAt(LocalDateTime.now());
        }

        FulfillmentTask updated = taskRepository.save(task);
        auditLogService.log("FulfillmentTask", taskId, AuditLog.AuditAction.STATUS_CHANGE, oldStatus, newStatus);
        return fulfillmentTaskMapper.toDTO(updated);
    }

    public FulfillmentTaskDTO assignTask(Long taskId, Long staffId) {
        FulfillmentTask task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found: " + taskId));

        Account staff = accountRepository.findById(staffId)
                .orElseThrow(() -> new RuntimeException("Staff not found: " + staffId));

        task.setAssignee(staff);
        FulfillmentTask updated = taskRepository.save(task);
        auditLogService.log("FulfillmentTask", taskId, AuditLog.AuditAction.UPDATE, null,
                "Assigned to " + staff.getEmail());
        return fulfillmentTaskMapper.toDTO(updated);
    }

    public List<FulfillmentTaskDTO> getMyTasks() {
        Account currentUser = SecurityUtils.getCurrentAccount();
        if (currentUser == null)
            throw new RuntimeException("Not authenticated");
        return taskRepository.findByAssigneeId(currentUser.getId()).stream()
                .map(fulfillmentTaskMapper::toDTO)
                .collect(Collectors.toList());
    }

    public Page<FulfillmentTaskDTO> getAllTasks(String query, String status, String taskType, Long assigneeId,
            Pageable pageable) {
        FulfillmentTask.TaskStatus taskStatus = null;
        FulfillmentTask.TaskType type = null;
        if (status != null && !status.isEmpty()) {
            try {
                taskStatus = FulfillmentTask.TaskStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException ignored) {
            }
        }
        if (taskType != null && !taskType.isEmpty()) {
            try {
                type = FulfillmentTask.TaskType.valueOf(taskType.toUpperCase());
            } catch (IllegalArgumentException ignored) {
            }
        }
        return taskRepository.searchTasks(query, taskStatus, type, assigneeId, pageable)
                .map(fulfillmentTaskMapper::toDTO);
    }
}

package com.example.EyeCareHubDB.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.EyeCareHubDB.entity.FulfillmentTask;

@Repository
public interface FulfillmentTaskRepository extends JpaRepository<FulfillmentTask, Long> {

    List<FulfillmentTask> findByOrderIdOrderByDisplayOrder(Long orderId);

    List<FulfillmentTask> findByAssigneeIdAndStatusIn(Long assigneeId, List<FulfillmentTask.TaskStatus> statuses);

    List<FulfillmentTask> findByAssigneeId(Long assigneeId);

    Page<FulfillmentTask> findAll(Pageable pageable);

    Page<FulfillmentTask> findByStatus(FulfillmentTask.TaskStatus status, Pageable pageable);

    @Query("SELECT t FROM FulfillmentTask t WHERE " +
            "(:status IS NULL OR t.status = :status) AND " +
            "(:taskType IS NULL OR t.taskType = :taskType) AND " +
            "(:assigneeId IS NULL OR t.assignee.id = :assigneeId) AND " +
            "(:query IS NULL OR t.order.orderNumber LIKE %:query%)")
    Page<FulfillmentTask> searchTasks(@Param("query") String query,
            @Param("status") FulfillmentTask.TaskStatus status,
            @Param("taskType") FulfillmentTask.TaskType taskType,
            @Param("assigneeId") Long assigneeId,
            Pageable pageable);
}

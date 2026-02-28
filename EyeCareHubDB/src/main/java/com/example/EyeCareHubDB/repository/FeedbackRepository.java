package com.example.EyeCareHubDB.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.EyeCareHubDB.entity.Feedback;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

    List<Feedback> findByProductIdAndStatus(Long productId, Feedback.FeedbackStatus status);

    Page<Feedback> findByProductIdAndStatus(Long productId, Feedback.FeedbackStatus status, Pageable pageable);

    Page<Feedback> findAll(Pageable pageable);

    Page<Feedback> findByStatus(Feedback.FeedbackStatus status, Pageable pageable);

    @Query("SELECT AVG(f.rating) FROM Feedback f WHERE f.product.id = :productId AND f.status = 'APPROVED'")
    Double getAverageRatingByProductId(@Param("productId") Long productId);

    @Query("SELECT COUNT(f) FROM Feedback f WHERE f.product.id = :productId AND f.status = 'APPROVED'")
    Long countApprovedByProductId(@Param("productId") Long productId);

    @Query("SELECT f.rating, COUNT(f) FROM Feedback f WHERE f.product.id = :productId AND f.status = 'APPROVED' GROUP BY f.rating ORDER BY f.rating DESC")
    List<Object[]> getRatingDistribution(@Param("productId") Long productId);

    boolean existsByAccountIdAndOrderItemId(Long accountId, Long orderItemId);

    @Query("SELECT f FROM Feedback f WHERE " +
            "(:status IS NULL OR f.status = :status) AND " +
            "(:rating IS NULL OR f.rating = :rating) AND " +
            "(:productId IS NULL OR f.product.id = :productId) AND " +
            "(:query IS NULL OR f.title LIKE %:query% OR f.comment LIKE %:query% OR f.account.email LIKE %:query%)")
    Page<Feedback> searchFeedbacks(@Param("query") String query,
            @Param("status") Feedback.FeedbackStatus status,
            @Param("rating") Integer rating,
            @Param("productId") Long productId,
            Pageable pageable);
}

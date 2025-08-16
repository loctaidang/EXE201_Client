package com.todo.chrono.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.todo.chrono.entity.Payment;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Integer> {
    // Doanh thu theo ngày
    @Query("""
                SELECT COALESCE(SUM(p.totalMoney), 0) FROM Payment p
                WHERE FUNCTION('YEAR', p.paidAt) = :year
                AND FUNCTION('MONTH', p.paidAt) = :month
                AND FUNCTION('DAY', p.paidAt) = :day
                AND p.paymentStatus = 'PAID'
                AND p.paymentMethod = 'MOMO'
            """)
    BigDecimal getTotalRevenueByDay(@Param("year") int year, @Param("month") int month, @Param("day") int day);

    // Doanh thu theo tháng
    @Query("""
                SELECT COALESCE(SUM(p.totalMoney), 0) FROM Payment p
                WHERE FUNCTION('YEAR', p.paidAt) = :year
                AND FUNCTION('MONTH', p.paidAt) = :month
                AND p.paymentStatus = 'PAID'
                AND p.paymentMethod = 'MOMO'
            """)
    BigDecimal getTotalRevenueByMonth(@Param("year") int year, @Param("month") int month);

    // Doanh thu theo năm
    @Query("""
                SELECT COALESCE(SUM(p.totalMoney), 0) FROM Payment p
                WHERE FUNCTION('YEAR', p.paidAt) = :year
                AND p.paymentStatus = 'PAID'
                AND p.paymentMethod = 'MOMO'
            """)
    BigDecimal getTotalRevenueByYear(@Param("year") int year);

    // Doanh thu theo khoảng thời gian
    @Query("""
                SELECT COALESCE(SUM(p.totalMoney), 0) FROM Payment p
                WHERE DATE(p.paidAt) BETWEEN :startDate AND :endDate
                AND p.paymentStatus = 'PAID'
                AND p.paymentMethod = 'MOMO'
            """)
    BigDecimal getTotalRevenueBetweenDates(@Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    // Tổng doanh thu theo gói subscription trong khoảng thời gian
    @Query("""
                SELECT COALESCE(SUM(p.totalMoney), 0) FROM Payment p
                WHERE p.paymentStatus = 'PAID'
                AND p.paymentMethod = 'MOMO'
                AND p.subscriptionPlan.id = :subscriptionPlanId
                AND DATE(p.paidAt) BETWEEN :startDate AND :endDate
            """)
    BigDecimal getRevenueBySubscriptionPlanAndDateRange(
            @Param("subscriptionPlanId") int subscriptionPlanId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("""
                SELECT COALESCE(SUM(p.totalMoney), 0) FROM Payment p
                WHERE FUNCTION('YEAR', p.paidAt) = :year
                AND FUNCTION('MONTH', p.paidAt) = :month
                AND FUNCTION('DAY', p.paidAt) = :day
                AND p.subscriptionPlan.id = :subscriptionPlanId
                AND p.paymentStatus = 'PAID'
                AND p.paymentMethod = 'MOMO'
            """)
    BigDecimal getRevenueBySubscriptionPlanAndDay(
            @Param("subscriptionPlanId") int subscriptionPlanId,
            @Param("year") int year,
            @Param("month") int month,
            @Param("day") int day);

    @Query("""
                SELECT COALESCE(SUM(p.totalMoney), 0) FROM Payment p
                WHERE FUNCTION('YEAR', p.paidAt) = :year
                AND FUNCTION('MONTH', p.paidAt) = :month
                AND p.subscriptionPlan.id = :subscriptionPlanId
                AND p.paymentStatus = 'PAID'
                AND p.paymentMethod = 'MOMO'
            """)
    BigDecimal getRevenueBySubscriptionPlanAndMonth(
            @Param("subscriptionPlanId") int subscriptionPlanId,
            @Param("year") int year,
            @Param("month") int month);

    @Query("""
                SELECT COALESCE(SUM(p.totalMoney), 0) FROM Payment p
                WHERE FUNCTION('YEAR', p.paidAt) = :year
                AND p.subscriptionPlan.id = :subscriptionPlanId
                AND p.paymentStatus = 'PAID'
                AND p.paymentMethod = 'MOMO'
            """)
    BigDecimal getRevenueBySubscriptionPlanAndYear(
            @Param("subscriptionPlanId") int subscriptionPlanId,
            @Param("year") int year);

    @Query("""
                SELECT p FROM Payment p
                WHERE p.user.id = :userId
                ORDER BY p.paidAt DESC
            """)
    List<Payment> findPaymentsByUserId(@Param("userId") int userId);

    List<Payment> findByUserIdOrderByPaidAtDesc(Integer userId);
    
    @Query("""
       SELECT p FROM Payment p
       LEFT JOIN FETCH p.user
       LEFT JOIN FETCH p.subscriptionPlan
       """)
List<Payment> findAllWithDeletedPlans();

}

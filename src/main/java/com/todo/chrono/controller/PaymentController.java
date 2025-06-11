package com.todo.chrono.controller;

import com.todo.chrono.dto.request.PaymentDTO;
import com.todo.chrono.dto.request.PaymentHistoryDTO;
import com.todo.chrono.dto.request.RechargeRequestDTO;
import com.todo.chrono.service.PaymentService;
import com.todo.chrono.util.error.IdInvalidException;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@CrossOrigin("*")
@RestController
@AllArgsConstructor
@RequestMapping("/payment")
@SecurityRequirement(name = "api")
public class PaymentController {
    @Autowired
    PaymentService paymentService;

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('FREE', 'ADMIN','PREMIUM')")
    public ResponseEntity<String> createPayment(@RequestBody RechargeRequestDTO paymentDTO) throws Exception {
        String savedPayment = paymentService.createUrl(paymentDTO);
        return new ResponseEntity<>(savedPayment, HttpStatus.CREATED);
    }

    @PostMapping("/callback")
    @PreAuthorize("hasAnyRole('FREE', 'ADMIN','PREMIUM')")
    public ResponseEntity<String> paymentCallback(@RequestBody Map<String, String> queryParams) {
        paymentService.handlePaymentCallback(queryParams);
        return new ResponseEntity<>("Payment processed", HttpStatus.OK);
    }

    @GetMapping("/day")
    public ResponseEntity<BigDecimal> getRevenueByDay(
            @RequestParam int year,
            @RequestParam int month,
            @RequestParam int day) {
        return ResponseEntity.ok(paymentService.getRevenueByDay(year, month, day));
    }

    @GetMapping("/month")
    public ResponseEntity<BigDecimal> getRevenueByMonth(
            @RequestParam int year,
            @RequestParam int month) {
        return ResponseEntity.ok(paymentService.getRevenueByMonth(year, month));
    }

    @GetMapping("/year")
    public ResponseEntity<BigDecimal> getRevenueByYear(@RequestParam int year) {
        return ResponseEntity.ok(paymentService.getRevenueByYear(year));
    }

    @GetMapping("/range")
    public ResponseEntity<BigDecimal> getRevenueBetweenDates(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(paymentService.getRevenueBetweenDates(startDate, endDate));
    }

    @GetMapping("/revenue/{subscription_plans_id}")
    public ResponseEntity<BigDecimal> getRevenueBySubscriptionPlan(
            @PathVariable int subscriptionPlanId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate end) {
        BigDecimal revenue = paymentService.getRevenueBySubscriptionPlan(subscriptionPlanId, start, end);
        return ResponseEntity.ok(revenue);
    }

    @GetMapping("/revenue/{subscription_plans_id}/day")
    public ResponseEntity<BigDecimal> getRevenueByDay(
            @PathVariable int subscriptionPlanId,
            @RequestParam int year,
            @RequestParam int month,
            @RequestParam int day) {
        return ResponseEntity
                .ok(paymentService.getRevenueBySubscriptionPlanAndDay(subscriptionPlanId, year, month, day));
    }

    @GetMapping("/revenue/{subscription_plans_id}/month")
    public ResponseEntity<BigDecimal> getRevenueByMonth(
            @PathVariable int subscriptionPlanId,
            @RequestParam int year,
            @RequestParam int month) {
        return ResponseEntity.ok(paymentService.getRevenueBySubscriptionPlanAndMonth(subscriptionPlanId, year, month));
    }

    @GetMapping("/revenue/{subscription_plans_id}/year")
    public ResponseEntity<BigDecimal> getRevenueByYear(
            @PathVariable int subscriptionPlanId,
            @RequestParam int year) {
        return ResponseEntity.ok(paymentService.getRevenueBySubscriptionPlanAndYear(subscriptionPlanId, year));
    }

    @GetMapping("/history/{userId}")
    public ResponseEntity<List<PaymentHistoryDTO>> getPaymentHistory(@PathVariable Integer userId) {
        try {
            List<PaymentHistoryDTO> history = paymentService.getPaymentHistoryByUserId(userId);
            return ResponseEntity.ok(history);
        } catch (IdInvalidException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // @GetMapping("/user/{user_id}")
    // @PreAuthorize("hasAnyRole('PARENT','MANAGER')")
    // public ResponseEntity <List<PaymentDTO>>getTopicByChapterId
    // (@PathVariable("user_id") int user_id) throws RuntimeException {
    // List<PaymentDTO> paymentDTOS = paymentService.getPaymetsByUserId(user_id);
    // return ResponseEntity.ok(paymentDTOS);
    // }

    // @GetMapping("/course/{course_id}")
    // @PreAuthorize("hasAnyRole('PARENT','MANAGER')")
    // public ResponseEntity <List<ResPaymentDTO>>
    // getPaymetsByCourseId(@PathVariable("course_id") int course_id) throws
    // RuntimeException {
    // List<ResPaymentDTO> paymentDTOS =
    // paymentService.getPaymetsByCourseId(course_id);
    // return ResponseEntity.ok(paymentDTOS);
    // }
    // @GetMapping("/user/{user_id}/date/{payment_date}")
    // @PreAuthorize("hasAnyRole('PARENT','MANAGER')")
    // public ResponseEntity<List<PaymentDTO>>
    // getPaymentsByUserIdAndPaymentDate(@PathVariable int user_id,
    // @PathVariable String payment_date) {
    // List<PaymentDTO> payments =
    // paymentService.getPaymentsByUserIdAndPaymentDate(user_id, payment_date);
    // return ResponseEntity.ok(payments);
    // }
    // @GetMapping("/user/{user_id}/month/{payment_month}")
    // @PreAuthorize("hasAnyRole('PARENT','MANAGER')")
    // public ResponseEntity<List<PaymentDTO>>
    // getPaymentsByUserIdAndPaymentMonth(@PathVariable int user_id,
    // @PathVariable String payment_month) {
    // List<PaymentDTO> payments =
    // paymentService.getPaymentsByUserIdAndPaymentMonth(user_id, payment_month);
    // return ResponseEntity.ok(payments);
    // }
    // @GetMapping("/user/{user_id}/year/{payment_year}")
    // @PreAuthorize("hasAnyRole('PARENT','MANAGER')")
    // public ResponseEntity<List<PaymentDTO>>
    // getPaymentsByUserIdAndPaymentYear(@PathVariable int user_id,
    // @PathVariable String payment_year) {
    // List<PaymentDTO> payments =
    // paymentService.getPaymentsByUserIdAndPaymentYear(user_id, payment_year);
    // return ResponseEntity.ok(payments);
    // }
}

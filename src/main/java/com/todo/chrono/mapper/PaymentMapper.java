package com.todo.chrono.mapper;

import com.todo.chrono.dto.request.PaymentDTO;
import com.todo.chrono.entity.Payment;
import com.todo.chrono.entity.SubscriptionPlans;
import com.todo.chrono.dto.request.PaymentHistoryDTO;

public class PaymentMapper {
    public static PaymentDTO mapToPaymentDTO(Payment payment) {
        PaymentDTO dto = new PaymentDTO();
        dto.setId(payment.getId());
        dto.setPaymentStatus(payment.getPaymentStatus());
        dto.setPayment_method(payment.getPaymentMethod());
        dto.setTotal_money(payment.getTotalMoney());
        dto.setPaidAt(payment.getPaidAt());
        // dto.setUserId(payment.getUser().getId());

        return dto;
    }

    public static Payment mapToPayment(PaymentDTO dto) {
        Payment payment = new Payment();
        payment.setId(dto.getId());
        payment.setPaymentStatus(dto.getPaymentStatus());
        payment.setPaymentMethod(dto.getPayment_method());
        payment.setTotalMoney(dto.getTotal_money());
        payment.setPaidAt(dto.getPaidAt());
        // payment.setUser(new User(dto.getUserId()));
        return payment;
    }



    public static Payment mapToPaymentHistoryDTO(PaymentHistoryDTO dto) {
        Payment payment = new Payment();
        payment.setId(dto.getPaymentId());
        payment.setTotalMoney(dto.getTotalMoney());
        payment.setPaymentMethod(dto.getPaymentMethod());
        payment.setPaymentStatus(dto.getPaymentStatus());
        payment.setPaidAt(dto.getPaidAt());

        SubscriptionPlans plan = new SubscriptionPlans();
        plan.setName(dto.getSubscriptionPlanName());
        payment.setSubscriptionPlan(plan);

        return payment;
    }
    public static PaymentHistoryDTO mapToPaymentHistoryDTO(Payment payment) {
        PaymentHistoryDTO dto = new PaymentHistoryDTO();
        dto.setPaymentId(payment.getId());
        dto.setSubscriptionPlanName(payment.getSubscriptionPlan().getName());
        dto.setTotalMoney(payment.getTotalMoney());
        dto.setPaymentMethod(payment.getPaymentMethod());
        dto.setPaymentStatus(payment.getPaymentStatus());
        dto.setPaidAt(payment.getPaidAt());
        if (payment.getUser() != null) {
            dto.setUserId(payment.getUser().getId());
            dto.setUsername(payment.getUser().getUsername());
        }
        return dto;
    }
}
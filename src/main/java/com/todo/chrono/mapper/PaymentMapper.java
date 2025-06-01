package com.todo.chrono.mapper;


import com.todo.chrono.dto.request.PaymentDTO;
import com.todo.chrono.entity.Payment;

public class PaymentMapper {
    public static PaymentDTO mapToPaymentDTO(Payment payment) {
        PaymentDTO dto = new PaymentDTO();
        dto.setId(payment.getId());
        dto.setPaymentStatus(payment.getPaymentStatus());
        dto.setPayment_method(payment.getPayment_method());
        dto.setTotal_money(payment.getTotal_money());
        dto.setPaidAt(payment.getPaidAt());
        // dto.setUserId(payment.getUser().getId());

        return dto;
    }

    public static Payment mapToPayment(PaymentDTO dto) {
        Payment payment = new Payment();
        payment.setId(dto.getId());
        payment.setPaymentStatus(dto.getPaymentStatus());
        payment.setPayment_method(dto.getPayment_method());
        payment.setTotal_money(dto.getTotal_money());
        payment.setPaidAt(dto.getPaidAt());
        // payment.setUser(new User(dto.getUserId())); 
        return payment;
    }
}
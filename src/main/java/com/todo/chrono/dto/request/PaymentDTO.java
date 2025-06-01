package com.todo.chrono.dto.request;

import com.todo.chrono.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor


public class PaymentDTO {
    private int id;
    // private int user_id;
    private PaymentStatus paymentStatus;
    private String payment_method;
    private double total_money;
    private LocalDateTime paidAt;
}

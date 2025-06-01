package com.todo.chrono.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import com.todo.chrono.entity.Payment;


public interface PaymentRepository extends JpaRepository<Payment, Integer> {
    
}

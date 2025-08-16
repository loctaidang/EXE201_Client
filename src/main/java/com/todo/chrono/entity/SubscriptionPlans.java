package com.todo.chrono.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "subscription_plans")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SubscriptionPlans {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @Column(name = "name")
    private String name;
    @Column(name = "description")
    private String description;
    @Column(name = "price")
    private double price;
    @Column(name = "duration_days")
    private int durationDays;    
    @Column(name = "is_deleted",nullable = false)
    private Boolean isDeleted;
    
    //relationship
    @OneToMany(mappedBy = "subscriptionPlan", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonIgnore
    private List<Payment> payments;
}

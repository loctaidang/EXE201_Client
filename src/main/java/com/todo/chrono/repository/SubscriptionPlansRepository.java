package com.todo.chrono.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.todo.chrono.entity.SubscriptionPlans;
import java.util.Optional;
import java.util.List;

public interface SubscriptionPlansRepository extends JpaRepository<SubscriptionPlans, Integer> {
    Optional<SubscriptionPlans> findByName(String name);
    boolean existsByName(String name);
    List<SubscriptionPlans> findByIsDeletedFalse();
}

package com.todo.chrono.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.todo.chrono.entity.SubscriptionPlans;
import java.util.Optional;

public interface SubscriptionPlansRepository extends JpaRepository<SubscriptionPlans, Integer> {
    Optional<SubscriptionPlans> findByName(String name);
    boolean existsByName(String name);

}

package com.todo.chrono.service.subscriptionPlansService;

import com.todo.chrono.dto.request.SubscriptionPlansDTO;
import com.todo.chrono.dto.request.SubscriptionPlansCreateDTO;
import com.todo.chrono.util.error.IdInvalidException;

import java.util.List;

public interface SubscriptionPlansService {
    SubscriptionPlansDTO createSubscriptionPlans(SubscriptionPlansCreateDTO subscriptionPlansCreateDTO) throws IdInvalidException;

    SubscriptionPlansDTO getSubscriptionPlansById ( Integer subscription_plans_id) throws IdInvalidException;

    List<SubscriptionPlansDTO> getSubscriptionPlansAll();

    SubscriptionPlansDTO updateSubscriptionPlans (SubscriptionPlansCreateDTO subscriptionPlansCreateDTO, Integer subscription_plans_id) throws IdInvalidException;

    void deleteSubscriptionPlans (Integer subscription_plans_id) throws IdInvalidException;

   
}

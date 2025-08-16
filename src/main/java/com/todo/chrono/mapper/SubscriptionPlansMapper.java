package com.todo.chrono.mapper;

import com.todo.chrono.dto.request.SubscriptionPlansDTO;
import com.todo.chrono.dto.request.SubscriptionPlansCreateDTO;
import com.todo.chrono.entity.SubscriptionPlans;

public class SubscriptionPlansMapper {

    public static SubscriptionPlansDTO mapToSubscriptionPlansDTO(SubscriptionPlans subscriptionPlans){
        SubscriptionPlansDTO subscriptionPlansDTO = new SubscriptionPlansDTO();
        subscriptionPlansDTO.setId(subscriptionPlans.getId());
        subscriptionPlansDTO.setName(subscriptionPlans.getName());
        subscriptionPlansDTO.setDescription(subscriptionPlans.getDescription());
        subscriptionPlansDTO.setPrice(subscriptionPlans.getPrice());
        subscriptionPlansDTO.setDurationDays(subscriptionPlans.getDurationDays());
        return subscriptionPlansDTO;

    }
    public static SubscriptionPlans mapToSubscriptionPlans(SubscriptionPlansDTO subscriptionPlansDTO){
        SubscriptionPlans subscriptionPlans = new SubscriptionPlans();  
        subscriptionPlans.setId(subscriptionPlansDTO.getId());
        subscriptionPlans.setName(subscriptionPlansDTO.getName());
        subscriptionPlans.setDescription(subscriptionPlansDTO.getDescription());
        subscriptionPlans.setPrice(subscriptionPlansDTO.getPrice());
        subscriptionPlans.setDurationDays(subscriptionPlansDTO.getDurationDays());
        return subscriptionPlans;
    }
    public static SubscriptionPlans mapToSubscriptionPlans(SubscriptionPlansCreateDTO subscriptionPlansCreateDTO){
        SubscriptionPlans subscriptionPlans = new SubscriptionPlans();
        subscriptionPlans.setName(subscriptionPlansCreateDTO.getName());
        subscriptionPlans.setDescription(subscriptionPlansCreateDTO.getDescription());
        subscriptionPlans.setPrice(subscriptionPlansCreateDTO.getPrice());
        subscriptionPlans.setDurationDays(subscriptionPlansCreateDTO.getDurationDays());
        subscriptionPlans.setIsDeleted(false);
        return subscriptionPlans;
    }
}

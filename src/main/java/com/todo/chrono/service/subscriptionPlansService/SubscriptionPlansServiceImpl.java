package com.todo.chrono.service.subscriptionPlansService;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import com.todo.chrono.repository.SubscriptionPlansRepository;
import com.todo.chrono.dto.request.SubscriptionPlansDTO;
import com.todo.chrono.util.error.IdInvalidException;
import com.todo.chrono.entity.SubscriptionPlans;
import com.todo.chrono.mapper.SubscriptionPlansMapper;
import java.util.Optional;
import com.todo.chrono.dto.request.SubscriptionPlansCreateDTO;
import java.util.List;
import com.todo.chrono.entity.Payment;
import com.todo.chrono.repository.PaymentRepository;
import java.time.LocalDateTime;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class SubscriptionPlansServiceImpl implements SubscriptionPlansService {

    private SubscriptionPlansRepository subscriptionPlansRepository;

    @Override
    public SubscriptionPlansDTO createSubscriptionPlans(SubscriptionPlansCreateDTO subscriptionPlansCreateDTO)
            throws IdInvalidException {
        if (subscriptionPlansRepository.existsByName(subscriptionPlansCreateDTO.getName())) {
            throw new IdInvalidException(
                    "Gói với tên = " + subscriptionPlansCreateDTO.getName()
                            + " đã tồn tại, vui lòng sử dụng tên khác.");
        }
        SubscriptionPlans subscriptionPlans = SubscriptionPlansMapper
                .mapToSubscriptionPlans(subscriptionPlansCreateDTO);
        SubscriptionPlans savedSubscriptionPlans = subscriptionPlansRepository.save(subscriptionPlans);
        return SubscriptionPlansMapper.mapToSubscriptionPlansDTO(savedSubscriptionPlans);
    }

    @Override
    public SubscriptionPlansDTO getSubscriptionPlansById(Integer subscription_plans_id) throws IdInvalidException {
        Optional<SubscriptionPlans> subscriptionPlans = subscriptionPlansRepository.findById(subscription_plans_id);
        if (subscriptionPlans.isPresent()) {
            return SubscriptionPlansMapper.mapToSubscriptionPlansDTO(subscriptionPlans.get());
        } else {
            throw new IdInvalidException("Subscription plan với id = " + subscription_plans_id + " không tồn tại");
        }
    }

    @Override
    public List<SubscriptionPlansDTO> getSubscriptionPlansAll() {
        List<SubscriptionPlans> subscriptionPlans = subscriptionPlansRepository.findAll();
        return subscriptionPlans.stream()
                .map(SubscriptionPlansMapper::mapToSubscriptionPlansDTO)
                .collect(Collectors.toList());
    }

    @Override
    public SubscriptionPlansDTO updateSubscriptionPlans(SubscriptionPlansCreateDTO updateSubscriptionPlans,
            Integer subscription_plans_id) throws IdInvalidException {
        SubscriptionPlans subscriptionPlans = subscriptionPlansRepository.findById(subscription_plans_id)
                .orElseThrow(() -> new RuntimeException("Subscription plan " + subscription_plans_id + " not found"));
        String newName = updateSubscriptionPlans.getName();
        String currentName = subscriptionPlans.getName();

        if (newName != null && !newName.equals(currentName)) {
            if (subscriptionPlansRepository.existsByName(newName)) {
                throw new IdInvalidException("Subscription plan với tên = " + newName + " đã tồn tại");
            }
            subscriptionPlans.setName(newName);
        }

        if (updateSubscriptionPlans.getPrice() != null) {
            subscriptionPlans.setPrice(updateSubscriptionPlans.getPrice());
        }
        if (updateSubscriptionPlans.getDescription() != null) {
            subscriptionPlans.setDescription(updateSubscriptionPlans.getDescription());
        }
        if (updateSubscriptionPlans.getDurationDays() != null) {
            subscriptionPlans.setDurationDays(updateSubscriptionPlans.getDurationDays());
        }
        SubscriptionPlans updateSubscriptionPlansObj = subscriptionPlansRepository.save(subscriptionPlans);
        return SubscriptionPlansMapper.mapToSubscriptionPlansDTO(updateSubscriptionPlansObj);
    }

    @Override
    public void deleteSubscriptionPlans(Integer subscription_plans_id) throws IdInvalidException {
        SubscriptionPlans subscriptionPlans = subscriptionPlansRepository.findById(subscription_plans_id)
                .orElseThrow(
                        () -> new IdInvalidException("Subscription plan với id = " + subscription_plans_id
                                + " không tồn tại hoặc đã bị xóa"));
        subscriptionPlansRepository.delete(subscriptionPlans);
    }

}

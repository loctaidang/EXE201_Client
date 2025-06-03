package com.todo.chrono.controller;



import com.todo.chrono.util.error.IdInvalidException;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.todo.chrono.dto.request.WorkspaceDTO;
import com.todo.chrono.service.subscriptionPlansService.SubscriptionPlansService;
import com.todo.chrono.dto.request.SubscriptionPlansDTO;
import com.todo.chrono.dto.request.SubscriptionPlansCreateDTO;
import com.todo.chrono.enums.TaskStatus;
import java.util.List;

@CrossOrigin("*")
@RestController
@AllArgsConstructor
@RequestMapping("/subscription-plans")
@SecurityRequirement(name = "api")
public class SubscriptionPlansController {

    private SubscriptionPlansService subscriptionPlansService;

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('FREE', 'PREMIUM','ADMIN')")
    public ResponseEntity<SubscriptionPlansDTO> createSubscriptionPlans(@RequestBody SubscriptionPlansCreateDTO subscriptionPlansCreateDTO) throws IdInvalidException{
        SubscriptionPlansDTO savedSubscriptionPlans = subscriptionPlansService.createSubscriptionPlans(subscriptionPlansCreateDTO);
        return new ResponseEntity<>(savedSubscriptionPlans, HttpStatus.CREATED);
    }
    @GetMapping("/{subscription_plans_id}")
    @PreAuthorize("hasAnyRole('FREE', 'PREMIUM','ADMIN')")
    public ResponseEntity<SubscriptionPlansDTO> getSubscriptionPlansById (@PathVariable("subscription_plans_id") Integer subscription_plans_id) throws IdInvalidException {
        SubscriptionPlansDTO subscriptionPlansDTO = subscriptionPlansService.getSubscriptionPlansById(subscription_plans_id);
        return ResponseEntity.ok(subscriptionPlansDTO);
    }

    @PutMapping("/{subscription_plans_id}")
    @PreAuthorize("hasAnyRole('FREE', 'PREMIUM','ADMIN')")
    public ResponseEntity<SubscriptionPlansDTO> updateSubscriptionPlans(@RequestBody SubscriptionPlansCreateDTO updatedSubscriptionPlans, @PathVariable("subscription_plans_id") Integer subscription_plans_id) throws IdInvalidException{
        SubscriptionPlansDTO subscriptionPlansDTO = subscriptionPlansService.updateSubscriptionPlans(updatedSubscriptionPlans, subscription_plans_id);
        return ResponseEntity.ok(subscriptionPlansDTO);
    }

    @DeleteMapping("/{subscription_plans_id}")
    @PreAuthorize("hasAnyRole('FREE', 'PREMIUM','ADMIN')")
    public ResponseEntity<Void> deleteSubscriptionPlans(@PathVariable("subscription_plans_id") Integer subscription_plans_id) throws IdInvalidException {
        SubscriptionPlansDTO currentSubscriptionPlans = this.subscriptionPlansService.getSubscriptionPlansById(subscription_plans_id);
        this.subscriptionPlansService.deleteSubscriptionPlans(currentSubscriptionPlans.getId());
        return ResponseEntity.ok(null);
    }

   @GetMapping
   @PreAuthorize("hasAnyRole('FREE', 'PREMIUM','ADMIN')")
   public ResponseEntity<List<SubscriptionPlansDTO>> getSubscriptionPlansAll(){
       List<SubscriptionPlansDTO> subscriptionPlans = subscriptionPlansService.getSubscriptionPlansAll();
       return ResponseEntity.ok(subscriptionPlans);
   }

}

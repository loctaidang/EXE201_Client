package com.todo.chrono.dto.request;


import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RechargeRequestDTO {
    String amount;
    int userId;
}

package com.paathshala.dto.payment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PaymentVerifyResponse {
    //private String username;
   // private String courseTitle;
    private boolean success;
    private int enrollmentId;
    private boolean paid;
    private String message;
}

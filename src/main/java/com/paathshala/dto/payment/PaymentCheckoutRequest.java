package com.paathshala.dto.payment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PaymentCheckoutRequest {

    private String username;
    private String courseTitle;
    private String successUrl;
    private String cancelUrl;
}

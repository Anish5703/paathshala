package com.paathshala.dto.payment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PaymentCheckoutResponse {
    //private String sessionId;    // checkout session id
    //private String username;
    //private String courseTitle;
    private boolean success;
    private String checkoutUrl;
    private String message;
}

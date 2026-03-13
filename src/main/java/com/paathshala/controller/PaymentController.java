package com.paathshala.controller;

import com.paathshala.dto.payment.PaymentCheckoutRequest;
import com.paathshala.dto.payment.PaymentCheckoutResponse;
import com.paathshala.dto.payment.PaymentVerifyRequest;
import com.paathshala.dto.payment.PaymentVerifyResponse;
import com.paathshala.exception.enrollment.PaymentFailedException;
import com.paathshala.service.PaymentService;
import com.stripe.exception.StripeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    private static final Logger log = LoggerFactory.getLogger(PaymentController.class);
    @PreAuthorize("hasRole('STUDENT')")
    @PostMapping("/create-checkout")
    public ResponseEntity<PaymentCheckoutResponse> createCheckout(
            @RequestBody PaymentCheckoutRequest request) {
        PaymentCheckoutResponse response = paymentService.createCheckout(request);
        HttpHeaders header = new HttpHeaders();
        header.set("Content-Type","application/json");
        return ResponseEntity.status(HttpStatus.OK).headers(header).body(response);
    }


    @PostMapping("/verify")
    public ResponseEntity<PaymentVerifyResponse> verifyPayment(
            @RequestBody PaymentVerifyRequest request) {
        return ResponseEntity.ok(paymentService.verifyAndEnroll(request));
    }
}
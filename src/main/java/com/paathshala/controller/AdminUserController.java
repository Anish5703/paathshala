package com.paathshala.controller;

import com.paathshala.dto.user.BasicUserResponse;
import com.paathshala.dto.user.PremiumUserResponse;
import com.paathshala.service.AdminUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminUserController {

    private final AdminUserService adminUserService;

    @GetMapping("/basic")
    public ResponseEntity<List<BasicUserResponse>> getBasicUsers() {
        List<BasicUserResponse> basicUsers = adminUserService.getBasicUsers();
        return ResponseEntity.ok(basicUsers);
    }

    @GetMapping("/premium")
    public ResponseEntity<List<PremiumUserResponse>> getPremiumUsers() {
        List<PremiumUserResponse> premiumUsers = adminUserService.getPremiumUsers();
        return ResponseEntity.ok(premiumUsers);
    }
}
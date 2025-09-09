package com.nuri.moneymanager.controller;

import com.nuri.moneymanager.dto.DtoAuth;
import com.nuri.moneymanager.dto.DtoProfile;
import com.nuri.moneymanager.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class ProfileController {
    private final ProfileService profileService;

    @PostMapping("/register")
    public ResponseEntity<DtoProfile> registerProfile(@RequestBody DtoProfile dtoProfile) {
        DtoProfile registerProfile = profileService.registerProfile(dtoProfile);
        return ResponseEntity.status(HttpStatus.CREATED).body(registerProfile);
    }
    @GetMapping("/activate")
    public ResponseEntity<String> activateProfile(@RequestParam String token) {
        boolean isActivated = profileService.activateProfile(token);
        if (isActivated) {
            return ResponseEntity.ok("Profile activated successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Activation token not found or already used.");
        }
    }
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login (@RequestBody DtoAuth dtoAuth) {
        try {
            if (!profileService.isAccountActive(dtoAuth.getEmail())){
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("message", "Account is not active. Please check your email for activation link."));
            }
            Map<String, Object> response = profileService.authenticateAndGenerateToken(dtoAuth);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Invalid email or password."));
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<DtoProfile> getPublicProfile() {
        DtoProfile profile = profileService.getPublicProfile(null);
        return ResponseEntity.ok(profile);
    }
}

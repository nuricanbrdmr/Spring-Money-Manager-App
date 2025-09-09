package com.nuri.moneymanager.service;

import com.nuri.moneymanager.dto.DtoAuth;
import com.nuri.moneymanager.dto.DtoProfile;
import com.nuri.moneymanager.entity.ProfileEntity;
import com.nuri.moneymanager.repository.ProfileRepository;
import com.nuri.moneymanager.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.neo4j.Neo4jProperties;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProfileService {
    @Autowired
    private final ProfileRepository profileRepository;
    @Autowired
    private final EmailService emailService;
    @Autowired
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    public DtoProfile registerProfile(DtoProfile dtoProfile) {
        ProfileEntity newProfile = toEntity(dtoProfile);
        newProfile.setActivationToken(UUID.randomUUID().toString());
        newProfile = profileRepository.save(newProfile);
        // send activation email
        String activationLink = "http://localhost:8080/api/v1.0/activate?token=" + newProfile.getActivationToken();
        String subject = "Activate your Money Manager account";
        String body = "Please click the following link to activate your account: <a href=\"" + activationLink + "\">Activate Account</a>";
        emailService.sendEmail(newProfile.getEmail(), subject, body);
        return toDTO(newProfile);
    }

    public ProfileEntity toEntity(DtoProfile dtoProfile) {
        ProfileEntity profileEntity = ProfileEntity.builder()
                .id(dtoProfile.getId())
                .fullName(dtoProfile.getFullName())
                .email(dtoProfile.getEmail())
                .password(passwordEncoder.encode(dtoProfile.getPassword()))
                .profileImageUrl(dtoProfile.getProfileImageUrl())
                .cratedAt(dtoProfile.getCratedAt())
                .updatedAt(dtoProfile.getUpdatedAt())
                .build();
        profileEntity.prePresist(); // Set default values
        return profileEntity;
    }
    public DtoProfile toDTO(ProfileEntity profileEntity) {
        return DtoProfile.builder()
                .id(profileEntity.getId())
                .fullName(profileEntity.getFullName())
                .email(profileEntity.getEmail())
                .profileImageUrl(profileEntity.getProfileImageUrl())
                .cratedAt(profileEntity.getCratedAt())
                .updatedAt(profileEntity.getUpdatedAt())
                .build();
    }

    public boolean activateProfile(String activationToken) {
        return profileRepository.findByActivationToken(activationToken)
                .map(profile -> {
                    profile.setIsActive(true); // Clear the activation token
                    profileRepository.save(profile);
                    return true;
                })
                .orElse(false);
    }
    public boolean isAccountActive(String email) {
        return profileRepository.findByEmail(email)
                .map(ProfileEntity::getIsActive)
                .orElse(false);
    }
    public ProfileEntity getCurrentProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return profileRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("Profile not found with email: " + authentication.getName()));
    }
    public DtoProfile getPublicProfile(String email) {
        ProfileEntity currentUser = null;
        if (email == null || email.isEmpty()) {
            currentUser = getCurrentProfile();
        }else {
            currentUser = profileRepository.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("Profile not found with email: " + email));
        }
        return DtoProfile.builder()
                .id(currentUser.getId())
                .fullName(currentUser.getFullName())
                .email(currentUser.getEmail())
                .profileImageUrl(currentUser.getProfileImageUrl())
                .cratedAt(currentUser.getCratedAt())
                .updatedAt(currentUser.getUpdatedAt())
                .build();
    }

    public Map<String, Object> authenticateAndGenerateToken(DtoAuth dtoAuth) {
        try {
         authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(dtoAuth.getEmail(), dtoAuth.getPassword()));
         // Generate JWT Token
            String jwtToken = jwtUtil.generateToken(dtoAuth.getEmail());
            return Map.of(
                    "token", jwtToken,
                    "user", getPublicProfile(dtoAuth.getEmail())
            );
        }catch (Exception e) {
            throw new UsernameNotFoundException("Invalid email or password.");
        }
    }
}

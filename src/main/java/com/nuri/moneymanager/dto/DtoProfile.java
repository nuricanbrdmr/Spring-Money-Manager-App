package com.nuri.moneymanager.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DtoProfile {
    private Long id;
    private String fullName;
    private String email;
    private String password;
    private String profileImageUrl;
    private LocalDateTime cratedAt;
    private LocalDateTime updatedAt;
}

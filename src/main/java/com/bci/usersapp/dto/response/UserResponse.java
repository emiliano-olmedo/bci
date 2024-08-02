package com.bci.usersapp.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private UUID userId;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime created;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime modified;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime lastLogin;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String token;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private Boolean isActive;
}

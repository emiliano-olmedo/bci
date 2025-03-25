package com.bci.usersapp.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
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

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private List<PhoneResponse> phones;

}

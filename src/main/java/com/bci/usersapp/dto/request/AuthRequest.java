package com.bci.usersapp.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import javax.validation.constraints.NotNull;
import io.swagger.v3.oas.annotations.media.Schema;


@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class AuthRequest {

    @NotNull(message = "'email' is required")
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "julio@testssw.cl", description = "user's email")
    private String email;

    @NotNull(message = "'password' is required")
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "a2asfGfdfdf4", description = "user's password")
    private String password;
}

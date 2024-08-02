package com.bci.usersapp.dto.request;

import com.bci.usersapp.dto.response.PhoneResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class UserRequest {

    @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "Julio Gonzalez", description = "name")
    private String name;

    @NotNull(message = "'email' is required")
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "julio@testssw.cl", description = "email")
    private String email;

    @NotNull(message = "'password' is required")
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "a2asfGfdfdf4", description = "password")
    private String password;

    @Valid
    @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED, description = "phones")
    List<PhoneResponse> phones;
}

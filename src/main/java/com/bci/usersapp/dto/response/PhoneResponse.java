package com.bci.usersapp.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class PhoneResponse {

    @NotNull(message = "'number' is required")
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "87650009", description = "number")
    private Long number;

    @NotNull(message = "'cityCode' is required")
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "7", description = "city code")
    private Integer cityCode;

    @NotNull(message = "'countryCode' is required")
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "25", description = "country code")
    private String countryCode;

}

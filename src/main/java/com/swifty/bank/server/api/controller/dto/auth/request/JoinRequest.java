package com.swifty.bank.server.api.controller.dto.auth.request;

import com.swifty.bank.server.core.domain.customer.constant.Gender;
import com.swifty.bank.server.core.domain.customer.constant.Nationality;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.UUID;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;

@Data
@Schema(description = "Information for join action")
public class JoinRequest {
    @Schema(description = "Do not send this field in json", example = "null (even 'uuid': '' x)",
            accessMode = Schema.AccessMode.READ_ONLY)
    private UUID uuid;

    @Schema(description = "Plain String for name", requiredMode = RequiredMode.REQUIRED)
    @NotNull
    @NotBlank
    private String name;

    @Schema(description = "'KOREA' only", requiredMode = RequiredMode.REQUIRED, implementation = Nationality.class)
    private Nationality nationality;

    @NotNull
    @Size(max = 14, min = 3)
    @Schema(description = "start with +82 and only digits 0-9 without dash", example = "+8201012345678",
            requiredMode = RequiredMode.REQUIRED)
    private String phoneNumber;

    @NotBlank
    @NotNull
    @Size(max = 6, min = 6)
    @Schema(description = "only digits 0-9 without dash, 6 size", example = "123456",
            requiredMode = RequiredMode.REQUIRED)
    private String password;

    @NotBlank
    @NotNull
    @Schema(description = "plain string for device id",
            example = "{decided by frontend}", requiredMode = RequiredMode.REQUIRED)
    private String deviceId;

    @NotNull
    @Pattern(regexp = "MALE|FEMALE|NONE", message = "[ERROR] gender is neither male nor female")
    @Schema(description = "value is only available for MALE, FEMALE, NONE", implementation = Gender.class,
            example = "MALE", requiredMode = RequiredMode.REQUIRED)
    private Gender gender;

    @NotNull
    @Pattern(regexp = "^\\d+$\n")
    @Size(max = 8, min = 8)
    @Schema(description = "8 size digits", example = "20000101", requiredMode = RequiredMode.REQUIRED)
    private String birthDate;
    private GrantedAuthority roles;
}
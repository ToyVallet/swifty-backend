package com.swifty.bank.server.api.controller.dto.customer.request;

import com.swifty.bank.server.core.domain.customer.constant.Gender;
import com.swifty.bank.server.core.domain.customer.constant.Nationality;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
@Schema(description = "dto for updating customer's whole information")
public class CustomerInfoUpdateConditionRequest {

    @NotNull
    @NotBlank
    @Schema(description = "Plain String for name")
    private String name;

    @NotNull
    @Size(max = 14, min = 3)
    @Schema(description = "start with +82 and only digits 0-9 without dash", example = "+8201012345678",
            required = true)
    private String phoneNumber;

    @NotNull
    @Pattern(regexp = "MALE|FEMALE|NONE", message = "[ERROR] gender is neither male nor female")
    @Schema(description = "value is only available for MALE, FEMALE, NONe", implementation = Gender.class)
    private Gender gender;

    @NotNull
    @Pattern(regexp = "^\\d+$\n")
    @Size(max = 8, min = 8)
    @Schema(description = "8 size digits", example = "20000101")
    private String birthDate;

    @NotNull
    @Pattern(regexp = "KOREA", message = "[ERROR] Customer whose nationality is not supported")
    @Schema(description = "'KOREA' only", implementation = Nationality.class)
    private Nationality nationality;

    public CustomerInfoUpdateConditionRequest(String name, String phoneNumber, String birthDate,
                                              Nationality nationality) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.birthDate = birthDate;
        this.nationality = nationality;
    }
}

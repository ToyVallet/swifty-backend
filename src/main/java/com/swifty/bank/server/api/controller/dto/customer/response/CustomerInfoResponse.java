package com.swifty.bank.server.api.controller.dto.customer.response;

import com.swifty.bank.server.core.domain.customer.constant.CustomerStatus;
import com.swifty.bank.server.core.domain.customer.constant.Gender;
import com.swifty.bank.server.core.domain.customer.constant.Nationality;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CustomerInfoResponse {
    @NotNull
    @NotBlank
    @Size(min = 1, max = 30)
    @Schema(description = "고객 이름",
            example = "김성명",
            requiredMode = RequiredMode.REQUIRED)
    private String name;

    @NotNull
    @NotBlank
    @Size(min = 3, max = 18)
    @Pattern(regexp = "^\\+[0-9]{1,17}")
    @Schema(description = "국제번호 형식으로 입력해주세요.",
            example = "+821012345678",
            requiredMode = RequiredMode.REQUIRED)
    private String phoneNumber;

    @NotNull
    @Pattern(regexp = "MALE|FEMALE|NONE", message = "성별 형식이 맞지 않습니다.")
    @Schema(example = "MALE", implementation = Gender.class,
            requiredMode = Schema.RequiredMode.REQUIRED)
    private Gender gender;

    @NotBlank
    @Pattern(regexp = "^\\d+$\n")
    @Size(max = 8, min = 8)
    @Schema(example = "20000101",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String birthDate;

    @NotNull
    @Pattern(regexp = "KOREA", message = "국가 형식이 맞지 않습니다.")
    @Schema(description = "현재는 KOREA만 지원합니다. ",
            implementation = Nationality.class,
            requiredMode = Schema.RequiredMode.REQUIRED)
    private Nationality nationality;

    @NotNull
    @Pattern(regexp = "ACTIVE|SUSPENDED|WITHDRAWL", message = "지원하지 않는 회원상태 입니다.")
    @Schema(description = "회원상태는 ACTIVE,SUSPENDED,WITHDRAWL 지원",
            implementation = CustomerStatus.class,
            requiredMode = Schema.RequiredMode.REQUIRED)
    private CustomerStatus customerStatus;

    public CustomerInfoResponse(String name, String phoneNumber, Gender gender, String birthDate,
                                Nationality nationality, CustomerStatus customerStatus) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.gender = gender;
        this.birthDate = birthDate;
        this.nationality = nationality;
        this.customerStatus = customerStatus;
    }
}

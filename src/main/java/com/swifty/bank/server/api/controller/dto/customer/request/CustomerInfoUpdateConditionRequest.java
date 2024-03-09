package com.swifty.bank.server.api.controller.dto.customer.request;

import com.swifty.bank.server.core.domain.customer.constant.Gender;
import com.swifty.bank.server.core.domain.customer.constant.Nationality;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

@Getter
@Schema(description = "회원정보를 수정 할 데이터")
public class CustomerInfoUpdateConditionRequest {

    @Schema(example = "홍길동",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String name;

    @NotNull
    @NotBlank
    @Size(min = 3, max = 18)
    @Pattern(regexp = "^\\+[0-9]{1,17}")
    @Schema(description = "국제번호 형식으로 입력해주세요.",
            example = "+821012345678",
            requiredMode = RequiredMode.REQUIRED)
    private String phoneNumber;


    @Pattern(regexp = "MALE|FEMALE|NONE", message = "성별 형식이 맞지 않습니다.")
    @Schema(example = "MALE", implementation = Gender.class,
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Gender gender;


    @Pattern(regexp = "^\\d+$\n")
    @Size(max = 8, min = 8)
    @Schema(example = "20000101",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String birthDate;


    @Pattern(regexp = "KOREA", message = "국가 형식이 맞지 않습니다.")
    @Schema(description = "현재는 KOREA만 지원합니다. ",
            implementation = Nationality.class,
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Nationality nationality;

    @Builder
    public CustomerInfoUpdateConditionRequest(String name, String phoneNumber, Gender gender, String birthDate,
                                              Nationality nationality) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.gender = gender;
        this.birthDate = birthDate;
        this.nationality = nationality;
    }
}

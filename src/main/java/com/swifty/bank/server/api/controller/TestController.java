package com.swifty.bank.server.api.controller;

import com.swifty.bank.server.api.controller.annotation.PassAuth;
import com.swifty.bank.server.api.controller.dto.test.TestRequest;
import com.swifty.bank.server.api.controller.dto.test.TestResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/test")
@Slf4j
@Tag(name = "swagger 문서화 테스트")
public class TestController {
    @PassAuth
    @Operation(summary = "swagger 문서화 테스트", description = "swagger 문서화 테스트를 위한 엔드포인트 입니다")
    @PostMapping(value = "/swagger-schema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공했습니다",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = TestResponse.class)
                    )
                    }),
            @ApiResponse(responseCode = "400", description = "악 실패했습니다",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = TestResponse.class)
                    )
                    }),
    })
    public ResponseEntity<TestResponse> testSchema(
            @RequestBody @Valid TestRequest testRequest) {
        TestResponse testResponse = new TestResponse(
                testRequest.getAge(),
                testRequest.getName(),
                testRequest.getPhoneNumber()
        );

        return ResponseEntity
                .ok()
                .body(testResponse);
    }

    @PassAuth
    @Operation(summary = "Example", description = "swagger 문서화 테스트를 위한 엔드포인트 입니다")
    @PostMapping(value = "/swagger-example-object")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content = @Content(examples = {
                    @ExampleObject(name = "example object",
                            summary = "example object test",
                            description = "내가 정의한 response json.",
                            value = "[{\"result\": \"SUCCESS\", "
                                    + "\"message\":\"적절한 값\", "
                                    + "\"data\":\"933933933\"}")
            }, mediaType = MediaType.APPLICATION_JSON_VALUE))})
    public ResponseEntity<TestResponse> testExampleObject(
            @RequestBody @Valid TestRequest testRequest) {
        TestResponse testResponse = new TestResponse(
                testRequest.getAge(),
                testRequest.getName(),
                testRequest.getPhoneNumber()
        );

        return ResponseEntity
                .ok()
                .body(testResponse);
    }
}

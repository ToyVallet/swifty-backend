package com.swifty.bank.server.api.controller;


import com.swifty.bank.server.api.controller.dto.MessageResponse;
import com.swifty.bank.server.api.controller.dto.card.request.CreateCardRequest;
import com.swifty.bank.server.api.service.CardApiService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping(value = "/card")
@Tag(name = "카드 API")
@Slf4j
public class CardController {
    private final CardApiService cardApiService;

    @PostMapping("")
    public ResponseEntity<?> createCard(
            @CookieValue("access-token") String accessToken,
            @RequestBody @Valid CreateCardRequest createCardRequest, @CookieValue("keypad-token") String keypadToken) {

        cardApiService.createCard(accessToken,keypadToken,createCardRequest);

        return ResponseEntity
                .ok()
                .body(new MessageResponse("정상적으로 카드를 생성하였습니다."));
    }
}

package com.swifty.bank.server.api.controller;

import com.swifty.bank.server.api.controller.annotation.PassAuth;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping(value = "/test")
@Slf4j
public class TestController {
    @PassAuth
    @GetMapping(value = "/no-such-element")
    public ResponseEntity<?> noSuchElement() {
        throw new NoSuchElementException("오 노");
    }
}
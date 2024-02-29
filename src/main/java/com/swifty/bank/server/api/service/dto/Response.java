package com.swifty.bank.server.api.service.dto;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

public enum Response {
    SUCCESS(HttpStatus.OK,  "success"),
    FAIL(HttpStatus.OK,  "fail"),
    NOT_FOUND(HttpStatus.NOT_FOUND,"not found")
    ;


    private ResponseEntity responseEntity;

    Response(HttpStatus httpStatus, String message) {
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("message",message);

        this.responseEntity = new ResponseEntity<>(headers, httpStatus);
    }

    public ResponseEntity<?> get() {
        return this.responseEntity;
    }

    public ResponseEntity<?> setMessage(String message) {
        MultiValueMap<String, String> newHeaders = new LinkedMultiValueMap<>();
        newHeaders.add("message",message);

        this.responseEntity = new ResponseEntity<>(newHeaders,this.responseEntity.getStatusCode());

        return this.responseEntity;
    }

    public ResponseEntity<?> setData(Object data) {

        this.responseEntity = new ResponseEntity<>(data,this.responseEntity.getHeaders(),this.responseEntity.getStatusCode());

        return this.responseEntity;
    }


}

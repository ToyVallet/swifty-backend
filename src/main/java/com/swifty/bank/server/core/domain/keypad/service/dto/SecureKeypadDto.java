package com.swifty.bank.server.core.domain.keypad.service.dto;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SecureKeypadDto {
    private List<Integer> key;
    private List<String> shuffledKeypadImages;
}

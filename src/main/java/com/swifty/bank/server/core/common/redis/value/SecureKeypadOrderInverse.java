package com.swifty.bank.server.core.common.redis.value;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class SecureKeypadOrderInverse {
    private List<Integer> keypadOrderInverse;
}

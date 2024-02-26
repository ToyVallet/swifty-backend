package com.swifty.bank.server.core.common.authentication.repository;

import com.swifty.bank.server.core.common.authentication.RefreshToken;
import com.swifty.bank.server.core.common.authentication.repository.auth.AuthJpqlRepository;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthRepository extends JpaRepository<RefreshToken, UUID>, AuthJpqlRepository {
    @Override
    <A extends RefreshToken> A save(A auth);
}

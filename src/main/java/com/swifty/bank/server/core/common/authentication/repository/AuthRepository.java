package com.swifty.bank.server.core.common.authentication.repository;

import com.swifty.bank.server.core.common.authentication.Auth;
import com.swifty.bank.server.core.common.authentication.repository.auth.AuthJpqlRepository;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthRepository extends JpaRepository<Auth, UUID>, AuthJpqlRepository {
    @Override
    <A extends Auth> A save(A auth);
}

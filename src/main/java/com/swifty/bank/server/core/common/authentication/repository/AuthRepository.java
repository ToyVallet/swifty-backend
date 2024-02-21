package com.swifty.bank.server.core.common.authentication.repository;

import com.swifty.bank.server.core.common.authentication.Auth;
import com.swifty.bank.server.core.common.authentication.repository.custom.AuthJpqlRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AuthRepository extends JpaRepository<Auth, UUID>, AuthJpqlRepository {
    @Override
    <A extends Auth> A save(A auth);
}

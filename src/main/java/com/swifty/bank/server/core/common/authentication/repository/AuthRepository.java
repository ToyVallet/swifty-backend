package com.swifty.bank.server.core.common.authentication.repository;

import com.swifty.bank.server.core.common.authentication.RefreshTokenDb;
import com.swifty.bank.server.core.common.authentication.repository.auth.AuthJpqlRepository;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthRepository extends JpaRepository<RefreshTokenDb, UUID>, AuthJpqlRepository {
    @Override
    <A extends RefreshTokenDb> A save(A auth);
}

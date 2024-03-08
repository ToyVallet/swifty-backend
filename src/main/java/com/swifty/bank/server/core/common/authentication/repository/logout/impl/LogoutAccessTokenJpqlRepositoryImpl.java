package com.swifty.bank.server.core.common.authentication.repository.logout.impl;

import com.swifty.bank.server.core.common.authentication.LogoutAccessToken;
import com.swifty.bank.server.core.common.authentication.repository.logout.LogoutAccessTokenJpqlRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.NonUniqueResultException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LogoutAccessTokenJpqlRepositoryImpl implements LogoutAccessTokenJpqlRepository {
    private final EntityManager em;
    private final boolean isDeleted = false;

    @Override
    public Optional<LogoutAccessToken> findSingleLogoutAccessTokenWithAccessToken(String accessToken) {
        try {
            return Optional.of(em.createQuery("SELECT A FROM LogoutAccessToken A WHERE A.accessToken = :accessToken AND A.isDeleted = :isDeleted"
                            , LogoutAccessToken.class)
                    .setParameter("accessToken", accessToken)
                    .setParameter("isDeleted", isDeleted)
                    .getSingleResult());
        } catch (NonUniqueResultException e) {
            throw e;
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }
}

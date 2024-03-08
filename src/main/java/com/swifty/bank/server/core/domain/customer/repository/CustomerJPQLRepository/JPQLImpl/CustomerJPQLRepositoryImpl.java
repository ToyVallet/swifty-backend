package com.swifty.bank.server.core.domain.customer.repository.CustomerJPQLRepository.JPQLImpl;

import com.swifty.bank.server.api.controller.dto.customer.response.CustomerInfoResponse;
import com.swifty.bank.server.core.domain.customer.Customer;
import com.swifty.bank.server.core.domain.customer.repository.CustomerJPQLRepository.CustomerJPQLRepository;
import jakarta.persistence.EntityManager;
import java.util.Optional;
import java.util.UUID;

import jakarta.persistence.NoResultException;
import jakarta.persistence.NonUniqueResultException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class CustomerJPQLRepositoryImpl implements CustomerJPQLRepository {
    private final EntityManager em;
    private final boolean isDeleted = false;

    @Override
    public Optional<Customer> findOneByUUID(UUID uuid) {
        try {
            return Optional.of(em.createQuery(
                            "SELECT C FROM Customer C WHERE C.id = :uuid AND C.isDeleted = :isDeleted", Customer.class
                    )
                    .setParameter("uuid", uuid)
                    .setParameter("isDeleted", isDeleted)
                    .getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Customer> findOneByDeviceId(String deviceId) {
        try {
            return Optional.of(em.createQuery(
                            "SELECT C FROM Customer C WHERE C.deviceId = :deviceId AND C.isDeleted = :isDeleted",
                            Customer.class
                    )
                    .setParameter("deviceId", deviceId)
                    .setParameter("isDeleted", isDeleted)
                    .getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Customer> findOneByPhoneNumber(String phoneNumber) {
        try {
            return Optional.of(em.createQuery(
                            "SELECT C FROM Customer C WHERE  C.phoneNumber = :phoneNumber AND C.isDeleted = :isDeleted",
                            Customer.class
                    )
                    .setParameter("phoneNumber", phoneNumber)
                    .setParameter("isDeleted", isDeleted)
                    .getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }

    }

    @Override
    public Optional<CustomerInfoResponse> findCustomerInfoResponseByUUID(UUID uuid) {
        try {
            return Optional.of(em.createQuery(
                            "SELECT new com.swifty.bank.server.api.controller.dto.customer.response.CustomerInfoResponse(" +
                                    "C.name" +
                                    ",C.phoneNumber" +
                                    ",C.gender" +
                                    ",C.birthDate" +
                                    ",C.nationality" +
                                    ",C.customerStatus" +
                                    ") " +
                                    "FROM Customer C " +
                                    "WHERE  C.id = :uuid " +
                                    "AND C.isDeleted = :isDeleted",
                            CustomerInfoResponse.class
                    )
                    .setParameter("uuid", uuid)
                    .setParameter("isDeleted", isDeleted)
                    .getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public void deleteCustomer(Customer customer) {
        em.remove(customer);
    }

}

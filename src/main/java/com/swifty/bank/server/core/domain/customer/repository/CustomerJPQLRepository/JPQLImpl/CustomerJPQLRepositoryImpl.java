package com.swifty.bank.server.core.domain.customer.repository.CustomerJPQLRepository.JPQLImpl;

import com.swifty.bank.server.api.controller.dto.customer.response.CustomerInfoResponse;
import com.swifty.bank.server.core.domain.customer.Customer;
import com.swifty.bank.server.core.domain.customer.repository.CustomerJPQLRepository.CustomerJPQLRepository;
import jakarta.persistence.EntityManager;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class CustomerJPQLRepositoryImpl implements CustomerJPQLRepository {
    private final EntityManager em;
    private final boolean isDeleted = false;

    @Override
    public Optional<Customer> findOneByUUID(UUID uuid) {

        return em.createQuery(
                        "SELECT C FROM Customer C WHERE C.id = :uuid AND C.isDeleted = :isDeleted", Customer.class
                )
                .setParameter("uuid", uuid)
                .setParameter("isDeleted", isDeleted)
                .getResultList()
                .stream()
                .findAny();
    }

    @Override
    public Optional<Customer> findOneByDeviceId(String deviceId) {
        return em.createQuery(
                        "SELECT C FROM Customer C WHERE C.deviceId = :deviceId AND C.isDeleted = :isDeleted",
                        Customer.class
                )
                .setParameter("deviceId", deviceId)
                .setParameter("isDeleted", isDeleted)
                .getResultList()
                .stream()
                .findAny();
    }

    @Override
    public Optional<Customer> findOneByPhoneNumber(String phoneNumber) {
        return em.createQuery(
                        "SELECT C FROM Customer C WHERE  C.phoneNumber = :phoneNumber AND C.isDeleted = :isDeleted",
                        Customer.class
                )
                .setParameter("phoneNumber", phoneNumber)
                .setParameter("isDeleted", isDeleted)
                .getResultList()
                .stream()
                .findAny();
    }

    @Override
    public Optional<CustomerInfoResponse> findCustomerInfoResponseByUUID(UUID uuid) {
        return em.createQuery(
                        "SELECT new com.swifty.bank.server.core.domain.customer.dto.CustomerInfoResponse(" +
                                "c.name" +
                                ",c.phoneNumber" +
                                ",c.birthDate" +
                                ",c.nationality" +
                                ",c.customerStatus" +
                                ") " +
                                "FROM Customer C " +
                                "WHERE  C.uuid = :uuid " +
                                "AND C.isDeleted = :isDeleted",
                        CustomerInfoResponse.class
                )
                .setParameter("uuid", uuid)
                .setParameter("isDeleted", isDeleted)
                .getResultList()
                .stream()
                .findAny();
    }

    @Override
    public void deleteCustomer(Customer customer) {
        em.remove(customer);
    }

}

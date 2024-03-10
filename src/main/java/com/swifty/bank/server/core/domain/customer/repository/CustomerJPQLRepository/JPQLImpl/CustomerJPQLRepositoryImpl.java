package com.swifty.bank.server.core.domain.customer.repository.CustomerJPQLRepository.JPQLImpl;

import com.swifty.bank.server.core.domain.customer.Customer;
import com.swifty.bank.server.core.domain.customer.dto.CustomerInfoDto;
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
    public Optional<Customer> findOneByUuid(UUID uuid) {

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
    public Optional<CustomerInfoDto> findCustomerInfoResponseByUuid(UUID uuid) {
        return em.createQuery(
                        "SELECT new com.swifty.bank.server.core.domain.customer.dto.CustomerInfoDto(" +
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
                        CustomerInfoDto.class
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
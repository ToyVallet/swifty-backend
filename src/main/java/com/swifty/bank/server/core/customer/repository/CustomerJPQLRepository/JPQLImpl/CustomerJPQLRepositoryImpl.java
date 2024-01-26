package com.swifty.bank.server.core.customer.repository.CustomerJPQLRepository.JPQLImpl;

import com.swifty.bank.server.core.customer.Customer;
import com.swifty.bank.server.core.customer.repository.CustomerJPQLRepository.CustomerJPQLRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

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
    public void deleteCustomer(Customer customer) {
        em.remove(customer);
    }

}

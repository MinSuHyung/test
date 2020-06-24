package com.rest.api.repo;


import com.rest.api.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface PaymentJpaRepo extends JpaRepository<Payment, Long> {
    Payment findByUid(String uid);
    List<Payment> findByTransDataContainingAndCreatedAtGreaterThan(String cardInfo, LocalDateTime createdAt);

}


package com.rest.api.repo;


import com.rest.api.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentJpaRepo extends JpaRepository<Payment, Long> {
    Payment findByUid(String uid);
}


package com.rest.api.repo;


import com.rest.api.entity.Cancellation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.awt.print.Pageable;
import java.util.List;

public interface CancellationJpaRepo extends JpaRepository<Cancellation, Long> {
    Cancellation findByUid(String uid);
    List findAllByPaymentUid(String paymentUid);
}


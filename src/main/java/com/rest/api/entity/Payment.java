package com.rest.api.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Builder
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EnableJpaAuditing
@Table(name = "payment")
public class Payment {
    @Id
    @Column(nullable = false, unique = true, length = 30)
    private String uid;
    @Column(nullable = false, unique = true, length = 200)
    private String cardInfo; // 카드번호|유효기간|cvc 암호화
    @Column(nullable = false)
    private Integer installment;
    @Column(nullable = false)
    private Integer paymentAmount;
    @Column(nullable = false)
    private Integer vatAmount;
    @Column(nullable = false, length = 450)
    private String transData;
    @CreatedDate
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "paymentUid")
    private List<Cancellation> cancellations;

}


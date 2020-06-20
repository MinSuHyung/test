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

@Builder
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EnableJpaAuditing
@Table(name = "cancellation")
public class Cancellation {
    @Id
    @Column(nullable = false, unique = true, length = 30)
    private String uid;

    @Column(nullable = false, length = 30)
    private String paymentUid;

    @Column(nullable = false)
    private Integer cancellationAmount;

    @Column(nullable = false)
    private Integer vatAmount;

    @Column(nullable = false, length = 450)
    private String transData;

    @CreatedDate
    private LocalDateTime createdAt;

}
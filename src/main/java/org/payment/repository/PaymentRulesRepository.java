package org.payment.repository;

import org.payment.entity.PaymentRules;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRulesRepository extends JpaRepository<PaymentRules, String> {
}

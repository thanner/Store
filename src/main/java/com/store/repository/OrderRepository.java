package com.store.repository;

import com.store.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {

    boolean existsByOrderIdAndCustomer_CustomerId(Integer orderId, Integer customerId);

    Optional<Order> findByOrderIdAndCustomer_CustomerId(Integer orderId, Integer customerId);

}
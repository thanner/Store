package com.store.repository;

import com.store.domain.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Integer> {

    boolean existsByOrderItemIdAndOrder_OrderIdAndOrder_Customer_CustomerId(Integer orderItemId, Integer orderId, Integer customerId);

    Optional<OrderItem> findByOrderItemIdAndOrder_OrderIdAndOrder_Customer_CustomerId(Integer orderItemId, Integer orderId, Integer customerId);

}
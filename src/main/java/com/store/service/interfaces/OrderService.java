package com.store.service.interfaces;

import com.store.domain.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface OrderService {

    void save(Integer customerId, Order order);

    Optional<Order> findOrder(Integer customerId, Integer orderId);

    Page<Order> findOrderByExample(Integer customerId, Order order, Pageable pageable);

    Optional<Order> updateById(Integer customerId, Integer orderId, Order order);

    void deleteById(Integer customerId, Integer orderId);

}

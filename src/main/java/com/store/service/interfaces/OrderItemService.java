package com.store.service.interfaces;

import com.store.domain.OrderItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface OrderItemService {

    void save(Integer customerId, Integer orderId, OrderItem orderItem);

    Optional<OrderItem> findOrderItem(Integer customerId, Integer orderId, Integer orderItemId);

    Page<OrderItem> findOrderItemByExample(Integer customerId, Integer orderId, OrderItem orderItem, Pageable pageable);

    Optional<OrderItem> updateById(Integer customerId, Integer orderId, Integer orderItemId, OrderItem orderItem);

    void deleteById(Integer customerId, Integer orderId, Integer orderItemId);

}

package com.store.service.implementation;

import com.store.domain.Customer;
import com.store.domain.Order;
import com.store.domain.OrderItem;
import com.store.domain.Product;
import com.store.repository.OrderItemRepository;
import com.store.service.interfaces.OrderItemService;
import com.store.service.interfaces.OrderService;
import com.store.service.interfaces.ProductService;
import com.store.util.ExceptionMessage;
import com.store.util.NonNullPropertiesCopier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;

@Service("orderItemService")
@Transactional
public class OrderItemServiceImpl implements OrderItemService {

    private final OrderItemRepository orderItemRepository;
    private final OrderService orderService;
    private final ProductService productService;
    private final NonNullPropertiesCopier copier;

    @Autowired
    public OrderItemServiceImpl(OrderItemRepository orderItemRepository, OrderService orderService, ProductService productService, NonNullPropertiesCopier copier) {
        this.orderItemRepository = orderItemRepository;
        this.orderService = orderService;
        this.productService = productService;
        this.copier = copier;
    }

    @Override
    public OrderItem save(Integer customerId, Integer orderId, OrderItem orderItem) {
        Optional<Order> optionalOrder = orderService.findOrder(customerId, orderId);
        Optional<Product> optionalProduct = orderItem.getProduct() != null ? productService.findProduct(orderItem.getProduct().getProductId()) : Optional.empty();
        if (optionalOrder.isEmpty()) {
            throw new EntityNotFoundException(ExceptionMessage.OrderNotFound);
        } else if (optionalProduct.isEmpty()) {
            throw new EntityNotFoundException(ExceptionMessage.ProductNotFound);
        } else {
            orderItem.setOrder(optionalOrder.get());
            orderItem.setProduct(optionalProduct.get());
            return orderItemRepository.save(orderItem);
        }
    }

    @Override
    public Optional<OrderItem> findOrderItem(Integer customerId, Integer orderId, Integer orderItemId) {
        return orderItemRepository.findByOrderItemIdAndOrder_OrderIdAndOrder_Customer_CustomerId(orderItemId, orderId, customerId);
    }

    @Override
    public Page<OrderItem> findOrderItemByExample(Integer customerId, Integer orderId, OrderItem orderItem, Pageable pageable) {
        orderItem.setOrder(Order.builder().orderId(orderId).customer(Customer.builder().customerId(customerId).build()).build());
        return orderItemRepository.findAll(Example.of(orderItem), pageable);
    }

    @Override
    public Optional<OrderItem> updateById(Integer customerId, Integer orderId, Integer orderItemId, OrderItem orderItem) {
        Optional<OrderItem> persisted = orderItemRepository.findByOrderItemIdAndOrder_OrderIdAndOrder_Customer_CustomerId(orderItemId, orderId, customerId);
        if (persisted.isPresent()) {
            copier.copyNonNullProperties(orderItem, persisted.get());
            return persisted;
        } else {
            throw new EntityNotFoundException(ExceptionMessage.OrdeItemNotFound);
        }
    }

    @Override
    public void deleteById(Integer customerId, Integer orderId, Integer orderItemId) {
        if (orderItemRepository.existsByOrderItemIdAndOrder_OrderIdAndOrder_Customer_CustomerId(orderItemId, orderId, customerId)) {
            orderItemRepository.deleteById(orderItemId);
        } else {
            throw new EntityNotFoundException(ExceptionMessage.OrdeItemNotFound);
        }
    }

}
package com.store.service;

import com.store.domain.Customer;
import com.store.domain.Order;
import com.store.exception.ExceptionMessage;
import com.store.exception.ResourceNotFoundException;
import com.store.repository.OrderRepository;
import com.store.util.NonNullPropertiesCopier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final CustomerService customerService;
    private final NonNullPropertiesCopier copier;

    @Autowired
    public OrderService(OrderRepository orderRepository, CustomerService customerService, NonNullPropertiesCopier copier) {
        this.orderRepository = orderRepository;
        this.customerService = customerService;
        this.copier = copier;
    }

    public Order save(Integer customerId, Order order) {
        Customer optionalCustomer = customerService.findCustomer(customerId);
        order.setCustomer(optionalCustomer);
        return orderRepository.save(order);
    }

    public Order findOrder(Integer customerId, Integer orderId) {
        return findOrderById(customerId, orderId);
    }

    public Page<Order> findOrderByExample(Integer customerId, Order order, Pageable pageable) {
        order.setCustomer(Customer.builder().id(customerId).build());
        return orderRepository.findAll(Example.of(order), pageable);
    }

    public Order updateById(Integer customerId, Integer orderId, Order order) {
        Order persisted = findOrderById(customerId, orderId);
        copier.copyNonNullProperties(order, persisted);
        return persisted;
    }

    public void deleteById(Integer customerId, Integer orderId) {
        if (orderRepository.existsByIdAndCustomer_Id(orderId, customerId)) {
            orderRepository.deleteById(orderId);
        } else {
            throw new ResourceNotFoundException(ExceptionMessage.OrderNotFound);
        }
    }

    private Order findOrderById(Integer customerId, Integer orderId) throws ResourceNotFoundException {
        return orderRepository.findByIdAndCustomer_Id(orderId, customerId).orElseThrow(() -> new ResourceNotFoundException(ExceptionMessage.OrderNotFound));
    }

}

package com.store.service.implementation;

import com.store.domain.Customer;
import com.store.domain.Order;
import com.store.repository.OrderRepository;
import com.store.service.interfaces.CustomerService;
import com.store.service.interfaces.OrderService;
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

@Service("orderService")
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final CustomerService customerService;
    private final NonNullPropertiesCopier copier;

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository, CustomerService customerService, NonNullPropertiesCopier copier) {
        this.orderRepository = orderRepository;
        this.customerService = customerService;
        this.copier = copier;
    }

    @Override
    public Order save(Integer customerId, Order order) {
        Optional<Customer> optionalCustomer = customerService.findCustomer(customerId);
        if (optionalCustomer.isPresent()) {
            order.setCustomer(optionalCustomer.get());
            return orderRepository.save(order);
        } else {
            throw new EntityNotFoundException(ExceptionMessage.CustomerNotFound);
        }
    }

    @Override
    public Optional<Order> findOrder(Integer customerId, Integer orderId) {
        return orderRepository.findByOrderIdAndCustomer_CustomerId(orderId, customerId);
    }

    @Override
    public Page<Order> findOrderByExample(Integer customerId, Order order, Pageable pageable) {
        order.setCustomer(Customer.builder().customerId(customerId).build());
        return orderRepository.findAll(Example.of(order), pageable);
    }

    @Override
    public Optional<Order> updateById(Integer customerId, Integer orderId, Order order) {
        Optional<Order> persisted = orderRepository.findByOrderIdAndCustomer_CustomerId(orderId, customerId);
        if (persisted.isPresent()) {
            copier.copyNonNullProperties(order, persisted.get());
            return persisted;
        } else {
            throw new EntityNotFoundException(ExceptionMessage.OrderNotFound);
        }
    }

    @Override
    public void deleteById(Integer customerId, Integer orderId) {
        if (orderRepository.existsByOrderIdAndCustomer_CustomerId(orderId, customerId)) {
            orderRepository.deleteById(orderId);
        } else {
            throw new EntityNotFoundException(ExceptionMessage.OrderNotFound);
        }
    }

}
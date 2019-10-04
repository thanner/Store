package com.store.controller;

import com.store.api.OrderApi;
import com.store.domain.Customer;
import com.store.domain.Order;
import com.store.resource.OrderResource;
import com.store.service.interfaces.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedResources;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Optional;

@RestController
public class OrderController implements OrderApi {

    private OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @Override
    public ResponseEntity<OrderResource> addOrder(Integer customerId, Order order) {
        orderService.save(customerId, order);
        return new ResponseEntity<>(new OrderResource(order), HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<OrderResource> getOrderById(Integer customerId, Integer orderId) {
        Optional<Order> orderOptional = orderService.findOrder(customerId, orderId);
        return getResponseEntity(orderOptional, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<PagedResources<Order>> getOrder(Integer customerId, Integer orderId, Customer customer, BigDecimal value, Pageable pageable, PagedResourcesAssembler assembler) {
        Order orderExample = Order.builder().orderId(orderId).customer(customer).value(value).build();
        Page<Order> orderPage = orderService.findOrderByExample(customerId, orderExample, pageable);
        return new ResponseEntity<PagedResources<Order>>(assembler.toResource(orderPage), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<OrderResource> updateOrderById(Integer customerId, Integer orderId, Order order) {
        Optional<Order> orderOptional = orderService.updateById(customerId, orderId, order);
        return getResponseEntity(orderOptional, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> deleteOrderById(Integer customerId, Integer orderId) {
        orderService.deleteById(customerId, orderId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private ResponseEntity<OrderResource> getResponseEntity(Optional<Order> orderOptional, HttpStatus httpStatus) {
        return orderOptional.map(order -> new ResponseEntity<>(new OrderResource(order), httpStatus)).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

}

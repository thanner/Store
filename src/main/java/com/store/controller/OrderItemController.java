package com.store.controller;

import com.store.api.OrderItemApi;
import com.store.domain.OrderItem;
import com.store.resource.OrderItemResource;
import com.store.service.interfaces.OrderItemService;
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
public class OrderItemController implements OrderItemApi {

    private OrderItemService orderItemService;

    @Autowired
    public OrderItemController(OrderItemService orderItemService) {
        this.orderItemService = orderItemService;
    }

    @Override
    public ResponseEntity<OrderItemResource> addOrderItem(Integer customerId, Integer orderId, OrderItem orderItem) {
        orderItemService.save(customerId, orderId, orderItem);
        return new ResponseEntity<>(new OrderItemResource(orderItem), HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<OrderItemResource> getOrderItemById(Integer customerId, Integer orderId, Integer orderItemId) {
        Optional<OrderItem> orderItemOptional = orderItemService.findOrderItem(customerId, orderId, orderItemId);
        return getResponseEntity(orderItemOptional, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<PagedResources<OrderItem>> getOrderItem(Integer customerId, Integer orderId, Integer orderItemId, BigDecimal amount, BigDecimal price, Pageable pageable, PagedResourcesAssembler assembler) {
        OrderItem orderItemExample = OrderItem.builder().orderItemId(orderItemId).amount(amount).price(price).build();
        Page<OrderItem> orderItemPage = orderItemService.findOrderItemByExample(customerId, orderId, orderItemExample, pageable);
        return new ResponseEntity<PagedResources<OrderItem>>(assembler.toResource(orderItemPage), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<OrderItemResource> updateOrderItemById(Integer customerId, Integer orderId, Integer orderItemId, OrderItem orderItem) {
        Optional<OrderItem> orderItemOptional = orderItemService.updateById(customerId, orderId, orderItemId, orderItem);
        return getResponseEntity(orderItemOptional, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> deleteOrderItemById(Integer customerId, Integer orderId, Integer orderItemId) {
        orderItemService.deleteById(customerId, orderId, orderItemId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private ResponseEntity<OrderItemResource> getResponseEntity(Optional<OrderItem> orderItemOptional, HttpStatus httpStatus) {
        return orderItemOptional.map(orderItem -> new ResponseEntity<>(new OrderItemResource(orderItem), httpStatus)).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

}

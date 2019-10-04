package com.store.resource;

import com.store.controller.OrderItemController;
import com.store.domain.OrderItem;
import lombok.Getter;
import org.springframework.hateoas.ResourceSupport;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Getter
public class OrderItemResource extends ResourceSupport {

    private final OrderItem orderItem;

    public OrderItemResource(OrderItem orderItem) {
        this.orderItem = orderItem;
        final Integer customerId = orderItem.getOrder().getCustomer().getCustomerId();
        final Integer orderId = orderItem.getOrder().getOrderId();
        final Integer orderItemId = orderItem.getOrderItemId();

        //add(linkTo(OrderItemController.class).withRel("orderItems"));
        add(linkTo(methodOn(OrderItemController.class).getOrderItemById(customerId, orderId, orderItemId)).withSelfRel());
    }

}
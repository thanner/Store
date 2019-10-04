package com.store.api;

import com.store.domain.Customer;
import com.store.domain.Order;
import com.store.resource.OrderResource;
import io.swagger.annotations.*;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.ExposesResourceFor;
import org.springframework.hateoas.PagedResources;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;

@Api(value = "order")
@RequestMapping(value = "/customers/{customer-id}/orders")
@ExposesResourceFor(Order.class)
public interface OrderApi {

    @ApiOperation(value = "Add a new order", nickname = "addOrder", authorizations = {@Authorization(value = "bearerAuth")}, tags = {"order",})
    @ApiResponses(value = {
            @ApiResponse(code = 401, message = "Access token is missing or invalid"),
            @ApiResponse(code = 405, message = "Invalid input")
    })
    @PostMapping
    ResponseEntity<OrderResource> addOrder(@ApiParam(value = "Customer id of the order", required = true) @Valid @PathVariable(value = "customer-id") Integer customerId,
                                           @ApiParam(value = "Order that will be added", required = true) @Valid Order order);

    @ApiOperation(value = "Get order by id", nickname = "getOrderById", response = Order.class,
            notes = "Retrieves a order by id",
            authorizations = {@Authorization(value = "bearerAuth")}, tags = {"order",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "successful operation", response = Order.class),
            @ApiResponse(code = 400, message = "Invalid status value")
    })
    @GetMapping(value = "/{order-id}")
    ResponseEntity<OrderResource> getOrderById(@ApiParam(value = "Customer id") @Valid @PathVariable(value = "customer-id") Integer customerId,
                                               @ApiParam(value = "Order id") @Valid @PathVariable(value = "order-id") Integer orderId);

    @ApiOperation(value = "Get orders", nickname = "getOrder", response = Order.class, responseContainer = "List",
            notes = "Retrieves a collection of users by taking into account order features. Can be done pagination (quantity of items returned) and ordering",
            authorizations = {@Authorization(value = "bearerAuth")}, tags = {"order",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "successful operation", response = Order.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "Invalid status value")
    })
    @GetMapping
    ResponseEntity<PagedResources<Order>> getOrder(@ApiParam(value = "Customer id") @Valid @PathVariable(value = "customer-id") Integer customerId,
                                                   @ApiParam(value = "Order id") @Valid @RequestParam(value = "id", required = false) Integer orderId,
                                                   @ApiParam(value = "Customer") @Valid @RequestParam(value = "customer", required = false) Customer customer,
                                                   @ApiParam(value = "Order value") @Valid @RequestParam(value = "value", required = false) BigDecimal value,
                                                   @PageableDefault Pageable pageable,
                                                   PagedResourcesAssembler assembler);


    @ApiOperation(value = "Update an existing order", nickname = "updateCustomerById", authorizations = {@Authorization(value = "bearerAuth")}, tags = {"order",})
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Invalid order id supplied"),
            @ApiResponse(code = 401, message = "Access token is missing or invalid"),
            @ApiResponse(code = 404, message = "Order not found"),
            @ApiResponse(code = 405, message = "Validation exception")
    })
    @PutMapping(value = "/{order-id}")
    ResponseEntity<OrderResource> updateOrderById(@ApiParam(value = "Customer id of the order", required = true) @Valid @PathVariable(value = "customer-id") Integer customerId,
                                                  @ApiParam(value = "Order id that will be updated", required = true) @Valid @PathVariable(value = "order-id") Integer id,
                                                  @ApiParam(value = "Data that will be updated in order", required = true) @Valid Order order);


    @ApiOperation(value = "Delete order", nickname = "deleteOrderById", authorizations = {@Authorization(value = "bearerAuth")}, tags = {"order",})
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Invalid order id supplied"),
            @ApiResponse(code = 401, message = "Access token is missing or invalid"),
            @ApiResponse(code = 404, message = "Order not found")
    })
    @DeleteMapping(value = "/{order-id}")
    ResponseEntity<Void> deleteOrderById(@ApiParam(value = "Customer id of the order", required = true) @Valid @PathVariable(value = "customer-id") Integer customerId,
                                         @ApiParam(value = "Order id that will be deleted", required = true) @Valid @PathVariable(value = "order-id") Integer id);

}
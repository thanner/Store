package com.store.service;

import com.store.AbstractTest;
import com.store.domain.Order;
import com.store.exception.ResourceNotFoundException;
import com.store.repository.OrderRepository;
import com.store.util.NonNullPropertiesCopier;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@WebMvcTest(value = OrderService.class, secure = false)
public class OrderServiceTest extends AbstractTest {

    @Autowired
    private OrderService orderService;

    @MockBean
    private OrderRepository orderRepository;
    @MockBean
    private CustomerService customerService;
    @MockBean
    private NonNullPropertiesCopier copier;

    private Order order;

    private Integer wrongOrderId;

    @Before
    public void setup() {
        super.setup();
        order = setupOrder();
        wrongOrderId = orderId + 1;

        given(orderRepository.findByIdAndCustomer_Id(orderId, customerId)).willReturn(Optional.ofNullable(order));
        given(orderRepository.existsByIdAndCustomer_Id(orderId, customerId)).willReturn(true);
    }

    @Test
    public void whenSaveOrderReturnActiveOrder_thenAssertionSucceeds() {
        given(orderRepository.save(order)).willReturn(order);

        Order persisted = orderService.save(customerId, order);
        verifyOrder(persisted);
    }

    @Test
    public void whenFindOrder_thenAssertionSucceeds() {
        Order persisted = orderService.findOrder(customerId, orderId);
        verifyOrder(persisted);
    }

    @Test
    public void whenFindOrderByExample_thenAssertionSucceeds() {
        List<Order> orderList = new ArrayList<>();
        orderList.add(order);
        Page<Order> pageOrder = new PageImpl<>(orderList);
        given(orderRepository.findAll(Example.of(order), Pageable.unpaged())).willReturn(pageOrder);

        Page<Order> persisted = orderService.findOrderByExample(customerId, order, Pageable.unpaged());
        verifyOrder(persisted.getContent().get(0));
    }

    @Test
    public void whenUpdateOrder_thenAssertionSucceeds() {
        Order persisted = orderService.updateById(customerId, orderId, order);
        verifyOrder(persisted);
    }

    @Test
    public void whenDeleteOrder_thenAssertionSucceeds() {
        orderService.deleteById(customerId, orderId);
    }

    @Test(expected = ResourceNotFoundException.class)
    public void whenFindOrderNotFound_thenResourceNotFoundException() {
        orderService.findOrder(customerId, wrongOrderId);
    }

    @Test(expected = ResourceNotFoundException.class)
    public void whenUpdateOrderNotFound_thenResourceNotFoundException() {
        orderService.updateById(customerId, wrongOrderId, order);
    }

    @Test(expected = ResourceNotFoundException.class)
    public void whenDeleteOrderNotFound_thenResourceNotFoundException() {
        orderService.deleteById(customerId, wrongOrderId);
    }

    private void verifyOrder(Order orderToVerify) {
        assertThat(orderToVerify)
                .hasFieldOrPropertyWithValue("orderId", order.getId())
                .hasFieldOrPropertyWithValue("value", order.getValue())
        ;
    }

}

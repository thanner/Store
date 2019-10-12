package com.store.service;

import com.store.AbstractTest;
import com.store.domain.OrderItem;
import com.store.exception.ResourceNotFoundException;
import com.store.repository.OrderItemRepository;
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

@WebMvcTest(value = OrderItemService.class, secure = false)
public class OrderItemServiceTest extends AbstractTest {

    @Autowired
    private OrderItemService orderItemService;

    @MockBean
    private OrderItemRepository orderItemRepository;
    @MockBean
    private OrderService orderService;
    @MockBean
    private ProductService productService;
    @MockBean
    private NonNullPropertiesCopier copier;

    private OrderItem orderItem;

    private Integer wrongOrderItemId;

    @Before
    public void setup() {
        super.setup();
        orderItem = setupOrderItem();
        wrongOrderItemId = orderItemId + 1;

        given(orderItemRepository.findByIdAndOrder_IdAndOrder_Customer_Id(orderItemId, orderId, customerId)).willReturn(Optional.ofNullable(orderItem));
        given(orderItemRepository.existsByIdAndOrder_IdAndOrder_Customer_Id(orderItemId, orderId, customerId)).willReturn(true);
    }

    @Test
    public void whenSaveOrderReturnActiveOrder_thenAssertionSucceeds() {
        given(orderItemRepository.save(orderItem)).willReturn(orderItem);

        OrderItem persisted = orderItemService.save(customerId, orderId, orderItem);
        verifyOrderItem(persisted);
    }

    @Test
    public void whenFindOrder_thenAssertionSucceeds() {
        OrderItem persisted = orderItemService.findOrderItem(customerId, orderId, orderItemId);
        verifyOrderItem(persisted);
    }

    @Test
    public void whenFindOrderByExample_thenAssertionSucceeds() {
        List<OrderItem> orderItemList = new ArrayList<>();
        orderItemList.add(orderItem);
        Page<OrderItem> pageOrder = new PageImpl<>(orderItemList);
        given(orderItemRepository.findAll(Example.of(orderItem), Pageable.unpaged())).willReturn(pageOrder);

        Page<OrderItem> persisted = orderItemService.findOrderItemByExample(customerId, orderId, orderItem, Pageable.unpaged());
        verifyOrderItem(persisted.getContent().get(0));
    }

    @Test
    public void whenUpdateOrder_thenAssertionSucceeds() {
        OrderItem persisted = orderItemService.updateById(customerId, orderId, orderItemId, orderItem);
        verifyOrderItem(persisted);
    }

    @Test
    public void whenDeleteOrder_thenAssertionSucceeds() {
        orderItemService.deleteById(customerId, orderId, orderItemId);
    }

    @Test(expected = ResourceNotFoundException.class)
    public void whenFindOrderNotFound_thenResourceNotFoundException() {
        orderItemService.findOrderItem(customerId, orderId, wrongOrderItemId);
    }

    @Test(expected = ResourceNotFoundException.class)
    public void whenUpdateOrderNotFound_thenResourceNotFoundException() {
        orderItemService.updateById(customerId, orderId, wrongOrderItemId, orderItem);
    }

    @Test(expected = ResourceNotFoundException.class)
    public void whenDeleteOrderNotFound_thenResourceNotFoundException() {
        orderItemService.deleteById(customerId, orderId, wrongOrderItemId);
    }

    private void verifyOrderItem(OrderItem orderToVerify) {
        assertThat(orderToVerify)
                .hasFieldOrPropertyWithValue("orderItemId", orderItem.getId())
                .hasFieldOrPropertyWithValue("amount", orderItem.getAmount())
                .hasFieldOrPropertyWithValue("price", orderItem.getPrice())
        ;
    }

}
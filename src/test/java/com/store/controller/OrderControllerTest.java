package com.store.controller;

import com.store.AbstractTest;
import com.store.domain.Order;
import com.store.service.OrderService;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(value = OrderController.class, secure = false)
public class OrderControllerTest extends AbstractTest {

    @MockBean
    private OrderService orderService;

    private Order order;

    @Before
    public void setup() {
        super.setup();
        order = setupOrder();
    }

    @Test
    public void getOrderByIdShouldReturnOrder() throws Exception {
        given(orderService.findOrder(eq(customerId), eq(orderId))).willReturn(order);
        final ResultActions result = mockMvc.perform(get(orderPath + "/" + orderId));
        result.andExpect(status().isOk());
        verifyJsonOrderById(result);
    }

    @Test
    public void getOrdersShouldReturnOrderPaged() throws Exception {
        List<Order> orderList = new ArrayList<>();
        orderList.add(order);
        Page<Order> pageOrder = new PageImpl<>(orderList);
        given(orderService.findOrderByExample(eq(customerId), any(Order.class), any(Pageable.class))).willReturn(pageOrder);
        final ResultActions result = mockMvc.perform(get(orderPath + "?id=" + orderId));
        result.andExpect(status().isOk());
        verifyJsonOrderPaged(result);
    }

    @Test
    public void postReturnsCorrectResponse() throws Exception {
        given(orderService.save(any(Integer.class), any(Order.class))).willReturn(order);
        final ResultActions result =
                mockMvc.perform(post(orderPath)
                        .content(mapper.writeValueAsBytes(order))
                        .contentType(MediaType.APPLICATION_JSON_UTF8));
        result.andExpect(status().isCreated());
        verifyJsonOrderById(result);
    }

    @Test
    public void putReturnsCorrectResponse() throws Exception {
        given(orderService.updateById(eq(customerId), eq(orderId), any(Order.class))).willReturn(order);
        final ResultActions result = mockMvc.perform(put(orderPath + "/" + orderId)
                .content(mapper.writeValueAsBytes(order))
                .contentType(MediaType.APPLICATION_JSON_UTF8));
        result.andExpect(status().isOk());
        verifyJsonOrderById(result);
    }

    @Test
    public void deleteReturnsCorrectResponse() throws Exception {
        mockMvc.perform(delete(orderPath + "/" + orderId))
                .andExpect(status().isNoContent())
                .andExpect(content().string(StringUtils.EMPTY));
    }

    private void verifyJsonOrderById(final ResultActions result) throws Exception {
        verifyJsonOrder(result, "order");
        result
                .andExpect(jsonPath("_links.customerOrderItems.href", is(orderPath + "/" + orderId + "/order-items")))
                .andExpect(jsonPath("_links.self.href", is(orderPath + "/" + orderId)));
    }

    private void verifyJsonOrderPaged(final ResultActions result) throws Exception {
        verifyJsonOrder(result, "_embedded.orderList[0]");
        result.andExpect(jsonPath("_links.self.href", is(orderPath + "?id=" + orderId)));
    }

    private void verifyJsonOrder(final ResultActions result, String orderPath) throws Exception {
        result
                .andExpect(jsonPath(orderPath + ".id", is(order.getId())))
                .andExpect(jsonPath(orderPath + ".value", is(order.getValue().doubleValue())));
    }
}
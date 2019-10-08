package com.store;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.store.controller.OrderItemController;
import com.store.domain.Customer;
import com.store.domain.Order;
import com.store.domain.OrderItem;
import com.store.domain.Product;
import com.store.service.interfaces.OrderItemService;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(value = OrderItemController.class, secure = false)
public class OrderItemControllerTest extends AbstractTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private OrderItemService orderItemService;

    private OrderItem orderItem;

    @Before
    public void setup() {
        setupOrder();
    }

    private void setupOrder() {
        orderItem = OrderItem.builder().orderItemId(orderItemId).amount(new BigDecimal(1.30)).price(new BigDecimal(1.20)).order(Order.builder().orderId(orderId).customer(Customer.builder().customerId(customerId).build()).build()).product(Product.builder().build()).build();
    }

    @Test
    public void getOrderByIdShouldReturnOrder() throws Exception {
        given(orderItemService.findOrderItem(eq(customerId), eq(orderId), eq(orderItemId))).willReturn(Optional.of(orderItem));
        final ResultActions result = mockMvc.perform(get(orderItemPath + "/" + orderItemId));
        result.andExpect(status().isOk());
        verifyJsonOrderById(result);
    }

    @Test
    public void getOrdersShouldReturnOrderPaged() throws Exception {
        List<OrderItem> orderItemList = new ArrayList<>();
        orderItemList.add(orderItem);
        Page<OrderItem> pageOrderItem = new PageImpl<>(orderItemList);
        given(orderItemService.findOrderItemByExample(eq(customerId), eq(orderId), any(OrderItem.class), any(Pageable.class))).willReturn(pageOrderItem);
        final ResultActions result = mockMvc.perform(get(orderItemPath + "?id=" + orderItemId));
        result.andExpect(status().isOk());
        verifyJsonOrderPaged(result);
    }

    @Test
    public void postReturnsCorrectResponse() throws Exception {
        given(orderItemService.save(any(Integer.class), any(Integer.class), any(OrderItem.class))).willReturn(orderItem);
        final ResultActions result =
                mockMvc.perform(post(orderItemPath)
                        .content(mapper.writeValueAsBytes(orderItem))
                        .contentType(MediaType.APPLICATION_JSON_UTF8));
        result.andExpect(status().isCreated());
        verifyJsonOrderById(result);
    }

    @Test
    public void putReturnsCorrectResponse() throws Exception {
        given(orderItemService.updateById(eq(customerId), eq(orderId), eq(orderItemId), any(OrderItem.class))).willReturn(Optional.of(orderItem));
        final ResultActions result = mockMvc.perform(put(orderItemPath + "/" + orderItemId)
                .content(mapper.writeValueAsBytes(orderItem))
                .contentType(MediaType.APPLICATION_JSON_UTF8));
        result.andExpect(status().isOk());
        verifyJsonOrderById(result);
    }

    @Test
    public void deleteReturnsCorrectResponse() throws Exception {
        mockMvc.perform(delete(orderItemPath + "/" + orderItemId))
                .andExpect(status().isNoContent())
                .andExpect(content().string(StringUtils.EMPTY));
    }

    private void verifyJsonOrderById(final ResultActions result) throws Exception {
        verifyJsonOrder(result, "orderItem");
        result.andExpect(jsonPath("_links.self.href", is(orderItemPath + "/" + orderItemId)));
    }

    private void verifyJsonOrderPaged(final ResultActions result) throws Exception {
        verifyJsonOrder(result, "_embedded.orderItemList[0]");
        result.andExpect(jsonPath("_links.self.href", is(orderItemPath + "?id=" + orderItemId)));
    }

    private void verifyJsonOrder(final ResultActions result, String orderItemPath) throws Exception {
        result
                .andExpect(jsonPath(orderItemPath + ".id", is(orderItem.getOrderItemId())))
                .andExpect(jsonPath(orderItemPath + ".price", is(orderItem.getPrice())))
                .andExpect(jsonPath(orderItemPath + ".amount", is(orderItem.getAmount())));
    }
}

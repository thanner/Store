package com.store.integration;

import com.store.AbstractTest;
import com.store.domain.Order;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.ResultActions;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.hamcrest.core.Is.is;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import static org.springframework.test.annotation.DirtiesContext.ClassMode;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
public class OrderIntegrationTest extends AbstractTest {

    private Order order;
    private Order orderWithoutId;

    @Before
    public void setup() {
        super.setup();
        order = setupOrder();
        order.setValue(BigDecimal.ZERO);

        orderWithoutId = order.toBuilder().id(null).build();

        try {
            postResource(customerPath, setupCustomer().toBuilder().id(null).build());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void givenNewOrder_whenPostOrder_thenCreated() throws Exception {
        final ResultActions result = postResource(orderPath, orderWithoutId);
        result.andExpect(status().isCreated());
        verifyJsonOrderById(result);
    }

    @Test
    public void givenNewOrder_whenPostOrderWithtNegativeValue_thenBadRequest() throws Exception {
        final ResultActions result = postResource(customerPath, order.toBuilder().value(BigDecimal.valueOf(-1.00)).build());
        result.andExpect(status().isBadRequest());
    }

    @Test
    public void givenOrderSaved_whenGetOrder_thenAssertionSucceeds() throws Exception {
        postResource(orderPath, orderWithoutId);

        final ResultActions result = getResource(orderPath + "/" + orderId);
        result.andExpect(status().isOk());
        verifyJsonOrderById(result);
    }

    @Test
    public void givenOrderSaved_whenGetOrderPaged_thenAssertionSucceeds() throws Exception {
        postResource(orderPath, orderWithoutId);

        final ResultActions result = getResource(orderPath + "?id=" + orderId);
        result.andExpect(status().isOk());
        verifyJsonOrderPaged(result);
    }

    @Test
    public void givenOrderSaved_whenDeleteOrder_thenNoContent() throws Exception {
        postResource(orderPath, orderWithoutId);

        deleteResource(orderPath, orderId).andExpect(status().isNoContent()).andExpect(content().string(StringUtils.EMPTY));
    }

    private void verifyJsonOrderById(final ResultActions result) throws Exception {
        verifyJsonOrder(result, "order");
        result
                .andExpect(jsonPath("_links.customerOrderItems.href", is(orderPath + "/" + orderId + "/order-items")))
                .andExpect(jsonPath("_links.self.href", is(orderPath + "/" + orderId)));
    }

    private void verifyJsonOrderPaged(final ResultActions result) throws Exception {
        verifyJsonOrder(result, "_embedded.orderList[0]");
        result.andExpect(jsonPath("_links.self.href", is(orderPath + "?id=" + orderId + "&page=0&size=10")));
    }

    private void verifyJsonOrder(final ResultActions result, String orderPath) throws Exception {
        result
                .andExpect(jsonPath(orderPath + ".id", is(order.getId())))
                .andExpect(jsonPath(orderPath + ".value", is(order.getValue().setScale(2, RoundingMode.HALF_UP).toString())))
        ;
    }
}

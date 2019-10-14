package com.store.integration;

import com.store.AbstractTest;
import com.store.domain.Order;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.ResultActions;

import static org.hamcrest.core.Is.is;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import static org.springframework.test.annotation.DirtiesContext.ClassMode;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class OrderIntegrationTest extends AbstractTest {

    private Order order;

    @Before
    public void setup() {
        super.setup();
        order = setupOrder();

        try {
            postResource(customerPath, setupCustomer());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void stage1_whenPostOrder_thenAssertionSucceeds() throws Exception {
        final ResultActions result = postResource(orderPath, order);
        result.andExpect(status().isCreated());
        verifyJsonOrderById(result);
    }

    @Test
    public void stage2_whenGetOrder_thenAssertionSucceeds() throws Exception {
        postResource(orderPath, order);

        final ResultActions result = mockMvc.perform(get(orderPath + "/" + orderId));
        result.andExpect(status().isOk());
        verifyJsonOrderById(result);
    }

    @Test
    public void stage3_whenGetOrderPaged_thenAssertionSucceeds() throws Exception {
        postResource(orderPath, order);

        final ResultActions result = mockMvc.perform(get(orderPath + "?id=" + orderId));
        result.andExpect(status().isOk());
        verifyJsonOrderPaged(result);
    }

    @Test
    public void stage4_whenPutOrder_thenAssertionSucceeds() throws Exception {
        postResource(orderPath, order);

        final ResultActions result = mockMvc.perform(put(orderPath + "/" + orderId)
                .content(mapper.writeValueAsBytes(order))
                .contentType(MediaType.APPLICATION_JSON_UTF8));
        result.andExpect(status().isOk());
        verifyJsonOrderById(result);
    }

    @Test
    public void stage5_whenDeleteOrder_thenAssertionSucceeds() throws Exception {
        postResource(orderPath, order);

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
                .andExpect(jsonPath(orderPath + ".value", is(order.getValue().doubleValue())));
    }
}

package com.store.integration;

import com.store.AbstractTest;
import com.store.domain.OrderItem;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.web.servlet.ResultActions;

import static org.hamcrest.core.Is.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class OrderItemIntegrationTest extends AbstractTest {

    private OrderItem orderItem;

    @Before
    public void setup() {
        super.setup();
        orderItem = setupOrderItem();

        try {
            postResource(customerPath, setupCustomer());
            postResource(orderPath, setupOrder());
            postResource(productPath, setupProduct());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void stage1_whenPostOrderItem_thenAssertionSucceeds() throws Exception {
        final ResultActions result = postResource(orderItemPath, orderItem);
        result.andExpect(status().isCreated());
        verifyJsonOrderById(result);
    }

    @Test
    public void stage2_whenGetOrderItem_thenAssertionSucceeds() throws Exception {
        postResource(orderItemPath, orderItem);

        final ResultActions result = mockMvc.perform(get(orderItemPath + "/" + orderItemId));
        result.andExpect(status().isOk());
        verifyJsonOrderById(result);
    }

    @Test
    public void stage3_whenGetOrderItemPaged_thenAssertionSucceeds() throws Exception {
        postResource(orderItemPath, orderItem);

        final ResultActions result = mockMvc.perform(get(orderItemPath + "?id=" + orderItemId));
        result.andExpect(status().isOk());
        verifyJsonOrderPaged(result);
    }

    @Test
    public void stage4_whenPutOrderItem_thenAssertionSucceeds() throws Exception {
        postResource(orderItemPath, orderItem);

        final ResultActions result = mockMvc.perform(put(orderItemPath + "/" + orderItemId)
                .content(mapper.writeValueAsBytes(orderItem))
                .contentType(MediaType.APPLICATION_JSON_UTF8));
        result.andExpect(status().isOk());
        verifyJsonOrderById(result);
    }

    @Test
    public void stage5_whenDeleteOrderItem_thenAssertionSucceeds() throws Exception {
        postResource(orderItemPath, orderItem);

        deleteResource(orderItemPath, orderItemId).andExpect(status().isNoContent()).andExpect(content().string(StringUtils.EMPTY));
    }

    private void verifyJsonOrderById(final ResultActions result) throws Exception {
        verifyJsonOrder(result, "orderItem");
        result.andExpect(jsonPath("_links.self.href", is(orderItemPath + "/" + orderItemId)));
    }

    private void verifyJsonOrderPaged(final ResultActions result) throws Exception {
        verifyJsonOrder(result, "_embedded.orderItemList[0]");
        result.andExpect(jsonPath("_links.self.href", is(orderItemPath + "?id=" + orderItemId + "&page=0&size=10")));
    }

    private void verifyJsonOrder(final ResultActions result, String orderItemPath) throws Exception {
        result
                .andExpect(jsonPath(orderItemPath + ".id", is(orderItem.getId())))
                .andExpect(jsonPath(orderItemPath + ".price", is(orderItem.getPrice().doubleValue())))
                .andExpect(jsonPath(orderItemPath + ".amount", is(orderItem.getAmount().doubleValue())));
    }
}

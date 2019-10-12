package com.store.integration;

import com.store.AbstractTest;
import com.store.domain.Customer;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.ResultActions;

import static org.hamcrest.core.Is.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CustomerIntegrationTest extends AbstractTest {

    private Customer customer;

    @Before
    public void setup() {
        super.setup();
        customer = setupCustomer();
    }

    @Test
    public void stage1_whenPostCustomer_thenAssertionSucceeds() throws Exception {
        final ResultActions result = postResource(customerPath, customer);
        result.andExpect(status().isCreated());
        verifyJsonCustomerById(result);
    }

    @Test
    public void stage2_whenGetCustomer_thenAssertionSucceeds() throws Exception {
        final ResultActions result = mockMvc.perform(get(customerPath + "/" + customerId));
        result.andExpect(status().isOk());
        verifyJsonCustomerById(result);
    }

    @Test
    public void stage3_whenGetCustomerPaged_thenAssertionSucceeds() throws Exception {
        final ResultActions result = mockMvc.perform(get(customerPath + "?id=" + customer.getId() + "&name=" + customer.getName()));
        result.andExpect(status().isOk());
        verifyJsonCustomerPaged(result);
    }

    @Test
    public void stage4_whenPutCustomer_thenAssertionSucceeds() throws Exception {
        customer.setName("newName");
        final ResultActions result = mockMvc.perform(put(customerPath + "/" + customerId)
                .content(mapper.writeValueAsBytes(customer))
                .contentType(MediaType.APPLICATION_JSON_UTF8));
        result.andExpect(status().isOk());
        verifyJsonCustomerById(result);
    }

    @Test
    public void stage5_whenDeleteCustomer_thenAssertionSucceeds() throws Exception {
        mockMvc.perform(delete(customerPath + "/" + customerId))
                .andExpect(status().isNoContent())
                .andExpect(content().string(StringUtils.EMPTY));
    }

    private void verifyJsonCustomerById(final ResultActions result) throws Exception {
        verifyJsonCustomer(result, "customer");
        result
                .andExpect(jsonPath("_links.customers.href", is(customerPath)))
                .andExpect(jsonPath("_links.customerOrders.href", is(customerPath + "/" + customerId + "/orders")))
                .andExpect(jsonPath("_links.self.href", is(customerPath + "/" + customerId)));
    }

    private void verifyJsonCustomerPaged(final ResultActions result) throws Exception {
        verifyJsonCustomer(result, "_embedded.customerList[0]");
        result.andExpect(jsonPath("_links.self.href", is(customerPath + "?page=0&size=10")));
    }

    private void verifyJsonCustomer(final ResultActions result, String customerPath) throws Exception {
        result
                .andExpect(jsonPath(customerPath + ".id", is(customer.getId())))
                .andExpect(jsonPath(customerPath + ".name", is(customer.getName())))
                .andExpect(jsonPath(customerPath + ".cpf", is(customer.getCpf())))
                .andExpect(jsonPath(customerPath + ".birthDate", is(customer.getBirthDate().format(formatter))))
                .andExpect(jsonPath(customerPath + ".accountStatus", is(customer.getAccountStatus().toString())));
    }

}

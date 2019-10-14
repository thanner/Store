package com.store.integration;

import com.store.AbstractTest;
import com.store.domain.Customer;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.ResultActions;

import static org.hamcrest.core.Is.is;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import static org.springframework.test.annotation.DirtiesContext.ClassMode;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
public class CustomerIntegrationTest extends AbstractTest {

    private Customer customer;
    private Customer customerWithoutId;

    @Before
    public void setup() {
        super.setup();
        customer = setupCustomer();
        customerWithoutId = customer.toBuilder().id(null).build();
    }


    //@WithMockUser(username = "thanner")
    //@WithMockUser(username = "usernameapi")
    @Test
    public void givenNewCustomer_whenPostCustomer_thenCreated() throws Exception {
        final ResultActions result = postResource(customerPath, customerWithoutId);
        result.andExpect(status().isCreated());
        verifyJsonCustomerById(result);
    }

    @Test
    public void givenNewCustomer_whenPostCustomerWithoutName_thenBadRequest() throws Exception {
        final ResultActions result = postResource(customerPath, customer.toBuilder().name(null).build());
        result.andExpect(status().isBadRequest());
    }

    @Test
    public void givenNewCustomer_whenPostCustomerWithoutCpf_thenBadRequest() throws Exception {
        final ResultActions result = postResource(customerPath, customer.toBuilder().cpf(null).build());
        result.andExpect(status().isBadRequest());
    }

    @Test
    public void givenCustomerSaved_whenGetCustomer_thenAssertionSucceeds() throws Exception {
        postResource(customerPath, customerWithoutId);

        final ResultActions result = getResource(customerPath + "/" + customerId);
        result.andExpect(status().isOk());
        verifyJsonCustomerById(result);
    }

    @Test
    public void givenCustomerSaved_whenGetCustomerPaged_thenAssertionSucceeds() throws Exception {
        postResource(customerPath, customerWithoutId);

        final ResultActions result = getResource(customerPath + "?id=" + customer.getId() + "&name=" + customer.getName());
        result.andExpect(status().isOk());
        verifyJsonCustomerPaged(result);
    }

    @Test
    public void givenCustomerSaved_whenPutCustomer_thenOk() throws Exception {
        postResource(customerPath, customerWithoutId);

        customer.setName("new name");
        final ResultActions result = putResource(customerPath + "/" + customerId, customer);
        result.andExpect(status().isOk());
        verifyJsonCustomerById(result);
    }

    @Test
    public void givenCustomerSaved_whenDeleteCustomer_thenNoContent() throws Exception {
        postResource(customerPath, customerWithoutId);

        deleteResource(customerPath, customerId).andExpect(status().isNoContent()).andExpect(content().string(StringUtils.EMPTY));
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

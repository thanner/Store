package com.store.controller;

import com.store.AbstractTest;
import com.store.domain.Customer;
import com.store.service.CustomerService;
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

@WebMvcTest(value = CustomerController.class, secure = false)
public class CustomerControllerTest extends AbstractTest {

    @MockBean
    private CustomerService customerService;

    private Customer customer;

    @Before
    public void setup() {
        super.setup();
        customer = setupCustomer();
    }

    @Test
    public void getCustomerByIdShouldReturnCustomer() throws Exception {
        given(customerService.findCustomer(any(Integer.class))).willReturn(customer);
        final ResultActions result = mockMvc.perform(get(customerPath + "/" + customerId));
        result.andExpect(status().isOk());
        verifyJsonCustomerById(result);
    }

    @Test
    public void getCustomersShouldReturnCustomerPaged() throws Exception {
        List<Customer> customerList = new ArrayList<>();
        customerList.add(customer);
        Page<Customer> pageCustomer = new PageImpl<>(customerList);
        given(customerService.findCustomerByExample(any(Customer.class), any(Pageable.class))).willReturn(pageCustomer);

        final ResultActions result = mockMvc.perform(get(customerPath + "?id=" + customer.getId() + "&name=" + customer.getName()));
        result.andExpect(status().isOk());
        verifyJsonCustomerPaged(result);
    }

    @Test
    public void postReturnsCorrectResponse() throws Exception {
        given(customerService.createNewCustomer(any(Customer.class))).willReturn(customer);
        final ResultActions result =
                mockMvc.perform(post(customerPath)
                        .content(mapper.writeValueAsBytes(customer))
                        .contentType(MediaType.APPLICATION_JSON_UTF8));
        result.andExpect(status().isCreated());
        verifyJsonCustomerById(result);
    }

    @Test
    public void putReturnsCorrectResponse() throws Exception {
        given(customerService.updateById(eq(customerId), any(Customer.class))).willReturn(customer);
        final ResultActions result = mockMvc.perform(put(customerPath + "/" + customerId)
                .content(mapper.writeValueAsBytes(customer))
                .contentType(MediaType.APPLICATION_JSON_UTF8));
        result.andExpect(status().isOk());
        verifyJsonCustomerById(result);
    }

    @Test
    public void deleteReturnsCorrectResponse() throws Exception {
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
        result.andExpect(jsonPath("_links.self.href", is(customerPath)));
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

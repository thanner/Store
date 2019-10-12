package com.store.service;

import com.store.AbstractTest;
import com.store.domain.AccountStatus;
import com.store.domain.Customer;
import com.store.exception.ResourceNotFoundException;
import com.store.repository.CustomerRepository;
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

@WebMvcTest(value = CustomerService.class, secure = false)
public class CustomerServiceTest extends AbstractTest {

    @Autowired
    private CustomerService customerService;

    @MockBean
    private CustomerRepository customerRepository;

    @MockBean
    private NonNullPropertiesCopier copier;

    private Customer customer;

    private Integer wrongCustomerId;

    @Before
    public void setup() {
        super.setup();
        customer = setupCustomer();
        wrongCustomerId = customerId + 1;
        given(customerRepository.findById(customerId)).willReturn(Optional.ofNullable(customer));
    }

    @Test
    public void whenSaveCustomerReturnActiveCustomer_thenAssertionSucceeds() {
        Customer inactiveCustomer = setupCustomer();
        inactiveCustomer.setAccountStatus(AccountStatus.INACTIVE);
        given(customerRepository.save(inactiveCustomer)).willReturn(inactiveCustomer);

        Customer persisted = customerService.createNewCustomer(inactiveCustomer);
        verifyCustomer(persisted);
    }

    @Test
    public void whenFindCustomer_thenAssertionSucceeds() {
        Customer persisted = customerService.findCustomer(customerId);
        verifyCustomer(persisted);
    }

    @Test
    public void whenFindCustomerByExample_thenAssertionSucceeds() {
        List<Customer> customerList = new ArrayList<>();
        customerList.add(customer);
        Page<Customer> pageCustomer = new PageImpl<>(customerList);
        given(customerRepository.findAll(Example.of(customer), Pageable.unpaged())).willReturn(pageCustomer);

        Page<Customer> persisted = customerService.findCustomerByExample(customer, Pageable.unpaged());
        verifyCustomer(persisted.getContent().get(0));
    }

    @Test
    public void whenUpdateCustomer_thenAssertionSucceeds() {
        Customer persisted = customerService.updateById(customerId, customer);
        verifyCustomer(persisted);
    }

    @Test
    public void whenDeleteCustomer_thenAssertionSucceeds() {
        customerService.deleteById(customerId);
    }

    @Test(expected = ResourceNotFoundException.class)
    public void whenFindCustomerNotFound_thenResourceNotFoundException() {
        customerService.findCustomer(wrongCustomerId);
    }

    @Test(expected = ResourceNotFoundException.class)
    public void whenUpdateCustomerNotFound_thenResourceNotFoundException() {
        customerService.updateById(wrongCustomerId, customer);
    }

    @Test(expected = ResourceNotFoundException.class)
    public void whenDeleteCustomerNotFound_thenResourceNotFoundException() {
        customerService.deleteById(wrongCustomerId);
    }

    private void verifyCustomer(Customer customerToVerify) {
        assertThat(customerToVerify)
                .hasFieldOrPropertyWithValue("customerId", customer.getId())
                .hasFieldOrPropertyWithValue("name", customer.getName())
                .hasFieldOrPropertyWithValue("cpf", customer.getCpf())
                .hasFieldOrPropertyWithValue("birthDate", customer.getBirthDate())
                .hasFieldOrPropertyWithValue("accountStatus", customer.getAccountStatus())
        ;
    }

}

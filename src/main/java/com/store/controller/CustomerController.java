package com.store.controller;

import com.store.api.CustomerApi;
import com.store.domain.Customer;
import com.store.resource.CustomerResource;
import com.store.service.interfaces.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedResources;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.Optional;

@RestController
public class CustomerController implements CustomerApi {

    private final CustomerService customerService;

    @Autowired
    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @Override
    public ResponseEntity<CustomerResource> addCustomer(Customer customer) {
        Customer persistedCustomer = customerService.createNewCustomer(customer);
        return new ResponseEntity<>(new CustomerResource(persistedCustomer), HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<CustomerResource> getCustomerById(@PathVariable Integer customerId) {
        Optional<Customer> customerOptional = customerService.findCustomer(customerId);
        return getResponseEntity(customerOptional, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<PagedResources<CustomerResource>> getCustomer(Integer customerId, String name, String cpf, LocalDate birthDate, Pageable pageable, PagedResourcesAssembler assembler) {
        Customer customerExample = Customer.builder().customerId(customerId).name(name).cpf(cpf).birthDate(birthDate).build();
        Page<Customer> customerPage = customerService.findCustomerByExample(customerExample, pageable);
        return new ResponseEntity<PagedResources<CustomerResource>>(assembler.toResource(customerPage), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<CustomerResource> updateCustomerById(Integer customerId, Customer customer) {
        Optional<Customer> customerOptional = customerService.updateById(customerId, customer);
        return getResponseEntity(customerOptional, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> deleteCustomerById(Integer customerId) {
        customerService.deleteById(customerId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    private ResponseEntity<CustomerResource> getResponseEntity(Optional<Customer> customerOptional, HttpStatus httpStatus) {
        return customerOptional.map(customer -> new ResponseEntity<>(new CustomerResource(customer), httpStatus)).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

}

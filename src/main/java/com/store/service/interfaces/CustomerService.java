package com.store.service.interfaces;

import com.store.domain.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface CustomerService {

    Customer createNewCustomer(Customer customer);

    Optional<Customer> findCustomer(Integer customerId);

    Page<Customer> findCustomerByExample(Customer customer, Pageable pageable);

    Optional<Customer> updateById(Integer customerId, Customer customer);

    void deleteById(Integer customerId);

}

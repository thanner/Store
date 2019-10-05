package com.store.service.implementation;

import com.store.domain.AccountStatus;
import com.store.domain.Customer;
import com.store.repository.CustomerRepository;
import com.store.service.interfaces.CustomerService;
import com.store.util.NonNullPropertiesCopier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service("customerService")
@Transactional
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final NonNullPropertiesCopier copier;

    @Autowired
    public CustomerServiceImpl(CustomerRepository customerRepository, NonNullPropertiesCopier copier) {
        this.customerRepository = customerRepository;
        this.copier = copier;
    }

    @Override
    public Customer createNewCustomer(Customer customer) {
        customer.setAccountStatus(AccountStatus.ACTIVE);
        return customerRepository.save(customer);
    }

    @Override
    public Optional<Customer> findCustomer(Integer customerId) {
        return customerRepository.findById(customerId);
    }

    @Override
    public Page<Customer> findCustomerByExample(Customer customer, Pageable pageable) {
        return customerRepository.findAll(Example.of(customer), pageable);
    }

    @Override
    public Optional<Customer> updateById(Integer customerId, Customer customer) {
        Optional<Customer> persisted = customerRepository.findById(customerId);
        persisted.ifPresent(value -> copier.copyNonNullProperties(customer, value));
        return persisted;
    }

    @Override
    public void deleteById(Integer customerId) {
        Customer customer = customerRepository.getOne(customerId);
        customer.setAccountStatus(AccountStatus.INACTIVE);
    }

}
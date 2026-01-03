package com.pskwiercz.app.services.customer;

import com.pskwiercz.app.model.Customer;

import java.util.List;

public interface ICustomerService {
    Customer createCustomer(Customer customer);

    Customer getCustomerById(Long id);

    List<Customer> getAllCustomers();

    Customer updateCustomer(Long id, Customer updatedCustomer);

    void deleteCustomer(Long id);

    Customer getCustomerByEmail(String emailAddress);
}
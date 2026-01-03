package com.pskwiercz.app.repository;

import com.pskwiercz.app.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<Customer, Long> {

    Customer findByEmailAddressAndPhoneNumber(String emailAddress, String phoneNumber);

}

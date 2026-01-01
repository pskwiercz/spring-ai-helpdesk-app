package com.pskwiercz.app.repository;

import com.pskwiercz.app.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByEmailAddressAndPhoneNumber(String emailAddress, String phoneNumber);

}

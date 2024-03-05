package com.ebank.ebankbackend.repositories;

import com.ebank.ebankbackend.entities.BankAccount;
import com.ebank.ebankbackend.entities.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CustomerRepository extends JpaRepository<Customer,Long> {
    List<Customer> findByNameContains(String keyword);
}

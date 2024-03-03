package com.ebank.ebankbackend.repositories;

import com.ebank.ebankbackend.entities.BankAccount;
import com.ebank.ebankbackend.entities.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer,Long> {
}

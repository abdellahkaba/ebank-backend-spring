package com.ebank.ebankbackend.repositories;

import com.ebank.ebankbackend.entities.BankAccount;
import com.ebank.ebankbackend.entities.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CustomerRepository extends JpaRepository<Customer,Long> {
    @Query("select c from Customer c where c.name like :key")
    List<Customer> searchCustomers(@Param(value = "key") String keyword);
}

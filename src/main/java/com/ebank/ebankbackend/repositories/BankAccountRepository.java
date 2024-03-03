package com.ebank.ebankbackend.repositories;

import com.ebank.ebankbackend.entities.AccountOperation;
import com.ebank.ebankbackend.entities.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BankAccountRepository extends JpaRepository<BankAccount,String> {
}

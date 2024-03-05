package com.ebank.ebankbackend.services;

import com.ebank.ebankbackend.dtos.*;
import com.ebank.ebankbackend.entities.BankAccount;
import com.ebank.ebankbackend.entities.CurrentAccount;
import com.ebank.ebankbackend.entities.Customer;
import com.ebank.ebankbackend.entities.SavingAccount;
import com.ebank.ebankbackend.exceptions.BalanceNotSufficientException;
import com.ebank.ebankbackend.exceptions.BankAccountNotFoundException;
import com.ebank.ebankbackend.exceptions.CustomerNotFoundException;

import java.util.List;

public interface BankAccountService {
    //Ajouter un client
    CustomerDTO saveCustomer (CustomerDTO customerDTO);
    //Ajouter un compte
    CurrentBankAccountDTO saveCurrentBankAccount (double initialBalance, double overDraft, Long customerId) throws CustomerNotFoundException;
    SavingBankAccountDTO saveSavingBankAccount (double initialBalance, double interestRate, Long customerId) throws CustomerNotFoundException;

    //Une methode qui consule la liste des clients
    List<CustomerDTO> listCustomers () ;
    //Une methode qui consulte un compte
    BankAccountDTO getBankAccount (String accountId) throws BankAccountNotFoundException;
    //Pour debiter un compte
    void debit (String accountId, double amount, String description) throws BankAccountNotFoundException, BalanceNotSufficientException;
    //Pour crediter un compte
    void credit (String accountId, double amount, String description) throws BankAccountNotFoundException, BalanceNotSufficientException;
    //Transferer un compte
    void transfert (String accountIdSource, String accountIdDescription, double amount) throws BankAccountNotFoundException, BalanceNotSufficientException;

    List<BankAccountDTO> bankAccountList();

    CustomerDTO getCustomer(Long customerId) throws CustomerNotFoundException;

    CustomerDTO updateCustomer(CustomerDTO customerDTO);

    void deleteCustomer(Long customerId);

    List<AccountOperationDTO> accountHistory(String accountId);

    AccountHistoryDTO getAccountHistory(String accountId, int page, int size) throws BankAccountNotFoundException;


    List<CustomerDTO> searchCustomers(String keyword);
}

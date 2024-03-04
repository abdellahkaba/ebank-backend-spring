package com.ebank.ebankbackend;

import com.ebank.ebankbackend.entities.*;
import com.ebank.ebankbackend.enums.AccountStatus;
import com.ebank.ebankbackend.enums.OperationType;
import com.ebank.ebankbackend.exceptions.BalanceNotSufficientException;
import com.ebank.ebankbackend.exceptions.BankAccountNotFoundException;
import com.ebank.ebankbackend.exceptions.CustomerNotFoundException;
import com.ebank.ebankbackend.repositories.AccountOperationRepository;
import com.ebank.ebankbackend.repositories.BankAccountRepository;
import com.ebank.ebankbackend.repositories.CustomerRepository;
import com.ebank.ebankbackend.services.BankAccountService;
import com.ebank.ebankbackend.services.BankService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@SpringBootApplication
public class EbankBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(EbankBackendApplication.class, args);
    }
    @Bean
    CommandLineRunner commandLineRunner (BankAccountService bankAccountService) {
        return args -> {
            Stream.of("Hamza","Jean","Kallo").forEach(name -> {
                Customer customer = new Customer();
                customer.setName(name);
                customer.setEmail(name+"@gmail.com");
                bankAccountService.saveCustomer(customer) ;
            });

            //La liste des customers
            bankAccountService.listCustomers().forEach(customer -> {
                //Pour chaque customer on creer des comptes
                try {
                    bankAccountService.saveCurrentBankAccount(Math.random()*9000, 10000, customer.getId()) ;
                    bankAccountService.saveSavingBankAccount(Math.random()*110000,5.5, customer.getId());
                    bankAccountService.bankAccountList().forEach(account ->  {
                        List<BankAccount> bankAccounts = bankAccountService.bankAccountList();
                        for (BankAccount bankAccount:bankAccounts){
                            for (int i = 0; i <5; i++){
                                try {
                                    bankAccountService.credit(bankAccount.getId(),10000+Math.random()*120000,"Credit");
                                    bankAccountService.debit(bankAccount.getId(),1000+Math.random()*7000,"Debit");
                                } catch (BankAccountNotFoundException | BalanceNotSufficientException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        }
                    });
                } catch (CustomerNotFoundException e) {
                   e.printStackTrace();
                }


            });

        };
    }


  // @Bean
    CommandLineRunner start (
            CustomerRepository customerRepository,
            AccountOperationRepository accountOperationRepository,
            BankAccountRepository bankAccountRepository
   ){
        return args -> {
            Stream.of("Abdellah","Mariam","Oumar","Fatima").forEach(name -> {
                Customer customer = new Customer();
                customer.setName(name);
                customer.setEmail(name+"@gmail.com");
                customerRepository.save(customer);
            });
            customerRepository.findAll().forEach(cust -> {
                CurrentAccount currentAccount = new CurrentAccount();
                currentAccount.setId(UUID.randomUUID().toString());
                currentAccount.setBalance(Math.random()*7000);
                currentAccount.setCreatedAt(new Date());
                currentAccount.setStatus(AccountStatus.CREATED);
                currentAccount.setCustomer(cust);
                currentAccount.setOverDraft(400);
                bankAccountRepository.save(currentAccount);

                SavingAccount savingAccount = new SavingAccount();
                savingAccount.setId(UUID.randomUUID().toString());
                savingAccount.setBalance(Math.random()*7000);
                savingAccount.setCreatedAt(new Date());
                savingAccount.setStatus(AccountStatus.CREATED);
                savingAccount.setCustomer(cust);
                savingAccount.setInterestRate(5.3);
                bankAccountRepository.save(savingAccount);
            });

            bankAccountRepository.findAll().forEach(acc -> {
                for(int i = 0; i<15; i++){
                    AccountOperation accountOperation = new AccountOperation();
                    accountOperation.setOperationDate(new Date());
                    accountOperation.setAmount(Math.random()*12000);
                    accountOperation.setOperationType(Math.random()>0.5? OperationType.DEBIT: OperationType.CREDIT);
                    accountOperation.setBankAccount(acc);
                    accountOperationRepository.save(accountOperation);
                }

            });
        };
    }

}

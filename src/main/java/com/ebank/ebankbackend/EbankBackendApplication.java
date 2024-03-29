package com.ebank.ebankbackend;

import com.ebank.ebankbackend.dtos.BankAccountDTO;
import com.ebank.ebankbackend.dtos.CurrentBankAccountDTO;
import com.ebank.ebankbackend.dtos.CustomerDTO;
import com.ebank.ebankbackend.dtos.SavingBankAccountDTO;
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
                CustomerDTO customerDTO = new CustomerDTO();
                customerDTO.setName(name);
                customerDTO.setEmail(name+"@gmail.com");
                bankAccountService.saveCustomer(customerDTO) ;
            });

            //La liste des customers
            bankAccountService.listCustomers().forEach(customer -> {
                //Pour chaque customer on creer des comptes
                try {
                    bankAccountService.saveCurrentBankAccount(Math.random()*9000, 10000, customer.getId()) ;
                    bankAccountService.saveSavingBankAccount(Math.random()*110000,5.5, customer.getId());

                } catch (CustomerNotFoundException e) {
                   e.printStackTrace();
                }
                

            });
            List<BankAccountDTO> bankAccounts = bankAccountService.bankAccountList();
            for (BankAccountDTO bankAccount:bankAccounts){
                for (int i = 0; i <5; i++){
                    String accountId ;
                    if (bankAccount instanceof SavingBankAccountDTO){
                        accountId = ((SavingBankAccountDTO) bankAccount).getId();
                    }else {
                        accountId = ((CurrentBankAccountDTO) bankAccount).getId();

                    }
                    try {
                        bankAccountService.credit(accountId,10000+Math.random()*120000,"Credit");
                        bankAccountService.debit(accountId,1000+Math.random()*7000, "Debit");
                    } catch (BankAccountNotFoundException | BalanceNotSufficientException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
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

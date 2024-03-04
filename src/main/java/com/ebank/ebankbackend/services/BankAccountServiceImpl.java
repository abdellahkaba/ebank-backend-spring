package com.ebank.ebankbackend.services;

import com.ebank.ebankbackend.dtos.CustomerDTO;
import com.ebank.ebankbackend.entities.*;
import com.ebank.ebankbackend.enums.OperationType;
import com.ebank.ebankbackend.exceptions.BalanceNotSufficientException;
import com.ebank.ebankbackend.exceptions.BankAccountNotFoundException;
import com.ebank.ebankbackend.exceptions.CustomerNotFoundException;
import com.ebank.ebankbackend.mappers.BankAccountMapperImpl;
import com.ebank.ebankbackend.repositories.AccountOperationRepository;
import com.ebank.ebankbackend.repositories.BankAccountRepository;
import com.ebank.ebankbackend.repositories.CustomerRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
@Slf4j

public class BankAccountServiceImpl implements BankAccountService{
    private CustomerRepository customerRepository;
    private BankAccountRepository bankAccountRepository;
    private AccountOperationRepository accountOperationRepository;
    private BankAccountMapperImpl dtoMapper ;
   // Logger log = LoggerFactory.getLogger(this.getClass().getName()) ;
    @Override
    public CustomerDTO saveCustomer(CustomerDTO customerDTO) {
        log.info("Saving new Customer");
        //On creer d'abord un objet Customer
        Customer customer = dtoMapper.fromCustomerDTO(customerDTO);
        Customer savedCustomer = customerRepository.save(customer);

        return dtoMapper.fromCustomer(savedCustomer);
    }

    @Override
    public CurrentAccount saveCurrentBankAccount(double initialBalance, double overDraft, Long customerId) throws CustomerNotFoundException {
        Customer customer = customerRepository.findById(customerId).orElse(null);
        if (customer==null){
            throw new CustomerNotFoundException("Customers not found ") ;
        }
        CurrentAccount currentAccount = new CurrentAccount();
        currentAccount.setId(UUID.randomUUID().toString());
        currentAccount.setCreatedAt(new Date());
        currentAccount.setBalance(initialBalance);
        currentAccount.setOverDraft(overDraft);
        currentAccount.setCustomer(customer);
        return bankAccountRepository.save(currentAccount) ;
    }

    @Override
    public SavingAccount saveSavingBankAccount(double initialBalance, double interestRate, Long customerId) throws CustomerNotFoundException {
        Customer customer = customerRepository.findById(customerId).orElse(null);
        if (customer==null){
            throw new CustomerNotFoundException("Customers not found ") ;
        }
        SavingAccount savingAccount = new SavingAccount();
        savingAccount.setId(UUID.randomUUID().toString());
        savingAccount.setCreatedAt(new Date());
        savingAccount.setBalance(initialBalance);
        savingAccount.setInterestRate(interestRate);
        savingAccount.setCustomer(customer);
        return bankAccountRepository.save(savingAccount) ;
    }

    /*Une methode qui permet de tranferer une liste de customer vers une liste de
    de customerDTO*/
    @Override
    public List<CustomerDTO> listCustomers() {
        List<Customer> customers = customerRepository.findAll();
        //Pour chaque customer on genere un objet customerDTO
        /**
         * Programmation fonctionnelle en utilisant les stream
        */
        List<CustomerDTO> customerDTOS = customers.stream().map(customer -> dtoMapper.fromCustomer(customer)).collect(Collectors.toList());

        /* Programation Imperative
        List<CustomerDTO> customerDTOS = new ArrayList<>();
        for (Customer customer:customers){
            //on creer un objet CustomerDTO
            CustomerDTO  customerDTO = dtoMapper.fromCustomer(customer);
            customerDTOS.add(customerDTO);
        }*/

        return customerDTOS ;

    }

    @Override
    public BankAccount getBankAccount(String accountId) throws BankAccountNotFoundException {
      BankAccount bankAccount;
        bankAccount = bankAccountRepository.findById(accountId).orElseThrow(()-> new BankAccountNotFoundException("BankAccount not found"));
        return bankAccount;
    }

    @Override
    public void debit(String accountId, double amount, String description) throws BankAccountNotFoundException, BalanceNotSufficientException {
        BankAccount bankAccount = getBankAccount(accountId);
        if (bankAccount.getBalance() < amount)
            throw new BalanceNotSufficientException("Balance not suffient");
        AccountOperation accountOperation = new AccountOperation() ;
        accountOperation.setOperationType(OperationType.DEBIT);
        accountOperation.setAmount(amount);
        accountOperation.setDescription(description);
        accountOperation.setOperationDate(new Date());
        accountOperation.setBankAccount(bankAccount);
        accountOperationRepository.save(accountOperation);
        bankAccount.setBalance(bankAccount.getBalance() - amount);
        bankAccountRepository.save(bankAccount) ;
    }

    @Override
    public void credit(String accountId, double amount, String description) throws BankAccountNotFoundException, BalanceNotSufficientException {
        BankAccount bankAccount = getBankAccount(accountId);

        AccountOperation accountOperation = new AccountOperation() ;
        accountOperation.setOperationType(OperationType.CREDIT);
        accountOperation.setAmount(amount);
        accountOperation.setDescription(description);
        accountOperation.setOperationDate(new Date());
        accountOperation.setBankAccount(bankAccount);
        accountOperationRepository.save(accountOperation);
        bankAccount.setBalance(bankAccount.getBalance() + amount);
        bankAccountRepository.save(bankAccount) ;
    }

    @Override
    public void transfert(String accountIdSource, String accountIdDescription, double amount) throws BankAccountNotFoundException, BalanceNotSufficientException {
        debit(accountIdSource,amount, "Transfer to " + accountIdDescription);
        credit(accountIdDescription,amount, "Transfert from" + accountIdSource);
    }

    @Override
    public List<BankAccount> bankAccountList(){
        return bankAccountRepository.findAll();
    }
    /**
     * Une fonction qui consulte un customer
     */
    @Override
    public CustomerDTO getCustomer(Long customerId) throws CustomerNotFoundException {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found"));
        return dtoMapper.fromCustomer(customer) ;
    }
}

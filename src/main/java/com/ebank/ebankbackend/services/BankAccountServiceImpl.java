package com.ebank.ebankbackend.services;

import com.ebank.ebankbackend.dtos.*;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
    public CurrentBankAccountDTO saveCurrentBankAccount(double initialBalance, double overDraft, Long customerId) throws CustomerNotFoundException {
        Customer customer = customerRepository.findById(customerId).orElse(null);
        if (customer == null) {
            throw new CustomerNotFoundException("Customers not found ");
        }
        CurrentAccount currentAccount = new CurrentAccount();
        currentAccount.setId(UUID.randomUUID().toString());
        currentAccount.setCreatedAt(new Date());
        currentAccount.setBalance(initialBalance);
        currentAccount.setOverDraft(overDraft);
        currentAccount.setCustomer(customer);
        CurrentAccount savedBankAccount = bankAccountRepository.save(currentAccount);
        return dtoMapper.fromCurrentBankAccount(savedBankAccount);
    }
    @Override
    public SavingBankAccountDTO saveSavingBankAccount(double initialBalance, double interestRate, Long customerId) throws CustomerNotFoundException {
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
        SavingAccount savedBankAccount = bankAccountRepository.save(savingAccount) ;
        return dtoMapper.fromSavingBankAccount(savingAccount) ;
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
    public BankAccountDTO getBankAccount(String accountId) throws BankAccountNotFoundException {
      BankAccount bankAccount;
        bankAccount = bankAccountRepository.findById(accountId).orElseThrow(()-> new BankAccountNotFoundException("BankAccount not found"));
        if(bankAccount instanceof SavingAccount) {
            SavingAccount savingAccount = (SavingAccount) bankAccount ;
            return dtoMapper.fromSavingBankAccount(savingAccount);
        }else {
            CurrentAccount currentAccount = (CurrentAccount) bankAccount ;
            return dtoMapper.fromCurrentBankAccount(currentAccount);
        }
    }

    @Override
    public void debit(String accountId, double amount, String description) throws BankAccountNotFoundException, BalanceNotSufficientException {
        BankAccount bankAccount;
        bankAccount = bankAccountRepository.findById(accountId).orElseThrow(()-> new BankAccountNotFoundException("BankAccount not found"));
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
        BankAccount bankAccount;
        bankAccount = bankAccountRepository.findById(accountId).orElseThrow(()-> new BankAccountNotFoundException("BankAccount not found"));

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
    public List<BankAccountDTO> bankAccountList(){
        List<BankAccount> bankAccounts = bankAccountRepository.findAll();
        List<BankAccountDTO> bankAccountDTO = bankAccounts.stream().map(bankAccount -> {
            if (bankAccount instanceof SavingAccount) {
                SavingAccount savingAccount = (SavingAccount) bankAccount;
                return dtoMapper.fromSavingBankAccount(savingAccount);
            } else {
                CurrentAccount currentAccount = (CurrentAccount) bankAccount;
                return dtoMapper.fromCurrentBankAccount(currentAccount);
            }

        }).collect(Collectors.toList());
        return bankAccountDTO ;
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

    /**
     * Une fonction qui met a jout un customer
     */

    @Override
    public CustomerDTO updateCustomer(CustomerDTO customerDTO) {
        log.info("Saving new Customer");
        //On creer d'abord un objet Customer
        Customer customer = dtoMapper.fromCustomerDTO(customerDTO);
        Customer savedCustomer = customerRepository.save(customer);

        return dtoMapper.fromCustomer(savedCustomer);
    }

    /**
     * Une fonction qui supprime un customer
     */

    @Override
    public void deleteCustomer(Long customerId) {
        customerRepository.deleteById(customerId);
    }

    /**
     * Consulter La liste des operations sur un compte
     */
    @Override
    public List<AccountOperationDTO> accountHistory(String accountId) {
        List<AccountOperation> accountOperations = accountOperationRepository.findByBankAccountId(accountId) ;
       return accountOperations.stream().map(op -> dtoMapper.fromAccountOperation(op)).collect(Collectors.toList());
    }

    @Override
    public AccountHistoryDTO getAccountHistory(String accountId, int page, int size) throws BankAccountNotFoundException {
       BankAccount bankAccount = bankAccountRepository.findById(accountId).orElse(null) ;
       if (bankAccount == null) throw  new BankAccountNotFoundException("Account not found");
       Page<AccountOperation> accountOperations = accountOperationRepository.findByBankAccountId(accountId, PageRequest.of(page,size)) ;
       AccountHistoryDTO accountHistoryDTO = new AccountHistoryDTO() ;
       List<AccountOperationDTO> accountOperationDTOS = accountOperations.getContent().stream().map(op -> dtoMapper.fromAccountOperation(op)).collect(Collectors.toList());
       accountHistoryDTO.setAccountOperationDTOS(accountOperationDTOS);
       accountHistoryDTO.setAccountId(bankAccount.getId());
       accountHistoryDTO.setBalance(bankAccount.getBalance());
       accountHistoryDTO.setCurrentPage(page);
       accountHistoryDTO.setPageSize(size);
       accountHistoryDTO.setTotalPages(accountOperations.getTotalPages());
       return accountHistoryDTO ;

    }

    @Override
    public List<CustomerDTO> searchCustomers(String keyword) {
        List<Customer> customers = customerRepository.findByNameContains(keyword);
        List<CustomerDTO> listCustomerDTO = customers.stream().map(customer -> dtoMapper.fromCustomer(customer)).collect(Collectors.toList());
        return listCustomerDTO;
    }
}

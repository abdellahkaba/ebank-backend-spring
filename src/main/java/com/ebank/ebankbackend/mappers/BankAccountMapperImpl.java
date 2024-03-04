package com.ebank.ebankbackend.mappers;

import com.ebank.ebankbackend.dtos.AccountOperationDTO;
import com.ebank.ebankbackend.dtos.CurrentBankAccountDTO;
import com.ebank.ebankbackend.dtos.CustomerDTO;
import com.ebank.ebankbackend.dtos.SavingBankAccountDTO;
import com.ebank.ebankbackend.entities.AccountOperation;
import com.ebank.ebankbackend.entities.CurrentAccount;
import com.ebank.ebankbackend.entities.Customer;
import com.ebank.ebankbackend.entities.SavingAccount;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
public class BankAccountMapperImpl {

    /**
     * Les Methodes de mappers pour copier une liste d'objet dans une autre
     * liste d'objet avec BeanUtils
     */
    public CustomerDTO fromCustomer(Customer customer){
        CustomerDTO customerDTO = new CustomerDTO();
        BeanUtils.copyProperties(customer, customerDTO);
        return customerDTO ;
    }
    public Customer fromCustomerDTO(CustomerDTO customerDTO){
        Customer customer = new Customer() ;
        BeanUtils.copyProperties(customerDTO,customer);
        return customer ;
    }

    public SavingBankAccountDTO fromSavingBankAccount(SavingAccount savingAccount) {
        SavingBankAccountDTO savingBankAccountDTO = new SavingBankAccountDTO() ;
        BeanUtils.copyProperties(savingAccount,savingBankAccountDTO);
        //le customers correspondant
        savingBankAccountDTO.setCustomerDTO(fromCustomer(savingAccount.getCustomer()));
        //on recupere le nom de la classe de l'objet
        savingBankAccountDTO.setType(savingAccount.getClass().getSimpleName());
        return savingBankAccountDTO ;
    }

    public SavingAccount fromSavingBankAccountDTO(SavingBankAccountDTO savingBankAccountDTO) {
        SavingAccount savingAccount = new SavingAccount() ;
        BeanUtils.copyProperties(savingBankAccountDTO,savingAccount);
        //le customers correspondant
        savingAccount.setCustomer(fromCustomerDTO(savingBankAccountDTO.getCustomerDTO()));
        return savingAccount ;
    }

    public CurrentBankAccountDTO fromCurrentBankAccount(CurrentAccount currentAccount) {
        CurrentBankAccountDTO currentBankAccountDTO = new CurrentBankAccountDTO() ;
        BeanUtils.copyProperties(currentAccount,currentBankAccountDTO);
        //Le customers correspondant
        currentBankAccountDTO.setCustomerDTO(fromCustomer(currentAccount.getCustomer()));
        //on recupere le nom de la classe de l'objet
        currentBankAccountDTO.setType(currentAccount.getClass().getSimpleName());
        return currentBankAccountDTO ;
    }

    public CurrentAccount fromSavingBankAccountDTO(CurrentBankAccountDTO currentBankAccountDTO) {
        CurrentAccount currentAccount = new CurrentAccount() ;
        BeanUtils.copyProperties(currentBankAccountDTO,currentAccount);
        //le customers correspondant
        currentAccount.setCustomer(fromCustomerDTO(currentBankAccountDTO.getCustomerDTO()));
        return currentAccount ;
    }

    public AccountOperationDTO fromAccountOperation (AccountOperation accountOperation) {
        AccountOperationDTO accountOperationDTO = new AccountOperationDTO() ;
        BeanUtils.copyProperties(accountOperation, accountOperationDTO);
        return accountOperationDTO ;
    }
}

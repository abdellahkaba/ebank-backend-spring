package com.ebank.ebankbackend.web;

import com.ebank.ebankbackend.dtos.BankAccountDTO;
import com.ebank.ebankbackend.exceptions.BankAccountNotFoundException;
import com.ebank.ebankbackend.services.BankAccountService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController

public class BankAccountRestController {
    private BankAccountService bankAccountService ;
    public BankAccountRestController (BankAccountService bankAccountService) {
        this.bankAccountService = bankAccountService ;
    }

    @GetMapping("/account/{accountId}")
    public BankAccountDTO getBankAccount (String accountId) throws BankAccountNotFoundException {
        return bankAccountService.getBankAccount(accountId);
    }

    @GetMapping("/accounts")
    public List<BankAccountDTO> listAccounts () {
        return bankAccountService.bankAccountList();
    }

}

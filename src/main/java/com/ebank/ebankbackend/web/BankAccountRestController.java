package com.ebank.ebankbackend.web;

import com.ebank.ebankbackend.dtos.*;
import com.ebank.ebankbackend.exceptions.BalanceNotSufficientException;
import com.ebank.ebankbackend.exceptions.BankAccountNotFoundException;
import com.ebank.ebankbackend.services.BankAccountService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("*") //autorisation d'envoie de requete

public class BankAccountRestController {
    private BankAccountService bankAccountService ;
    public BankAccountRestController (BankAccountService bankAccountService) {
        this.bankAccountService = bankAccountService ;
    }

    @GetMapping("/accounts/{accountId}")
    public BankAccountDTO getBankAccount (@PathVariable String accountId) throws BankAccountNotFoundException {
        return bankAccountService.getBankAccount(accountId);
    }

    @GetMapping("/accounts")
    public List<BankAccountDTO> listAccounts () {
        return bankAccountService.bankAccountList();
    }

    @GetMapping("/accounts/{accountId}/operations")
    public List<AccountOperationDTO> getHistory (@PathVariable String accountId) {
        return bankAccountService.accountHistory(accountId) ;
    }

    @GetMapping("/accounts/{accountId}/pageOperations")
    public AccountHistoryDTO getAccountHistory (
            @PathVariable String accountId,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "5") int size) throws BankAccountNotFoundException {
        return bankAccountService.getAccountHistory(accountId,page,size) ;
    }

    /**
     * Operation pour debiter un compte
     * @param debitDTO
     * @return
     * @throws BankAccountNotFoundException
     * @throws BalanceNotSufficientException
     */
    @PostMapping("/accounts/debit")
    public DebitDTO debit(@RequestBody DebitDTO debitDTO) throws BankAccountNotFoundException, BalanceNotSufficientException {
        this.bankAccountService.debit(debitDTO.getAccountId(),debitDTO.getAmount(),debitDTO.getDescription());
        return debitDTO ;
    }

    /**
     * Operation pour crediter un compte
     * @param creditDTO
     * @return
     * @throws BankAccountNotFoundException
     * @throws BalanceNotSufficientException
     */
    @PostMapping("/accounts/credit")
    public CreditDTO debit(@RequestBody CreditDTO creditDTO) throws BankAccountNotFoundException, BalanceNotSufficientException {
        this.bankAccountService.credit(creditDTO.getAccountId(),creditDTO.getAmount(),creditDTO.getDescription());
        return creditDTO ;
    }

    @PostMapping("/accounts/transfert")
    public void transfert(@RequestBody TransfertRequestDTO transfertRequestDTO) throws BankAccountNotFoundException, BalanceNotSufficientException {
        this.bankAccountService.transfert(
                transfertRequestDTO.getAccountSource(),
                transfertRequestDTO.getAccountDestination(),
                transfertRequestDTO.getAmount()

                );

    }
}

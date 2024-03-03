package com.ebank.ebankbackend.services;

import com.ebank.ebankbackend.entities.BankAccount;
import com.ebank.ebankbackend.entities.CurrentAccount;
import com.ebank.ebankbackend.entities.SavingAccount;
import com.ebank.ebankbackend.repositories.BankAccountRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class BankService {
    @Autowired
    private BankAccountRepository bankAccountRepository ;
    public void consulter () {
        //Afficher les info d'un compte donnees
        BankAccount bankAccount =
                bankAccountRepository.findById("5e3db19b-5ecd-46ed-8280-7ca84f44296b").orElse(null);
        if (bankAccount!=null){
            System.out.println("********************************************************");
            System.out.println(bankAccount.getId());
            System.out.println(bankAccount.getBalance());
            System.out.println(bankAccount.getStatus());
            System.out.println(bankAccount.getCreatedAt());
            System.out.println(bankAccount.getCustomer().getName());
            System.out.println(bankAccount.getClass().getSimpleName());
            //Verification si c'est un compte courant ou Epargne
            if (bankAccount instanceof CurrentAccount){
                System.out.println( "Decouvert : "+ ((CurrentAccount)bankAccount).getOverDraft());
            } else if (bankAccount instanceof SavingAccount) {
                System.out.println("Taux d'interet : "+((SavingAccount)bankAccount).getInterestRate());
            }
            //Pour afficher les operations
            bankAccount.getAccountOperations().forEach(op -> {
                System.out.println("==============================================");
                System.out.println(op.getOperationType()+"\t"+op.getOperationDate()+"\t"+op.getAmount());
            });
        }
    }
}

package com.ebank.ebankbackend.web;

import com.ebank.ebankbackend.dtos.CustomerDTO;
import com.ebank.ebankbackend.entities.Customer;
import com.ebank.ebankbackend.services.BankAccountService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
@Slf4j
public class CustomerRestController {

    private BankAccountService bankAccountService ;

    @GetMapping("/customers")
    public List<CustomerDTO> customers () {
        return bankAccountService.listCustomers() ;
    }
}

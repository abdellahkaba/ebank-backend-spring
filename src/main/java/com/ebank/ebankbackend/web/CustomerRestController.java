package com.ebank.ebankbackend.web;

import com.ebank.ebankbackend.dtos.CustomerDTO;
import com.ebank.ebankbackend.entities.Customer;
import com.ebank.ebankbackend.exceptions.CustomerNotFoundException;
import com.ebank.ebankbackend.services.BankAccountService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@Slf4j
@CrossOrigin("*") // autorisation d'envoi de requete
public class CustomerRestController {

    private BankAccountService bankAccountService ;

    @GetMapping("/customers")
    public List<CustomerDTO> customers () {
        return bankAccountService.listCustomers() ;
    }

    @GetMapping("/customers/search")
    public List<CustomerDTO> searchCustomers (@RequestParam(name = "keyword", defaultValue = "") String keyword ) {

        return bankAccountService.searchCustomers(keyword) ;
    }
    @GetMapping("/customers/{id}")
    public CustomerDTO getCustomer(@PathVariable(name = "id") Long customerId) throws CustomerNotFoundException {
        return bankAccountService.getCustomer(customerId);
    }

    @PostMapping("/customers")
    public CustomerDTO saveCustomer( CustomerDTO customerDTO){
      return bankAccountService.saveCustomer(customerDTO);
    }

    @PutMapping("/customers/{customerId}")
    public CustomerDTO updateCustomer(@PathVariable Long customerId,  CustomerDTO customerDTO){
        customerDTO.setId(customerId);
        return bankAccountService.updateCustomer(customerDTO) ;
    }

    @DeleteMapping("/customers/{id}")
    public void deleteCustomer (@PathVariable Long id){
        bankAccountService.deleteCustomer(id);
    }
}

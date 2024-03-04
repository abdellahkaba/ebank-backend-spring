package com.ebank.ebankbackend.mappers;

import com.ebank.ebankbackend.dtos.CustomerDTO;
import com.ebank.ebankbackend.entities.Customer;
import com.fasterxml.jackson.databind.util.BeanUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
public class BankAccountMapperImpl {

    public CustomerDTO fromCustomer(Customer customer){
        CustomerDTO customerDTO = new CustomerDTO();
        //Copier les proprietes de la classe Customer vers celle de CustomerDTO
        BeanUtils.copyProperties(customer, customerDTO);
//        customerDTO.setId(customer.getId());
//        customerDTO.setName(customer.getName());
//        customerDTO.setEmail(customer.getEmail());
        return customerDTO ;
    }
    public Customer formCustomerDTO(CustomerDTO customerDTO){
        Customer customer = new Customer() ;
        BeanUtils.copyProperties(customerDTO,customer);
        return customer ;
    }
}

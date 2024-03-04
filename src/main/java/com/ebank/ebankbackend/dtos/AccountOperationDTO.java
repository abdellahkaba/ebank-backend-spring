package com.ebank.ebankbackend.dtos;

import com.ebank.ebankbackend.enums.OperationType;
import lombok.Data;

import java.util.Date;


@Data
public class AccountOperationDTO {

    private Long id ;
    private Date operationDate;
    private double amount;
    private OperationType operationType ;
    private String description ;
}

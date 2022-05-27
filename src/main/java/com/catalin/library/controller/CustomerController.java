package com.catalin.library.controller;

import com.catalin.library.dto.CustomerDto;
import com.catalin.library.service.CustomerService;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "api/customers")
@Validated
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping(produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<CustomerDto> createCustomer(@RequestBody @Valid CustomerDto request) {

        final CustomerDto response = customerService.createCustomer(request);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/{userName}", produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<CustomerDto> getCustomer(@PathVariable("userName") String userName) {

        final CustomerDto response = customerService.getCustomerByUserName(userName);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping(value = "/{userName}", produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<CustomerDto> updateCustomer(@PathVariable("userName") String userName,
            @RequestBody CustomerDto request) {

        final CustomerDto response = customerService.updateCustomerByUserName(userName, request);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping(value = "/{userName}", produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<CustomerDto> deleteCustomer(@PathVariable("userName") String userName) {

        customerService.deleteCustomerByUserName(userName);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}

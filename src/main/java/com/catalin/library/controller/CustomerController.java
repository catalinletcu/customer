package com.catalin.library.controller;

import com.catalin.library.dto.CustomerDto;
import com.catalin.library.service.CustomerService;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping(produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<CustomerDto> createCustomer(@RequestBody @Valid CustomerDto request) {

        final CustomerDto response = customerService.createCustomer(request);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/{username}", produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<CustomerDto> getCustomer(@PathVariable("username") String username) {

        final CustomerDto response = customerService.getCustomerByUsername(username);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping(value = "/{username}", produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<CustomerDto> updateCustomer(@PathVariable("username") String username,
            @RequestBody CustomerDto request) {

        final CustomerDto response = customerService.updateCustomerByUsername(username, request);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping(value = "/{username}", produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<CustomerDto> deleteCustomer(@PathVariable("username") String username) {

        customerService.deleteCustomerByUsername(username);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}

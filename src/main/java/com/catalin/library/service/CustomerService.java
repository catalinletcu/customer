package com.catalin.library.service;

import com.catalin.library.dto.CustomerDto;
import com.catalin.library.entity.CustomerEntity;
import com.catalin.library.exception.BadRequestException;
import com.catalin.library.exception.NotFoundException;
import com.catalin.library.mapper.CustomerMapper;
import com.catalin.library.repository.CustomerRepository;
import java.util.Optional;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CustomerService {

    private static final String EMAIL_REGEX_PATTERN = "^(.+)@(\\S+)$";

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    public CustomerDto createCustomer(CustomerDto customer) {
        validateEmail(customer.getUsername());
        verifyUser(customer);

        //Password must be encrypted before saving
        final CustomerEntity customerEntity = customerMapper.mapFromDto(customer);
        customerEntity.setRole("USER");
        customerRepository.save(customerEntity);

        return customerMapper.mapFromEntity(customerEntity);
    }

    public CustomerDto getCustomerByUsername(String username) {
        final CustomerEntity customerEntity = getCustomerEntity(username);

        return customerMapper.mapFromEntity(customerEntity);
    }

    public CustomerDto updateCustomerByUsername(String username, CustomerDto customer) {
        final CustomerEntity customerEntity = getCustomerEntity(username);

        customerEntity.setLastName(customer.getLastName());
        customerEntity.setFirstName(customer.getFirstName());
        customerEntity.setPassword(customer.getPassword());

        return customerMapper.mapFromEntity(customerEntity);
    }

    public void deleteCustomerByUsername(String username) {
        final CustomerEntity customerEntity = getCustomerEntity(username);

        customerRepository.delete(customerEntity);
    }

    private CustomerEntity getCustomerEntity(String username) {
        return Optional.ofNullable(customerRepository.getByUsername(username))
                .orElseThrow(() -> new NotFoundException(String.format("User %s not found.", username)));
    }

    private void validateEmail(String username) {
        if (!Pattern.compile(EMAIL_REGEX_PATTERN).matcher(username).matches()) {
            throw new BadRequestException(String.format("User %s has an invalid format.", username));
        }
    }

    private void verifyUser(CustomerDto request) {
        if (customerRepository.existsByUsername(request.getUsername())) {
            throw new BadRequestException(String.format("User %s already exists.", request.getUsername()));
        }
    }
}

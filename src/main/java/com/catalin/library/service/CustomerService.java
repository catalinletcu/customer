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

    public CustomerDto createCustomer(CustomerDto request) {
        validateEmail(request.getUserName());
        verifyUser(request);

        //Password must be encrypted before saving
        final CustomerEntity customerEntity = customerMapper.mapFromDto(request);
        customerEntity.setRole("USER");
        customerRepository.save(customerEntity);

        return customerMapper.mapFromEntity(customerEntity);
    }

    public CustomerDto getCustomerByUserName(String userName) {
        final CustomerEntity customerEntity = getCustomerEntity(userName);

        return customerMapper.mapFromEntity(customerEntity);
    }

    public CustomerDto updateCustomerByUserName(String userName, CustomerDto request) {
        final CustomerEntity customerEntity = getCustomerEntity(userName);

        customerEntity.setLastName(request.getLastName());
        customerEntity.setFirstName(request.getFirstName());
        customerEntity.setPassword(request.getPassword());

        return customerMapper.mapFromEntity(customerRepository.getByUserName(userName));
    }

    public void deleteCustomerByUserName(String userName) {
        final CustomerEntity customerEntity = getCustomerEntity(userName);

        customerRepository.delete(customerEntity);
    }

    private CustomerEntity getCustomerEntity(String userName) {
        return Optional.ofNullable(customerRepository.getByUserName(userName))
                .orElseThrow(() -> new NotFoundException(String.format("User %s not found.", userName)));
    }

    private void validateEmail(String userName) {
        if (!Pattern.compile(EMAIL_REGEX_PATTERN).matcher(userName).matches()) {
            throw new BadRequestException(String.format("User %s has an invalid format.", userName));
        }
    }

    private void verifyUser(CustomerDto request) {
        if (customerRepository.existsByUserName(request.getUserName())) {
            throw new BadRequestException(String.format("User %s already exists.", request.getUserName()));
        }
    }
}

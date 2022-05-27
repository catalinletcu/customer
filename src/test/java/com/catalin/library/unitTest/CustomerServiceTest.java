package com.catalin.library.unitTest;

import com.catalin.library.dto.CustomerDto;
import com.catalin.library.entity.CustomerEntity;
import com.catalin.library.exception.BadRequestException;
import com.catalin.library.mapper.CustomerMapper;
import com.catalin.library.repository.CustomerRepository;
import com.catalin.library.service.CustomerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    CustomerRepository customerRepository;

    @Mock
    CustomerMapper customerMapper;

    @InjectMocks
    CustomerService service;

    @Test
    void createCustomerWithValidRequestThenReturnNewCreatedCustomer() {
        final CustomerDto customerDto = createCustomerDto();
        final CustomerEntity customerEntity = createCustomerEntity();

        when(customerRepository.existsByUsername(customerDto.getUsername())).thenReturn(false);
        when(customerMapper.mapFromDto(customerDto)).thenReturn(customerEntity);
        when(customerRepository.save(customerEntity)).thenReturn(customerEntity);
        when(customerMapper.mapFromEntity(customerEntity)).thenReturn(customerDto);

        final CustomerDto response = service.createCustomer(customerDto);

        assertNotNull(response);
        assertThat(response).usingRecursiveComparison().isEqualTo(customerDto);
    }

    @Test
    void createCustomerWithInvalidUsernameThenThrowException() {
        final CustomerDto customerDto = createCustomerDto();

        when(customerRepository.existsByUsername(customerDto.getUsername())).thenReturn(true);

        final BadRequestException exception = assertThrows(BadRequestException.class, () -> service.createCustomer(customerDto));

        assertNotNull(exception);
        assertEquals("User username@gmail.com already exists.", exception.getMessage());
    }

    private CustomerDto createCustomerDto() {
        final CustomerDto customerDto = new CustomerDto();
        customerDto.setPassword("pass");
        customerDto.setFirstName("firstName");
        customerDto.setLastName("lastName");
        customerDto.setUsername("username@gmail.com");
        return customerDto;
    }

    private CustomerEntity createCustomerEntity() {
        final CustomerEntity customerEntity = new CustomerEntity();
        customerEntity.setPassword("pass");
        customerEntity.setFirstName("firstName");
        customerEntity.setLastName("lastName");
        customerEntity.setUsername("username@gmail.com");
        return customerEntity;
    }
}

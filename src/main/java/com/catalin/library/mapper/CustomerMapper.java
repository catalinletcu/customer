package com.catalin.library.mapper;

import com.catalin.library.dto.CustomerDto;
import com.catalin.library.entity.CustomerEntity;
import org.springframework.stereotype.Component;

@Component
public class CustomerMapper {

    public CustomerEntity mapFromDto(CustomerDto source) {
        final CustomerEntity destination = new CustomerEntity();
        destination.setFirstName(source.getFirstName());
        destination.setLastName(source.getLastName());
        destination.setUserName(source.getUserName());
        destination.setPassword(source.getPassword());

        return destination;
    }

    public CustomerDto mapFromEntity(CustomerEntity source) {
        final CustomerDto destination = new CustomerDto();
        destination.setFirstName(source.getFirstName());
        destination.setLastName(source.getLastName());
        destination.setUserName(source.getUserName());
        destination.setPassword(source.getPassword());

        return destination;
    }
}

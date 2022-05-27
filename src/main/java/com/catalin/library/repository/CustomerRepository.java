package com.catalin.library.repository;

import com.catalin.library.entity.CustomerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<CustomerEntity, Long> {

    CustomerEntity getByUserName(String userName);

    boolean existsByUserName(String userName);
}

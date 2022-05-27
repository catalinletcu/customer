package com.catalin.library.repository;

import com.catalin.library.entity.LoginEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoginRepository extends JpaRepository<LoginEntity, Integer> {

    Optional<LoginEntity> findByToken(String token);
}

package com.catalin.library.repository;

import com.catalin.library.entity.BookEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<BookEntity, Integer> {

    BookEntity getBookEntityByResourceId(String bookId);

    boolean existsByAuthorAndTitle(String author, String title);
}

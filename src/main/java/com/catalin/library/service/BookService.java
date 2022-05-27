package com.catalin.library.service;

import com.catalin.library.dto.BookDto;
import com.catalin.library.entity.BookEntity;
import com.catalin.library.exception.BadRequestException;
import com.catalin.library.exception.NotFoundException;
import com.catalin.library.mapper.BookMapper;
import com.catalin.library.repository.BookRepository;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class BookService {

    private final BookMapper bookMapper;
    private final BookRepository bookRepository;

    public BookDto createBook(BookDto request) {
        verifyBook(request);
        final BookEntity bookEntity = bookMapper.mapFromDto(request);
        bookEntity.setBookInternalId(UUID.randomUUID().toString());

        return bookMapper.mapFromEntity(bookRepository.save(bookEntity));
    }

    public BookDto getBookById(String bookId) {
        final BookEntity bookEntity = getBookEntity(bookId);

        return bookMapper.mapFromEntity(bookEntity);
    }

    public BookDto updateBookById(String bookId, BookDto request) {
        final BookEntity bookEntity = getBookEntity(bookId);
        bookEntity.setType(request.getType());
        bookEntity.setTitle(request.getTitle());
        bookEntity.setAuthor(request.getAuthor());

        return bookMapper.mapFromEntity(bookRepository.getByBookInternalId(bookId));
    }

    public void deleteBookById(String bookId) {
        final BookEntity bookEntity = getBookEntity(bookId);
        bookRepository.delete(bookEntity);
    }

    private BookEntity getBookEntity(String bookId) {
        return Optional.ofNullable(bookRepository.getByBookInternalId(bookId))
                .orElseThrow(() -> new NotFoundException(String.format("Book with id %s not found.", bookId)));
    }

    private void verifyBook(BookDto request) {
        if (bookRepository.existsByAuthorAndTitle(request.getAuthor(), request.getTitle())) {
            throw new BadRequestException(String.format("Book %s already exists.", request.getTitle()));
        }
    }
}

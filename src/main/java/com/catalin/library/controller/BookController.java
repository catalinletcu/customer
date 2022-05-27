package com.catalin.library.controller;

import com.catalin.library.dto.BookDto;
import com.catalin.library.service.BookService;
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
@RequestMapping(value = "api/books")
@Validated
public class BookController {

    private final BookService bookService;

    @PostMapping(produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<BookDto> createBook(@RequestBody @Valid BookDto request) {

        final BookDto response = bookService.createBook(request);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/{bookId}", produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<BookDto> getBook(@PathVariable("bookId") String bookId) {

        final BookDto response = bookService.getBookById(bookId);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping(value = "/{bookId}", produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<BookDto> updateBook(@PathVariable("bookId") String bookId,
            @RequestBody BookDto request) {

        final BookDto response = bookService.updateBookById(bookId, request);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping(value = "/{bookId}", produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<BookDto> deleteBook(@PathVariable("bookId") String bookId) {

        bookService.deleteBookById(bookId);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}

package com.catalin.library;

import com.catalin.library.dto.ApiError;
import com.catalin.library.dto.BookDto;
import com.catalin.library.entity.BookEntity;
import com.catalin.library.repository.BookRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.web.util.UriComponentsBuilder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest(classes = LibraryApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = "spring.profiles.active=test")
@Sql(scripts = "/scripts/truncate-tables.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class BookControllerIntegrationTest {

    @Autowired
    BookRepository bookRepository;

    @Autowired
    protected TestRestTemplate restTemplate;

    protected static final String HOST = "localhost";
    protected static final String HTTP_PROTOCOL = "http";

    @LocalServerPort
    protected int port;

    @Test
    void createBookWithValidRequestThenReturnNewCreatedBook() {
        final BookDto request = createBookDto();

        final ResponseEntity<BookDto> responseEntity =
                restTemplate.postForEntity(constructBaseUrl().build().toUriString(), request, BookDto.class);

        assertNotNull(responseEntity);
        assertNotNull(responseEntity.getBody());
        assertNotNull(responseEntity.getBody().getId());
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(request.getTitle(), responseEntity.getBody().getTitle());
        assertEquals(request.getAuthor(), responseEntity.getBody().getAuthor());
        assertEquals(request.getType(), responseEntity.getBody().getType());
    }

    @Test
    void createBookWithExistingDetailsThenThrowException() {
        createAndSaveBookEntity();
        final BookDto request = createBookDto();

        final ResponseEntity<ApiError> responseEntity =
                restTemplate.postForEntity(constructBaseUrl().build().toUriString(), request, ApiError.class);

        assertNotNull(responseEntity);
        assertNotNull(responseEntity.getBody());
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals(ApiError.BAD_REQUEST, responseEntity.getBody().getCode());
        assertEquals("Book selected poetry already exists.", responseEntity.getBody().getMessage());
    }

    @Test
    void getBookWithValidIdThenReturnBook() {
        final BookEntity bookEntity = createAndSaveBookEntity();

        final ResponseEntity<BookDto> responseEntity =
                restTemplate.getForEntity(constructBaseUrl().path(bookEntity.getBookInternalId()).build().toUriString(), BookDto.class);

        assertNotNull(responseEntity);
        assertNotNull(responseEntity.getBody());
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(bookEntity.getTitle(), responseEntity.getBody().getTitle());
        assertEquals(bookEntity.getAuthor(), responseEntity.getBody().getAuthor());
        assertEquals(bookEntity.getType(), responseEntity.getBody().getType());
        assertEquals(bookEntity.getBookInternalId(), responseEntity.getBody().getId());
    }

    @Test
    void getBookWithInvalidIdThenThrowException() {
        createAndSaveBookEntity();

        final ResponseEntity<ApiError> responseEntity =
                restTemplate.getForEntity(constructBaseUrl().path("INVALID").build().toUriString(), ApiError.class);

        assertNotNull(responseEntity);
        assertNotNull(responseEntity.getBody());
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertEquals(ApiError.NOT_FOUND, responseEntity.getBody().getCode());
        assertEquals("Book with id INVALID not found.", responseEntity.getBody().getMessage());
    }

    @Test
    void updateBookWithValidRequestThenReturnUpdatedBook() {
        final BookEntity bookEntity = createAndSaveBookEntity();

        final BookDto updatedBook = new BookDto();
        updatedBook.setAuthor("John2");
        updatedBook.setTitle("selected poetry2");

        final ResponseEntity<BookDto> responseEntity =
                restTemplate.exchange(constructBaseUrl().path(bookEntity.getBookInternalId()).build().toUriString(), HttpMethod.PUT,
                        new HttpEntity<>(updatedBook), BookDto.class);

        assertNotNull(responseEntity);
        assertNotNull(responseEntity.getBody());
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody().getType());
        assertEquals(updatedBook.getTitle(), responseEntity.getBody().getTitle());
        assertEquals(updatedBook.getAuthor(), responseEntity.getBody().getAuthor());
        assertEquals(bookEntity.getBookInternalId(), responseEntity.getBody().getId());
    }

    @Test
    void updateBookWithInvalidRequestThenThrowException() {
        createAndSaveBookEntity();

        final ResponseEntity<ApiError> responseEntity =
                restTemplate.exchange(constructBaseUrl().path("INVALID").build().toUriString(), HttpMethod.PUT,
                        new HttpEntity<>(new BookDto()), ApiError.class);

        assertNotNull(responseEntity);
        assertNotNull(responseEntity.getBody());
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertEquals(ApiError.NOT_FOUND, responseEntity.getBody().getCode());
        assertEquals("Book with id INVALID not found.", responseEntity.getBody().getMessage());
    }

    @Test
    void deleteBookWithValidIdThenReturnNoContent() {
        final BookEntity bookEntity = createAndSaveBookEntity();

        final ResponseEntity<BookDto> responseEntity =
                restTemplate.exchange(constructBaseUrl().path(bookEntity.getBookInternalId()).build().toUriString(), HttpMethod.DELETE,
                        new HttpEntity<>(""), BookDto.class);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
        assertNull(bookRepository.getByBookInternalId(bookEntity.getBookInternalId()));
    }

    private BookDto createBookDto() {
        final BookDto request = new BookDto();
        request.setType("poetry");
        request.setAuthor("John");
        request.setTitle("selected poetry");
        return request;
    }

    private BookEntity createAndSaveBookEntity() {
        final BookEntity request = new BookEntity();
        request.setBookInternalId("123");
        request.setAuthor("John");
        request.setTitle("selected poetry");
        return bookRepository.save(request);
    }

    protected UriComponentsBuilder constructBaseUrl() {
        return UriComponentsBuilder.newInstance().scheme(HTTP_PROTOCOL)
                .host(HOST)
                .port(getPort())
                .pathSegment(getBasePath());
    }

    protected String getBasePath() {
        return "api/books";
    }

    protected int getPort() {
        return port;
    }
}

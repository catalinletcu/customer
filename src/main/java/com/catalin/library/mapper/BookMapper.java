package com.catalin.library.mapper;

import com.catalin.library.dto.BookDto;
import com.catalin.library.entity.BookEntity;
import org.springframework.stereotype.Component;

@Component
public class BookMapper {

    public BookEntity mapFromDto(BookDto source) {
        final BookEntity destination = new BookEntity();
        destination.setAuthor(source.getAuthor());
        destination.setTitle(source.getTitle());
        destination.setType(source.getType());

        return destination;
    }

    public BookDto mapFromEntity(BookEntity source) {
        final BookDto destination = new BookDto();
        destination.setAuthor(source.getAuthor());
        destination.setTitle(source.getTitle());
        destination.setType(source.getType());
        destination.setId(source.getBookInternalId());

        return destination;
    }
}

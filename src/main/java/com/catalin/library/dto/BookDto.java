package com.catalin.library.dto;

import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookDto {

    private String id;

    @NotNull(message = "Book title should not be null.")
    private String title;

    @NotNull(message = "Book author should not be null.")
    private String author;

    private String type;
}

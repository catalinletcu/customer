package com.catalin.library.dto;

import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDto {

    private String firstName;

    private String lastName;

    @NotNull(message = "UserName should not be null.")
    private String userName;

    @NotNull(message = "Password should not be null.")
    private String password;
}

package com.buyit.ecommerce.dto.request;

import com.buyit.ecommerce.anotations.ValidEmail;
import com.buyit.ecommerce.anotations.ValidPassword;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRegisterDTO {

    @NotBlank(message = "First name cannot be blank")
    @Size(max = 200, message = "First name cannot have more than 200 letters")
    @JsonProperty(value = "firstname")
    private String firstName;

    @NotBlank(message = "Last name cannot be blank")
    @Size(max = 200, message = "Last name cannot have more than 200 characters.")
    @JsonProperty(value = "lastname")
    private String lastName;

    @NotBlank(message = "Email cannot be blank.")
    @Size(max = 200, message = "Email cannot have more than 200 characters.")
    @JsonProperty(value = "email")
    @ValidEmail
    private String email;

    @NotBlank(message = "Username cannot be blank")
    @Size(min = 6, max = 50, message = "Username must have between 6 and 50 characters.")
    @JsonProperty(value = "username")
    private String userName;

    @NotBlank(message = "Password cannot be blank")
    @Size(min = 8, max = 20, message = "Your password must have between 8 and 20 characters")
    @JsonProperty(value = "password")
    @ValidPassword
    private String password;

    @JsonProperty("role")
    private String role;
}

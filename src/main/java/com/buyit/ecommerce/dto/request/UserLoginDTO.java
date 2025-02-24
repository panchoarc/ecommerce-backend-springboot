package com.buyit.ecommerce.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserLoginDTO {


    @NotBlank(message = "Username cannot be blank")
    @Size(min = 6, max = 50, message = "Username must have between 6 and 50 characters.")
    @JsonProperty(value = "username")
    private String userName;

    @NotBlank(message = "Password cannot be blank")
    @Size(min = 8, max = 20, message = "Your password must have between 8 and 20 characters")
    @JsonProperty(value = "password")
    private String password;
}

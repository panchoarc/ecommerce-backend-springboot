package com.buyit.ecommerce.dto.response.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoResponse {

    private String email;
    private String firstName;
    private String lastName;
    private String userName;

    private List<String> roles;

}

package com.buyit.ecommerce.service;

import com.buyit.ecommerce.dto.request.UserRegisterDTO;

public interface AuthService {

    void createUser(UserRegisterDTO userRegisterDTO);
}
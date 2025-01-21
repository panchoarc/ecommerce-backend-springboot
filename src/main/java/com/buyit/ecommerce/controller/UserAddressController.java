package com.buyit.ecommerce.controller;

import com.buyit.ecommerce.dto.request.address.CreateAddressRequest;
import com.buyit.ecommerce.dto.request.address.UpdateAddressRequest;
import com.buyit.ecommerce.dto.response.address.CreateAddressResponse;
import com.buyit.ecommerce.dto.response.address.UpdateAddressResponse;
import com.buyit.ecommerce.dto.response.address.UserAddressResponse;
import com.buyit.ecommerce.service.AddressService;
import com.buyit.ecommerce.service.UserService;
import com.buyit.ecommerce.util.ApiResponse;
import com.buyit.ecommerce.util.Pagination;
import com.buyit.ecommerce.util.ResponseBuilder;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/address")
@RequiredArgsConstructor
public class UserAddressController {

    private final AddressService addressService;
    private final UserService userService;


    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<List<UserAddressResponse>> getMyAddresses(@AuthenticationPrincipal Jwt user,
                                                                 @RequestParam(defaultValue = "0") int page,
                                                                 @RequestParam(defaultValue = "10") int size) {

        String keycloakId = userService.extractKeycloakIdFromUser(user);
        Page<UserAddressResponse> myAddresses = addressService.getMyAddresses(keycloakId, page, size);
        Pagination pagination = ResponseBuilder.buildPagination(myAddresses);
        return ResponseBuilder.successPaginated("My addresses found", myAddresses.getContent(), pagination);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<UserAddressResponse> getAddressById(@PathVariable("id") Long id,
                                                           @AuthenticationPrincipal Jwt user) {

        String keycloakId = userService.extractKeycloakIdFromUser(user);
        UserAddressResponse addressFound = addressService.getAddress(keycloakId, id);
        return ResponseBuilder.success("User address found", addressFound);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<CreateAddressResponse> createAddressForUser(@AuthenticationPrincipal Jwt user,
                                                                   @Valid @RequestBody CreateAddressRequest createAddressRequest) {

        String keycloakId = userService.extractKeycloakIdFromUser(user);
        CreateAddressResponse addressResponse = addressService.createAddress(keycloakId, createAddressRequest);
        return ResponseBuilder.success("Address created successfully", addressResponse);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<UpdateAddressResponse> updateAddress(@PathVariable("id") Long id,
                                                            @AuthenticationPrincipal Jwt user,
                                                            @Valid @RequestBody UpdateAddressRequest updateAddressRequest) {

        String keycloakId = userService.extractKeycloakIdFromUser(user);
        UpdateAddressResponse updateAddress = addressService.updateAddress(keycloakId, id, updateAddressRequest);
        return ResponseBuilder.success("Address updated successfully", updateAddress);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ApiResponse<Void> deleteAddress(@PathVariable("id") Long id) {
        addressService.deleteAddress(id);
        return ResponseBuilder.success("Address deleted successfully", null);
    }
}

package com.buyit.ecommerce.controller;

import com.buyit.ecommerce.anotations.RequirePermission;
import com.buyit.ecommerce.constants.PermissionsConstants;
import com.buyit.ecommerce.dto.request.address.CreateAddressRequest;
import com.buyit.ecommerce.dto.request.address.UpdateAddressRequest;
import com.buyit.ecommerce.dto.response.address.CreateAddressResponse;
import com.buyit.ecommerce.dto.response.address.UpdateAddressResponse;
import com.buyit.ecommerce.dto.response.address.UserAddressResponse;
import com.buyit.ecommerce.service.AddressService;
import com.buyit.ecommerce.service.UserService;
import com.buyit.ecommerce.util.Pagination;
import com.buyit.ecommerce.util.ResponseAPI;
import com.buyit.ecommerce.util.ResponseBuilder;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @RequirePermission(value = PermissionsConstants.ADDRESS_SEARCH)
    @PreAuthorize("hasAuthority('" + PermissionsConstants.ADDRESS_SEARCH + "')")
    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public ResponseAPI<List<UserAddressResponse>> getMyAddresses(@AuthenticationPrincipal Jwt jwt,
                                                                 @RequestParam(defaultValue = "0") int page,
                                                                 @RequestParam(defaultValue = "10") int size) {

        String keycloakId = jwt.getSubject();
        Page<UserAddressResponse> myAddresses = addressService.getMyAddresses(keycloakId, page, size);
        Pagination pagination = ResponseBuilder.buildPagination(myAddresses);
        return ResponseBuilder.successPaginated("My addresses found", myAddresses.getContent(), pagination);
    }

    @RequirePermission(value = PermissionsConstants.ADDRESS_GET_MY_ADDRESS)
    @PreAuthorize("hasAuthority('" + PermissionsConstants.ADDRESS_GET_MY_ADDRESS + "')")
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseAPI<UserAddressResponse> getAddressById(@PathVariable("id") Long id,
                                                           @AuthenticationPrincipal Jwt user) {

        String keycloakId = userService.extractKeycloakIdFromUser(user);
        UserAddressResponse addressFound = addressService.getAddress(keycloakId, id);
        return ResponseBuilder.success("User address found", addressFound);
    }


    @RequirePermission(value = PermissionsConstants.ADDRESS_CREATE)
    @PreAuthorize("hasAuthority('" + PermissionsConstants.ADDRESS_CREATE + "')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseAPI<CreateAddressResponse> createAddressForUser(@AuthenticationPrincipal Jwt user,
                                                                   @Valid @RequestBody CreateAddressRequest createAddressRequest) {

        String keycloakId = user.getSubject();
        CreateAddressResponse addressResponse = addressService.createAddress(keycloakId, createAddressRequest);
        return ResponseBuilder.success("Address created successfully", addressResponse);
    }

    @RequirePermission(value = PermissionsConstants.ADDRESS_UPDATE)
    @PreAuthorize("hasAuthority('" + PermissionsConstants.ADDRESS_UPDATE + "')")
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseAPI<UpdateAddressResponse> updateAddress(@PathVariable("id") Long id,
                                                            @AuthenticationPrincipal Jwt user,
                                                            @Valid @RequestBody UpdateAddressRequest updateAddressRequest) {

        String keycloakId = userService.extractKeycloakIdFromUser(user);
        UpdateAddressResponse updateAddress = addressService.updateAddress(keycloakId, id, updateAddressRequest);
        return ResponseBuilder.success("Address updated successfully", updateAddress);
    }

    @RequirePermission(value = PermissionsConstants.ADDRESS_DELETE)
    @PreAuthorize("hasAuthority('" + PermissionsConstants.ADDRESS_DELETE + "')")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseAPI<Void> deleteAddress(@PathVariable("id") Long id, @AuthenticationPrincipal Jwt user) {
        String keycloakId = userService.extractKeycloakIdFromUser(user);
        addressService.deleteAddress(keycloakId, id);
        return ResponseBuilder.success("Address deleted successfully", null);
    }
}

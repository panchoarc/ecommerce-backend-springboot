package com.buyit.ecommerce.service;

import com.buyit.ecommerce.dto.request.address.CreateAddressRequest;
import com.buyit.ecommerce.dto.request.address.UpdateAddressRequest;
import com.buyit.ecommerce.dto.response.address.CreateAddressResponse;
import com.buyit.ecommerce.dto.response.address.UpdateAddressResponse;
import com.buyit.ecommerce.dto.response.address.UserAddressResponse;
import com.buyit.ecommerce.entity.Address;
import org.springframework.data.domain.Page;

public interface AddressService {

    CreateAddressResponse createAddress(String keycloakUserId, CreateAddressRequest createAddressRequest);

    UserAddressResponse getAddress(String keycloakUserId, Long addressId);

    UpdateAddressResponse updateAddress(String keycloakId, Long id, UpdateAddressRequest updateAddressRequest);

    void deleteAddress(String keycloakId,Long id);

    Page<UserAddressResponse> getMyAddresses(String keycloakUserId, int page, int size);


    Address getMyAddress(String keycloakUserId, Long addressId);
}

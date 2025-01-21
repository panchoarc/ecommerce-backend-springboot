package com.buyit.ecommerce.service.impl;

import com.buyit.ecommerce.dto.request.address.CreateAddressRequest;
import com.buyit.ecommerce.dto.request.address.UpdateAddressRequest;
import com.buyit.ecommerce.dto.response.address.CreateAddressResponse;
import com.buyit.ecommerce.dto.response.address.UpdateAddressResponse;
import com.buyit.ecommerce.dto.response.address.UserAddressResponse;
import com.buyit.ecommerce.entity.Address;
import com.buyit.ecommerce.entity.User;
import com.buyit.ecommerce.exception.custom.ResourceNotFoundException;
import com.buyit.ecommerce.mapper.AddressMapper;
import com.buyit.ecommerce.repository.AddressRepository;
import com.buyit.ecommerce.service.AddressService;
import com.buyit.ecommerce.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;
    private final AddressMapper addressMapper;
    private final UserService userService;

    @Override
    public Page<UserAddressResponse> getMyAddresses(String keycloakId, int page, int size) {

        User dbUser = userService.getUserByKeycloakId(keycloakId);
        Pageable pageable = PageRequest.of(page, size);
        Page<Address> addressPage = addressRepository.findAllByUser(dbUser, pageable);

        return addressPage.map(addressMapper::toUserAddressResponseDTO);

    }

    @Override
    public Address getMyAddress(String keycloakUserId, Long id) {
        User dbUser = userService.getUserByKeycloakId(keycloakUserId);
        return getAddressByUserIdAndAddress(id, dbUser);

    }


    @Override
    public UserAddressResponse getAddress(String keycloakId, Long addressId) {

        User dbUser = userService.getUserByKeycloakId(keycloakId);
        Address userAddress = getAddressByUserIdAndAddress(addressId, dbUser);
        return addressMapper.toUserAddressResponseDTO(userAddress);

    }


    @Override
    public CreateAddressResponse createAddress(String keycloakId, CreateAddressRequest createAddressRequest) {

        User dbUser = userService.getUserByKeycloakId(keycloakId);

        Address createAddress = new Address();
        createAddress.setCity(createAddressRequest.getCity());
        createAddress.setCountry(createAddressRequest.getCountry());
        createAddress.setPostalCode(createAddressRequest.getPostalCode());
        createAddress.setStreet(createAddressRequest.getStreet());
        createAddress.setUser(dbUser);

        Address savedAddress = addressRepository.save(createAddress);

        return addressMapper.createAddressResponseDTO(savedAddress);
    }


    @Override
    public UpdateAddressResponse updateAddress(String keycloakId, Long id, UpdateAddressRequest updateAddressRequest) {

        User dbUser = userService.getUserByKeycloakId(keycloakId);
        Address address = getAddresByUserIdAndAddress(id);

        address.setUser(dbUser);
        address.setCity(updateAddressRequest.getCity());
        address.setCountry(updateAddressRequest.getCountry());
        address.setPostalCode(updateAddressRequest.getPostalCode());
        address.setStreet(updateAddressRequest.getStreet());

        Address updatedAddress = addressRepository.save(address);

        return addressMapper.toUpdatedResponseDTO(updatedAddress);


    }

    @Override
    public void deleteAddress(Long id) {
        Address dbAddress = getAddresByUserIdAndAddress(id);
        addressRepository.delete(dbAddress);
    }

    private Address getAddresByUserIdAndAddress(Long id) {
        return addressRepository.findById(id).
                orElseThrow(() -> new ResourceNotFoundException("Address not found"));
    }

    private Address getAddressByUserIdAndAddress(Long addressId, User dbUser) {
        return addressRepository.findByUserAndAddressId(dbUser, addressId).
                orElseThrow(() -> new ResourceNotFoundException("User address not found"));
    }


}

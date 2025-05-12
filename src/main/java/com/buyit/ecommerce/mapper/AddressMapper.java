package com.buyit.ecommerce.mapper;

import com.buyit.ecommerce.dto.response.address.CreateAddressResponse;
import com.buyit.ecommerce.dto.response.address.UpdateAddressResponse;
import com.buyit.ecommerce.dto.response.address.UserAddressResponse;
import com.buyit.ecommerce.entity.Address;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AddressMapper {

    @Mapping(source = "addressId", target = "id")
    UserAddressResponse toUserAddressResponseDTO(Address address);

    @Mapping(source = "addressId", target = "id")
    CreateAddressResponse createAddressResponseDTO(Address address);

    @Mapping(source = "addressId", target = "id")
    UpdateAddressResponse toUpdatedResponseDTO(Address updatedAddress);
}

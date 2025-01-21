package com.buyit.ecommerce.repository;

import com.buyit.ecommerce.entity.Address;
import com.buyit.ecommerce.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {

    Page<Address> findAllByUser(User user, Pageable pageable);

    Optional<Address> findByUserAndAddressId(User user, Long addressId);
}

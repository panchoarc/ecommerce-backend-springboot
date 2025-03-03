package com.buyit.ecommerce.repository;

import com.buyit.ecommerce.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsersRepository extends JpaRepository<User, Long> {

    Optional<User> findByKeycloakUserId(String keycloakUserId);

    Optional<User> findByEmailOrUserName(String email, String userName);

    Optional<User> findByEmail(String email);

    Optional<User> findByUserName(String userName);
}

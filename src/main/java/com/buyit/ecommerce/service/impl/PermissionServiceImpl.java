package com.buyit.ecommerce.service.impl;

import com.buyit.ecommerce.repository.PermissionRepository;
import com.buyit.ecommerce.service.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionService {


    private final PermissionRepository permissionRepository;
    @Override
    public boolean hasPermission(String userId, String permissionCode) {

        return permissionRepository.hasPermission(userId, permissionCode);

    }
}

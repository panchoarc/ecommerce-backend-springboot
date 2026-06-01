package com.buyit.ecommerce.service;

public interface PermissionService {

    boolean hasPermission(String userId,String permissionCode);
}

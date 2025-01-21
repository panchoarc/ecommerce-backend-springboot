package com.buyit.ecommerce.service;

import java.util.Collection;

public interface PermissionsService {


    boolean hasAccess(String endpointUrl, String method, Collection<String> userRoles);
}

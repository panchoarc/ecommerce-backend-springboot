package com.buyit.ecommerce.startup;

import com.buyit.ecommerce.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class RoleSyncRunner{

    private final RoleService roleService;

    @EventListener(ApplicationReadyEvent.class)
    public void run(){

        roleService.syncKeycloakRoles();
        roleService.assignAdminPermissions();

    }
}

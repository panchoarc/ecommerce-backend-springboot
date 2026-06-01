package com.buyit.ecommerce.controller;


import com.buyit.ecommerce.service.PermissionService;
import com.buyit.ecommerce.util.ResponseAPI;
import com.buyit.ecommerce.util.ResponseBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController("/permissions")
@RequiredArgsConstructor
@Slf4j
public class PermissionsController {

    private final PermissionService permissionService;

    @GetMapping
    public ResponseAPI<String> getAllPermissions(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        return ResponseBuilder.success("Permissions resolved", "");
    }
}

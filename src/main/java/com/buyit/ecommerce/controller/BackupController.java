package com.buyit.ecommerce.controller;

import com.buyit.ecommerce.service.DatabaseService;
import com.buyit.ecommerce.util.ApiResponse;
import com.buyit.ecommerce.util.ResponseBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/backup")
@RequiredArgsConstructor
public class BackupController {

    private final DatabaseService databaseService;

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<String> createBackup() throws IOException, InterruptedException {

        String urlBackup = databaseService.createDatabaseBackup();
        return ResponseBuilder.success("Backup created successfully", urlBackup);
    }
}

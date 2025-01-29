package com.buyit.ecommerce.controller;

import com.buyit.ecommerce.service.BackupService;
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

    private final BackupService backupService;


    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public String createBackup() throws IOException, InterruptedException {
        return backupService.createDatabaseBackup();
    }

}

package com.buyit.ecommerce.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileService {

    String uploadFile(MultipartFile file) throws IOException;

    void deleteFile(String url) throws IOException;
}

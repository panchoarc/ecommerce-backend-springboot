package com.buyit.ecommerce.service;

import com.google.zxing.WriterException;

import java.io.IOException;

public interface QRCodeService {


    String generateQRCodeImage(String url) throws WriterException, IOException;
}

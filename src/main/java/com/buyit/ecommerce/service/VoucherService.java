package com.buyit.ecommerce.service;

import com.google.zxing.WriterException;

import java.io.IOException;

public interface VoucherService {


    byte[] generateVoucher(String keycloakUserId,String orderNumber) throws IOException, WriterException;
}

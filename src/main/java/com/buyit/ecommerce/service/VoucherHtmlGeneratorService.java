package com.buyit.ecommerce.service;

import com.buyit.ecommerce.dto.response.order.OrderDetailsDTO;

public interface VoucherHtmlGeneratorService {


    String generateVoucherHtml(OrderDetailsDTO orderDetailsDTO, String qrCode);
}

package com.buyit.ecommerce.service;

import java.io.IOException;

public interface PdfGeneratorService {

    byte[] generateFromHtml(String html) throws IOException;
}

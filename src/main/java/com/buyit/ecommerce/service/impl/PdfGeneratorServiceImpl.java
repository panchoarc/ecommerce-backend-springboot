package com.buyit.ecommerce.service.impl;

import com.buyit.ecommerce.service.PdfGeneratorService;
import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
@RequiredArgsConstructor
@Slf4j
public class PdfGeneratorServiceImpl implements PdfGeneratorService {

    @Override
    public byte[] generateFromHtml(String html) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            ConverterProperties converterProperties = new ConverterProperties();
            converterProperties.setBaseUri("classpath:/templates/");
            HtmlConverter.convertToPdf(html, outputStream, converterProperties);
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Error generando el PDF", e);
        }
    }
}

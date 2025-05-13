package com.buyit.ecommerce.dto.request.product;


import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ImageUploadWrapper {

    @Valid
    private List<ImageUploadForm> images;
}

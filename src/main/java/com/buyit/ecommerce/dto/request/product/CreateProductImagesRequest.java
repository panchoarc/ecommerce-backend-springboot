package com.buyit.ecommerce.dto.request.product;


import com.buyit.ecommerce.anotations.ValidImage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateProductImagesRequest implements Serializable {

    @ValidImage
    private transient MultipartFile[] images;
}

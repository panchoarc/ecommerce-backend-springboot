package com.buyit.ecommerce.dto.request.product;


import com.buyit.ecommerce.anotations.ValidImage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ImageUploadForm {

    @ValidImage
    private MultipartFile image;

    private Boolean isMain;
}

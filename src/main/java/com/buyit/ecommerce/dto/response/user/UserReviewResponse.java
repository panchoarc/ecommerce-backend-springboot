package com.buyit.ecommerce.dto.response.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserReviewResponse {
    private Long id;
    private String fullName;
    private String email;
}

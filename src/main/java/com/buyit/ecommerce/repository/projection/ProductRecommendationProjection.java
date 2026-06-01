package com.buyit.ecommerce.repository.projection;

import java.math.BigDecimal;

public interface ProductRecommendationProjection {

    Long getId();
    String getName();
    String getDescription();
    BigDecimal getPrice();
    Integer getStock();
    String getImg();
    Double getRating();

}
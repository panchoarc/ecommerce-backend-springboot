package com.buyit.ecommerce.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Pagination {
    private Integer totalCount;
    private Long totalPages;
    private Integer currentPage;
    private Integer pageSize;
}

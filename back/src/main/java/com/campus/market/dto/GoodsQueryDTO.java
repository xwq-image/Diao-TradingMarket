package com.campus.market.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class GoodsQueryDTO {
    private String keyword;
    private String category;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private String condition;
    private String status;
    private String sortBy = "latest";
    private Integer page = 1;
    private Integer size = 12;
}

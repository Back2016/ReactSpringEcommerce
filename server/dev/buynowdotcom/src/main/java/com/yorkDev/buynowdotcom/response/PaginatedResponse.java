package com.yorkDev.buynowdotcom.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class PaginatedResponse<T> {
    private List<T> items;
    private int currentPage;
    private int totalPages;
    private long totalItems;
}

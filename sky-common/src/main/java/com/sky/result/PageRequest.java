package com.sky.result;

import lombok.Data;

@Data
public class PageRequest {
    public int current=1;
    public int pageSize=10;
    private String sortField;
    private String  sortOrder="ASC";
}

package com.rootcode.backend.dto.request;

import jakarta.validation.constraints.Min;


public class GetAllBooksRequestDTO {

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    @Min(0)
    private int page = 0;

    @Min(1)
    private int size = 10;
    private String sortBy = "title";

    private String direction = "asc";
}

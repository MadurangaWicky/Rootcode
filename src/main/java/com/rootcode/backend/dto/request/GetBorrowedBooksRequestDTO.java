package com.rootcode.backend.dto.request;

import jakarta.validation.constraints.Min;


//Without creating more APIs, to do all in one, this DTO can be used to get borrowed books by user in any combination and any sorting.
public class GetBorrowedBooksRequestDTO {

    @Min(0)
    private int page = 0;

    @Min(1)
    private int size = 10;

    private String sortBy = "borrowedAt";
    private String direction = "desc";

    private Boolean isReturned;


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

    public Boolean getIsReturned() {
        return isReturned;
    }

    public void setIsReturned(Boolean isReturned) {
        this.isReturned = isReturned;
    }
}

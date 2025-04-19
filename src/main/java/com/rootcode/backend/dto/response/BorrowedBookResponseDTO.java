package com.rootcode.backend.dto.response;

import java.time.Instant;

public class BorrowedBookResponseDTO {
    private Long borrowId;

    public BorrowedBookResponseDTO(Long borrowId, Long bookId, String title, String author, int publishedYear, Instant borrowedAt, Instant returnedAt, Boolean isReturned) {
        this.borrowId = borrowId;
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.publishedYear = publishedYear;
        this.borrowedAt = borrowedAt;
        this.returnedAt = returnedAt;
        this.isReturned = isReturned;
    }

    private Long bookId;
    private String title;
    private String author;
    private int publishedYear;

    private Instant borrowedAt;
    private Instant returnedAt;

    private Boolean isReturned;

    // Getters and Setters

    public Long getBorrowId() {
        return borrowId;
    }

    public void setBorrowId(Long borrowId) {
        this.borrowId = borrowId;
    }

    public Long getBookId() {
        return bookId;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getPublishedYear() {
        return publishedYear;
    }

    public void setPublishedYear(int publishedYear) {
        this.publishedYear = publishedYear;
    }

    public Instant getBorrowedAt() {
        return borrowedAt;
    }

    public void setBorrowedAt(Instant borrowedAt) {
        this.borrowedAt = borrowedAt;
    }

    public Instant getReturnedAt() {
        return returnedAt;
    }

    public void setReturnedAt(Instant returnedAt) {
        this.returnedAt = returnedAt;
    }

    public Boolean getIsReturned() {
        return isReturned;
    }

    public void setIsReturned(Boolean isReturned) {
        this.isReturned = isReturned;
    }
}

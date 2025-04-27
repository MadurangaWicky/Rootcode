package com.rootcode.backend.repository;

import com.rootcode.backend.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    @Query("SELECT b FROM Book b WHERE b.availableCopies > 0")
    Page<Book> findAllAvailableBooks(Pageable pageable);

    @Query("SELECT b FROM Book b WHERE b.availableCopies > 0 " +
            "AND LOWER(b.author) LIKE LOWER(CONCAT('%', ?1, '%'))")
    Page<Book> searchByAuthor(String author, Pageable pageable);

    @Query("SELECT b FROM Book b WHERE b.availableCopies > 0 " +
            "AND LOWER(b.title) LIKE LOWER(CONCAT('%', ?1, '%'))")
    Page<Book> searchByTitle(String title, Pageable pageable);

    @Query("SELECT b FROM Book b WHERE b.availableCopies > 0 " +
            "AND b.publishedYear = ?1")
    Page<Book> searchByYear(Integer year, Pageable pageable);

    @Query("SELECT b FROM Book b WHERE b.availableCopies > 0 " +
            "AND b.publishedYear BETWEEN ?1 AND ?2")
    Page<Book> searchByYearRange(Integer startYear, Integer endYear, Pageable pageable);

    @Query("SELECT b FROM Book b WHERE b.availableCopies > 0 " +
            "AND LOWER(b.author) LIKE LOWER(CONCAT('%', ?1, '%')) " +
            "AND LOWER(b.title) LIKE LOWER(CONCAT('%', ?2, '%'))")
    Page<Book> searchByAuthorAndTitle(String author, String title, Pageable pageable);

    @Query("SELECT b FROM Book b WHERE b.availableCopies > 0 " +
            "AND LOWER(b.author) LIKE LOWER(CONCAT('%', ?1, '%')) " +
            "AND b.publishedYear = ?2")
    Page<Book> searchByAuthorAndYear(String author, Integer year, Pageable pageable);

    @Query("SELECT b FROM Book b WHERE b.availableCopies > 0 " +
            "AND LOWER(b.author) LIKE LOWER(CONCAT('%', ?1, '%')) " +
            "AND b.publishedYear BETWEEN ?2 AND ?3")
    Page<Book> searchByAuthorAndYearRange(String author, Integer startYear, Integer endYear, Pageable pageable);

    @Query("SELECT b FROM Book b WHERE b.availableCopies > 0 " +
            "AND LOWER(b.title) LIKE LOWER(CONCAT('%', ?1, '%')) " +
            "AND b.publishedYear = ?2")
    Page<Book> searchByTitleAndYear(String title, Integer year, Pageable pageable);

    @Query("SELECT b FROM Book b WHERE b.availableCopies > 0 " +
            "AND LOWER(b.title) LIKE LOWER(CONCAT('%', ?1, '%')) " +
            "AND b.publishedYear BETWEEN ?2 AND ?3")
    Page<Book> searchByTitleAndYearRange(String title, Integer startYear, Integer endYear, Pageable pageable);

    @Query("SELECT b FROM Book b WHERE b.availableCopies > 0 " +
            "AND LOWER(b.author) LIKE LOWER(CONCAT('%', ?1, '%')) " +
            "AND LOWER(b.title) LIKE LOWER(CONCAT('%', ?2, '%')) " +
            "AND b.publishedYear = ?3")
    Page<Book> searchByAuthorTitleAndYear(String author, String title, Integer year, Pageable pageable);

    @Query("SELECT b FROM Book b WHERE b.availableCopies > 0 " +
            "AND LOWER(b.author) LIKE LOWER(CONCAT('%', ?1, '%')) " +
            "AND LOWER(b.title) LIKE LOWER(CONCAT('%', ?2, '%')) " +
            "AND b.publishedYear BETWEEN ?3 AND ?4")
    Page<Book> searchByAuthorTitleAndYearRange(String author, String title, Integer startYear, Integer endYear, Pageable pageable);
}

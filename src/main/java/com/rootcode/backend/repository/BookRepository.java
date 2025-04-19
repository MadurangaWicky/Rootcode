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

    @Query("SELECT b FROM Book b WHERE b.availableCopies > 0 " +
            "AND (:author IS NULL OR LOWER(b.author) LIKE LOWER(CONCAT('%', :author, '%'))) " +
            "AND (:year IS NULL OR b.publishedYear = :year)")
    Page<Book> searchAvailableBooks(@Param("author") String author,
                                    @Param("year") Integer publishedYear,
                                    Pageable pageable);


}

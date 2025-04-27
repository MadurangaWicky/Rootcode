package com.rootcode.backend.repository;

import com.rootcode.backend.entity.Book;
import com.rootcode.backend.entity.BorrowRecord;
import com.rootcode.backend.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BorrowedRecordRepository extends JpaRepository<BorrowRecord, Long> {
    long countByBookAndIsReturnedFalse(Book book);
    Page<BorrowRecord> findAllByUserId(Long userId, Pageable pageable);

    Page<BorrowRecord> findAllByUserIdAndIsReturned(Long userId, Boolean isReturned, Pageable pageable);

    Page<BorrowRecord> findByUserId(Long userId, Pageable pageable);

    Page<BorrowRecord> findByUserIdAndIsReturned(Long userId, Boolean isReturned, Pageable pageable);

    Optional<BorrowRecord> findByIdAndUser(Long id, User user);





}

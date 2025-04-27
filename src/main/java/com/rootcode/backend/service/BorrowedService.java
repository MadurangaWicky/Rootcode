package com.rootcode.backend.service;


import com.rootcode.backend.dto.request.GetBorrowedBooksRequestDTO;
import com.rootcode.backend.dto.request.SearchBooksRequestDTO;
import com.rootcode.backend.dto.response.BookResponseDTO;
import com.rootcode.backend.dto.response.BorrowedBookResponseDTO;
import com.rootcode.backend.entity.Book;
import com.rootcode.backend.entity.BorrowRecord;
import com.rootcode.backend.entity.User;
import com.rootcode.backend.exception.CustomException;
import com.rootcode.backend.repository.BookRepository;
import com.rootcode.backend.repository.BorrowedRecordRepository;
import com.rootcode.backend.utility.errorcodes.CommonErrorCodes;
import org.slf4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class BorrowedService {
    private final BorrowedRecordRepository borrowedRecordRepository;
    private final BookRepository bookRepository;
    private Logger logger = org.slf4j.LoggerFactory.getLogger(BookService.class);

    public BorrowedService(BorrowedRecordRepository borrowedRecordRepository, BookRepository bookRepository) {
        this.borrowedRecordRepository = borrowedRecordRepository;
        this.bookRepository = bookRepository;
    }

    //This is for get by current authenticated user
    public Page<BorrowedBookResponseDTO> getBorrowedBooksByUser(User user, GetBorrowedBooksRequestDTO dto) {
        try {
            logger.info("Getting borrowed books by current user");
            logger.info("User: {}", user.getUsername());
            Sort.Direction sortDirection = dto.getDirection().equalsIgnoreCase("desc")
                    ? Sort.Direction.DESC
                    : Sort.Direction.ASC;

            Pageable pageable = PageRequest.of(
                    dto.getPage(),
                    dto.getSize(),
                    Sort.by(sortDirection, dto.getSortBy())
            );

            Page<BorrowRecord> records;

            if (dto.getIsReturned() != null) {
                records = borrowedRecordRepository.findAllByUserIdAndIsReturned(user.getId(), dto.getIsReturned(), pageable);
            } else {
                records = borrowedRecordRepository.findAllByUserId(user.getId(), pageable);
            }

            return records.map(record -> new BorrowedBookResponseDTO(
                    record.getId(),
                    record.getBook().getId(),
                    record.getBook().getTitle(),
                    record.getBook().getAuthor(),
                    record.getBook().getPublishedYear(),
                    record.getBorrowedAt(),
                    record.getReturnedAt(),
                    record.getReturned()
            ));

        }
        catch (Exception e) {
            logger.error("Error getting borrowed books by current user", e);
            throw new CustomException(CommonErrorCodes.INTERNAL_SERVER_ERROR, "Internal Server Error");
        }

    }

//This is for get by using a user id (for the librarian)
    public Page<BorrowedBookResponseDTO> getBorrowedBooksByUserId(Long userId, GetBorrowedBooksRequestDTO requestDTO) {
        try {
            logger.info("Getting borrowed books by user id {}", userId);

            Sort.Direction direction = requestDTO.getDirection().equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
            Pageable pageable = PageRequest.of(requestDTO.getPage(), requestDTO.getSize(), Sort.by(direction, requestDTO.getSortBy()));

            Page<BorrowRecord> records;

            if (requestDTO.getIsReturned() != null) {
                records = borrowedRecordRepository.findByUserIdAndIsReturned(userId, requestDTO.getIsReturned(), pageable);
            } else {
                records = borrowedRecordRepository.findByUserId(userId, pageable);
            }

            return records.map(record -> new BorrowedBookResponseDTO(
                    record.getId(),
                    record.getBook().getId(),
                    record.getBook().getTitle(),
                    record.getBook().getAuthor(),
                    record.getBook().getPublishedYear(),
                    record.getBorrowedAt(),
                    record.getReturnedAt(),
                    record.getReturned()
            ));
        }
        catch (Exception e) {
            logger.error("Error getting borrowed books by user id {}", userId, e);
            throw new CustomException(CommonErrorCodes.INTERNAL_SERVER_ERROR, "Internal Server Error");
        }
    }



    public Page<BookResponseDTO> searchBooks(SearchBooksRequestDTO dto) {
        try {
            if (dto.getPublishedYear() == null) {
                dto.setPublishedYear(0);
            }
            if (dto.getAuthor() == null) {
                dto.setAuthor("");
            }
            if (dto.getTitle() == null) {
                dto.setTitle("");
            }
            if (dto.getDirection() == null) {
                dto.setDirection("asc");
            }
            if (dto.getSortBy() == null) {
                dto.setSortBy("title");
            }
            if (dto.getPage() < 0) {
                dto.setPage(0);
            }
            if (dto.getSize() < 1) {
                dto.setSize(10);
            }

            Sort.Direction direction = dto.getDirection().equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
            Pageable pageable = PageRequest.of(dto.getPage(), dto.getSize(), Sort.by(direction, dto.getSortBy()));

            Page<Book> books;

            if (!dto.getAuthor().isEmpty() && !dto.getTitle().isEmpty() && dto.getPublishedYear() > 0) {
                books = bookRepository.searchByAuthorTitleAndYear(dto.getAuthor(), dto.getTitle(), dto.getPublishedYear(), pageable);
            } else if (!dto.getAuthor().isEmpty() && !dto.getTitle().isEmpty()) {
                books = bookRepository.searchByAuthorAndTitle(dto.getAuthor(), dto.getTitle(), pageable);
            } else if (!dto.getAuthor().isEmpty() && dto.getPublishedYear() > 0) {
                books = bookRepository.searchByAuthorAndYear(dto.getAuthor(), dto.getPublishedYear(), pageable);
            } else if (!dto.getTitle().isEmpty() && dto.getPublishedYear() > 0) {
                books = bookRepository.searchByTitleAndYear(dto.getTitle(), dto.getPublishedYear(), pageable);
            } else if (!dto.getAuthor().isEmpty()) {
                books = bookRepository.searchByAuthor(dto.getAuthor(), pageable);
            } else if (!dto.getTitle().isEmpty()) {
                books = bookRepository.searchByTitle(dto.getTitle(), pageable);
            } else if (dto.getPublishedYear() > 0) {
                books = bookRepository.searchByYear(dto.getPublishedYear(), pageable);
            } else {
                books = bookRepository.findAllAvailableBooks(pageable);
            }

            return books.map(book -> new BookResponseDTO(
                    book.getId(),
                    book.getTitle(),
                    book.getAuthor(),
                    book.getPublishedYear(),
                    book.getAvailableCopies()
            ));
        } catch (Exception e) {
            logger.error("Error searching books", e);
            throw new CustomException(CommonErrorCodes.INTERNAL_SERVER_ERROR, "Internal Server Error");
        }
    }


    @Transactional
    public void borrowBook(User user, Long bookId) {
        try {
            logger.info("Borrowing book with id {}", bookId);
            Book book = bookRepository.findById(bookId)
                    .orElseThrow(() -> new CustomException(CommonErrorCodes.BOOK_NOT_FOUND_ERROR_CODE, "Book not found"));

            if (book.getAvailableCopies() <= 0) {
                logger.error("No available copies to borrow");
                throw new CustomException(CommonErrorCodes.NO_COPIES_AVAILABLE, "No available copies to borrow");
            }

            BorrowRecord record = new BorrowRecord();
            record.setUser(user);
            record.setBook(book);
            record.setBorrowedAt(Instant.now());
            record.setReturned(false);

            book.setAvailableCopies(book.getAvailableCopies() - 1);

            borrowedRecordRepository.save(record);
            bookRepository.save(book);
        }
        catch (CustomException e) {
            logger.error("Error borrowing book with id {}", bookId, e);
            throw e;
        }
        catch (Exception e) {
            logger.error("Error borrowing book with id {}", bookId, e);
            throw new CustomException(CommonErrorCodes.INTERNAL_SERVER_ERROR, "Internal Server Error");
        }
    }


    @Transactional
    public void returnBook(User user, Long borrowId) {
        try {
            logger.info("Returning book with id {}", borrowId);
            BorrowRecord record = borrowedRecordRepository.findByIdAndUser(borrowId, user)
                    .orElseThrow(() -> new CustomException(CommonErrorCodes.BORROWED_RECORD_NOT_FOUND_ERROR_CODE, "Borrow record not found"));

            if (record.getReturned()) {
                logger.error("Book already returned");
                throw new CustomException(CommonErrorCodes.BOOK_ALREADY_RETURNED_ERROR, "Book already returned");
            }

            record.setReturned(true);
            record.setReturnedAt(Instant.now());

            Book book = record.getBook();
            book.setAvailableCopies(book.getAvailableCopies() + 1);

            borrowedRecordRepository.save(record);
            bookRepository.save(book);
        }

        catch (CustomException e) {
            logger.error("Error returning book with id {}", borrowId, e);
            logger.error("Error returning book with id {}", borrowId, e);
            throw e;
        }
        catch (Exception e) {
            logger.error("Error returning book with id {}", borrowId, e);
            throw new CustomException(CommonErrorCodes.INTERNAL_SERVER_ERROR, "Internal Server Error");
        }
    }






}

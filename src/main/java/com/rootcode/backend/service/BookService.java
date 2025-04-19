package com.rootcode.backend.service;

import com.rootcode.backend.dto.request.GetAllBooksRequestDTO;
import com.rootcode.backend.dto.request.SaveBookRequestDTO;
import com.rootcode.backend.dto.request.UpdateBookRequestDTO;
import com.rootcode.backend.entity.Book;
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

import java.util.Optional;

@Service
public class BookService {
    private final BookRepository bookRepository;
    private final BorrowedRecordRepository borrowedRecordRepository;

    private Logger logger = org.slf4j.LoggerFactory.getLogger(BookService.class);

    public BookService(BookRepository bookRepository, BorrowedRecordRepository borrowedRecordRepository) {
        this.bookRepository = bookRepository;
        this.borrowedRecordRepository = borrowedRecordRepository;
    }

    public Book saveBook(SaveBookRequestDTO saveBookRequestDTO) {
        try {
            logger.info("Saving book : {}", saveBookRequestDTO.getTitle());
            Book newBook = new Book();
            newBook.setTitle(saveBookRequestDTO.getTitle());
            newBook.setAuthor(saveBookRequestDTO.getAuthor());
            newBook.setPublishedYear(saveBookRequestDTO.getPublishedYear());
            newBook.setAvailableCopies(saveBookRequestDTO.getAvailableCopies());
            return bookRepository.save(newBook);
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new CustomException(CommonErrorCodes.INTERNAL_SERVER_ERROR, "Internal Server Error");

        }
    }

    public Book updateBook(UpdateBookRequestDTO updateBookRequestDTO) {
        try {
            logger.info("Updating book : {}", updateBookRequestDTO.getId());
            Optional<Book> optionalBook = bookRepository.findById(updateBookRequestDTO.getId());

            if (optionalBook.isEmpty()) {
                logger.error("Book not found for id : {}", updateBookRequestDTO.getId());
                throw new CustomException(CommonErrorCodes.BOOK_NOT_FOUND_ERROR_CODE, "Book not found");
            }

            Book existingBook = optionalBook.get();

            if (updateBookRequestDTO.getTitle() != null) {
                existingBook.setTitle(updateBookRequestDTO.getTitle());
            }

            if (updateBookRequestDTO.getAuthor() != null) {
                existingBook.setAuthor(updateBookRequestDTO.getAuthor());
            }

            if (updateBookRequestDTO.getPublishedYear() != null) {
                existingBook.setPublishedYear(updateBookRequestDTO.getPublishedYear());
            }

            if (updateBookRequestDTO.getAvailableCopies() != null) {
                existingBook.setAvailableCopies(updateBookRequestDTO.getAvailableCopies());
            }

            return bookRepository.save(existingBook);

        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new CustomException(CommonErrorCodes.INTERNAL_SERVER_ERROR, "Internal Server Error");
        }
    }


    //here i'm assume books can't delete if they are still borrowed

    public boolean deleteBook(Long id) {
        try {
            logger.info("Deleting book: {}", id);

            Optional<Book> optionalBook = bookRepository.findById(id);
            if (optionalBook.isEmpty()) {
                throw new CustomException(CommonErrorCodes.BOOK_NOT_FOUND_ERROR_CODE, "Book not found");
            }

            Book book = optionalBook.get();

            long borrowedCount = borrowedRecordRepository.countByBookAndIsReturnedFalse(book);

            if (borrowedCount > 0) {
                throw new CustomException(CommonErrorCodes.BOOK_COPIES_STILL_BORROWED, "Cannot delete book while copies are still borrowed");
            }
            bookRepository.delete(book);
            return true;
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new CustomException(CommonErrorCodes.INTERNAL_SERVER_ERROR, "Internal Server Error");
        }
    }

    public Page<Book> getAllBooks(GetAllBooksRequestDTO requestDTO) {
        try {
            logger.info("Getting all books");
            Sort.Direction sortDirection = requestDTO.getDirection().equalsIgnoreCase("desc")
                    ? Sort.Direction.DESC
                    : Sort.Direction.ASC;

            Pageable pageable = PageRequest.of(
                    requestDTO.getPage(),
                    requestDTO.getSize(),
                    Sort.by(sortDirection, requestDTO.getSortBy())
            );

            return bookRepository.findAll(pageable);

        } catch (Exception e) {
            throw new CustomException(CommonErrorCodes.INTERNAL_SERVER_ERROR, "Internal Server Error");
        }
    }







}

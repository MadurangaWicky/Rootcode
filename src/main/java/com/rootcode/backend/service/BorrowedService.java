package com.rootcode.backend.service;


import com.rootcode.backend.dto.request.GetBorrowedBooksRequestDTO;
import com.rootcode.backend.dto.response.BorrowedBookResponseDTO;
import com.rootcode.backend.entity.BorrowRecord;
import com.rootcode.backend.entity.User;
import com.rootcode.backend.exception.CustomException;
import com.rootcode.backend.repository.BorrowedRecordRepository;
import com.rootcode.backend.utility.errorcodes.CommonErrorCodes;
import org.slf4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class BorrowedService {
    private final BorrowedRecordRepository borrowedRecordRepository;
    private Logger logger = org.slf4j.LoggerFactory.getLogger(BookService.class);

    public BorrowedService(BorrowedRecordRepository borrowedRecordRepository) {
        this.borrowedRecordRepository = borrowedRecordRepository;
    }

    //This is for get by current authenticated user
    public Page<BorrowedBookResponseDTO> getBorrowedBooksByUser(User user, GetBorrowedBooksRequestDTO dto) {
        try {
            logger.info("Getting borrowed books by current user");
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
                records = borrowedRecordRepository.findAllByUserIdAndReturned(user.getId(), dto.getIsReturned(), pageable);
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

            Sort.Direction direction = requestDTO.getDirection().equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
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







}

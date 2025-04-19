package com.rootcode.backend.controller;

import com.rootcode.backend.dto.response.StandardResponse;
import com.rootcode.backend.entity.User;
import com.rootcode.backend.service.BorrowedService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/borrowed")
public class BorrowedController {

    private final BorrowedService borrowedService;


    public BorrowedController(BorrowedService borrowedService) {
        this.borrowedService = borrowedService;
    }

    @PostMapping("/borrow")
    public ResponseEntity<StandardResponse> borrowBook(@RequestParam Long bookId, @AuthenticationPrincipal User currentUser) {
        borrowedService.borrowBook(currentUser, bookId);
        return ResponseEntity.ok(new StandardResponse(true, "Book borrowed successfully"));
    }


    @PostMapping("/return")
    public ResponseEntity<StandardResponse> returnBook(@RequestParam Long borrowId, @AuthenticationPrincipal User currentUser) {
        borrowedService.returnBook(currentUser, borrowId);
        return ResponseEntity.ok(new StandardResponse(true, "Book returned successfully"));
    }





}

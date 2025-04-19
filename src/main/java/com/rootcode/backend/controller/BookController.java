package com.rootcode.backend.controller;


import com.rootcode.backend.dto.request.GetAllBooksRequestDTO;
import com.rootcode.backend.dto.request.SaveBookRequestDTO;
import com.rootcode.backend.dto.request.UpdateBookRequestDTO;
import com.rootcode.backend.dto.response.StandardResponse;
import com.rootcode.backend.entity.Book;
import com.rootcode.backend.service.BookService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/book")
public class BookController {
    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @PostMapping("/saveBook")
    public ResponseEntity<StandardResponse> saveBook(@Valid @RequestBody SaveBookRequestDTO saveBookRequestDTO) {
        StandardResponse response = new StandardResponse(true, bookService.saveBook(saveBookRequestDTO));
        return ResponseEntity.ok().body(response);
    }

    @PutMapping("/updateBook")
    public ResponseEntity<StandardResponse> updateBook(@Valid @RequestBody UpdateBookRequestDTO updateBookRequestDTO) {
        StandardResponse response = new StandardResponse(true, bookService.updateBook(updateBookRequestDTO));
        return ResponseEntity.ok().body(response);
    }


    @DeleteMapping("/deleteBook/{id}")
    public ResponseEntity<StandardResponse> deleteBook(@PathVariable Long id) {
        StandardResponse response = new StandardResponse(bookService.deleteBook(id), "Book deleted from DB");
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/books/list")
    public ResponseEntity<StandardResponse> getAllBooks(@RequestBody @Valid GetAllBooksRequestDTO requestDTO) {
        Page<Book> books = bookService.getAllBooks(requestDTO);
        StandardResponse response = new StandardResponse(true, books);
        return ResponseEntity.ok(response);
    }


}

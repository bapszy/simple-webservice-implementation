package com.library.controller;

import com.library.model.Book;
import com.library.service.LibraryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/books")
public class BookController {

    private final LibraryService libraryService;

    public BookController(LibraryService libraryService) {
        this.libraryService = libraryService;
    }

    @GetMapping
    public List getAllBooks() {
        return libraryService.getAllBooks();
    }

    @PostMapping
    public Book addBook(@RequestBody Book book) {
        return libraryService.addBook(book);
    }

    @PostMapping("/{bookId}/borrow")
    public Book borrowBook(@PathVariable Long bookId, @RequestParam Long borrowerId) {
        return libraryService.borrowBook(bookId, borrowerId);
    }
}
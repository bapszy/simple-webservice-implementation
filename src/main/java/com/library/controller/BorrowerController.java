package com.library.controller;

import com.library.model.Book;
import com.library.model.Borrower;
import com.library.service.LibraryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/borrowers")
public class BorrowerController {

    private final LibraryService libraryService;

    public BorrowerController(LibraryService libraryService) {
        this.libraryService = libraryService;
    }

    @PostMapping
    public Borrower createBorrower(@RequestBody Borrower borrower) {
        return libraryService.createBorrower(borrower);
    }

    @GetMapping("/{id}")
    public Borrower getBorrower(@PathVariable Long id) {
        return libraryService.getBorrower(id);
    }

    @GetMapping("/{id}/books")
    public List getBooksByBorrower(@PathVariable Long id) {
        return libraryService.getBooksByBorrower(id);
    }
}
package com.library.service;

import com.library.model.Book;
import com.library.model.Borrower;
import com.library.repository.BookRepository;
import com.library.repository.BorrowerRepository;
import io.micrometer.core.instrument.Counter;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class LibraryService {

    private final BookRepository bookRepository;
    private final BorrowerRepository borrowerRepository;
    private final Counter bookBorrowCounter;
    private final Tracer tracer;

    public LibraryService(BookRepository bookRepository,
                          BorrowerRepository borrowerRepository,
                          Counter bookBorrowCounter,
                          Tracer tracer) {
        this.bookRepository = bookRepository;
        this.borrowerRepository = borrowerRepository;
        this.bookBorrowCounter = bookBorrowCounter;
        this.tracer = tracer;
    }

    // --- Book Operations ---

    public List getAllBooks() {
        return bookRepository.findAll();
    }

    public Book addBook(Book book) {
        return bookRepository.save(book);
    }

    public Book borrowBook(Long bookId, Long borrowerId) {
        // Create a custom OpenTelemetry span for tracking this operation
        Span span = tracer.spanBuilder("borrowBookOperation").startSpan();
        try {
            Book book = bookRepository.findById(bookId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found with id: " + bookId));

            if (book.isBorrowed()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Book is already borrowed");
            }

            Borrower borrower = borrowerRepository.findById(borrowerId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Borrower not found with id: " + borrowerId));

            book.setBorrowed(true);
            book.setBorrower(borrower);

            Book savedBook = bookRepository.save(book);

            // Increment custom OpenTelemetry metric counter
            bookBorrowCounter.increment();

            return savedBook;

        } finally {
            span.end();
        }
    }

    // --- Borrower Operations ---

    public Borrower createBorrower(Borrower borrower) {
        return borrowerRepository.save(borrower);
    }

    public Borrower getBorrower(Long id) {
        return borrowerRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Borrower not found with id: " + id));
    }

    public List getBooksByBorrower(Long borrowerId) {
        if (!borrowerRepository.existsById(borrowerId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Borrower not found with id: " + borrowerId);
        }
        return bookRepository.findByBorrowerId(borrowerId);
    }
}
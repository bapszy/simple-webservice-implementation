package com.library.service;

import com.library.model.Book;
import com.library.model.Borrower;
import com.library.repository.BookRepository;
import com.library.repository.BorrowerRepository;
import io.micrometer.core.instrument.Counter;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanBuilder;
import io.opentelemetry.api.trace.Tracer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LibraryServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BorrowerRepository borrowerRepository;

    @Mock
    private Counter bookBorrowCounter;

    @Mock
    private Tracer tracer;

    @Mock
    private SpanBuilder spanBuilder;

    @Mock
    private Span span;

    @InjectMocks
    private LibraryService libraryService;

    @BeforeEach
    void setUp() {
        lenient().when(tracer.spanBuilder(anyString())).thenReturn(spanBuilder);
        lenient().when(spanBuilder.startSpan()).thenReturn(span);
    }

    @Test
    void borrowBook_Success() {
        Book book = new Book(1L, "Clean Code", "Robert C. Martin", false, null);
        Borrower borrower = new Borrower(1L, "John Doe", "john@example.com");

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(borrowerRepository.findById(1L)).thenReturn(Optional.of(borrower));
        when(bookRepository.save(any(Book.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Book result = libraryService.borrowBook(1L, 1L);

        assertTrue(result.isBorrowed());
        assertEquals(borrower, result.getBorrower());
        verify(bookBorrowCounter, times(1)).increment();
        verify(span, times(1)).end();
    }

    @Test
    void borrowBook_AlreadyBorrowed_ThrowsException() {
        Borrower borrower = new Borrower(1L, "John Doe", "john@example.com");
        Book book = new Book(1L, "Clean Code", "Robert C. Martin", true, borrower);

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        assertThrows(ResponseStatusException.class, () -> libraryService.borrowBook(1L, 1L));
        verify(bookBorrowCounter, never()).increment();
    }

    @Test
    void borrowBook_BookNotFound_ThrowsException() {
        when(bookRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> libraryService.borrowBook(99L, 1L));
    }
}
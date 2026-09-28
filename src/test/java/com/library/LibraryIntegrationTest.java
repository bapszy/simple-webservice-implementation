package com.library;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.model.Book;
import com.library.model.Borrower;
import com.library.repository.BookRepository;
import com.library.repository.BorrowerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class LibraryIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BorrowerRepository borrowerRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        bookRepository.deleteAll();
        borrowerRepository.deleteAll();
    }

    @Test
    void createBorrowerAndAddBook_ThenBorrowBook() throws Exception {
        // 1. Create a new Borrower via REST API
        Borrower borrower = new Borrower(null, "Alice Smith", "alice@example.com");
        String borrowerResponse = mockMvc.perform(post("/borrowers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(borrower)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Alice Smith")))
                .andReturn().getResponse().getContentAsString();

        Borrower createdBorrower = objectMapper.readValue(borrowerResponse, Borrower.class);

        // 2. Add a new Book via REST API
        Book book = new Book(null, "The Pragmatic Programmer", "Andy Hunt", false, null);
        String bookResponse = mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(book)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("The Pragmatic Programmer")))
                .andReturn().getResponse().getContentAsString();

        Book createdBook = objectMapper.readValue(bookResponse, Book.class);

        // 3. Borrow the Book
        mockMvc.perform(post("/books/" + createdBook.getId() + "/borrow")
                        .param("borrowerId", String.valueOf(createdBorrower.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.borrowed", is(true)))
                .andExpect(jsonPath("$.borrower.id", is(createdBorrower.getId().intValue())));

        // 4. Retrieve books borrowed by this user
        mockMvc.perform(get("/borrowers/" + createdBorrower.getId() + "/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title", is("The Pragmatic Programmer")));
    }
}
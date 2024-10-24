package com.example.library.controller;

import java.util.ArrayList;
import java.util.List;

import com.example.library.model.Book;
import com.example.library.service.BookService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookController.class)
public class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookService bookService;

    @Autowired
    private ObjectMapper objectMapper;

    private Book book;

    @BeforeEach
    public void setup() {
        book = new Book();
        book.setId(1L);
        book.setTitle("title");
        book.setAuthor("My name");
        book.setEditorial("Editorial");
        book.setIsbn("1234567890");
    }

    @Test
    public void saveBookTest() throws Exception{
        given(bookService.createBook(any(Book.class))).willReturn(book);

        ResultActions response = mockMvc.perform(post("/api/books/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(book)));

        response.andDo(print()).
                andExpect(status().isCreated())
                .andExpect(jsonPath("$.title", is(book.getTitle())))
                .andExpect(jsonPath("$.editorial", is(book.getEditorial())))
                .andExpect(jsonPath("$.isbn", is(book.getIsbn())))
                .andExpect(jsonPath("$.author", is(book.getAuthor())));
    }

    @Test
    public void getBookTest() throws Exception{

        Book book2 = new Book();
        book2.setId(2L);
        book2.setTitle("youremail@gmail.com");
        book2.setAuthor("My name 2 ");
        book2.setEditorial("Editorial 2");
        book2.setIsbn("0123456789");

        List<Book> bookList = new ArrayList<>();
        bookList.add(book);
        bookList.add(book2);
        given(bookService.getAllBooks()).willReturn(bookList);

        ResultActions response = mockMvc.perform(get("/api/books/"));
        response.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.size()",
                        is(bookList.size())));

    }

    @Test
    public void getBookByIdTest() throws Exception{
        given(bookService.getBookById(book.getId())).willReturn(book);
        ResultActions response = mockMvc.perform(get("/api/books/{id}", book.getId()));
        response.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.title", is(book.getTitle())))
                .andExpect(jsonPath("$.editorial", is(book.getEditorial())))
                .andExpect(jsonPath("$.isbn", is(book.getIsbn())))
                .andExpect(jsonPath("$.author", is(book.getAuthor())));
    }

    @Test
    public void updateBookTest() throws Exception{
        Book bookToUpdate = new Book();
        bookToUpdate.setId(2L);
        bookToUpdate.setTitle("youremail@gmail.com");
        bookToUpdate.setAuthor("My name 2 ");
        bookToUpdate.setEditorial("Editorial 2");
        bookToUpdate.setIsbn("0123456789");

        Mockito.when(bookService.updateBook(Mockito.any(Book.class), Mockito.any(Long.class))).thenReturn(bookToUpdate);

        ResultActions response = mockMvc.perform(put("/api/books/{id}", bookToUpdate.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bookToUpdate)));
        response.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.title", is(bookToUpdate.getTitle())))
                .andExpect(jsonPath("$.editorial", is(bookToUpdate.getEditorial())));
    }

    @Test
    public void deleteBookTest() throws Exception{
        willDoNothing().given(bookService).deleteBook(book.getId());
        ResultActions response = mockMvc.perform(delete("/api/books/{id}", book.getId()));
        response.andExpect(status().isNoContent())
                .andDo(print());
    }
}

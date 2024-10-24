package com.example.library.service;

import java.util.List;

import com.example.library.model.Book;

public interface BookService {
    List<Book> getAllBooks();

    Book getBookById(Long id);

    Book createBook(Book book);

    Book updateBook(Book book, Long id);

    void deleteBook(Long id);
}

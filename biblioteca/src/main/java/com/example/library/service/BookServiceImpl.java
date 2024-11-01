package com.example.library.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import com.example.library.repository.BookRepository;
import com.example.library.model.Book;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BookServiceImpl implements BookService {

    @Autowired
    private BookRepository bookRepository;

    @Override
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    @Override
    public Book getBookById(Long id) {
        Optional<Book> optionalBook = bookRepository.findById(id);
        return optionalBook.get();
    }

    @Override
    public Book createBook(Book book) {
        return bookRepository.save(book);
    }

    @Override
    public Book updateBook(Book book, Long id) {
        if (bookRepository.findById(id).isEmpty()) {
            throw new NoSuchElementException();
        }
        Book existingBook = bookRepository.findById(id).get();
        existingBook.setAuthor(book.getAuthor());
        existingBook.setEditorial(book.getEditorial());
        existingBook.setIsbn(book.getIsbn());
        existingBook.setTitle(book.getTitle());

        bookRepository.save(existingBook);

        return existingBook;
    }

    @Override
    public void deleteBook(Long id) {
        if (bookRepository.findById(id).isPresent()) {
            bookRepository.deleteById(id);
        } else {
            throw new NoSuchElementException();
        }
    }
}

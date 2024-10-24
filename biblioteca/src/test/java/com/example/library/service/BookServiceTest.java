package com.example.library.service;

import java.util.List;
import java.util.Optional;

import com.example.library.model.Book;
import com.example.library.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class BookServiceTest {

    @InjectMocks
    private BookServiceImpl bookServiceImpl;

    @Mock
    private BookRepository bookRepository;

    private Book book;

    private Book book2;

    @BeforeEach
    public void setup(){
        book = new Book();
        book.setId(1L);
        book.setTitle("title");
        book.setAuthor("My name");
        book.setEditorial("Editorial");
        book.setIsbn("1234567890");

        book2 = new Book();
        book2.setId(2L);
        book2.setTitle("youremail@gmail.com");
        book2.setAuthor("My name 2 ");
        book2.setEditorial("Editorial 2");
        book2.setIsbn("0123456789");
    }

    @Test
    public void testCreateBook() {
        given(bookRepository.save(book)).willReturn(book);
        Book savedBook = bookServiceImpl.createBook(book);
        System.out.println(savedBook);
        assertThat(savedBook).isNotNull();
    }

    @Test
    public void getAllUsers() {
        given(bookRepository.findAll()).willReturn(List.of(book, book2));
        List<Book> bookList = bookServiceImpl.getAllBooks();
        assertThat(bookList).isNotNull();
        assertThat(bookList.size()).isGreaterThan(1);
    }

    @Test
    public void getUserById() {
        given(bookRepository.findById(1L)).willReturn(Optional.of(book));
        Book existingBook = bookServiceImpl.getBookById(book.getId());
        assertThat(existingBook).isNotNull();
    }

    @Test
    public void deleteBook() {
        given(bookRepository.findById(book.getId())).willReturn(Optional.of(book));
        willDoNothing().given(bookRepository).deleteById(book.getId());
        bookServiceImpl.deleteBook(book.getId());
        verify(bookRepository, times(1)).deleteById(book.getId());
    }

    @Test
    public void updateUserTest() {
        given(bookRepository.findById(book.getId())).willReturn(Optional.of(book));
        Book book2Update = new Book();
        book2Update.setId(2L);
        book2Update.setTitle("new title");
        book2Update.setAuthor("Author Name ");
        book2Update.setEditorial("Editorial 2");
        book2Update.setIsbn("1234567898");
        Book bookUpdated =  bookServiceImpl.updateBook(book2Update, book.getId());
        assertThat(bookUpdated.getTitle()).isEqualTo("new title");
    }
}

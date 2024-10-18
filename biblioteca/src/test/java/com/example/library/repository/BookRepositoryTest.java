package com.example.library.repository;

import java.util.List;
import java.util.Optional;

import com.example.library.model.Book;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;


import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@TestPropertySource("classpath:application-test.properties")
public class BookRepositoryTest {

    @Autowired
    private BookRepository bookRepository;

    @Test
    public void saveBookTest(){
        Book book = new Book();
        book.setTitle("Book Title");
        book.setIsbn("0123456789");
        book.setAuthor("Author");
        book.setEditorial("Editorial");
        bookRepository.save(book);
        assertThat(book.getId()).isGreaterThan(1);
    }

    @Test
    public void getBookTest(){
        Book book = bookRepository.findById(1L).get();
        assertThat(book.getId()).isEqualTo(1L);
    }

    @Test
    public void getListOfBooksTest(){
        List<Book> books = bookRepository.findAll();
        Assertions.assertThat(books.size()).isGreaterThan(0);
    }

    @Test
    public void deleteBookTest(){
        bookRepository.deleteById(1L);
        Optional<Book> UserOptional = bookRepository.findById(1L);
        Assertions.assertThat(UserOptional).isEmpty();
    }

    @Test
    public void updateBookTest() {
        Book book = bookRepository.findById(1L).get();
        book.setTitle("newTitle");
        Book bookUpdated =  bookRepository.save(book);
        Assertions.assertThat(bookUpdated.getTitle()).isEqualTo("newTitle");
    }

    //@Test
    public void findBookByIsbn() {
        Book book = bookRepository.findByIsbn("1649374178").get();
        assertThat(book).isNotNull();;
    }
}

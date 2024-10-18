package com.example.library.service;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

import com.example.library.model.Book;
import com.example.library.model.BookReservation;
import com.example.library.model.User;
import com.example.library.repository.BookRepository;
import com.example.library.repository.BookReservationsRepository;
import com.example.library.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BookReservationsService {

    @Autowired
    private BookReservationsRepository bookReservationsRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    public BookReservation reserveBook(BookReservation bookReservation) {
        Long userId = bookReservation.getUserId();
        Long bookId = bookReservation.getBookId();
        validateUser(userId);
        validateBook(bookId);

        Optional<List<BookReservation>> bookReservationOptional = bookReservationsRepository.findByBookId(bookId);

        if (bookReservationOptional.isPresent()
                && !bookReservationOptional.get().isEmpty()) {
            throw new NoSuchElementException("Book is already reserved");
        }

        BookReservation reservation = new BookReservation();
        reservation.setBookId(bookId);
        reservation.setUserId(userId);
        return bookReservationsRepository.save(reservation);
    }

    public void removeBookReservation(BookReservation bookReservation) {
        Long userId = bookReservation.getUserId();
        Long bookId = bookReservation.getBookId();
        validateUser(userId);
        validateBook(bookId);

        Optional<List<BookReservation>> bookReservationOptional = bookReservationsRepository.findByBookId(bookId);

        if (bookReservationOptional.isEmpty()) {
            throw new NoSuchElementException("Book is not reserved");
        }
        List<BookReservation> existingReservations = bookReservationOptional.get();
        List<BookReservation> userExistingReservations = existingReservations
                .stream()
                .filter(reservation -> reservation.getUserId().equals(userId))
                .toList();

        if (userExistingReservations.isEmpty()) {
            throw new NoSuchElementException("User has not reserved this book");
        }

        Optional<BookReservation> reservationOptional = existingReservations.stream()
                .filter(res -> res.getUserId().equals(userId)
                        && res.getBookId().equals(bookId)).toList().stream().findFirst();
        if (reservationOptional.isEmpty()) {
            throw new NoSuchElementException("The reservation does not exist");
        }

        bookReservationsRepository.delete(reservationOptional.get());
    }

    public List<BookReservation> getUserBookReservations(Long userId) {
        Optional<List<BookReservation>> bookReservationOptional = bookReservationsRepository.findByUserId(userId);
        return bookReservationOptional.orElse(Collections.emptyList());
    }

    private void validateUser(Long id) {
        if (userRepository.findById(id).isEmpty()) {
            throw new NoSuchElementException("User not Found");
        }
    }

    private void validateBook(Long bookId) {
        if (bookRepository.findById(bookId).isEmpty()) {
            throw new NoSuchElementException("Book not Found");
        }
    }
}

package com.example.library.service;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import com.example.library.dto.BookReservationDTO;
import com.example.library.model.BookReservation;
import com.example.library.repository.BookRepository;
import com.example.library.repository.BookReservationsRepository;
import com.example.library.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BookReservationsServiceImpl implements BookReservationsService {

    @Autowired
    private BookReservationsRepository bookReservationsRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    @Override
    public BookReservation reserveBook(BookReservationDTO bookReservationDTO) {
        Long userId = bookReservationDTO.getUserId();
        Long bookId = bookReservationDTO.getBookId();
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

    @Override
    public void removeBookReservation(BookReservationDTO bookReservationDTO) {
        Long userId = bookReservationDTO.getUserId();
        Long bookId = bookReservationDTO.getBookId();
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

    @Override
    public List<BookReservation> getUserBookReservations(Long userId) {
        Optional<List<BookReservation>> bookReservationOptional = bookReservationsRepository.findByUserId(userId);
        return bookReservationOptional.orElse(Collections.emptyList());
    }

    @Override
    public void validateUser(Long id) {
        if (userRepository.findById(id).isEmpty()) {
            throw new NoSuchElementException("User not Found");
        }
    }

    @Override
    public void validateBook(Long bookId) {
        if (bookRepository.findById(bookId).isEmpty()) {
            throw new NoSuchElementException("Book not Found");
        }
    }
}

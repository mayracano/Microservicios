package com.example.library.service;

import java.util.List;

import com.example.library.model.BookReservation;

public interface BookReservationsService {
    BookReservation reserveBook(BookReservation bookReservation);

    void removeBookReservation(BookReservation bookReservation);

    List<BookReservation> getUserBookReservations(Long userId);

    void validateUser(Long id);

    void validateBook(Long bookId);
}

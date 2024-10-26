package com.example.library.service;

import java.util.List;

import com.example.library.dto.BookReservationDTO;
import com.example.library.model.BookReservation;

public interface BookReservationsService {
    BookReservation reserveBook(BookReservationDTO bookReservationDTO);

    void removeBookReservation(BookReservationDTO bookReservationDTO);

    List<BookReservation> getUserBookReservations(Long userId);

    void validateUser(Long id);

    void validateBook(Long bookId);
}

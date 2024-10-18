package com.example.library.repository;

import java.util.List;
import java.util.Optional;

import com.example.library.model.BookReservation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookReservationsRepository extends JpaRepository<BookReservation, Long> {

    Optional<List<BookReservation>> findByUserId(Long userId);
    Optional<List<BookReservation>> findByBookId(Long bookId);

}

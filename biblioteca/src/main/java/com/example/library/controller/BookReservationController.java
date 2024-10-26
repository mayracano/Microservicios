package com.example.library.controller;

import java.util.List;

import com.example.library.dto.BookReservationEvent;
import com.example.library.model.BookReservation;
import com.example.library.model.BookReservationStatus;
import com.example.library.service.BookReservationsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/reservations/")
public class BookReservationController {

    @Autowired
    private BookReservationsService bookReservationsService;

    @Autowired
    KafkaTemplate<String, BookReservationEvent> kafkaTemplate;

    @PostMapping
    public ResponseEntity<BookReservation> createReservation(@RequestBody BookReservation bookReservationData) {
        BookReservation bookReservation = bookReservationsService.reserveBook(bookReservationData);

        BookReservationEvent bookReservationEvent = new BookReservationEvent();
        bookReservationEvent.setBookReservation(bookReservation);
        bookReservationEvent.setBookReservationStatus(BookReservationStatus.CREATED);
        kafkaTemplate.send("new-reservation", bookReservationEvent);

        return new ResponseEntity<>(bookReservation, HttpStatus.CREATED);
    }

    @DeleteMapping
    public ResponseEntity<String> removeBookReservation(@RequestBody BookReservation bookReservation) {
        bookReservationsService.removeBookReservation(bookReservation);
        return new ResponseEntity<>("Book Reservation Removed", HttpStatus.OK);
    }

    @GetMapping("{id}")
    public List<BookReservation>  getUserBookReservations(@PathVariable("id") Long userId) {
        return bookReservationsService.getUserBookReservations(userId);
    }
}

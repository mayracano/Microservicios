package com.example.library.controller;

import java.util.List;

import com.example.library.dto.BookReservationDTO;
import com.example.library.dto.BookReservationEvent;
import com.example.library.dto.BookReservationStatus;
import com.example.library.model.BookReservation;
import com.example.library.service.BookReservationsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class BookReservationController {

    private static final Logger LOGGER = LoggerFactory.getLogger(BookReservationController.class);

    @Autowired
    private BookReservationsService bookReservationsService;

    @Autowired
    KafkaTemplate<String, BookReservationEvent> kafkaTemplate;

    @KafkaListener(topics = "new-reservation", groupId = "reservations-group")
    public void createReservation(String event) throws Exception {
        BookReservationEvent bookReservationEvent = new ObjectMapper()
                .readValue(event, BookReservationEvent.class);

        LOGGER.info(String.format("Received 'new-reservation', operation to register a Book reservation for for user: %s and book: %s", bookReservationEvent.getBookReservation().getBookId(), bookReservationEvent.getBookReservation().getUserId()));
        BookReservationDTO bookReservationDTO = bookReservationEvent.getBookReservation();

        BookReservationEvent bookReservationCompleteEvent = new BookReservationEvent();
        bookReservationCompleteEvent.setBookReservation(bookReservationDTO);

        try {
            BookReservation bookReservation = bookReservationsService.reserveBook(bookReservationDTO);
            bookReservationDTO.setId(bookReservation.getId());
            bookReservationCompleteEvent.setBookReservationStatus(BookReservationStatus.CREATED);
            kafkaTemplate.send("completed-reservations", bookReservationCompleteEvent);
            LOGGER.info(String.format("Sent 'completed-reservations' for user: %s and book: %s", bookReservationDTO.getBookId(), bookReservationDTO.getUserId()));
        } catch(Exception e) {
            bookReservationCompleteEvent.setBookReservationStatus(BookReservationStatus.REVERSED);
            kafkaTemplate.send("reversed-reservations-failed", bookReservationEvent);
            LOGGER.info(String.format("Sent 'reversed-reservations-failed' for user: %s and book: %s", bookReservationDTO.getBookId(), bookReservationDTO.getUserId()));
        }
    }

    public void removeBookReservation(@RequestBody BookReservationDTO bookReservationDTO) {
        bookReservationsService.removeBookReservation(bookReservationDTO);
    }

    public List<BookReservation> getUserBookReservations(@PathVariable("id") Long userId) {
        return bookReservationsService.getUserBookReservations(userId);
    }
}

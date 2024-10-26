package com.example.library.service;

import java.util.Optional;

import com.example.library.controller.BookReservationController;
import com.example.library.dto.BookReservationEvent;
import com.example.library.model.BookReservation;
import com.example.library.repository.BookReservationsRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Controller;

@Controller
public class CompleteReservation {

    private static final Logger LOGGER = LoggerFactory.getLogger(BookReservationController.class);

    @Autowired
    private BookReservationsRepository bookReservationsRepository;

    @KafkaListener(topics = "completed-reservations", groupId = "reservations-group")
    public void completeReservation(String event) {
        try {
            BookReservationEvent bookReservationEvent = new ObjectMapper().readValue(event, BookReservationEvent.class);

            LOGGER.info(String.format("Received 'completed-reservations', operation to complete a Book reservation for for user: %s and book: %s", bookReservationEvent.getBookReservation().getBookId(), bookReservationEvent.getBookReservation().getUserId()));
            Optional<BookReservation> bookReservationOptional = bookReservationsRepository.findById(bookReservationEvent.getBookReservation().getId());
            bookReservationOptional.ifPresent(bookReservation -> {
                //TODO: manage state on reservations
                bookReservationsRepository.save(bookReservation);
            });
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
            //TODO: Exceptions handling
        }
    }
}
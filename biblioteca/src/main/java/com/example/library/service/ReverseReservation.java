package com.example.library.service;

import java.util.Optional;

import com.example.library.dto.BookReservationEvent;
import com.example.library.model.BookReservation;
import com.example.library.repository.BookReservationsRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

@Component
public class ReverseReservation {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReverseReservation.class);

    @Autowired
    private BookReservationsRepository bookReservationsRepository;

    @KafkaListener(topics = "reversed-reservations", groupId = "reservations-group")
    public void reverseReservation(String event) {
        try {
            BookReservationEvent bookReservationEvent = new ObjectMapper().readValue(event, BookReservationEvent.class);

            LOGGER.info(String.format("Received 'reversed-reservations', operation to reverse the register a Book reservation for for user: %s and book: %s", bookReservationEvent.getBookReservation().getBookId(), bookReservationEvent.getBookReservation().getUserId()));
            Optional<BookReservation> OptionalReservation = bookReservationsRepository.findByUserIdAndBookId(bookReservationEvent.getBookReservation().getUserId(), bookReservationEvent.getBookReservation().getBookId());
            OptionalReservation.ifPresent(bookReservation -> bookReservationsRepository.delete(bookReservation));
            //TODO: manage states on reservations instead of deleting them
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
            //TODO: Exceptions handling
        }
    }
}
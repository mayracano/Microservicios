package com.example.library.service;

import java.util.Optional;

import com.example.library.dto.BookReservationEvent;
import com.example.library.model.BookReservation;
import com.example.library.repository.BookReservationsRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Controller;

@Controller
public class ReverseReservation {

    @Autowired
    private BookReservationsRepository bookReservationsRepository;

    @KafkaListener(topics = "reversed-reservations", groupId = "reservations-group")
    public void reverseReservation(String event) {
        try {
            BookReservationEvent bookReservationEvent = new ObjectMapper().readValue(event, BookReservationEvent.class);
            Optional<BookReservation> bookReservationOptional = bookReservationsRepository.findById(bookReservationEvent.getBookReservation().getId());
            bookReservationOptional.ifPresent(bookReservation -> bookReservationsRepository.delete(bookReservation));
            //TODO: manage states on reservations instead of deleting them
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
            //TODO: Exceptions handling
        }
    }
}

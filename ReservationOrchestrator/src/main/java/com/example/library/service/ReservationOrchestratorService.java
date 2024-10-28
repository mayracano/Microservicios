package com.example.library.service;

import java.util.Optional;

import com.example.library.dto.BookReservationDTO;
import com.example.library.dto.BookReservationEvent;
import com.example.library.dto.BookReservationStatus;
import com.example.library.model.BookReservation;
import com.example.library.repository.ReservationRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class ReservationOrchestratorService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReservationOrchestratorService.class);

    @Autowired
    KafkaTemplate<String, BookReservationEvent> kafkaTemplate;

    @Autowired
    ReservationRepository reservationRepository;

    public void createReservation(BookReservationDTO bookReservationDTO) {
        BookReservation bookReservation = createReservationStatusRecord(bookReservationDTO);
        bookReservationDTO.setId(bookReservation.getReservationId());
        addReservation(bookReservationDTO);
        removeFromInventory(bookReservationDTO);
    }

    @KafkaListener(topics = "new-reservation-failed", groupId = "reservations-group")
    public void reverseAddReservation(String event) throws Exception {
        BookReservationEvent bookReservationEvent = new ObjectMapper().readValue(event, BookReservationEvent.class);
        LOGGER.info(String.format("Received 'new-reservation-failed', operation to register a Book reservation Failed for for user: %s and book: %s", bookReservationEvent.getBookReservation().getBookId(), bookReservationEvent.getBookReservation().getUserId()));
        reservationRepository.findById(bookReservationEvent.getBookReservation().getId())
                .ifPresent(this::updateReservationStatusFailed);
        kafkaTemplate.send("reversed-inventory", bookReservationEvent);
        LOGGER.info(String.format("Sent 'reversed-inventory' for user: %s and book: %s", bookReservationEvent.getBookReservation().getBookId(), bookReservationEvent.getBookReservation().getUserId()));
    }

    @KafkaListener(topics = "completed-reservation", groupId = "reservations-group")
    public void completeReservation(String event) throws Exception {
        BookReservationEvent bookReservationEvent = new ObjectMapper().readValue(event, BookReservationEvent.class);
        LOGGER.info(String.format("Received 'completed-reservation', operation to register Book Reservation Completed for: %s and book %s", bookReservationEvent.getBookReservation().getBookId(), bookReservationEvent.getBookReservation().getUserId()));
        completeStatus(bookReservationEvent);
    }

    @KafkaListener(topics = "removed-inventory-failed", groupId = "reservations-group")
    public void reverseInventory(String event) throws Exception {
        BookReservationEvent bookReservationEvent = new ObjectMapper().readValue(event, BookReservationEvent.class);
        LOGGER.info(String.format("Received 'removed-inventory-failed', operation to remove book from inventory Failed for: %s and book %s", bookReservationEvent.getBookReservation().getBookId(), bookReservationEvent.getBookReservation().getUserId()));
        reservationRepository.findById(bookReservationEvent.getBookReservation().getId())
                .ifPresent(this::updateReservationStatusFailed);
        kafkaTemplate.send("reversed-reservations", bookReservationEvent);
        LOGGER.info(String.format("Sent 'reversed-reservations' for user: %s and book: %s", bookReservationEvent.getBookReservation().getBookId(), bookReservationEvent.getBookReservation().getUserId()));
    }

    @KafkaListener(topics = "completed-inventory", groupId = "reservations-group")
    public void completeInventory(String event) throws Exception {
        BookReservationEvent bookReservationEvent = new ObjectMapper().readValue(event, BookReservationEvent.class);
        LOGGER.info(String.format("Received 'completed-inventory', operation to complete the book reservation for: %s and book %s", bookReservationEvent.getBookReservation().getBookId(), bookReservationEvent.getBookReservation().getUserId()));
        completeStatus(bookReservationEvent);
    }

    private void completeStatus(BookReservationEvent bookReservationEvent) {
        Optional<BookReservation> optionalBookReservation = reservationRepository.findById(bookReservationEvent.getBookReservation().getId());
        if (optionalBookReservation.isPresent()) {
            BookReservation bookReservation = optionalBookReservation.get();
            updateReservationStatusOK(bookReservation);
            if (bookReservation.getReservationStatus().equals(BookReservationStatus.COMPLETED)) {
                kafkaTemplate.send("completed-reservations-ok", bookReservationEvent);
                LOGGER.info(String.format("Sent 'completed-reservations-ok' for user: %s and book: %s", bookReservationEvent.getBookReservation().getBookId(), bookReservationEvent.getBookReservation().getUserId()));
            }
        }
    }

    private void removeFromInventory(BookReservationDTO bookReservationDTO) {
        BookReservationEvent bookReservationInventoryEvent = new BookReservationEvent();
        bookReservationInventoryEvent.setBookReservation(bookReservationDTO);
        bookReservationInventoryEvent.setBookReservationStatus(BookReservationStatus.CREATED);
        kafkaTemplate.send("removed-inventory", bookReservationInventoryEvent);
        LOGGER.info(String.format("Sent 'removed-inventory' for user: %s and book: %s", bookReservationDTO.getBookId(), bookReservationDTO.getUserId()));
    }

    private void addReservation(BookReservationDTO bookReservationDTO) {
        BookReservationEvent bookReservationEvent = new BookReservationEvent();
        bookReservationEvent.setBookReservation(bookReservationDTO);
        bookReservationEvent.setBookReservationStatus(BookReservationStatus.CREATED);
        kafkaTemplate.send("new-reservation", bookReservationEvent);
        LOGGER.info(String.format("sent 'new-reservation' for user: %s and book: %s", bookReservationDTO.getBookId(), bookReservationDTO.getUserId()));
    }

    private BookReservation createReservationStatusRecord(BookReservationDTO bookReservationDTO) {
        BookReservation bookReservation = new BookReservation();
        bookReservation.setBookId(bookReservationDTO.getBookId());
        bookReservation.setUserId(bookReservationDTO.getUserId());
        bookReservation.setReservationStatus(BookReservationStatus.PENDING);
        return reservationRepository.save(bookReservation);
    }

    private void updateReservationStatusOK(BookReservation bookReservation) {
        if (bookReservation.getReservationStatus().equals(BookReservationStatus.PENDING)) {
            bookReservation.setReservationStatus(BookReservationStatus.CREATED);
        } else if (bookReservation.getReservationStatus().equals(BookReservationStatus.CREATED)) {
            bookReservation.setReservationStatus(BookReservationStatus.COMPLETED);
        }
        reservationRepository.save(bookReservation);
    }

    private void updateReservationStatusFailed(BookReservation bookReservation) {
        if (bookReservation.getReservationStatus().equals(BookReservationStatus.PENDING)
        || bookReservation.getReservationStatus().equals(BookReservationStatus.CREATED)) {
            bookReservation.setReservationStatus(BookReservationStatus.REVERSED);
        }
        reservationRepository.save(bookReservation);
    }
}

package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.Status;

import java.time.LocalDateTime;
import java.util.List;

public interface JpaBookingRepository extends JpaRepository<Booking, Long> {


    List<Booking> findAllBookingsByBooker_IdOrderByStartDesc(Long userId);

    List<Booking> findAllBookingsByBooker_IdAndStatus(Long userId, Status status);

    List<Booking> findAllBookingsByBooker_IdAndEndIsBeforeOrderByStartDesc(Long userId, LocalDateTime now);

    List<Booking> findAllBookingsByBooker_IdAndStartIsAfterOrderByStartDesc(Long userId, LocalDateTime now);

    List<Booking> findBookingsByBooker_IdAndStatus(Long userId, Status status);

    @Query("SELECT b FROM Booking b " +
            "JOIN b.item i " +
            "JOIN i.owner o " +
            "WHERE o.id = :userId " +
            "ORDER BY b.start DESC")
    List<Booking> findAllBookingsByItemOwnerOrderByStartDesc(@Param("userId") Long userId);

    @Query("SELECT b FROM Booking b " +
            "JOIN b.item i " +
            "JOIN i.owner o " +
            "WHERE o.id = :userId AND b.status = :status")
    List<Booking> findAllBookingsByItemOwnerAndStatus(@Param("userId") Long userId, @Param("status") Status status);

    @Query("SELECT b FROM Booking b " +
            "JOIN b.item i " +
            "JOIN i.owner o " +
            "WHERE o.id = :userId AND b.end < :now " +
            "ORDER BY b.start DESC")
    List<Booking> findAllBookingsByItemOwnerAndEndIsBeforeOrderByStartDesc(@Param("userId") Long userId, @Param("now") LocalDateTime now);

    @Query("SELECT b FROM Booking b " +
            "JOIN b.item i " +
            "JOIN i.owner o " +
            " WHERE o.id = :userId AND b.start > :now " +
            " ORDER BY b.start DESC")
    List<Booking> findAllBookingsByItemOwnerAndStartIsAfterOrderByStartDesc(@Param("userId") Long userId, @Param("now") LocalDateTime now);

    @Query("SELECT b FROM Booking b " +
            "JOIN b.item i " +
            "JOIN i.owner o " +
            "WHERE o.id = :userId AND b.status = :status")
    List<Booking> findBookingsByItemOwnerAndStatus(@Param("userId") Long userId, @Param("status") Status status);

    List<Booking> findAllByBooker_IdAndItem_IdAndStatusAndEndBefore(Long userId, Long itemId, Status status, LocalDateTime now);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.id = :id AND b.end > :now " +
            "ORDER BY b.end DESC")
    Booking findLastBookingByItem_Id(@Param("id") Long id, @Param("now") LocalDateTime now);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.id = :id AND b.start > :now " +
            "ORDER BY b.start ASC")
    Booking findNextBookingByItem_Id(@Param("id") Long id, @Param("now") LocalDateTime now);
}
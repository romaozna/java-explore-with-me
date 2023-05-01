package ru.practicum.ewm.event.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewm.event.dto.EventState;
import ru.practicum.ewm.event.model.Event;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findAllByInitiatorId(Long initiatorId, Pageable pageable);

    Event findByInitiatorIdAndId(Long initiatorId, Long id);

    List<Event> findAllByCategoryId(Long categoryId);

    @Query("SELECT e FROM Event AS e " +
            "WHERE (:users IS NULL OR e.initiator.id IN :users) " +
            "AND (:states IS NULL OR e.state IN :states) " +
            "AND (:categories IS NULL OR e.category.id IN :categories) " +
            "AND (coalesce(:rangeStart, :rangeEnd) IS NULL " +
            "OR e.eventDate BETWEEN coalesce(:rangeStart, e.eventDate) AND coalesce(:rangeEnd, e.eventDate))")
    List<Event> getEvents(@Param("rangeStart") LocalDateTime rangeStart,
                          @Param("rangeEnd") LocalDateTime rangeEnd,
                          @Param("users") List<Long> users,
                          @Param("states") List<EventState> states,
                          @Param("categories") List<Long> categories,
                          Pageable pageable);

    @Query("SELECT e FROM Event AS e " +
            "WHERE e.state = :state " +
            "AND (:text IS NULL " +
            "OR LOWER(e.description) LIKE LOWER(CONCAT('%', :text, '%')) " +
            "OR LOWER(e.annotation) LIKE LOWER(CONCAT('%', :text, '%'))) " +
            "AND (:categories IS NULL OR e.category.id IN (:categories)) " +
            "AND (:paid IS NULL OR e.paid = :paid) " +
            "AND ((coalesce(:rangeStart, :rangeEnd) IS NULL AND e.eventDate > now()) " +
            "OR e.eventDate BETWEEN coalesce(:rangeStart, e.eventDate) AND coalesce(:rangeEnd, e.eventDate))")
    List<Event> getPublicEvents(Pageable pageable,
                                @Param("state") EventState state,
                                @Param("text") String text,
                                @Param("categories") List<Long> categories,
                                @Param("paid") Boolean paid,
                                @Param("rangeStart") LocalDateTime rangeStart,
                                @Param("rangeEnd") LocalDateTime rangeEnd);

    Event findEventByIdAndState(Long id, EventState state);
}

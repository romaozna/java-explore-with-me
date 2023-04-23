package ru.practicum.ewm.requests.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm.requests.model.Request;
import ru.practicum.ewm.requests.model.RequestStat;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {

    List<Request> findAllByRequesterId(Long requesterId);

    List<Request> findAllByEventId(Long eventId);

    Request findByRequesterIdAndId(Long requesterId, Long id);

    @Query("select new RequestStat(r.eventId, COUNT(r.eventId)) " +
            "from Request r " +
            "where r.eventId IN ?1 " +
            "group by r.eventId")
    List<RequestStat> getRequestsStats(List<Long> ids);
}
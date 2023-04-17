package ru.practicum.ewm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.model.EndpointHit;
import ru.practicum.ewm.model.ViewStat;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EndpointHitRepository extends JpaRepository<EndpointHit, Long> {

    @Query("SELECT new ViewStat(hit.uri, hit.app, COUNT(DISTINCT hit.ip)) " +
            "FROM EndpointHit hit " +
            "WHERE hit.timestamp BETWEEN :start AND :end " +
            "AND hit.uri IN :uris " +
            "GROUP BY hit.app, hit.uri " +
            "ORDER BY COUNT(hit.ip) DESC")
    List<ViewStat> getUniqueViewStats(@Param("uris") List<String> uris,
                                      @Param("start") LocalDateTime start,
                                      @Param("end") LocalDateTime end);

    @Query("SELECT new ViewStat(hit.uri, hit.app, COUNT(hit.ip)) " +
            "FROM EndpointHit hit " +
            "WHERE hit.timestamp BETWEEN :start AND :end " +
            "AND hit.uri IN :uris " +
            "GROUP BY hit.app, hit.uri " +
            "ORDER BY COUNT(hit.ip) DESC")
    List<ViewStat> getNonUniqueViewStats(@Param("uris") List<String> uris,
                                         @Param("start") LocalDateTime start,
                                         @Param("end") LocalDateTime end);

    @Query("SELECT new ViewStat(hit.uri, hit.app, COUNT(hit.ip)) " +
            "FROM EndpointHit hit " +
            "WHERE hit.timestamp BETWEEN :start AND :end " +
            "GROUP BY hit.app, hit.uri " +
            "ORDER BY COUNT(hit.ip) DESC")
    List<ViewStat> getNonUniqueViewStats(@Param("start") LocalDateTime start,
                                         @Param("end") LocalDateTime end);

    @Query("SELECT new ViewStat(hit.uri, hit.app, COUNT(DISTINCT hit.ip)) " +
            "FROM EndpointHit hit " +
            "WHERE hit.timestamp BETWEEN :start AND :end " +
            "GROUP BY hit.app, hit.uri " +
            "ORDER BY COUNT(hit.ip) DESC")
    List<ViewStat> getUniqueViewStats(@Param("start") LocalDateTime start,
                                      @Param("end") LocalDateTime end);
}

package ru.practicum.ewm;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.ewm.model.EndpointHit;
import ru.practicum.ewm.model.ViewStat;
import ru.practicum.ewm.repository.EndpointHitRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class EndpointHitRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private EndpointHitRepository endpointHitRepository;

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final EndpointHit endpointHit = new EndpointHit(
            null,
            "ewm-main-service",
            "/events",
            "192.168.209.131",
            LocalDateTime
                    .parse("2022-09-15 19:30:00", DATE_TIME_FORMATTER));

    @Test
    void createHitTest() {
        EndpointHit created = endpointHitRepository.save(endpointHit);

        Assertions.assertNotNull(created);
        Assertions.assertEquals(1L, created.getId());
        Assertions.assertEquals(endpointHit.getApp(), created.getApp());
        Assertions.assertEquals(endpointHit.getUri(), created.getUri());
        Assertions.assertEquals(endpointHit.getIp(), created.getIp());
        Assertions.assertEquals(endpointHit.getTimestamp(), created.getTimestamp());
    }

    @Test
    void getStatsWithRequiredParamsOnlyTest() {
        entityManager.persist(endpointHit);
        entityManager.flush();

        List<ViewStat> found = endpointHitRepository.getNonUniqueViewStats(
                LocalDateTime.parse("2020-05-05 00:00:00", DATE_TIME_FORMATTER),
                LocalDateTime.parse("2023-05-05 00:00:00", DATE_TIME_FORMATTER));

        Assertions.assertNotNull(found);
        Assertions.assertEquals(1, found.size());
        Assertions.assertEquals(endpointHit.getApp(), found.get(0).getApp());
        Assertions.assertEquals(endpointHit.getUri(), found.get(0).getUri());
        Assertions.assertEquals(1L, found.get(0).getHits());
    }

    @Test
    void getUniqueStatsWithParamsOnlyTest() {
        entityManager.persist(endpointHit);
        entityManager.persist(endpointHit);
        entityManager.flush();

        List<ViewStat> found = endpointHitRepository.getUniqueViewStats(
                LocalDateTime.parse("2020-05-05 00:00:00", DATE_TIME_FORMATTER),
                LocalDateTime.parse("2023-05-05 00:00:00", DATE_TIME_FORMATTER));

        Assertions.assertNotNull(found);
        Assertions.assertEquals(1, found.size());
        Assertions.assertEquals(endpointHit.getApp(), found.get(0).getApp());
        Assertions.assertEquals(endpointHit.getUri(), found.get(0).getUri());
        Assertions.assertEquals(1L, found.get(0).getHits());
    }
}
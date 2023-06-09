package ru.practicum.ewm.compilation.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewm.compilation.model.Compilation;

import java.util.List;
import java.util.Optional;

public interface CompilationRepository extends JpaRepository<Compilation, Long> {

    @EntityGraph(value = "compilation-with-events", type = EntityGraph.EntityGraphType.LOAD)
    List<Compilation> findByPinned(@Param("pinned") Boolean pinned, Pageable pageable);

    @EntityGraph(value = "compilation-with-events", type = EntityGraph.EntityGraphType.LOAD)
    Optional<Compilation> findCompilationById(Long id);

    Integer deleteCompilationById(Long compilationId);
}

package ru.practicum.ewm.event.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.event.dto.EventState;
import ru.practicum.ewm.location.model.Location;
import ru.practicum.ewm.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String annotation;
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
    @javax.persistence.Transient
    private int confirmedRequests;
    @Column(name = "created_on", nullable = false)
    @CreationTimestamp
    private LocalDateTime createdOn;
    @Column(nullable = false)
    private String description;
    @Column(name = "event_date", nullable = false)
    private LocalDateTime eventDate;
    @ManyToOne
    @JoinColumn(name = "initiator_id")
    private User initiator;
    @ManyToOne
    @JoinColumn(name = "location_id")
    private Location location;
    @Column(nullable = false)
    private Boolean paid;
    @Column(name = "participant_limit", nullable = false)
    private Integer participantLimit;
    @Column(name = "published_on", nullable = false)
    private LocalDateTime publishedOn;
    @Column(name = "request_moderation", nullable = false)
    private Boolean requestModeration;
    @Column(nullable = false)
    private EventState state;
    @Column(nullable = false)
    private String title;
}

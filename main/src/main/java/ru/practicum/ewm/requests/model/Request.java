package ru.practicum.ewm.requests.model;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "requests")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "event_id", nullable = false)
    private Long eventId;
    @Column(name = "requester_id", nullable = false)
    private Long requesterId;
    @Column(nullable = false)
    private RequestStatus status;
    @Column(nullable = false)
    @CreationTimestamp
    private LocalDateTime created;
}

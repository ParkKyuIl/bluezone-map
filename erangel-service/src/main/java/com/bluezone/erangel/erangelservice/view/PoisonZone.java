package com.bluezone.erangel.erangelservice.view;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

import java.time.Instant;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PoisonZone {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String map;

    private double x;
    private double y;

    private double radius;

    private Instant timestamp;

    // DTO → Entity 변환 생성자
    public PoisonZone(PoisonZoneDTO dto) {
        this.map = dto.getMap();
        this.x = dto.getPoisonZone().getX();
        this.y = dto.getPoisonZone().getY();
        this.radius = dto.getPoisonRadius();
        this.timestamp = Instant.parse(dto.getTimestamp());
    }
}

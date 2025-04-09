package com.bluezone.erangel.erangelservice;

import com.bluezone.erangel.erangelservice.service.ErangelHeatMapService;
import com.bluezone.erangel.erangelservice.view.PoisonZone;
import com.bluezone.erangel.erangelservice.repository.ErangelRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Transactional
public class KafkaErangelConsumer {

    private final ErangelRepository repository;
    private final ErangelHeatMapService erangelService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @KafkaListener(topics = "poison-zone", groupId = "pubg-group")
    public void consume(String message) {
        try {


            JsonNode json = objectMapper.readTree(message);

            String map = json.get("map").asText();


            if (!map.equals("Baltic_Main")) return;

            JsonNode pos = json.get("poisonZone");
            double x = pos.get("x").asDouble();
            double y = pos.get("y").asDouble();
            double radius = json.get("poisonRadius").asDouble();
            Instant timestamp = Instant.parse(json.get("timestamp").asText());

            PoisonZone zone = PoisonZone.builder()
                    .map(map)
                    .x(x)
                    .y(y)
                    .radius(radius)
                    .timestamp(timestamp)
                    .build();


            repository.save(zone);
            erangelService.calculateAndCacheHeatmap();
            System.out.println("✅ 저장 완료: " + zone.getX());

        } catch (Exception e) {
            System.out.println(e);
        }
    }
}

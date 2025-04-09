package com.bluezone.erangel.erangelservice.view;

import lombok.Data;

@Data
public class PoisonZoneDTO {
    private String map;
    private Zone poisonZone;
    private double poisonRadius;
    private String timestamp;
    @Data
    public static class Zone {
        private double x;
        private double y;
    }
}

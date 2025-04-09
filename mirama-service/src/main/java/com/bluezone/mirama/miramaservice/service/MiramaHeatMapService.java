package com.bluezone.mirama.miramaservice.service;

import com.bluezone.mirama.miramaservice.repository.MiramaRepository;
import com.bluezone.mirama.miramaservice.view.PoisonZone;
import com.bluezone.mirama.miramaservice.view.TopZoneDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MiramaHeatMapService {

    private final MiramaRepository repository;

    private final RedisTemplate<String, Object> redisTemplate;

    private static final int MAP_SIZE = 816_000;
    private static final int GRID_SIZE = 256;
    private static final int TOP_N = 400;

    public  void calculateAndCacheHeatmap() {
        String map = "Baltic_Main";
        List<PoisonZone> zones = repository.findAll();
        System.out.println(zones);
        if (zones.isEmpty()) return;

        double[][] heatmap = new double[GRID_SIZE][GRID_SIZE];
        double[] xBins = linspace();
        double[] yBins = linspace();

        for (PoisonZone zone : zones) {
            double x = zone.getX();
            double y = MAP_SIZE - zone.getY(); // 좌표계 변환
            int xIdx = binIndex(x, xBins);
            int yIdx = binIndex(y, yBins);
            if (xIdx >= 0 && yIdx >= 0 && xIdx < GRID_SIZE && yIdx < GRID_SIZE)
                heatmap[yIdx][xIdx]++;
        }

        // Top 3
        List<TopZoneDTO> topZones = new ArrayList<>();
        boolean[][] selected = new boolean[GRID_SIZE][GRID_SIZE];

        for (int k = 0; k < TOP_N; k++) {
            int maxX = 0, maxY = 0;
            double maxVal = -1;
            for (int i = 0; i < GRID_SIZE; i++) {
                for (int j = 0; j < GRID_SIZE; j++) {
                    double val = heatmap[i][j];
                    double px = xBins[j];
                    double py = yBins[i];

                    if (!selected[i][j] && val > maxVal && val > 0 ) {
                        maxVal = val;
                        maxX = j;
                        maxY = i;
                    }
                }
            }

            // val > 0인 좌표만 추가
            if (maxVal > 0) {
                selected[maxY][maxX] = true;
                topZones.add(new TopZoneDTO(xBins[maxX], yBins[maxY], Instant.now().toString()));
            } else {
                break; // 유효한 좌표가 더 이상 없으면 중단
            }
        }



        redisTemplate.opsForValue().set("heatmap::" + map, topZones);
        System.out.println("consumer done");
    }


    public List<PoisonZone> getAllZones(){
        return repository.findAll();
    }


    private int binIndex(double val, double[] bins) {
        for (int i = 0; i < bins.length - 1; i++) {
            if (val >= bins[i] && val < bins[i + 1]) return i;
        }
        return -1;
    }

    private double[] linspace() {
        double[] result = new double[MiramaHeatMapService.GRID_SIZE];
        double step = ((double) MiramaHeatMapService.MAP_SIZE - (double) 0) / (MiramaHeatMapService.GRID_SIZE - 1);
        for (int i = 0; i < MiramaHeatMapService.GRID_SIZE; i++) result[i] = (double) 0 + step * i;
        return result;
    }

}

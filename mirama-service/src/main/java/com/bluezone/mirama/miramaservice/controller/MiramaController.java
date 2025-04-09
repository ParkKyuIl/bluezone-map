package com.bluezone.mirama.miramaservice.controller;

import com.bluezone.mirama.miramaservice.service.MiramaHeatMapService;
import com.bluezone.mirama.miramaservice.view.PoisonZone;
import com.bluezone.mirama.miramaservice.view.TopZoneDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/erangel")
public class MiramaController {

    private final MiramaHeatMapService erangelService;
    private final RedisTemplate<String, Object> redisTemplate;

    // 🔄 에란겔 히트맵 캐싱
    @GetMapping("/cache")
    public String cacheErangelHeatmap() {
        erangelService.calculateAndCacheHeatmap();
        return "✅ Heatmap cached for Erangel";
    }

    // 🔄 에란겔 히트맵 캐싱
    @GetMapping("/zones")
    public List<PoisonZone> getAllZones() {
        return erangelService.getAllZones();
    }

    // 🔍 캐싱된 에란겔 히트맵 조회
    @GetMapping("/heatmap")
    public List<TopZoneDTO> getErangelHeatmap() {
        Object data = redisTemplate.opsForValue().get("heatmap::Baltic_Main");
        System.out.println(data);
        if (data instanceof List<?>) {
            return (List<TopZoneDTO>) data;
        }
        return Collections.emptyList();
    }
}

package com.bluezone.mirama.miramaservice.repository;

import com.bluezone.mirama.miramaservice.view.PoisonZone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MiramaRepository extends JpaRepository<PoisonZone, Long> {
    // 기본 CRUD 사용 가능

//    @Query(value = """
//        SELECT x, y, COUNT(*) AS freq
//        FROM poison_zone
//        GROUP BY x, y
//        ORDER BY freq DESC
//        LIMIT 3
//    """, nativeQuery = true)
//    List<TopZoneDTO> findTop3Zones();

    List<PoisonZone> findAllByMap(String map);
}

package com.bluezone.erangel.erangelservice.repository;

import com.bluezone.erangel.erangelservice.view.PoisonZone;
import com.bluezone.erangel.erangelservice.view.TopZoneDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ErangelRepository extends JpaRepository<PoisonZone, Long> {
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

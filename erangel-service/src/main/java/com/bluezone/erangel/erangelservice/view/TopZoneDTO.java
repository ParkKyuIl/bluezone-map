package com.bluezone.erangel.erangelservice.view;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TopZoneDTO {

    private double x;
    private double y;

    private String cachedAt; // 저장 시각

}
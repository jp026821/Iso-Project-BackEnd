package com.example.loginframe.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IsoStandardDTO {

    private Long isoId;
    private String isoCode;
    private String isoName;


    public IsoStandardDTO(String isoCode, String isoName, Long isoId) {
        this.isoId = isoId;
        this.isoCode = isoCode;
        this.isoName = isoName;
    }
}

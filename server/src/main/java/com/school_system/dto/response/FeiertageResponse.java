package com.school_system.dto.response;

import com.school_system.entity.school.Feiertag;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FeiertageResponse {

    private String status;
    @Builder.Default
    private List<Feiertag> feiertage = new ArrayList<>();

}

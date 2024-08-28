package com.school_system.entity.school;




import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;



@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Feiertag {

    private String date;
    private String fname;
    private String all_states;
    private String nw;

}

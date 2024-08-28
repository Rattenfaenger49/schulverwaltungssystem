package com.school_system.common;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.*;
import org.springframework.http.HttpStatus;

import java.net.http.HttpResponse;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResponseObject<T>{

    private ResponseStatus status;

    private String message;

    private T data;



    public enum ResponseStatus {

        SUCCESSFUL("Successful"),

        FAILED("Failed");

        private final String value;


        ResponseStatus(String value)
        {
            this.value = value;
        }


        @JsonValue
        public String getValue()
        {
            return value;
        }
    }
}

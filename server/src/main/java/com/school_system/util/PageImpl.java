package com.school_system.util;
/*
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;

class RestResponsePage<T> extends PageImpl<T> {

    @Serial
    private static final long serialVersionUID = 867755909294344407L;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public RestResponsePage(@JsonProperty("content") List<T> content, @JsonProperty("number") int number, @JsonProperty("size") int size,
                            @JsonProperty("totalElements") Long totalElements, @JsonProperty("pageable") JsonNode pageable, @JsonProperty("last") boolean last,
                            @JsonProperty("totalPages") int totalPages, @JsonProperty("sort") JsonNode sort, @JsonProperty("first") boolean first,
                            @JsonProperty("numberOfElements") int numberOfElements) {
        super(content, PageRequest.of(number, size), totalElements);
    }
    @JsonCreator
    public RestResponsePage(@JsonProperty("content")List<T> content,@JsonProperty("pageable") Pageable pageable, long total) {
        super(content, pageable, total);
    }
    @JsonCreator
    public RestResponsePage(@JsonProperty("content")List<T> content) {
        super(content);
    }
    @JsonCreator
    public RestResponsePage() {
        super(new ArrayList<T>());
    }

}

 */
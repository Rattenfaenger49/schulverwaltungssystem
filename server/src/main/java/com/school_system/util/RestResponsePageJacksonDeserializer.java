package com.school_system.util;
/*

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.domain.PageRequest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RestResponsePageJacksonDeserializer extends JsonDeserializer<RestResponsePage<?>> {


    private final  ObjectMapper objectMapper;

    public RestResponsePageJacksonDeserializer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public RestResponsePage<?> deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
            throws IOException {

        JsonNode node = jsonParser.getCodec().readTree(jsonParser);

        JsonNode contentNode = node.get("content");
        JsonNode pageNode = node.get("page");

        List<Object> contentList = new ArrayList<>();
        if (contentNode.isArray()) {
            for (JsonNode elementNode : contentNode) {
                Object element = objectMapper.treeToValue(elementNode, Object.class);
                contentList.add(element);
            }
        }

        boolean first = pageNode.get("first").asBoolean();
        boolean last = pageNode.get("last").asBoolean();
        int size = pageNode.get("size").asInt();
        long totalElements = pageNode.get("totalElements").asLong();
        int totalPages = pageNode.get("totalPages").asInt();
        int number = pageNode.get("number").asInt();

        PageRequest pageRequest = PageRequest.of(number, size);

        return new RestResponsePage<>(contentList, pageRequest, totalElements);
    }
}

 */
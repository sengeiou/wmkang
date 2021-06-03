package wmkang.common.jackson;


import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import wmkang.common.api.Status;


public class StatusDeserializer extends JsonDeserializer<Status> {


    static Map<Integer, Status> statusMap = new HashMap<>();

    static {
        Stream.of(Status.values()).forEach(s -> statusMap.put(s.getCode(), s));
    }

    @Override
    public Status deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        int code = node.get("code").asInt();
        Status matching = statusMap.get(code);
        if(matching == null)
            new IllegalArgumentException("Undefined code - '" + code + "'");
        return matching;
    }
}

package dev.bolohonov.filmorate.model;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import dev.bolohonov.filmorate.storage.MpaDbStorage;

import java.io.IOException;

public class MpaDeSerializer extends JsonDeserializer<Mpa> {
    private final MpaDbStorage mpaDbStorage;

    public MpaDeSerializer(MpaDbStorage mpaDbStorage) {
        this.mpaDbStorage = mpaDbStorage;
    }

    @Override
    public Mpa deserialize(JsonParser jp, DeserializationContext deserializationContext)
            throws IOException, JacksonException {
        JsonNode node = jp.getCodec().readTree(jp);
        int id = (Integer) (node.get("id")).numberValue();
        return mpaDbStorage.getMpaById(id).get();
    }
}

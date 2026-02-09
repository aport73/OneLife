package xyz.acyber.oneLife.Serialization;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import org.bukkit.Material;

import java.lang.reflect.Type;

/**
 * Gson TypeAdapter for org.bukkit.Material.
 * Serializes as the enum name (String). Deserializes case-insensitively; returns null for unknown names.
 */
public class MaterialTypeAdapter implements JsonSerializer<Material>, JsonDeserializer<Material> {

    @Override
    public JsonElement serialize(Material src, Type typeOfSrc, JsonSerializationContext context) {
        if (src == null) return null;
        return new JsonPrimitive(src.name());
    }

    @Override
    public Material deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (json == null || json.isJsonNull()) return null;
        try {
            String name = json.getAsString();
            if (name == null) return null;
            return Material.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException ex) {
            // Unknown material name; return null so calling code can handle it
            return null;
        }
    }
}


package xyz.acyber.oneLife.Serialization;

import java.lang.reflect.Type;

import org.bukkit.NamespacedKey;
import org.bukkit.potion.PotionEffectType;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * Gson TypeAdapter for org.bukkit.potion.PotionEffectType.
 * Serializes as the effect's namespaced key (String). Deserializes case-insensitively;
 * returns null for unknown or legacy placeholder objects.
 */
public class PotionEffectTypeAdapter implements JsonSerializer<PotionEffectType>, JsonDeserializer<PotionEffectType> {

    @Override
    public JsonElement serialize(PotionEffectType src, Type typeOfSrc, JsonSerializationContext context) {
        if (src == null) return null;
        return new JsonPrimitive(src.getKey().toString());
    }

    @Override
    public PotionEffectType deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (json == null || json.isJsonNull()) return null;

        if (json.isJsonObject()) {
            JsonObject obj = json.getAsJsonObject();
            if (obj.has("key") && obj.get("key").isJsonPrimitive()) {
                return fromKeyString(obj.get("key").getAsString());
            }
            if (obj.has("name") && obj.get("name").isJsonPrimitive()) {
                return fromKeyString(obj.get("name").getAsString());
            }
            if (obj.has("type") && obj.get("type").isJsonPrimitive()) {
                return fromKeyString(obj.get("type").getAsString());
            }
            return null;
        }

        if (!json.isJsonPrimitive()) return null;
        return fromKeyString(json.getAsString());
    }

    private PotionEffectType fromKeyString(String raw) {
        if (raw == null || raw.isEmpty()) return null;
        NamespacedKey key = NamespacedKey.fromString(raw);
        if (key == null) {
            key = NamespacedKey.minecraft(raw.toLowerCase());
        }
        return PotionEffectType.getByKey(key);
    }
}

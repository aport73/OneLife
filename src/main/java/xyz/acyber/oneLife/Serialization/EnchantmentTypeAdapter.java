package xyz.acyber.oneLife.Serialization;

import java.lang.reflect.Type;

import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * Gson TypeAdapter for org.bukkit.enchantments.Enchantment.
 * Serializes as the enchantment's namespaced key (String). Deserializes case-insensitively;
 * returns null for unknown or legacy placeholder objects.
 */
public class EnchantmentTypeAdapter implements JsonSerializer<Enchantment>, JsonDeserializer<Enchantment> {

    @Override
    public JsonElement serialize(Enchantment src, Type typeOfSrc, JsonSerializationContext context) {
        if (src == null) return null;
        return new JsonPrimitive(src.getKey().toString());
    }

    @Override
    public Enchantment deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (json == null || json.isJsonNull()) return null;

        // Legacy/placeholder object from older serialization: {"handle":{}}
        if (json.isJsonObject()) {
            JsonObject obj = json.getAsJsonObject();
            if (obj.has("key") && obj.get("key").isJsonPrimitive()) {
                return fromKeyString(obj.get("key").getAsString());
            }
            if (obj.has("name") && obj.get("name").isJsonPrimitive()) {
                return fromKeyString(obj.get("name").getAsString());
            }
            return null;
        }

        if (!json.isJsonPrimitive()) return null;
        String raw = json.getAsString();
        return fromKeyString(raw);
    }

    private Enchantment fromKeyString(String raw) {
        if (raw == null || raw.isEmpty()) return null;
        NamespacedKey key = NamespacedKey.fromString(raw);
        if (key == null) {
            key = NamespacedKey.minecraft(raw.toLowerCase());
        }
        return Enchantment.getByKey(key);
    }
}

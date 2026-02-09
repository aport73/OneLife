package xyz.acyber.oneLife.Serialization;

import java.lang.reflect.Type;

import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;

/**
 * Gson TypeAdapter for org.bukkit.attribute.Attribute.
 * Serializes as the namespaced key (String). Deserializes from either a namespaced key
 * (e.g., "minecraft:scale") or a legacy enum-style name (e.g., "SCALE").
 */
public class AttributeTypeAdapter implements JsonSerializer<Attribute>, JsonDeserializer<Attribute> {

    @Override
    public JsonElement serialize(Attribute src, Type typeOfSrc, JsonSerializationContext context) {
        if (src == null) return null;
        NamespacedKey key = src.getKey();
        return new JsonPrimitive(key.toString());
    }

    @Override
    public Attribute deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (json == null || json.isJsonNull()) return null;
        String name = json.getAsString();
        if (name == null || name.isBlank()) return null;

        NamespacedKey key;
        if (name.contains(":")) {
            key = NamespacedKey.fromString(name.toLowerCase());
        } else {
            key = NamespacedKey.minecraft(name.toLowerCase());
        }
        if (key == null) return null;
        return RegistryAccess.registryAccess().getRegistry(RegistryKey.ATTRIBUTE).get(key);
    }
}

package xyz.acyber.oneLife.Serialization;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.lang.reflect.Type;

/**
 * Gson adapter for org.bukkit.Location. Serializes as an object:
 * { "world": "world_name", "x": 0.0, "y": 0.0, "z": 0.0, "yaw": 0.0, "pitch": 0.0 }
 * Deserializes by looking up world by name; if world is missing it will set world=null and still populate coords.
 */
public class LocationTypeAdapter implements JsonSerializer<Location>, JsonDeserializer<Location> {

    @Override
    public JsonElement serialize(Location src, Type typeOfSrc, JsonSerializationContext context) {
        if (src == null) return null;
        JsonObject obj = new JsonObject();
        obj.add("world", new JsonPrimitive(src.getWorld() == null ? "" : src.getWorld().getName()));
        obj.add("x", new JsonPrimitive(src.getX()));
        obj.add("y", new JsonPrimitive(src.getY()));
        obj.add("z", new JsonPrimitive(src.getZ()));
        obj.add("yaw", new JsonPrimitive(src.getYaw()));
        obj.add("pitch", new JsonPrimitive(src.getPitch()));
        return obj;
    }

    @Override
    public Location deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (json == null || json.isJsonNull()) return null;
        JsonObject obj = json.getAsJsonObject();
        String worldName = obj.has("world") ? obj.get("world").getAsString() : null;
        World world = null;
        if (worldName != null && !worldName.isEmpty()) {
            world = Bukkit.getWorld(worldName);
        }
        double x = obj.has("x") ? obj.get("x").getAsDouble() : 0.0;
        double y = obj.has("y") ? obj.get("y").getAsDouble() : 0.0;
        double z = obj.has("z") ? obj.get("z").getAsDouble() : 0.0;
        float yaw = obj.has("yaw") ? obj.get("yaw").getAsFloat() : 0f;
        float pitch = obj.has("pitch") ? obj.get("pitch").getAsFloat() : 0f;
        return new Location(world, x, y, z, yaw, pitch);
    }
}


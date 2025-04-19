package com.mikarific.bugmine.config;

import com.google.gson.GsonBuilder;
import com.mikarific.bugmine.config.annotations.TriggerReload;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;

import java.lang.reflect.Field;
import java.util.Arrays;

public class ServerConfig {
    private static final ConfigClassHandler<ServerConfig> HANDLER = ConfigClassHandler.createBuilder(ServerConfig.class)
            .id(new Identifier("bugmine", "config.server"))
            .serializer(config -> GsonConfigSerializerBuilder.create(config)
                    .setPath(FabricLoader.getInstance().getConfigDir().resolve("bugmine/server.json5"))
                    .appendGsonBuilder(GsonBuilder::setPrettyPrinting)
                    .setJson5(true)
                    .build())
            .build();

    @SerialEntry
    public static boolean allowLevelDryLand = false;

    @SerialEntry
    public static boolean applyLevelBiomeTags = true;

    @SerialEntry
    public static boolean functionalShields = true;

    @SerialEntry
    public static boolean obtainableDragonFire = true;

    @SerialEntry @TriggerReload
    public static boolean obtainableInItTogether = true;

    @SerialEntry
    public static boolean obtainableNoDrops = true;

    @SerialEntry
    public static boolean preventCtrlQFreeze = true;

    @SerialEntry
    public static boolean preventIngredientSwapping = true;

    @SerialEntry
    public static boolean preventIngredientThrowing = true;

    @SerialEntry
    public static boolean preventPacketDisconnect = true;

    @SerialEntry
    public static boolean preventSoulLinkCrash = true;

    @SerialEntry
    public static boolean rabbitsSpawnsRabbits = true;

    public static void save() {
        HANDLER.save();
    }

    public static void load() {
        HANDLER.load();
    }

    public static String[] getOptions() {
        return Arrays.stream(ServerConfig.class.getFields()).filter((field) -> field.isAnnotationPresent(SerialEntry.class)).map(Field::getName).toArray(String[]::new);
    }

    public static String[] getValues(String option) {
        try {
            if (ServerConfig.class.getField(option).getType() == boolean.class) return new String[]{"true", "false"};
            return new String[]{};
        } catch (NoSuchFieldException e) {
            return new String[]{};
        }
    }
}

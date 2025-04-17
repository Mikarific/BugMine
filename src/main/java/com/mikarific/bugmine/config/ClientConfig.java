package com.mikarific.bugmine.config;

import com.google.gson.GsonBuilder;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;

import java.lang.reflect.Field;
import java.util.Arrays;

public class ClientConfig {
    private static final ConfigClassHandler<ClientConfig> HANDLER = ConfigClassHandler.createBuilder(ClientConfig.class)
            .id(new Identifier("bugmine", "config.client"))
            .serializer(config -> GsonConfigSerializerBuilder.create(config)
                    .setPath(FabricLoader.getInstance().getConfigDir().resolve("bugmine/client.json5"))
                    .appendGsonBuilder(GsonBuilder::setPrettyPrinting)
                    .setJson5(true)
                    .build())
            .build();

    @SerialEntry
    public static boolean allowSoulLinkOnLanServers = FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT;

    @SerialEntry
    public static boolean witherSkeletonsNotZombifiedPiglins = true;

    public static void save() {
        HANDLER.save();
    }

    public static void load() {
        HANDLER.load();
    }

    public static String[] getOptions() {
        return Arrays.stream(ClientConfig.class.getFields()).filter((field) -> field.isAnnotationPresent(SerialEntry.class)).map(Field::getName).toArray(String[]::new);
    }
}

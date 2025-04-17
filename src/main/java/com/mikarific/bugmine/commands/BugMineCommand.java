package com.mikarific.bugmine.commands;

import com.mikarific.bugmine.config.ClientConfig;
import com.mikarific.bugmine.config.ServerConfig;
import com.mikarific.bugmine.config.annotations.TriggerReload;
import com.mikarific.bugmine.networking.ServerNetworkingHandler;
import com.mikarific.bugmine.networking.payloads.BugMineConfigPayloadS2C;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.logging.LogUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.apache.commons.lang3.ArrayUtils;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import static net.minecraft.command.CommandSource.suggestMatching;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class BugMineCommand {
    private static Field getFieldFromOptionName(String option) {
        try {
            if (Arrays.stream(ServerConfig.class.getFields()).anyMatch(f -> f.getName().equals(option))) {
                return ServerConfig.class.getField(option);
            } else {
                return ClientConfig.class.getField(option);
            }
        } catch (NoSuchFieldException e) {
            return null;
        }
    }

    private static Object getValueFromField(Field field) {
        if (field == null) return null;
        try {
            return field.get(null);
        } catch (IllegalAccessException e) {
            return null;
        }
    }

    private static String[] getOptions() {
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER) {
            return ServerConfig.getOptions();
        } else {
            return ArrayUtils.addAll(ClientConfig.getOptions(), ServerConfig.getOptions());
        }
    }

    public static String[] getValues(String option) {
        Field field = getFieldFromOptionName(option);
        if (field == null) return new String[]{};
        if (field.getType() == boolean.class) return new String[]{"true", "false"};
        return new String[]{};
    }

    private static void saveOption(String option) {
        if (Arrays.stream(ServerConfig.class.getFields()).anyMatch(f -> f.getName().equals(option))) {
            ServerConfig.save();
        }
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            if (Arrays.stream(ClientConfig.class.getFields()).anyMatch(f -> f.getName().equals(option))) {
                ClientConfig.save();
            }
        }
    }

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("bugmine")
            .requires((source) -> FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT || source.hasPermissionLevel(2))
            .then(argument("option", StringArgumentType.word())
                .suggests((context, builder) -> suggestMatching(getOptions(), builder))
                .executes(context -> showOption(context.getSource(), StringArgumentType.getString(context, "option")))
                .then(argument("value", StringArgumentType.word())
                    .suggests((context, builder) -> suggestMatching(getValues(StringArgumentType.getString(context, "option")), builder))
                    .executes(context -> setOption(context.getSource(), StringArgumentType.getString(context, "option"), StringArgumentType.getString(context, "value")))
                )
            )
        );
    }

    private static int setOption(ServerCommandSource source, String option, String value) throws CommandSyntaxException {
        if (!List.of(getOptions()).contains(option)) throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherParseException().create("BugMine config option not found");

        Field field = getFieldFromOptionName(option);
        if (field == null) throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherParseException().create("BugMine config option not found");

        Object parsedValue = null;
        if (field.getType() == boolean.class) parsedValue = Arrays.asList(getValues(option)).contains(value.toLowerCase()) ? Boolean.parseBoolean(value) : null;
        if (parsedValue == null) throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherParseException().create("Can't set config option to that value");
        try {
            field.set(null, parsedValue);
        } catch (IllegalAccessException e) {
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherParseException().create("Can't set config option to that value");
        }

        if (field.isAnnotationPresent(TriggerReload.class)) {
            //noinspection resource
            source.method_69818().reloadResources(source.method_69818().getDataPackManager().getEnabledIds()).exceptionally((throwable) -> {
                LogUtils.getLogger().warn("Failed to execute reload", throwable);
                return null;
            });
        }

        saveOption(option);

        //noinspection resource
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER) {
            for (ServerPlayerEntity player : ServerNetworkingHandler.getPlayersWithClientMod(source.method_69818().getPlayerManager())) {
                ServerPlayNetworking.send(player, new BugMineConfigPayloadS2C(option, parsedValue.toString()));
            }
        }
        Object finalParsedValue = parsedValue;
        source.sendFeedback(() -> Text.literal("")
                .append(Text.translatable("bugmine.options." + option + ".name").setStyle(Style.EMPTY.withBold(true)))
                .append(Text.literal(": ").setStyle(Style.EMPTY))
                .append(Text.literal(finalParsedValue.toString()).setStyle(getValueStyle(finalParsedValue))), true);
        return 1;
    }

    private static int showOption(ServerCommandSource source, String option) throws CommandSyntaxException {
        if (!List.of(getOptions()).contains(option)) throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherParseException().create("BugMine config option not found");

        Object value = getValueFromField(getFieldFromOptionName(option));
        if (value == null) throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherParseException().create("BugMine config option not found");
        source.sendFeedback(() -> Text.literal("")
                .append(Text.translatable("bugmine.options." + option + ".name").setStyle(Style.EMPTY.withBold(true)))
                .append(Text.literal("\n").setStyle(Style.EMPTY))
                .append(Text.translatable("bugmine.options." + option + ".description"))
                .append(Text.literal("\n"))
                .append(Text.literal("Current Value: "))
                .append(Text.literal(value.toString()).setStyle(getValueStyle(value))), false);
        return 1;
    }

    private static Style getValueStyle(Object value) {
        if (value.toString().equals("true")) return Style.EMPTY.withBold(true).withColor(Formatting.GREEN);
        if (value.toString().equals("false")) return Style.EMPTY.withBold(true).withColor(Formatting.RED);
        return Style.EMPTY.withBold(true).withColor(Formatting.WHITE);
    }
}

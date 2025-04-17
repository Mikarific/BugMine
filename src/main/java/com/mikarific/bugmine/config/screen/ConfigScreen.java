package com.mikarific.bugmine.config.screen;

import com.mikarific.bugmine.config.ClientConfig;
import com.mikarific.bugmine.config.ServerConfig;
import com.mikarific.bugmine.networking.ClientNetworkingHandler;
import com.mikarific.bugmine.networking.payloads.BugMineConfigPayloadC2S;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class ConfigScreen {
    private static boolean isSingleplayerOrOp() {
        if (MinecraftClient.getInstance().getCurrentServerEntry() != null) {
            return MinecraftClient.getInstance().player != null && MinecraftClient.getInstance().player.hasPermissionLevel(2);
        } else {
            return true;
        }
    }

    private static boolean isSingleplayerOrDisconnected() {
        return MinecraftClient.getInstance().getCurrentServerEntry() == null;
    }

    private static boolean serverHasBugmine() {
        return ClientNetworkingHandler.isOnServer();
    }

    public static Screen getScreen(Screen parent) {
        return YetAnotherConfigLib.createBuilder()
                .title(Text.translatable("bugmine.config.title"))
                .category(ConfigCategory.createBuilder()
                        .name(Text.translatable("bugmine.category.bug_fixes.name"))
                        .tooltip(Text.translatable("bugmine.category.bug_fixes.tooltip"))
                        .group(OptionGroup.createBuilder()
                                .name(Text.translatable("bugmine.group.client.name"))
                                .description(OptionDescription.of(Text.translatable("bugmine.group.client.description")))
                                .option(Option.<Boolean>createBuilder()
                                        .name(Text.translatable("bugmine.options.allowSoulLinkOnLanServers.name"))
                                        .description(OptionDescription.of(Text.translatable("bugmine.options.allowSoulLinkOnLanServers.description")))
                                        .available(isSingleplayerOrOp() && isSingleplayerOrDisconnected())
                                        .binding(
                                                ClientConfig.allowSoulLinkOnLanServers,
                                                () -> isSingleplayerOrDisconnected() && ClientConfig.allowSoulLinkOnLanServers,
                                                newVal -> {
                                                    ClientConfig.allowSoulLinkOnLanServers = newVal;
                                                    ClientConfig.save();
                                                }
                                        )
                                        .controller(TickBoxControllerBuilder::create)
                                        .build()
                                )
                                .option(Option.<Boolean>createBuilder()
                                        .name(Text.translatable("bugmine.options.witherSkeletonsNotZombifiedPiglins.name"))
                                        .description(OptionDescription.of(Text.translatable("bugmine.options.witherSkeletonsNotZombifiedPiglins.description")))
                                        .binding(
                                                ClientConfig.witherSkeletonsNotZombifiedPiglins,
                                                () -> ClientConfig.witherSkeletonsNotZombifiedPiglins,
                                                newVal -> {
                                                    ClientConfig.witherSkeletonsNotZombifiedPiglins = newVal;
                                                    ClientConfig.save();
                                                }
                                        )
                                        .controller(TickBoxControllerBuilder::create)
                                        .build()
                                )
                                .build()
                        )
                        .group(OptionGroup.createBuilder()
                                .name(Text.translatable("bugmine.group.server.name"))
                                .description(OptionDescription.of(Text.translatable("bugmine.group.server.description")))
                                .option(Option.<Boolean>createBuilder()
                                        .name(Text.translatable("bugmine.options.functionalShields.name"))
                                        .description(OptionDescription.of(Text.translatable("bugmine.options.functionalShields.description")))
                                        .available(serverHasBugmine() && isSingleplayerOrOp())
                                        .binding(
                                                ServerConfig.functionalShields,
                                                () -> ServerConfig.functionalShields,
                                                newVal -> {
                                                    ServerConfig.functionalShields = newVal;
                                                    if (isSingleplayerOrDisconnected()) {
                                                        ServerConfig.save();
                                                    } else {
                                                        ClientPlayNetworking.send(new BugMineConfigPayloadC2S("functionalShields", newVal.toString()));
                                                    }
                                                }
                                        )
                                        .controller(TickBoxControllerBuilder::create)
                                        .build()
                                )
                                .option(Option.<Boolean>createBuilder()
                                        .name(Text.translatable("bugmine.options.obtainableDragonFire.name"))
                                        .description(OptionDescription.of(Text.translatable("bugmine.options.obtainableDragonFire.description")))
                                        .available(serverHasBugmine() && isSingleplayerOrOp())
                                        .binding(
                                                ServerConfig.obtainableDragonFire,
                                                () -> ServerConfig.obtainableDragonFire,
                                                newVal -> {
                                                    ServerConfig.obtainableDragonFire = newVal;
                                                    if (isSingleplayerOrDisconnected()) {
                                                        ServerConfig.save();
                                                    } else {
                                                        ClientPlayNetworking.send(new BugMineConfigPayloadC2S("obtainableDragonFire", newVal.toString()));
                                                    }
                                                }
                                        )
                                        .controller(TickBoxControllerBuilder::create)
                                        .build()
                                )
                                .option(Option.<Boolean>createBuilder()
                                        .name(Text.translatable("bugmine.options.obtainableInItTogether.name"))
                                        .description(OptionDescription.of(Text.translatable("bugmine.options.obtainableInItTogether.description")))
                                        .available(serverHasBugmine() && isSingleplayerOrOp())
                                        .binding(
                                                ServerConfig.obtainableInItTogether,
                                                () -> ServerConfig.obtainableInItTogether,
                                                newVal -> {
                                                    ServerConfig.obtainableInItTogether = newVal;
                                                    if (isSingleplayerOrDisconnected()) {
                                                        ServerConfig.save();
                                                    } else {
                                                        ClientPlayNetworking.send(new BugMineConfigPayloadC2S("obtainableInItTogether", newVal.toString()));
                                                    }
                                                }
                                        )
                                        .controller(TickBoxControllerBuilder::create)
                                        .build()
                                )
                                .option(Option.<Boolean>createBuilder()
                                        .name(Text.translatable("bugmine.options.obtainableNoDrops.name"))
                                        .description(OptionDescription.of(Text.translatable("bugmine.options.obtainableNoDrops.description")))
                                        .available(serverHasBugmine() && isSingleplayerOrOp())
                                        .binding(
                                                ServerConfig.obtainableNoDrops,
                                                () -> ServerConfig.obtainableNoDrops,
                                                newVal -> {
                                                    ServerConfig.obtainableNoDrops = newVal;
                                                    if (isSingleplayerOrDisconnected()) {
                                                        ServerConfig.save();
                                                    } else {
                                                        ClientPlayNetworking.send(new BugMineConfigPayloadC2S("obtainableInItTogether", newVal.toString()));
                                                    }
                                                }
                                        )
                                        .controller(TickBoxControllerBuilder::create)
                                        .build()
                                )
                                .option(Option.<Boolean>createBuilder()
                                        .name(Text.translatable("bugmine.options.preventCtrlQFreeze.name"))
                                        .description(OptionDescription.of(Text.translatable("bugmine.options.preventCtrlQFreeze.description")))
                                        .available(serverHasBugmine() && isSingleplayerOrOp())
                                        .binding(
                                                ServerConfig.preventCtrlQFreeze,
                                                () -> ServerConfig.preventCtrlQFreeze,
                                                newVal -> {
                                                    ServerConfig.preventCtrlQFreeze = newVal;
                                                    if (isSingleplayerOrDisconnected()) {
                                                        ServerConfig.save();
                                                    } else {
                                                        ClientPlayNetworking.send(new BugMineConfigPayloadC2S("preventCtrlQFreeze", newVal.toString()));
                                                    }
                                                }
                                        )
                                        .controller(TickBoxControllerBuilder::create)
                                        .build()
                                )
                                .option(Option.<Boolean>createBuilder()
                                        .name(Text.translatable("bugmine.options.preventIngredientSwapping.name"))
                                        .description(OptionDescription.of(Text.translatable("bugmine.options.preventIngredientSwapping.description")))
                                        .available(serverHasBugmine() && isSingleplayerOrOp())
                                        .binding(
                                                ServerConfig.preventIngredientSwapping,
                                                () -> ServerConfig.preventIngredientSwapping,
                                                newVal -> {
                                                    ServerConfig.preventIngredientSwapping = newVal;
                                                    if (isSingleplayerOrDisconnected()) {
                                                        ServerConfig.save();
                                                    } else {
                                                        ClientPlayNetworking.send(new BugMineConfigPayloadC2S("preventIngredientSwapping", newVal.toString()));
                                                    }
                                                }
                                        )
                                        .controller(TickBoxControllerBuilder::create)
                                        .build()
                                )
                                .option(Option.<Boolean>createBuilder()
                                        .name(Text.translatable("bugmine.options.preventIngredientThrowing.name"))
                                        .description(OptionDescription.of(Text.translatable("bugmine.options.preventIngredientThrowing.description")))
                                        .available(serverHasBugmine() && isSingleplayerOrOp())
                                        .binding(
                                                ServerConfig.preventIngredientThrowing,
                                                () -> ServerConfig.preventIngredientThrowing,
                                                newVal -> {
                                                    ServerConfig.preventIngredientThrowing = newVal;
                                                    if (isSingleplayerOrDisconnected()) {
                                                        ServerConfig.save();
                                                    } else {
                                                        ClientPlayNetworking.send(new BugMineConfigPayloadC2S("preventIngredientThrowing", newVal.toString()));
                                                    }
                                                }
                                        )
                                        .controller(TickBoxControllerBuilder::create)
                                        .build()
                                )
                                .option(Option.<Boolean>createBuilder()
                                        .name(Text.translatable("bugmine.options.preventPacketDisconnect.name"))
                                        .description(OptionDescription.of(Text.translatable("bugmine.options.preventPacketDisconnect.description")))
                                        .available(serverHasBugmine() && isSingleplayerOrOp())
                                        .binding(
                                                ServerConfig.preventPacketDisconnect,
                                                () -> ServerConfig.preventPacketDisconnect,
                                                newVal -> {
                                                    ServerConfig.preventPacketDisconnect = newVal;
                                                    if (isSingleplayerOrDisconnected()) {
                                                        ServerConfig.save();
                                                    } else {
                                                        ClientPlayNetworking.send(new BugMineConfigPayloadC2S("preventPacketDisconnect", newVal.toString()));
                                                    }
                                                }
                                        )
                                        .controller(TickBoxControllerBuilder::create)
                                        .build()
                                )
                                .option(Option.<Boolean>createBuilder()
                                        .name(Text.translatable("bugmine.options.preventSoulLinkCrash.name"))
                                        .description(OptionDescription.of(Text.translatable("bugmine.options.preventSoulLinkCrash.description")))
                                        .available(serverHasBugmine() && isSingleplayerOrOp())
                                        .binding(
                                                ServerConfig.preventSoulLinkCrash,
                                                () -> ServerConfig.preventSoulLinkCrash,
                                                newVal -> {
                                                    ServerConfig.preventSoulLinkCrash = newVal;
                                                    if (isSingleplayerOrDisconnected()) {
                                                        ServerConfig.save();
                                                    } else {
                                                        ClientPlayNetworking.send(new BugMineConfigPayloadC2S("preventSoulLinkCrash", newVal.toString()));
                                                    }
                                                }
                                        )
                                        .controller(TickBoxControllerBuilder::create)
                                        .build()
                                )
                                .option(Option.<Boolean>createBuilder()
                                        .name(Text.translatable("bugmine.options.rabbitsSpawnsRabbits.name"))
                                        .description(OptionDescription.of(Text.translatable("bugmine.options.rabbitsSpawnsRabbits.description")))
                                        .available(serverHasBugmine() && isSingleplayerOrOp())
                                        .binding(
                                                ServerConfig.rabbitsSpawnsRabbits,
                                                () -> ServerConfig.rabbitsSpawnsRabbits,
                                                newVal -> {
                                                    ServerConfig.rabbitsSpawnsRabbits = newVal;
                                                    if (isSingleplayerOrDisconnected()) {
                                                        ServerConfig.save();
                                                    } else {
                                                        ClientPlayNetworking.send(new BugMineConfigPayloadC2S("rabbitsSpawnsRabbits", newVal.toString()));
                                                    }
                                                }
                                        )
                                        .controller(TickBoxControllerBuilder::create)
                                        .build()
                                )
                                .build()
                        )
                        .build()
                )
                .category(ConfigCategory.createBuilder()
                                .name(Text.translatable("bugmine.category.qol.name"))
                                .tooltip(Text.translatable("bugmine.category.qol.tooltip"))
//                .group(OptionGroup.createBuilder()
//                    .name(Text.translatable("bugmine.group.client.name"))
//                    .description(OptionDescription.of(Text.translatable("bugmine.group.client.description")))
//                    .build()
//                )
//                .group(OptionGroup.createBuilder()
//                    .name(Text.translatable("bugmine.group.server.name"))
//                    .description(OptionDescription.of(Text.translatable("bugmine.group.server.description")))
//                    .build()
//                )
                                .build()
                )
                .build()
                .generateScreen(parent);
    }
}

package com.mikarific.bugmine.mixins;

import com.mikarific.bugmine.config.Config;
import net.minecraft.aprilfools.WorldEffect;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(WorldEffect.class)
public abstract class WorldEffectMixin {
    @Redirect(method = "isPossibleToUnlock", at = @At(value = "INVOKE", target = "net/minecraft/server/MinecraftServer.isSingleplayer()Z"))
    private boolean allowSoulLinkOnLanServers$isPossibleToUnlock(MinecraftServer server) {
        return Config.allowSoulLinkOnLanServers ? !server.isRemote() : server.isSingleplayer();
    }

    @Redirect(method = "method_69927", at = @At(value = "INVOKE", target = "net/minecraft/server/MinecraftServer.isSingleplayer()Z"))
    private boolean allowSoulLinkOnLanServers$isPossibleToRandomize(MinecraftServer server) {
        return Config.allowSoulLinkOnLanServers ? !server.isRemote() : server.isSingleplayer();
    }
}

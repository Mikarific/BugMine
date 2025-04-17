package com.mikarific.bugmine.mixins.fixes;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mikarific.bugmine.config.ClientConfig;
import net.minecraft.aprilfools.WorldEffect;
import net.minecraft.client.render.item.model.MineIngredientItemModel;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(MineIngredientItemModel.class)
public class MineIngredientItemModelMixin {
    @WrapOperation(method = "method_70520", at = @At(value = "INVOKE", target = "net/minecraft/aprilfools/WorldEffect.itemModel()Lnet/minecraft/util/Identifier;"))
    private static Identifier witherSkeletonsNotZombifiedPiglins(WorldEffect worldEffect, Operation<Identifier> original) {
        if (ClientConfig.witherSkeletonsNotZombifiedPiglins) {
            if (worldEffect.id().equals("wither_skeletons")) return Items.WITHER_SKELETON_SPAWN_EGG.getComponents().get(DataComponentTypes.ITEM_MODEL);
        }
        return original.call(worldEffect);
    }
}

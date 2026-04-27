package com.dace.vanillaplus.mixin.world.level.block.entity;

import com.dace.vanillaplus.data.registryobject.VPAttributes;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(BeaconBlockEntity.class)
public abstract class BeaconBlockEntityMixin extends BlockEntityMixin<BeaconBlockEntity> {
    @Redirect(method = "applyEffects", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/level/Level;getEntitiesOfClass(Ljava/lang/Class;Lnet/minecraft/world/phys/AABB;)Ljava/util/List;"))
    @SuppressWarnings("unchecked")
    private static List<Player> modifyEffectPlayerList(Level instance, Class<Player> baseClass, AABB bb) {
        return (List<Player>) instance.players();
    }

    @WrapOperation(method = "applyEffects", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/player/Player;addEffect(Lnet/minecraft/world/effect/MobEffectInstance;)Z"))
    private static boolean checkEffectRange(Player instance, MobEffectInstance newEffect, Operation<Boolean> original, @Local(name = "bb") AABB bb) {
        double beaconEffectRange = (instance.getAttributeValue(VPAttributes.BEACON_EFFECT_RANGE.getHolder().orElseThrow()) - 1) * 0.5;

        return bb.inflate(bb.getXsize() * beaconEffectRange, 0, bb.getZsize() * beaconEffectRange)
                .intersects(instance.getBoundingBox()) && original.call(instance, newEffect);
    }
}

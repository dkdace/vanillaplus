package com.dace.vanillaplus.mixin.world.entity.raid;

import com.dace.vanillaplus.data.registryobject.VPGameRules;
import com.dace.vanillaplus.extension.VPMixin;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.entity.raid.Raids;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Raids.class)
public abstract class RaidsMixin implements VPMixin<Raids> {
    @Redirect(method = "createOrExtendRaid", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/raid/Raid;getMaxRaidOmenLevel()I"))
    private int modifyMaxRaidOmenLevel(Raid raid, @Local(name = "level") ServerLevel level) {
        return VPGameRules.getValue(VPGameRules.MAX_BAD_OMEN_LEVEL, level);
    }
}

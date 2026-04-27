package com.dace.vanillaplus.mixin.server.commands;

import com.dace.vanillaplus.data.registryobject.VPGameRules;
import com.dace.vanillaplus.extension.VPMixin;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.commands.RaidCommand;
import net.minecraft.world.entity.raid.Raid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(RaidCommand.class)
public abstract class RaidCommandMixin implements VPMixin<RaidCommand> {
    @Redirect(method = "setRaidOmenLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/raid/Raid;getMaxRaidOmenLevel()I"))
    private static int modifyMaxRaidOmenLevel(Raid raid, @Local(argsOnly = true) CommandSourceStack source) {
        return VPGameRules.getValue(VPGameRules.MAX_BAD_OMEN_LEVEL, source.getLevel());
    }
}

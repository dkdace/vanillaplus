package com.dace.vanillaplus.mixin.world.entity.monster.cubemob;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.monster.cubemob.SulfurCube;
import net.minecraft.world.level.gamerules.GameRules;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(SulfurCube.class)
public abstract class SulfurCubeMixin extends AbstractCubeMobMixin<SulfurCube> {
    @Override
    protected boolean shouldDropLoot(ServerLevel level) {
        return level.getGameRules().get(GameRules.MOB_DROPS);
    }
}

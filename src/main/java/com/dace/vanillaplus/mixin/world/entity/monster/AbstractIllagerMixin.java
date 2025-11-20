package com.dace.vanillaplus.mixin.world.entity.monster;

import com.dace.vanillaplus.data.modifier.EntityModifier;
import com.dace.vanillaplus.mixin.world.entity.raid.RaiderMixin;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.AbstractIllager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(AbstractIllager.class)
public abstract class AbstractIllagerMixin<T extends AbstractIllager, U extends EntityModifier.LivingEntityModifier> extends RaiderMixin<T, U> {
    @Override
    @Overwrite
    public boolean canAttack(LivingEntity target) {
        return super.canAttack(target);
    }
}

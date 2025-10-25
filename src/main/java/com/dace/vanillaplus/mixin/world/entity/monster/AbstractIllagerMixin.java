package com.dace.vanillaplus.mixin.world.entity.monster;

import com.dace.vanillaplus.data.modifier.EntityModifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.AbstractIllager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(AbstractIllager.class)
public abstract class AbstractIllagerMixin<T extends AbstractIllager, U extends EntityModifier.LivingEntityModifier> extends MonsterMixin<T, U> {
    @Override
    @Overwrite
    public boolean canAttack(LivingEntity livingEntity) {
        return super.canAttack(livingEntity);
    }
}

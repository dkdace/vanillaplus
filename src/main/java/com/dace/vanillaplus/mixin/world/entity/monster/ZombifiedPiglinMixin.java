package com.dace.vanillaplus.mixin.world.entity.monster;

import com.dace.vanillaplus.data.modifier.EntityModifier;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.ZombifiedPiglin;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ZombifiedPiglin.class)
public abstract class ZombifiedPiglinMixin extends MonsterMixin<ZombifiedPiglin, EntityModifier.LivingEntityModifier> {
    @Shadow
    public abstract void setTarget(@Nullable LivingEntity target);

    @Shadow
    protected abstract void alertOthers();

    @Override
    protected void onDie(DamageSource damageSource, CallbackInfo ci) {
        Entity entity = damageSource.getEntity();
        if (!(entity instanceof LivingEntity))
            return;

        setTarget((LivingEntity) entity);
        alertOthers();
    }
}

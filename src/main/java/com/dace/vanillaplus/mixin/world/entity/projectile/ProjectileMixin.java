package com.dace.vanillaplus.mixin.world.entity.projectile;

import com.dace.vanillaplus.mixin.world.entity.EntityMixin;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.Projectile;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Projectile.class)
public abstract class ProjectileMixin<T extends Projectile> extends EntityMixin<T> {
    @Shadow
    @Nullable
    public abstract Entity getOwner();
}

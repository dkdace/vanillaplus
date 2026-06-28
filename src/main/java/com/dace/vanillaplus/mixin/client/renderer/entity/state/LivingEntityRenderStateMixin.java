package com.dace.vanillaplus.mixin.client.renderer.entity.state;

import com.dace.vanillaplus.extension.client.renderer.entity.state.VPLivingEntityRenderState;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(LivingEntityRenderState.class)
@Getter
@Setter
public abstract class LivingEntityRenderStateMixin implements VPLivingEntityRenderState {
    @Unique
    private boolean canRenderHealth;
    @Unique
    private float health;
    @Unique
    private float maxHealth;
    @Unique
    private float absorptionHealth;
    @Unique
    private int armor;
    @Unique
    private int armorToughness;
    @Unique
    @NonNull
    private Gui.HeartType heartType = Gui.HeartType.NORMAL;
}

package com.dace.vanillaplus.extension.client.renderer.entity.state;

import com.dace.vanillaplus.extension.VPMixin;
import lombok.NonNull;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;

/**
 * {@link LivingEntityRenderState}를 확장하는 인터페이스.
 */
public interface VPLivingEntityRenderState extends VPMixin<LivingEntityRenderState> {
    @NonNull
    static VPLivingEntityRenderState cast(@NonNull LivingEntityRenderState object) {
        return (VPLivingEntityRenderState) object;
    }

    boolean isCanRenderHealth();

    void setCanRenderHealth(boolean canRenderHealth);

    float getHealth();

    void setHealth(float health);

    float getMaxHealth();

    void setMaxHealth(float maxHealth);

    float getAbsorptionHealth();

    void setAbsorptionHealth(float absorptionHealth);

    int getArmor();

    void setArmor(int armor);

    @NonNull
    Gui.HeartType getHeartType();

    void setHeartType(@NonNull Gui.HeartType heartType);
}

package com.dace.vanillaplus.extension.client.gui;

import com.dace.vanillaplus.extension.VPMixin;
import com.dace.vanillaplus.util.IdentifierUtil;
import lombok.NonNull;
import net.minecraft.client.gui.Gui;
import net.minecraft.resources.Identifier;

/**
 * {@link Gui}를 확장하는 인터페이스.
 */
public interface VPGui extends VPMixin<Gui> {
    /** 방어 강도 스프라이트 식별자 (반 칸) */
    Identifier ARMOR_TOUGHNESS_HALF_SPRITE = IdentifierUtil.fromPath("hud/armor_toughness_half");
    /** 방어 강도 스프라이트 식별자 (한 칸) */
    Identifier ARMOR_TOUGHNESS_FULL_SPRITE = IdentifierUtil.fromPath("hud/armor_toughness_full");
    /** 포만감 스프라이트 식별자 (반 칸) */
    Identifier FOOD_SATURATION_HALF_SPRITE = IdentifierUtil.fromPath("hud/food_saturation_half");
    /** 포만감 스프라이트 식별자 (한 칸) */
    Identifier FOOD_SATURATION_FULL_SPRITE = IdentifierUtil.fromPath("hud/food_saturation_full");

    @NonNull
    static VPGui cast(@NonNull Gui object) {
        return (VPGui) object;
    }

    /**
     * 최근 피해량을 갱신한다.
     *
     * @param damage   피해량
     * @param isKilled 처치 여부
     */
    void updateRecentDamage(float damage, boolean isKilled);
}

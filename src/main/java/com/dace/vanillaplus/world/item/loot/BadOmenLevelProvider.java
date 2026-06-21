package com.dace.vanillaplus.world.item.loot;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.NonNull;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import org.jetbrains.annotations.Nullable;

/**
 * 흉조 레벨 기반 값을 반환하는 숫자 제공자 클래스.
 *
 * @param amount 레벨 기반 값
 */
public record BadOmenLevelProvider(@NonNull LevelBasedValue amount) implements NumberProvider {
    /** JSON 코덱 */
    public static final MapCodec<BadOmenLevelProvider> TYPED_CODEC = RecordCodecBuilder.mapCodec(instance -> instance
            .group(LevelBasedValue.CODEC.fieldOf("amount").forGetter(BadOmenLevelProvider::amount))
            .apply(instance, BadOmenLevelProvider::new));

    /**
     * 지정한 엔티티의 습격 정보를 반환한다.
     *
     * @param entity 엔티티
     * @return 습격 정보
     */
    @Nullable
    private static Raid getRaidFromEntity(@NonNull Entity entity) {
        if (!(entity instanceof Raider raider))
            return null;

        Raid raid = raider.getCurrentRaid();
        return raid != null && raid.isActive() ? raid : null;
    }

    @Override
    @NonNull
    public MapCodec<? extends NumberProvider> codec() {
        return TYPED_CODEC;
    }

    @Override
    public float getFloat(@NonNull LootContext context) {
        Raid raid = getRaidFromEntity(context.getParameter(LootContextParams.THIS_ENTITY));
        return raid == null ? 0 : amount.calculate(raid.getRaidOmenLevel());
    }
}

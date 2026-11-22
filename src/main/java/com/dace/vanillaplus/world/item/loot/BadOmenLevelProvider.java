package com.dace.vanillaplus.world.item.loot;

import com.dace.vanillaplus.extension.world.entity.VPMob;
import com.dace.vanillaplus.extension.world.level.block.entity.trialspawner.VPTrialSpawnerStateData;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.NonNull;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.level.block.entity.TrialSpawnerBlockEntity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;

import java.util.Optional;

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
     * 지정한 엔티티의 습격 징조 레벨을 반환한다.
     *
     * @param entity 엔티티
     * @return 습격 징조 레벨
     */
    @NonNull
    private static Optional<Integer> getRaidOmenLevel(@NonNull Entity entity) {
        if (!(entity instanceof Raider raider))
            return Optional.empty();

        Raid raid = raider.getCurrentRaid();
        return raid != null && raid.isActive() ? Optional.of(raid.getRaidOmenLevel()) : Optional.empty();
    }

    /**
     * 지정한 엔티티의 시련 징조 레벨을 반환한다.
     *
     * @param serverLevel 월드
     * @param entity      엔티티
     * @return 시련 징조 레벨
     */
    @NonNull
    private static Optional<Integer> getTrialOmenLevel(@NonNull ServerLevel serverLevel, @NonNull Entity entity) {
        if (!(entity instanceof Mob mob))
            return Optional.empty();

        BlockPos blockPos = VPMob.cast(mob).getTrialSpawnerPos();
        return blockPos != null && serverLevel.getBlockEntity(blockPos) instanceof TrialSpawnerBlockEntity trialSpawnerBlockEntity
                ? VPTrialSpawnerStateData.cast(trialSpawnerBlockEntity.getTrialSpawner().getStateData()).getTrialOmenLevel()
                : Optional.empty();
    }

    @Override
    @NonNull
    public MapCodec<? extends NumberProvider> codec() {
        return TYPED_CODEC;
    }

    @Override
    public float getFloat(@NonNull LootContext context) {
        Entity entity = context.getParameter(LootContextParams.THIS_ENTITY);
        return getRaidOmenLevel(entity).or(() -> getTrialOmenLevel(context.getLevel(), entity)).map(amount::calculate).orElse(0F);
    }
}

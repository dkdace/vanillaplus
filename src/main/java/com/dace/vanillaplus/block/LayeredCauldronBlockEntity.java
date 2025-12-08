package com.dace.vanillaplus.block;

import com.dace.vanillaplus.extension.world.level.block.VPLayeredCauldronBlock;
import com.dace.vanillaplus.registryobject.VPBlockEntityTypes;
import lombok.Getter;
import lombok.NonNull;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.ARGB;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * 물이 담긴 가마솥 블록 엔티티 클래스.
 */
public final class LayeredCauldronBlockEntity extends BlockEntity {
    /** 담을 수 있는 물약 종류 최대치 */
    private static final int MAX_POTIONS = 3;

    /** 물약 내용물 */
    @Nullable
    @Getter
    private PotionContents potionContents;
    /** 현재 색상 */
    @Getter
    private int color = 0;
    /** 색상 혼합 횟수 */
    private int colorCount = 0;
    /** 투명도 혼합 횟수 */
    private int colorAlphaCount = 0;
    /** 투명도 합계 */
    private float colorAlpha = 0;
    /** 빨간색 합계 */
    private float colorRed = 0;
    /** 초록색 합계 */
    private float colorGreen = 0;
    /** 파란색 합계 */
    private float colorBlue = 0;

    public LayeredCauldronBlockEntity(@NonNull BlockPos blockPos, @NonNull BlockState blockState) {
        super(VPBlockEntityTypes.LAYERED_CAULDRON.get(), blockPos, blockState);
    }

    /**
     * 서버에서 매 틱마다 실행할 작업.
     *
     * @param serverLevel                월드
     * @param blockPos                   블록 위치
     * @param blockState                 블록 상태
     * @param layeredCauldronBlockEntity 현재 블록 엔티티
     */
    public static void serverTick(ServerLevel serverLevel, BlockPos blockPos, BlockState blockState,
                                  LayeredCauldronBlockEntity layeredCauldronBlockEntity) {
        if (blockState.getValue(VPLayeredCauldronBlock.UPDATE_COLOR))
            serverLevel.setBlockAndUpdate(blockPos, blockState.setValue(VPLayeredCauldronBlock.UPDATE_COLOR, false));
    }

    @Override
    protected void loadAdditional(@NonNull ValueInput valueInput) {
        super.loadAdditional(valueInput);

        color = valueInput.getIntOr("Color", 0);
        colorCount = valueInput.getIntOr("ColorCount", 0);
        colorAlphaCount = valueInput.getIntOr("ColorAlphaCount", 0);
        colorAlpha = valueInput.getFloatOr("ColorAlpha", 0);
        colorRed = valueInput.getFloatOr("ColorRed", 0);
        colorGreen = valueInput.getFloatOr("ColorGreen", 0);
        colorBlue = valueInput.getFloatOr("ColorBlue", 0);
        potionContents = valueInput.read("PotionContents", PotionContents.CODEC).orElse(null);
    }

    @Override
    protected void saveAdditional(@NonNull ValueOutput valueOutput) {
        super.saveAdditional(valueOutput);

        valueOutput.putInt("Color", color);
        valueOutput.putInt("ColorCount", colorCount);
        valueOutput.putInt("ColorAlphaCount", colorAlphaCount);
        valueOutput.putFloat("ColorAlpha", colorAlpha);
        valueOutput.putFloat("ColorRed", colorRed);
        valueOutput.putFloat("ColorGreen", colorGreen);
        valueOutput.putFloat("ColorBlue", colorBlue);
        valueOutput.storeNullable("PotionContents", PotionContents.CODEC, potionContents);
    }

    @Override
    @NonNull
    public CompoundTag getUpdateTag(@NonNull HolderLookup.Provider registries) {
        return saveCustomOnly(registries);
    }

    @Override
    @NonNull
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    /**
     * 지정한 물약 내용물을 추가한다.
     *
     * @param potionContents 물약 내용물
     */
    public void addPotionContents(@Nullable PotionContents potionContents) {
        if (potionContents == null)
            potionContents = new PotionContents(Potions.WATER);

        addColor(potionContents.hasEffects() ? potionContents.getColor() : 0);
        if (!mixPotionContents(potionContents))
            return;

        setChanged();

        float alpha = ARGB.alphaFloat(color);
        int opacity = 1;
        if (alpha > 0.66)
            opacity = 3;
        else if (alpha > 0.33)
            opacity = 2;

        Objects.requireNonNull(level).setBlockAndUpdate(getBlockPos(), getBlockState().setValue(VPLayeredCauldronBlock.UPDATE_COLOR, true)
                .setValue(VPLayeredCauldronBlock.OPACITY, opacity));
    }

    /**
     * 지정한 물약 내용물을 혼합한다.
     *
     * @param potionContents 물약 내용물
     * @return 성공 여부
     */
    private boolean mixPotionContents(@NonNull PotionContents potionContents) {
        if (this.potionContents == null || this.potionContents.equals(potionContents)) {
            this.potionContents = potionContents;
            return true;
        }

        ArrayList<MobEffectInstance> mobEffectInstances = new ArrayList<>();
        float scale = 1F / getBlockState().getValue(LayeredCauldronBlock.LEVEL);

        this.potionContents.getAllEffects().forEach(mobEffectInstance ->
                mobEffectInstances.add(mobEffectInstance.withScaledDuration(1 - scale)));

        for (MobEffectInstance mobEffectInstance : potionContents.getAllEffects())
            mobEffectInstances.add(mobEffectInstance.withScaledDuration(scale));

        if (mobEffectInstances.isEmpty()) {
            this.potionContents = new PotionContents(Potions.WATER);
            return true;
        }

        HashMap<Pair<Holder<MobEffect>, Integer>, MobEffectInstance> mobEffectInstanceMap = new HashMap<>();

        mobEffectInstances.forEach(mobEffectInstance ->
                mobEffectInstanceMap.compute(Pair.of(mobEffectInstance.getEffect(), mobEffectInstance.getAmplifier()),
                        (k, v) -> {
                            if (v == null)
                                return mobEffectInstance;

                            return new MobEffectInstance(v.getEffect(), v.getDuration() + mobEffectInstance.getDuration(),
                                    v.getAmplifier(), v.isAmbient(), v.isVisible(), v.showIcon());
                        }));

        if (mobEffectInstanceMap.size() > MAX_POTIONS) {
            explode();
            return false;
        }

        this.potionContents = new PotionContents(Optional.empty(), Optional.of(color), List.copyOf(mobEffectInstanceMap.values()),
                Optional.empty());

        return true;
    }

    /**
     * 일반 가마솥으로 초기화하고 폭발 효과를 재생한다.
     */
    private void explode() {
        Objects.requireNonNull(level);

        Vec3 pos = getBlockPos().getCenter();
        ((ServerLevel) level).sendParticles(ParticleTypes.POOF, pos.x(), pos.y(), pos.z(), 30, 0.25, 0.25, 0.25,
                0.1);

        level.playSound(null, getBlockPos(), SoundEvents.GENERIC_EXPLODE.value(), SoundSource.BLOCKS, 2F, 1F);
        level.setBlockAndUpdate(getBlockPos(), Blocks.CAULDRON.defaultBlockState());
    }

    /**
     * 물약 내용물이 효과가 없는 순수한 물인지 확인한다.
     *
     * @return 순수한 물이면 {@code true} 반환
     */
    public boolean hasPureWater() {
        return potionContents != null && potionContents.is(Potions.WATER);
    }

    @Nullable
    public Holder<MobEffect> getRandomMobEffect(@NonNull RandomSource randomSource) {
        if (potionContents == null || !potionContents.hasEffects())
            return null;

        ArrayList<MobEffectInstance> mobEffectInstances = new ArrayList<>();
        potionContents.getAllEffects().forEach(mobEffectInstances::add);

        return mobEffectInstances.get(randomSource.nextInt(mobEffectInstances.size())).getEffect();
    }

    /**
     * 색상을 추가한다.
     *
     * @param color 색상
     */
    private void addColor(int color) {
        colorAlphaCount += 1;

        if (color != 0) {
            colorAlpha += ARGB.alphaFloat(color);
            colorRed += ARGB.redFloat(color);
            colorGreen += ARGB.greenFloat(color);
            colorBlue += ARGB.blueFloat(color);
            colorCount += 1;
        }

        if (colorCount == 0)
            this.color = 0;
        else
            this.color = ARGB.colorFromFloat(colorAlpha / colorAlphaCount, colorRed / colorCount, colorGreen / colorCount,
                    colorBlue / colorCount);
    }
}

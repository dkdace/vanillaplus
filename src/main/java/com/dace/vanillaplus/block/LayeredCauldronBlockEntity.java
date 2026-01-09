package com.dace.vanillaplus.block;

import com.dace.vanillaplus.data.modifier.BlockModifier;
import com.dace.vanillaplus.data.modifier.DataModifierInfo;
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
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.*;

/**
 * 물이 담긴 가마솥 블록 엔티티 클래스.
 */
public final class LayeredCauldronBlockEntity extends BlockEntity {
    /** 물약 내용물 */
    private PotionContents potionContents = new PotionContents(Potions.WATER);
    /** 현재 색상 */
    @Getter
    private int color = 0;
    /** 누적 색상 가중치 */
    private double colorWeightSum;

    public LayeredCauldronBlockEntity(@NonNull BlockPos blockPos, @NonNull BlockState blockState) {
        super(VPBlockEntityTypes.LAYERED_CAULDRON.get(), blockPos, blockState);
    }

    /**
     * 지정한 물약 내용물의 정확한 색상을 반환한다.
     *
     * @param potionContents 물약 내용물
     * @return 색상
     */
    private static int getPotionColor(@NonNull PotionContents potionContents) {
        return potionContents.getColor() == PotionContents.BASE_POTION_COLOR ? 0 : potionContents.getColor();
    }

    /**
     * 지정한 목록에서 중복된 상태 효과를 합쳐서 반환한다.
     *
     * <p>호환되지 않는 상태 효과가 존재하면 빈 목록을 반환한다.</p>
     *
     * @param mobEffectInstances 상태 효과 목록
     * @return 상태 효과 목록
     */
    @NonNull
    @UnmodifiableView
    private static List<MobEffectInstance> combineMobEffects(@NonNull List<MobEffectInstance> mobEffectInstances) {
        HashMap<Pair<Holder<MobEffect>, Integer>, MobEffectInstance> mobEffectInstanceMap = new HashMap<>();

        int maxPotionTypes = ((BlockModifier.WaterCauldronModifier) DataModifierInfo.BLOCK_MODIFIER.getOrThrow(Blocks.WATER_CAULDRON))
                .getMaxPotionTypes();

        for (MobEffectInstance mobEffectInstance : mobEffectInstances) {
            mobEffectInstanceMap.compute(Pair.of(mobEffectInstance.getEffect(), mobEffectInstance.getAmplifier()),
                    (k, v) -> {
                        if (v == null)
                            return mobEffectInstance;

                        return new MobEffectInstance(v.getEffect(), v.getDuration() + mobEffectInstance.getDuration(),
                                v.getAmplifier(), v.isAmbient(), v.isVisible(), v.showIcon());
                    });

            if (mobEffectInstance.getEffect().value().isInstantenous() || mobEffectInstanceMap.size() > maxPotionTypes)
                return Collections.emptyList();
        }

        return List.copyOf(mobEffectInstanceMap.values());
    }

    @Override
    protected void loadAdditional(@NonNull ValueInput valueInput) {
        super.loadAdditional(valueInput);

        color = valueInput.getIntOr("Color", 0);
        colorWeightSum = valueInput.getDoubleOr("ColorWeightSum", 0);
        potionContents = valueInput.read("PotionContents", PotionContents.CODEC).orElse(new PotionContents(Potions.WATER));
    }

    @Override
    protected void saveAdditional(@NonNull ValueOutput valueOutput) {
        super.saveAdditional(valueOutput);

        valueOutput.putInt("Color", color);
        valueOutput.putDouble("ColorWeightSum", colorWeightSum);
        valueOutput.store("PotionContents", PotionContents.CODEC, potionContents);
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
     * 내용물이 줄었을 때 실행할 작업.
     */
    public void onLowerFillLevel() {
        colorWeightSum -= colorWeightSum / (1 + getBlockState().getValue(LayeredCauldronBlock.LEVEL));
        setChanged();
    }

    /**
     * 물약 내용물을 추가한다.
     *
     * @param potionContents 물약 내용물
     */
    public void addPotionContents(@Nullable PotionContents potionContents) {
        if (potionContents == null)
            potionContents = new PotionContents(Potions.WATER);

        int levelValue = getBlockState().getValue(LayeredCauldronBlock.LEVEL);
        if (levelValue > 1 && colorWeightSum == 0)
            colorWeightSum = levelValue - 1.0;

        addColor(getPotionColor(potionContents), 1);
        if (mixPotionContents(potionContents))
            setChanged();
    }

    /**
     * 물약 내용물을 혼합한다.
     *
     * @param potionContents 물약 내용물
     * @return 성공 여부
     */
    private boolean mixPotionContents(@NonNull PotionContents potionContents) {
        Optional<Holder<Potion>> potion = potionContents.potion();
        List<MobEffectInstance> customEffects = potionContents.customEffects();
        int levelValue = getBlockState().getValue(LayeredCauldronBlock.LEVEL);

        if (levelValue > 1 && (!this.potionContents.potion().equals(potion) || !this.potionContents.customEffects().equals(customEffects))) {
            ArrayList<MobEffectInstance> mobEffectInstances = new ArrayList<>();
            float scale = 1F / levelValue;

            this.potionContents.getAllEffects().forEach(mobEffectInstance ->
                    mobEffectInstances.add(mobEffectInstance.withScaledDuration(1 - scale)));

            for (MobEffectInstance mobEffectInstance : potionContents.getAllEffects())
                mobEffectInstances.add(mobEffectInstance.withScaledDuration(scale));

            if (mobEffectInstances.isEmpty())
                potion = Optional.of(Potions.WATER);
            else {
                potion = Optional.empty();
                customEffects = combineMobEffects(mobEffectInstances);

                if (customEffects.isEmpty()) {
                    explode();
                    return false;
                }
            }
        }

        this.potionContents = new PotionContents(potion, Optional.empty(), customEffects, Optional.empty());
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
     * 물약 내용물을 반환한다.
     *
     * @return 물약 내용물
     */
    @NonNull
    public PotionContents getPotionContents() {
        return new PotionContents(potionContents.potion(), color == getPotionColor(potionContents) ? Optional.empty() : Optional.of(color),
                potionContents.customEffects(), Optional.empty());
    }

    /**
     * 내용물이 염료와 물약이 없는 순수한 물인지 확인한다.
     *
     * @return 순수한 물이면 {@code true} 반환
     */
    public boolean hasPureWater() {
        return color == 0 && potionContents.is(Potions.WATER);
    }

    /**
     * 물약 내용물에서 무작위 상태 효과를 반환한다.
     *
     * @param randomSource 랜덤 소스
     * @return 무작위 상태 효과
     */
    @Nullable
    public Holder<MobEffect> getRandomMobEffect(@NonNull RandomSource randomSource) {
        if (!potionContents.hasEffects())
            return null;

        ArrayList<MobEffectInstance> mobEffectInstances = new ArrayList<>();
        potionContents.getAllEffects().forEach(mobEffectInstances::add);

        return mobEffectInstances.get(randomSource.nextInt(mobEffectInstances.size())).getEffect();
    }

    /**
     * 염료 색상을 추가한다.
     *
     * @param dyeColor 염료 색상
     */
    public void addDyeColor(@NonNull DyeColor dyeColor) {
        int diffuseColor = dyeColor.getTextureDiffuseColor();

        int levelValue = getBlockState().getValue(LayeredCauldronBlock.LEVEL);
        if (colorWeightSum == 0)
            colorWeightSum = levelValue;

        addColor(ARGB.opaque(diffuseColor), 3);
        setChanged();
    }

    /**
     * 색상을 추가한다.
     *
     * @param color  색상
     * @param weight 가중치
     */
    private void addColor(int color, int weight) {
        colorWeightSum += weight;
        double scale = 1.0 / colorWeightSum * weight;

        int alpha = ARGB.alpha(this.color);
        alpha += (int) ((ARGB.alpha(color) - alpha) * scale);

        int red = ARGB.red(this.color);
        int green = ARGB.green(this.color);
        int blue = ARGB.blue(this.color);

        if (alpha > 0) {
            red += (int) ((ARGB.red(color) - red) * scale);
            green += (int) ((ARGB.green(color) - green) * scale);
            blue += (int) ((ARGB.blue(color) - blue) * scale);
        }

        this.color = ARGB.color(Math.min(alpha, 0xFF), Math.min(red, 0xFF), Math.min(green, 0xFF), Math.min(blue, 0xFF));

        Objects.requireNonNull(level).sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
    }
}

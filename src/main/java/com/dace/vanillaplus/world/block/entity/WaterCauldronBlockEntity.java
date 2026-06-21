package com.dace.vanillaplus.world.block.entity;

import com.dace.vanillaplus.data.registryobject.VPBlockEntityTypes;
import com.dace.vanillaplus.extension.world.level.block.VPLayeredCauldronBlock;
import com.dace.vanillaplus.util.ColorUtil;
import com.dace.vanillaplus.world.block.WaterCauldronConfig;
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
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Util;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * 물이 담긴 가마솥 블록 엔티티 클래스.
 */
public final class WaterCauldronBlockEntity extends BlockEntity {
    /** 기본 물약 내용물 */
    private static final PotionContents DEFAULT_POTION_CONTENTS = new PotionContents(Potions.WATER);
    /** 물 색상의 최소 투명도 */
    private static final float WATER_COLOR_MIN_ALPHA = 0.5F;

    /** 물약 내용물 */
    @NonNull
    @Getter
    private PotionContents potionContents = DEFAULT_POTION_CONTENTS;

    public WaterCauldronBlockEntity(@NonNull BlockPos blockPos, @NonNull BlockState blockState) {
        super(VPBlockEntityTypes.WATER_CAULDRON.get(), blockPos, blockState);
    }

    /**
     * 색상 채널을 혼합한다.
     *
     * @param oldColor 기존 색상
     * @param newColor 새로운 색상
     * @param level    내용물 레벨
     */
    private static int mixColorChannel(int oldColor, int newColor, int level) {
        return oldColor + Math.min((int) ((newColor - oldColor) * 1.0 / level), ColorUtil.MAX_VALUE);
    }

    /**
     * 색상을 혼합한다.
     *
     * @param oldColor 기존 색상
     * @param newColor 새로운 색상
     * @param level    내용물 레벨
     */
    private static int mixColor(int oldColor, int newColor, int level) {
        int alpha = mixColorChannel(ARGB.alpha(oldColor), ARGB.alpha(newColor), level);
        int red = mixColorChannel(ARGB.red(oldColor), ARGB.red(newColor), level);
        int green = mixColorChannel(ARGB.green(oldColor), ARGB.green(newColor), level);
        int blue = mixColorChannel(ARGB.blue(oldColor), ARGB.blue(newColor), level);

        return ARGB.color(alpha, red, green, blue);
    }

    @Override
    protected void loadAdditional(@NonNull ValueInput valueInput) {
        super.loadAdditional(valueInput);
        potionContents = valueInput.read("PotionContents", PotionContents.CODEC).orElse(DEFAULT_POTION_CONTENTS);
    }

    @Override
    protected void saveAdditional(@NonNull ValueOutput valueOutput) {
        super.saveAdditional(valueOutput);
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

    @Override
    public void setChanged() {
        super.setChanged();

        if (level != null)
            level.setBlockAndUpdate(getBlockPos(), getBlockState().setValue(VPLayeredCauldronBlock.UPDATE_COLOR, true));
    }

    /**
     * 가마솥의 물 색상을 반환한다.
     *
     * @param baseColor 바이옴의 기반 물 색상
     * @return 물 색상
     */
    public int getWaterColor(int baseColor) {
        int potionColor = potionContents.getColorOr(0);
        return ARGB.color(Mth.clampedLerp(ARGB.alphaFloat(potionColor), WATER_COLOR_MIN_ALPHA, 1), ColorUtil.mixColor(baseColor, potionColor));
    }

    /**
     * @param potionContents 물약 내용물
     */
    public void setPotionContents(@NonNull PotionContents potionContents) {
        if (WaterCauldronConfig.get().maxPotionEffects() > 0)
            this.potionContents = potionContents;
    }

    /**
     * 가마솥에 물약을 추가한다.
     *
     * @param potionContents 물약 내용물
     */
    public void addPotion(@Nullable PotionContents potionContents) {
        int maxPotionEffects = WaterCauldronConfig.get().maxPotionEffects();
        if (maxPotionEffects <= 0)
            return;

        if (potionContents == null)
            potionContents = DEFAULT_POTION_CONTENTS;

        if (mixPotionContents(potionContents, getBlockState().getValue(LayeredCauldronBlock.LEVEL), maxPotionEffects))
            setChanged();
    }

    /**
     * 가마솥에 물을 가득 채운다.
     *
     * @param previousLevel 이전 내용물 레벨
     */
    public void fillWater(int previousLevel) {
        int maxPotionEffects = WaterCauldronConfig.get().maxPotionEffects();
        if (maxPotionEffects <= 0)
            return;

        for (int i = previousLevel; i < LayeredCauldronBlock.MAX_FILL_LEVEL; i++) {
            if (!mixPotionContents(DEFAULT_POTION_CONTENTS, i + 1, maxPotionEffects))
                return;
        }

        setChanged();
    }

    /**
     * 물약 내용물을 혼합한다.
     *
     * @param potionContents   추가할 물약 내용물
     * @param levelValue       내용물 레벨
     * @param maxPotionEffects {@link WaterCauldronConfig#maxPotionEffects()}
     * @return 성공 여부
     */
    private boolean mixPotionContents(@NonNull PotionContents potionContents, int levelValue, int maxPotionEffects) {
        Optional<Holder<Potion>> newPotion = potionContents.potion();
        List<MobEffectInstance> newCustomEffects = potionContents.customEffects();
        Optional<Integer> newColor = potionContents.customColor();

        if (levelValue > LayeredCauldronBlock.MIN_FILL_LEVEL) {
            int rawOldColor = this.potionContents.getColorOr(0);
            int rawNewColor = potionContents.getColorOr(0);

            if (rawOldColor != rawNewColor) {
                rawNewColor = mixColor(rawOldColor, rawNewColor, levelValue);
                newColor = Optional.of(rawNewColor);
            }

            if (!this.potionContents.potion().equals(newPotion) || !this.potionContents.customEffects().equals(newCustomEffects)) {
                newCustomEffects = combineMobEffects(potionContents, levelValue);

                if (newCustomEffects.size() > maxPotionEffects) {
                    explode();
                    return false;
                }

                newPotion = newCustomEffects.isEmpty() ? Optional.of(Potions.WATER) : Optional.empty();
            }
        }

        this.potionContents = new PotionContents(newPotion, newColor, newCustomEffects, Optional.empty());
        return true;
    }

    /**
     * 현재 내용물의 상태 효과 목록과 지정한 내용물의 상태 효과 목록을 합쳐서 반환한다.
     *
     * @param potionContents 물약 내용물
     * @param levelValue     내용물 레벨
     * @return 새로운 상태 효과 목록
     */
    @NonNull
    @UnmodifiableView
    private List<MobEffectInstance> combineMobEffects(@NonNull PotionContents potionContents, int levelValue) {
        ArrayList<MobEffectInstance> result = new ArrayList<>();
        float scale = 1F / levelValue;

        this.potionContents.getAllEffects().forEach(mobEffectInstance -> {
            if (!mobEffectInstance.getEffect().value().isInstantenous())
                result.add(mobEffectInstance.withScaledDuration(1 - scale));
        });

        outer:
        for (MobEffectInstance newEffect : potionContents.getAllEffects()) {
            if (newEffect.getEffect().value().isInstantenous())
                continue;

            newEffect = newEffect.withScaledDuration(scale);

            for (int i = 0; i < result.size(); i++) {
                MobEffectInstance oldEffect = result.get(i);

                if (oldEffect.getEffect() == newEffect.getEffect() && oldEffect.getAmplifier() == newEffect.getAmplifier()) {
                    result.set(i, new MobEffectInstance(oldEffect.getEffect(), oldEffect.getDuration() + newEffect.getDuration(),
                            oldEffect.getAmplifier()));
                    continue outer;
                }
            }

            result.add(newEffect);
        }

        return Collections.unmodifiableList(result);
    }

    /**
     * 물약 내용물에서 무작위 색상을 반환한다.
     *
     * @param randomSource 랜덤 소스
     * @return 무작위 색상
     */
    public int getRandomColor(@NonNull RandomSource randomSource) {
        if (!potionContents.hasEffects())
            return 0;

        ArrayList<MobEffectInstance> mobEffectInstances = new ArrayList<>();
        potionContents.getAllEffects().forEach(mobEffectInstances::add);

        int potionColor = potionContents.getColorOr(0);
        if (potionColor == 0)
            return 0;

        int color = Util.getRandom(mobEffectInstances, randomSource).getEffect().value().getColor();
        float brightness = 1 - randomSource.nextFloat() * 0.25F;
        float red = ARGB.redFloat(color) * brightness;
        float green = ARGB.greenFloat(color) * brightness;
        float blue = ARGB.blueFloat(color) * brightness;

        return ARGB.colorFromFloat(ARGB.alphaFloat(potionColor), red, green, blue);
    }

    /**
     * 일반 가마솥으로 초기화하고 폭발 효과를 재생한다.
     */
    private void explode() {
        if (!(level instanceof ServerLevel serverLevel))
            return;

        Vec3 pos = getBlockPos().getCenter();
        serverLevel.sendParticles(ParticleTypes.POOF, pos.x(), pos.y(), pos.z(), 30, 0.25, 0.25, 0.25, 0.1);

        level.playSound(null, getBlockPos(), SoundEvents.GENERIC_EXPLODE.value(), SoundSource.BLOCKS, 2F, 1F);
        level.setBlockAndUpdate(getBlockPos(), Blocks.CAULDRON.defaultBlockState());
    }

    /**
     * 내용물이 색상과 물약이 없는 순수한 물인지 확인한다.
     *
     * @return 순수한 물이면 {@code true} 반환
     */
    public boolean hasPureWater() {
        return potionContents.customColor().isEmpty() && potionContents.is(Potions.WATER);
    }
}

package com.dace.vanillaplus.world.item.loot;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.ints.IntList;
import lombok.NonNull;
import net.minecraft.core.component.DataComponents;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Util;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.FireworkExplosion;
import net.minecraft.world.item.component.Fireworks;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link Items#FIREWORK_ROCKET} 아이템의 폭죽을 무작위로 지정하는 전리품 수정자 클래스.
 */
public final class SetRandomFireworks extends LootItemConditionalFunction {
    /** JSON 코덱 */
    public static final MapCodec<SetRandomFireworks> TYPED_CODEC = RecordCodecBuilder.mapCodec(instance ->
            commonFields(instance)
                    .and(ExtraCodecs.intRange(1, 256).optionalFieldOf("firework_stars", 1)
                            .forGetter(setRandomFireworks -> setRandomFireworks.fireworkStars))
                    .apply(instance, SetRandomFireworks::new));
    /** 폭죽 탄약 개수 */
    private final int fireworkStars;

    private SetRandomFireworks(List<LootItemCondition> predicates, int fireworkStars) {
        super(predicates);
        this.fireworkStars = fireworkStars;
    }

    @Override
    @NonNull
    public MapCodec<? extends LootItemConditionalFunction> codec() {
        return TYPED_CODEC;
    }

    @Override
    @NonNull
    public ItemStack run(@NonNull ItemStack itemStack, @NonNull LootContext lootContext) {
        ArrayList<FireworkExplosion> fireworkExplosions = new ArrayList<>();
        RandomSource randomSource = lootContext.getRandom();

        for (int i = 0; i < fireworkStars; i++) {
            FireworkExplosion.Shape shape = Util.getRandom(FireworkExplosion.Shape.values(), randomSource);
            DyeColor color = Util.getRandom(DyeColor.values(), randomSource);
            DyeColor fadeColor = Util.getRandom(DyeColor.values(), randomSource);

            fireworkExplosions.add(new FireworkExplosion(shape,
                    IntList.of(color.getFireworkColor()), IntList.of(fadeColor.getFireworkColor()), randomSource.nextBoolean(),
                    randomSource.nextBoolean()));
        }

        itemStack.set(DataComponents.FIREWORKS, new Fireworks(1 + randomSource.nextInt(3), fireworkExplosions));
        return itemStack;
    }
}

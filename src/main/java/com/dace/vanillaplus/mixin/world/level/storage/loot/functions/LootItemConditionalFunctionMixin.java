package com.dace.vanillaplus.mixin.world.level.storage.loot.functions;

import com.dace.vanillaplus.extension.VPMixin;
import com.mojang.datafixers.Products;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(LootItemConditionalFunction.class)
public abstract class LootItemConditionalFunctionMixin<T extends LootItemConditionalFunction> implements VPMixin<T> {
    @Shadow
    protected static <T extends LootItemConditionalFunction> Products.P1<RecordCodecBuilder.Mu<T>, List<LootItemCondition>> commonFields(RecordCodecBuilder.Instance<T> i) {
        throw new UnsupportedOperationException();
    }
}

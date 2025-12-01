package com.dace.vanillaplus.mixin.world.entity.ai.sensing;

import com.dace.vanillaplus.extension.VPMixin;
import com.google.common.collect.ImmutableMap;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.sensing.VillagerHostilesSensor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(VillagerHostilesSensor.class)
public abstract class VillagerHostilesSensorMixin implements VPMixin<VillagerHostilesSensor> {
    @Unique
    private static final float DISTANCE_MELEE = 8;
    @Unique
    private static final float DISTANCE_RANGED = 12;
    @Unique
    private static final float DISTANCE_LONG_RANGED = 15;

    @ModifyExpressionValue(method = "<clinit>", at = @At(value = "INVOKE",
            target = "Lcom/google/common/collect/ImmutableMap;builder()Lcom/google/common/collect/ImmutableMap$Builder;"))
    private static ImmutableMap.Builder<EntityType<?>, Float> addHostileEntityTypes(ImmutableMap.Builder<EntityType<?>, Float> map) {
        return map.put(EntityType.SKELETON, DISTANCE_LONG_RANGED)
                .put(EntityType.STRAY, DISTANCE_LONG_RANGED)
                .put(EntityType.BOGGED, DISTANCE_LONG_RANGED)
                .put(EntityType.WITHER_SKELETON, DISTANCE_LONG_RANGED)
                .put(EntityType.WITCH, DISTANCE_RANGED)
                .put(EntityType.SPIDER, DISTANCE_MELEE)
                .put(EntityType.CAVE_SPIDER, DISTANCE_MELEE)
                .put(EntityType.SLIME, DISTANCE_MELEE)
                .put(EntityType.MAGMA_CUBE, DISTANCE_MELEE)
                .put(EntityType.SILVERFISH, DISTANCE_MELEE);
    }
}

package com.dace.vanillaplus.mixin.world.entity.ai.sensing;

import com.dace.vanillaplus.extension.VPMixin;
import com.google.common.collect.ImmutableMap;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.sensing.VillagerHostilesSensor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(VillagerHostilesSensor.class)
public abstract class VillagerHostilesSensorMixin implements VPMixin<VillagerHostilesSensor> {
    @ModifyExpressionValue(method = "<clinit>", at = @At(value = "INVOKE",
            target = "Lcom/google/common/collect/ImmutableMap;builder()Lcom/google/common/collect/ImmutableMap$Builder;"))
    private static ImmutableMap.Builder<EntityType<?>, Float> addHostileEntityTypes(ImmutableMap.Builder<EntityType<?>, Float> map) {
        return map.put(EntityType.SKELETON, 15F)
                .put(EntityType.STRAY, 15F)
                .put(EntityType.BOGGED, 15F)
                .put(EntityType.WITHER_SKELETON, 15F)
                .put(EntityType.WITCH, 12F)
                .put(EntityType.SPIDER, 8F)
                .put(EntityType.CAVE_SPIDER, 8F)
                .put(EntityType.SLIME, 8F)
                .put(EntityType.MAGMA_CUBE, 8F)
                .put(EntityType.SILVERFISH, 8F);
    }
}

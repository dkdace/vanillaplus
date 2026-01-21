package com.dace.vanillaplus.registryobject;

import com.dace.vanillaplus.VPRegistry;
import com.dace.vanillaplus.block.WaterCauldronBlockEntity;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.RegistryObject;

import java.util.Set;

/**
 * 모드에서 사용하는 블록 엔티티 타입을 관리하는 클래스.
 */
@UtilityClass
public final class VPBlockEntityTypes {
    @NonNull
    private static <T extends BlockEntity> RegistryObject<BlockEntityType<T>> create(@NonNull String name,
                                                                                     @NonNull BlockEntityType.BlockEntitySupplier<? extends T> onCreate,
                                                                                     @NonNull Block @NonNull ... validBlocks) {
        return VPRegistry.register(VPRegistry.BLOCK_ENTITY_TYPE, name, () -> new BlockEntityType<>(onCreate, Set.of(validBlocks)));
    }

    public static final RegistryObject<BlockEntityType<WaterCauldronBlockEntity>> WATER_CAULDRON = create("water_cauldron",
            WaterCauldronBlockEntity::new, Blocks.WATER_CAULDRON);
}

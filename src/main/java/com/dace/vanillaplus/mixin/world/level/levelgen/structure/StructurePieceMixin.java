package com.dace.vanillaplus.mixin.world.level.levelgen.structure;

import com.dace.vanillaplus.extension.VPMixin;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(StructurePiece.class)
public abstract class StructurePieceMixin<T extends StructurePiece> implements VPMixin<T> {
    @Shadow
    protected abstract BlockPos.MutableBlockPos getWorldPos(int x, int y, int z);

    @Shadow
    protected abstract void placeBlock(WorldGenLevel level, BlockState blockState, int x, int y, int z, BoundingBox boundingBox);
}

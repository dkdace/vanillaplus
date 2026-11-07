package com.dace.vanillaplus.mixin.world.level.block;

import com.dace.vanillaplus.extension.VPMixin;
import com.dace.vanillaplus.world.block.SulfurBlock;
import net.minecraft.references.BlockItemId;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;

import java.util.function.Function;

@Mixin(Blocks.class)
public abstract class BlocksMixin implements VPMixin<Blocks> {
    @Shadow
    private static Block register(BlockItemId id, Function<BlockBehaviour.Properties, Block> factory, BlockBehaviour.Properties properties) {
        throw new UnsupportedOperationException();
    }

    @Redirect(method = "<clinit>", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/level/block/Blocks;register(Lnet/minecraft/references/BlockItemId;Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;)Lnet/minecraft/world/level/block/Block;",
            ordinal = 0), slice = @Slice(from = @At(value = "FIELD",
            target = "Lnet/minecraft/references/BlockItemIds;SULFUR:Lnet/minecraft/references/BlockItemId;", opcode = Opcodes.GETSTATIC)))
    private static Block registerSulfurBlock(BlockItemId id, BlockBehaviour.Properties properties) {
        return register(id, SulfurBlock::new, properties);
    }
}

package com.dace.vanillaplus.mixin.world.level.levelgen.structure.structures;

import com.dace.vanillaplus.mixin.world.level.levelgen.structure.StructurePieceMixin;
import com.dace.vanillaplus.world.block.entity.WaterCauldronBlockEntity;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BrewingStandBlock;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.entity.BrewingStandBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.structures.SwampHutPiece;
import net.minecraftforge.common.Tags;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SwampHutPiece.class)
public abstract class SwampHutPieceMixin extends StructurePieceMixin<SwampHutPiece> {
    @Inject(method = "postProcess", at = @At(value = "FIELD",
            target = "Lnet/minecraft/world/level/block/Blocks;CRAFTING_TABLE:Lnet/minecraft/world/level/block/Block;", opcode = Opcodes.GETSTATIC))
    private void placeBrewingStand(WorldGenLevel level, StructureManager structureManager, ChunkGenerator chunkGenerator, RandomSource randomSource,
                                   BoundingBox boundingBox, ChunkPos chunkPos, BlockPos blockPos, CallbackInfo ci) {
        placeBlock(level, Blocks.BREWING_STAND.defaultBlockState(), 2, 2, 6, boundingBox);
        if (!(level.getBlockEntity(getWorldPos(2, 2, 6)) instanceof BrewingStandBlockEntity brewingStandBlockEntity))
            return;

        for (int i = 0; i < BrewingStandBlock.HAS_BOTTLE.length; i++) {
            Item potionItem = BuiltInRegistries.ITEM.getRandomElementOf(Tags.Items.POTIONS, randomSource).map(Holder::value).orElse(null);

            if (potionItem != null) {
                ItemStack itemStack = PotionContents.createItemStack(potionItem, BuiltInRegistries.POTION.getRandom(randomSource).orElseThrow());
                brewingStandBlockEntity.setItem(i, itemStack);
            }
        }
    }

    @Definition(id = "placeBlock", method = "Lnet/minecraft/world/level/levelgen/structure/structures/SwampHutPiece;placeBlock(Lnet/minecraft/world/level/WorldGenLevel;Lnet/minecraft/world/level/block/state/BlockState;IIILnet/minecraft/world/level/levelgen/structure/BoundingBox;)V")
    @Definition(id = "CAULDRON", field = "Lnet/minecraft/world/level/block/Blocks;CAULDRON:Lnet/minecraft/world/level/block/Block;")
    @Definition(id = "defaultBlockState", method = "Lnet/minecraft/world/level/block/Block;defaultBlockState()Lnet/minecraft/world/level/block/state/BlockState;")
    @Expression("this.placeBlock(?, CAULDRON.defaultBlockState(), ?, ?, ?, ?)")
    @Redirect(method = "postProcess", at = @At("MIXINEXTRAS:EXPRESSION"))
    private void placePotionCauldron(SwampHutPiece instance, WorldGenLevel level, BlockState blockState, int x, int y, int z, BoundingBox boundingBox,
                                     @Local(argsOnly = true) RandomSource randomSource) {
        int levelValue = randomSource.nextInt(LayeredCauldronBlock.MAX_FILL_LEVEL) + 1;
        placeBlock(level, Blocks.WATER_CAULDRON.defaultBlockState().setValue(LayeredCauldronBlock.LEVEL, levelValue), x, y, z, boundingBox);

        if (level.getBlockEntity(getWorldPos(x, y, z)) instanceof WaterCauldronBlockEntity waterCauldronBlockEntity)
            waterCauldronBlockEntity.overridePotionContents(new PotionContents(BuiltInRegistries.POTION.getRandom(randomSource).orElseThrow()));
    }
}

package com.dace.vanillaplus.data;

import com.dace.vanillaplus.VPRegistry;
import com.dace.vanillaplus.VanillaPlus;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.item.component.MapItemColor;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.saveddata.maps.MapDecorationType;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DataPackRegistryEvent;
import org.jetbrains.annotations.Nullable;

/**
 * 구조물 지도를 관리하는 클래스.
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Mod.EventBusSubscriber(modid = VanillaPlus.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class StructureMap {
    /** 레지스트리 코덱 */
    public static final Codec<Holder<StructureMap>> CODEC = VPRegistry.STRUCTURE_MAP.createRegistryCodec();
    /** JSON 코덱 */
    private static final Codec<StructureMap> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance
            .group(ComponentSerialization.CODEC.fieldOf("description").forGetter(structureMap -> structureMap.itemComponent),
                    TagKey.hashedCodec(Registries.STRUCTURE).fieldOf("structure_tag")
                            .forGetter(structureMap -> structureMap.structureTagKey),
                    ExtraCodecs.RGB_COLOR_CODEC.fieldOf("color").forGetter(structureMap -> structureMap.color),
                    MapDecorationType.CODEC.fieldOf("decoration_type")
                            .forGetter(structureMap -> structureMap.mapDecorationTypeHolder))
            .apply(instance, StructureMap::new));

    /** 아이템 요소 */
    private final Component itemComponent;
    /** 구조물 데이터 태그 */
    private final TagKey<Structure> structureTagKey;
    /** 지도 색상 */
    private final int color;
    /** 지도 장식 타입 홀더 인스턴스 */
    private final Holder<MapDecorationType> mapDecorationTypeHolder;

    @SubscribeEvent
    private static void onDataPackNewRegistry(@NonNull DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(VPRegistry.STRUCTURE_MAP.getRegistryKey(), DIRECT_CODEC);
    }

    /**
     * 지도 아이템을 생성하여 반환한다.
     *
     * @param entity 대상 엔티티
     * @return 지도 아이템
     */
    @Nullable
    public ItemStack createMap(@NonNull Entity entity) {
        if (!(entity.level() instanceof ServerLevel serverLevel))
            return null;

        BlockPos blockPos = serverLevel.findNearestMapStructure(structureTagKey, entity.blockPosition(), 100, true);
        if (blockPos == null)
            return null;

        ItemStack itemstack = MapItem.create(serverLevel, blockPos.getX(), blockPos.getZ(), (byte) 2, true, true);
        itemstack.set(DataComponents.ITEM_NAME, itemComponent);

        MapItem.renderBiomePreviewMap(serverLevel, itemstack);
        MapItemSavedData.addTargetDecoration(itemstack, blockPos, "+", mapDecorationTypeHolder);

        itemstack.set(DataComponents.MAP_COLOR, new MapItemColor(color));

        return itemstack;
    }
}

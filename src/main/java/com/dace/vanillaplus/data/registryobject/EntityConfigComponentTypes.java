package com.dace.vanillaplus.data.registryobject;

import com.dace.vanillaplus.data.StaticRegistry;
import com.dace.vanillaplus.data.VPDataComponentMap;
import com.dace.vanillaplus.world.entity.CrossbowMobConfig;
import com.dace.vanillaplus.world.entity.animal.golem.IronGolemConfig;
import com.dace.vanillaplus.world.entity.boss.enderdragon.EnderDragonConfig;
import com.dace.vanillaplus.world.entity.decoration.ArmorStandConfig;
import com.dace.vanillaplus.world.entity.monster.NpcConfig;
import com.dace.vanillaplus.world.entity.monster.RavagerConfig;
import com.dace.vanillaplus.world.entity.monster.WitchConfig;
import com.dace.vanillaplus.world.entity.npc.villager.VillagerConfig;
import com.dace.vanillaplus.world.entity.raid.RaiderConfig;
import com.mojang.serialization.Codec;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraftforge.registries.RegistryObject;

import java.util.Collections;
import java.util.List;

/**
 * 엔티티 설정의 데이터 요소 타입을 관리하는 클래스.
 */
@UtilityClass
public final class EntityConfigComponentTypes {
    private static int currentId = 0;

    public static final RegistryObject<VPDataComponentMap.Key<List<AttributeInstance.Packed>>> ATTRIBUTES = create(
            "attributes", AttributeInstance.Packed.LIST_CODEC, Collections.emptyList());
    public static final RegistryObject<VPDataComponentMap.Key<Boolean>> SEE_THROUGH_TRANSPARENT_BLOCKS = create(
            "see_through_transparent_blocks", Codec.BOOL, false);
    public static final RegistryObject<VPDataComponentMap.Key<Boolean>> PREVENT_RIDING_IF_HAS_TARGET = create(
            "prevent_riding_if_has_target", Codec.BOOL, false);
    public static final RegistryObject<VPDataComponentMap.Key<Boolean>> JUMP_AT_TARGET_IF_CANNOT_REACH = create(
            "jump_at_target_if_cannot_reach", Codec.BOOL, false);
    public static final RegistryObject<VPDataComponentMap.Key<NpcConfig>> NPC = create(
            "npc", NpcConfig.CODEC, NpcConfig.DEFAULT);
    public static final RegistryObject<VPDataComponentMap.Key<CrossbowMobConfig>> CROSSBOW_MOB = create(
            "crossbow_mob", CrossbowMobConfig.CODEC, CrossbowMobConfig.DEFAULT);
    public static final RegistryObject<VPDataComponentMap.Key<ArmorStandConfig>> ARMOR_STAND = create(
            "armor_stand", ArmorStandConfig.CODEC, ArmorStandConfig.DEFAULT);
    public static final RegistryObject<VPDataComponentMap.Key<VillagerConfig>> VILLAGER = create(
            "villager", VillagerConfig.CODEC, VillagerConfig.DEFAULT);
    public static final RegistryObject<VPDataComponentMap.Key<IronGolemConfig>> IRON_GOLEM = create(
            "iron_golem", IronGolemConfig.CODEC, IronGolemConfig.DEFAULT);
    public static final RegistryObject<VPDataComponentMap.Key<RaiderConfig>> RAIDER = create(
            "raider", RaiderConfig.CODEC, RaiderConfig.DEFAULT);
    public static final RegistryObject<VPDataComponentMap.Key<RavagerConfig>> RAVAGER = create(
            "ravager", RavagerConfig.CODEC, RavagerConfig.DEFAULT);
    public static final RegistryObject<VPDataComponentMap.Key<WitchConfig>> WITCH = create(
            "witch", WitchConfig.CODEC, WitchConfig.DEFAULT);
    public static final RegistryObject<VPDataComponentMap.Key<EnderDragonConfig>> ENDER_DRAGON = create(
            "ender_dragon", EnderDragonConfig.CODEC, EnderDragonConfig.DEFAULT);

    @NonNull
    private static <T> RegistryObject<VPDataComponentMap.Key<T>> create(@NonNull String name, @NonNull Codec<T> codec, @NonNull T defaultValue) {
        return StaticRegistry.ENTITY_CONFIG_COMPONENT_TYPE.register(name, () -> new VPDataComponentMap.Key<>(currentId++, codec, defaultValue));
    }
}

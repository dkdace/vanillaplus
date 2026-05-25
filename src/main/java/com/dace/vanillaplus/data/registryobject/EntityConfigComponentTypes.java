package com.dace.vanillaplus.data.registryobject;

import com.dace.vanillaplus.data.StaticRegistry;
import com.dace.vanillaplus.world.entity.CrossbowMobConfig;
import com.dace.vanillaplus.world.entity.animal.golem.IronGolemConfig;
import com.dace.vanillaplus.world.entity.boss.enderdragon.EnderDragonConfig;
import com.dace.vanillaplus.world.entity.decoration.ArmorStandConfig;
import com.dace.vanillaplus.world.entity.monster.RavagerConfig;
import com.dace.vanillaplus.world.entity.monster.WitchConfig;
import com.dace.vanillaplus.world.entity.npc.NpcConfig;
import com.dace.vanillaplus.world.entity.npc.villager.VillagerConfig;
import com.dace.vanillaplus.world.entity.projectile.FireworkRocketConfig;
import com.dace.vanillaplus.world.entity.raid.RaiderConfig;
import com.mojang.serialization.Codec;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;
import java.util.function.Supplier;

/**
 * 엔티티 설정의 데이터 요소 타입을 관리하는 클래스.
 */
@UtilityClass
public final class EntityConfigComponentTypes {
    public static final RegistryObject<Codec<List<AttributeInstance.Packed>>> ATTRIBUTES = create(
            "attributes", () -> AttributeInstance.Packed.LIST_CODEC);
    public static final RegistryObject<Codec<Boolean>> SEE_THROUGH_TRANSPARENT_BLOCKS = create(
            "see_through_transparent_blocks", () -> Codec.BOOL);
    public static final RegistryObject<Codec<Boolean>> PREVENT_RIDING_IF_HAS_TARGET = create(
            "prevent_riding_if_has_target", () -> Codec.BOOL);
    public static final RegistryObject<Codec<Boolean>> JUMP_AT_TARGET_IF_CANNOT_REACH = create(
            "jump_at_target_if_cannot_reach", () -> Codec.BOOL);
    public static final RegistryObject<Codec<Boolean>> ATTACK_NPCS = create(
            "attack_npcs", () -> Codec.BOOL);
    public static final RegistryObject<Codec<NpcConfig>> NPC = create(
            "npc", () -> NpcConfig.CODEC);
    public static final RegistryObject<Codec<CrossbowMobConfig>> CROSSBOW_MOB = create(
            "crossbow_mob", () -> CrossbowMobConfig.CODEC);
    public static final RegistryObject<Codec<FireworkRocketConfig>> FIREWORK_ROCKET = create(
            "firework_rocket", () -> FireworkRocketConfig.CODEC);
    public static final RegistryObject<Codec<ArmorStandConfig>> ARMOR_STAND = create(
            "armor_stand", () -> ArmorStandConfig.CODEC);
    public static final RegistryObject<Codec<VillagerConfig>> VILLAGER = create(
            "villager", () -> VillagerConfig.CODEC);
    public static final RegistryObject<Codec<IronGolemConfig>> IRON_GOLEM = create(
            "iron_golem", () -> IronGolemConfig.CODEC);
    public static final RegistryObject<Codec<RaiderConfig>> RAIDER = create(
            "raider", () -> RaiderConfig.CODEC);
    public static final RegistryObject<Codec<RavagerConfig>> RAVAGER = create(
            "ravager", () -> RavagerConfig.CODEC);
    public static final RegistryObject<Codec<WitchConfig>> WITCH = create(
            "witch", () -> WitchConfig.CODEC);
    public static final RegistryObject<Codec<EnderDragonConfig>> ENDER_DRAGON = create(
            "ender_dragon", () -> EnderDragonConfig.CODEC);

    @NonNull
    private static <T> RegistryObject<Codec<T>> create(@NonNull String name, @NonNull Supplier<Codec<T>> onCodec) {
        return StaticRegistry.ENTITY_CONFIG_COMPONENT_TYPE.register(name, () -> Codec.lazyInitialized(onCodec));
    }
}

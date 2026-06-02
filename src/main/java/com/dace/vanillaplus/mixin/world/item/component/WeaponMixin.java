package com.dace.vanillaplus.mixin.world.item.component;

import com.dace.vanillaplus.extension.world.item.component.VPWeapon;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.Getter;
import lombok.NonNull;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.component.Weapon;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Weapon.class)
public abstract class WeaponMixin implements VPWeapon {
    @Shadow
    @Final
    public static Codec<Weapon> CODEC = RecordCodecBuilder.create(instance -> instance
            .group(ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("item_damage_per_attack", 1).forGetter(Weapon::itemDamagePerAttack),
                    ExtraCodecs.NON_NEGATIVE_FLOAT.optionalFieldOf("disable_blocking_for_seconds", 0.0F)
                            .forGetter(Weapon::disableBlockingForSeconds),
                    Codec.BOOL.optionalFieldOf("disable_blocking_on_full_strength_attack", false)
                            .forGetter(weapon -> ((WeaponMixin) (Object) weapon).disableBlockingOnFullStrengthAttack))
            .apply(instance, WeaponMixin::create));
    @Shadow
    @Final
    public static StreamCodec<RegistryFriendlyByteBuf, Weapon> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, Weapon::itemDamagePerAttack,
            ByteBufCodecs.FLOAT, Weapon::disableBlockingForSeconds,
            ByteBufCodecs.BOOL, weapon -> ((WeaponMixin) (Object) weapon).disableBlockingOnFullStrengthAttack,
            WeaponMixin::create);

    @Unique
    @Getter
    private boolean disableBlockingOnFullStrengthAttack = false;

    @Unique
    @NonNull
    private static Weapon create(int itemDamagePerAttack, float disableBlockingForSeconds, boolean disableBlockingOnFullStrengthAttack) {
        Weapon weapon = new Weapon(itemDamagePerAttack, disableBlockingForSeconds);
        ((WeaponMixin) (Object) weapon).disableBlockingOnFullStrengthAttack = disableBlockingOnFullStrengthAttack;

        return weapon;
    }
}

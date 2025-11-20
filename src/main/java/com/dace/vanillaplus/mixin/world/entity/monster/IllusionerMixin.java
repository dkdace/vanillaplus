package com.dace.vanillaplus.mixin.world.entity.monster;

import com.dace.vanillaplus.data.RaiderEffect;
import com.dace.vanillaplus.data.modifier.EntityModifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Illusioner;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Illusioner.class)
public abstract class IllusionerMixin extends AbstractIllagerMixin<Illusioner, EntityModifier.LivingEntityModifier> {
    @Inject(method = "registerGoals", at = @At(value = "NEW",
            target = "(Lnet/minecraft/world/entity/PathfinderMob;Ljava/lang/Class;FDD)Lnet/minecraft/world/entity/ai/goal/AvoidEntityGoal;"))
    private void addOpenDoorGoal(CallbackInfo ci) {
        targetSelector.addGoal(2, getThis().new RaiderOpenDoorGoal(getThis()));
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void setCanOpenDoors(EntityType<? extends Illusioner> entityType, Level level, CallbackInfo ci) {
        getNavigation().setCanOpenDoors(true);
    }

    @Overwrite
    public void applyRaidBuffs(ServerLevel serverLevel, int wave, boolean ignored) {
        RaiderEffect.IllusionerEffect illusionerEffect = RaiderEffect.fromEntityType(getType());
        illusionerEffect.getEnchantItemInfos().forEach(enchantItemEffect -> enchantItemEffect.applyEnchantment(getThis()));
    }

    @Override
    public ItemStack modifyProjectileItem(ItemStack itemStack) {
        return ((RaiderEffect.IllusionerEffect) RaiderEffect.fromEntityType(getType())).getTippedArrowInfo()
                .applyArrowPotionEffect(getThis(), itemStack);
    }
}

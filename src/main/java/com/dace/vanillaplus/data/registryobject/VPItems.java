package com.dace.vanillaplus.data.registryobject;

import com.dace.vanillaplus.data.StaticRegistry;
import com.dace.vanillaplus.util.IdentifierUtil;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.Consumables;
import net.minecraft.world.item.consume_effects.ClearAllStatusEffectsConsumeEffect;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

/**
 * 모드에서 사용하는 아이템을 관리하는 클래스.
 */
@UtilityClass
public final class VPItems {
    private static final DeferredRegister<Item> REGISTRY = StaticRegistry.createDeferredRegister(Registries.ITEM);

    public static final RegistryObject<Item> MILK_BOTTLE = create("milk_bottle", new Item.Properties()
            .craftRemainder(Items.GLASS_BOTTLE)
            .usingConvertsTo(Items.GLASS_BOTTLE)
            .stacksTo(3)
            .component(DataComponents.CONSUMABLE, Consumables.defaultDrink().onConsume(ClearAllStatusEffectsConsumeEffect.INSTANCE).build()));

    @NonNull
    private static RegistryObject<Item> create(@NonNull String name, @NonNull Item.Properties properties) {
        return REGISTRY.register(name, () -> new Item(properties.setId(REGISTRY.key(IdentifierUtil.fromPath(name)))));
    }
}

package com.pepperspray;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class PepperSprayMod implements ModInitializer {
    public static final String MOD_ID = "pepperspray";

    public static final StatusEffect PEPPER_BURN = Registry.register(
            Registries.STATUS_EFFECT,
            new Identifier(MOD_ID, "pepper_burn"),
            new PepperBurnEffect());

    public static final Item PEPPER_SPRAY = Registry.register(
            Registries.ITEM,
            new Identifier(MOD_ID, "pepper_spray"),
            new PepperSprayItem(new FabricItemSettings().maxCount(1)));

    @Override
    public void onInitialize() {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT)
                .register(entries -> entries.add(PEPPER_SPRAY));
    }
}

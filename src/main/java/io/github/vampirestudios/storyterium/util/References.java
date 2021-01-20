package io.github.vampirestudios.storyterium.util;

import io.github.vampirestudios.storyterium.socialVillager.FamiliarsProfession;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

import java.util.UUID;

public class References {

    public static final UUID ZERO_UUID = new UUID(0, 0);

    public static ItemStack getDefaultHeldItem(FamiliarsProfession profession) {
        switch (profession.getProfession().getPath()) {
            case "guard":
                return ItemStackCache.get(Items.DIAMOND_SWORD);
            case "archer":
                return ItemStackCache.get(Items.BOW);
            case "farmer":
                return ItemStackCache.get(Items.IRON_HOE);
            case "miner":
                return ItemStackCache.get(Items.IRON_PICKAXE);
            case "lumberjack":
                return ItemStackCache.get(Items.IRON_AXE);
            case "butcher":
                return ItemStackCache.get(Items.IRON_SWORD);
        }
        return ItemStack.EMPTY;
    }

}

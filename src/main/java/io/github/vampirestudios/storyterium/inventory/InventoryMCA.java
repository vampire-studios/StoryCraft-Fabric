package io.github.vampirestudios.storyterium.inventory;

import io.github.vampirestudios.storyterium.entity.FamiliarsEntity;
import net.minecraft.inventory.SimpleInventory;

public class InventoryMCA extends SimpleInventory {

    public InventoryMCA(FamiliarsEntity villager) {
        super(27);
    }

}
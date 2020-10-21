package io.github.paradoxicalblock.storycraft.inventory;

import io.github.paradoxicalblock.storycraft.entity.FamiliarsEntity;
import net.minecraft.inventory.SimpleInventory;

public class InventoryMCA extends SimpleInventory {

    public InventoryMCA(FamiliarsEntity villager) {
        super(27);
    }

}
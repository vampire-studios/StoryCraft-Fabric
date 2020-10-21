package io.github.paradoxicalblock.storycraft.entity;

import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.PathAwareEntity;

public class EntityUtils {

    public static DefaultAttributeContainer.Builder createGenericEntityAttributes() {
        return PathAwareEntity.createMobAttributes().
                add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.5D).
                add(EntityAttributes.GENERIC_MAX_HEALTH, 20.0D).
                add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 3.0F);
    }

}

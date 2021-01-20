package io.github.vampirestudios.storyterium.socialVillager;

import io.github.vampirestudios.storyterium.entity.FamiliarsEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.ai.goal.MoveToTargetPosGoal;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;

import java.util.function.Predicate;

public class PlantCropTask extends MoveToTargetPosGoal {
    private boolean active = false;
    private int plantTime = 0;
    private BlockState plantState = null;
    protected final FamiliarsEntity villager;

    public PlantCropTask(FamiliarsEntity mob, double speed, int range) {
        super(mob, speed, range);
        this.villager = mob;
    }

    private Predicate<BlockPos> isPlantable() {
        return bp -> this.villager.world.getBlockState(bp).getBlock() == Blocks.FARMLAND &&
                this.villager.world.isAir(bp.up());
    }

    @Override
    protected boolean isTargetPos(WorldView world, BlockPos pos) {
        return false;
    }

    @Override
    public boolean canStart() {
        return super.canStart();
    }

    @Override
    public boolean shouldContinue() {
        return super.shouldContinue();
    }

    @Override
    public boolean canStop() {
        return super.canStop();
    }
}

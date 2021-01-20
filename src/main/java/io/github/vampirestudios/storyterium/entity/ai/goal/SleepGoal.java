package io.github.vampirestudios.storyterium.entity.ai.goal;

import net.minecraft.block.BedBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;

public class SleepGoal extends Goal {

    private LivingEntity livingEntity;

    public SleepGoal(LivingEntity livingEntity) {
        this.livingEntity = livingEntity;
    }

    @Override
    public boolean canStart() {
        BlockPos blockPos_1 = new BlockPos(this.livingEntity.getBlockPos());
        BlockState blockState_1 = livingEntity.world.getBlockState(blockPos_1);
        return blockPos_1.isWithinDistance(blockPos_1, 2.0D) && blockState_1.getBlock().isIn(BlockTags.BEDS) && !blockState_1.get(BedBlock.OCCUPIED);
    }

    @Override
    public void start() {
        BlockPos blockPos_1 = new BlockPos(this.livingEntity.getBlockPos());
        livingEntity.sleep(blockPos_1);
    }

}
package io.github.vampirestudios.storyterium.entity.ai.goal;

import io.github.vampirestudios.storyterium.entity.FamiliarsEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.OreBlock;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.ai.goal.MoveToTargetPosGoal;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

public class FindDiamondBlockGoal extends MoveToTargetPosGoal {

    private final FamiliarsEntity owner;
    protected int breakProgress;
    protected int prevBreakProgress;
    private int timer;

    public FindDiamondBlockGoal(FamiliarsEntity villagerEntity_1, double double_1) {
        super(villagerEntity_1, double_1, 16);
        this.owner = villagerEntity_1;
        this.prevBreakProgress = -1;
    }

    public double getDesiredSquaredDistanceToTarget() {
        return 4.0D;
    }

    public boolean shouldResetPath() {
        return this.tryingTime % 100 == 0;
    }

    protected boolean isTargetPos(WorldView viewableWorld_1, BlockPos blockPos_1) {
        BlockState blockState_1 = viewableWorld_1.getBlockState(blockPos_1);
        return blockState_1.getBlock() instanceof OreBlock;
    }

    public void tick() {
        if (this.hasReached()) {
            if (this.timer >= 40) {
                this.eatSweetBerry();
            } else {
                ++this.timer;
            }
        } else if (!this.hasReached() && owner.getRandom().nextFloat() < 0.05F) {
            owner.playSound(SoundEvents.ENTITY_FOX_SNIFF, 1.0F, 1.0F);
        }

        super.tick();
    }

    protected void eatSweetBerry() {
        World world = this.owner.world;
        BlockState blockState = world.getBlockState(targetPos);
        if (blockState.getBlock() instanceof OreBlock) {
            ItemStack itemStack_1 = owner.getEquippedStack(EquipmentSlot.MAINHAND);
            if (itemStack_1.isEmpty()) {
                owner.setStackInHand(Hand.MAIN_HAND, new ItemStack(Items.DIAMOND));
            }
            owner.playSound(SoundEvents.BLOCK_STONE_BREAK, 1.0F, 1.0F);
            world.breakBlock(this.targetPos, false);
            world.spawnEntity(new ItemEntity(world, targetPos.getX(), targetPos.getY(), targetPos.getZ(), new ItemStack(blockState.getBlock().asItem())));
            this.owner.canPickupItem(new ItemStack(blockState.getBlock().asItem()));
            this.owner.sendPickup(this.owner, 1);
        }
    }

    public boolean canStart() {
        if (!super.canStart()) {
            return false;
        } else if (!this.mob.world.getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING)) {
            return false;
        } else {
            return super.canStart();
        }
    }

    public void start() {
        super.start();
        this.breakProgress = 0;
        this.timer = 0;
    }

}
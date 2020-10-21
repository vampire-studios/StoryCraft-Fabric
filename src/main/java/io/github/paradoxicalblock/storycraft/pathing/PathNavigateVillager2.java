/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.minecraft.block.Block
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLiving
 *  net.minecraft.entity.item.EntityArmorStand
 *  net.minecraft.init.Blocks
 *  net.minecraft.pathfinding.Path
 *  net.minecraft.pathfinding.PathFinder
 *  net.minecraft.pathfinding.PathNavigate
 *  net.minecraft.pathfinding.PathNodeType
 *  net.minecraft.pathfinding.PathPoint
 *  net.minecraft.pathfinding.WalkNodeProcessor
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 */
package io.github.paradoxicalblock.storycraft.pathing;

import java.util.LinkedList;
import java.util.Queue;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.init.Blocks;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.pathfinding.WalkNodeProcessor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.tangotek.tektopia.Village;
import net.tangotek.tektopia.entities.EntityVillageNavigator;
import net.tangotek.tektopia.pathing.BasePathingNode;
import net.tangotek.tektopia.pathing.PathFinder;

public class PathNavigateVillager2
extends PathNavigate {
    private int currentPointIndex = -1;
    private long pathUpdate = 0L;
    private int directPathThrottle = 0;
    private Queue<EntityArmorStand> lastDebugs = new LinkedList<EntityArmorStand>();

    public PathNavigateVillager2(EntityLiving entitylivingIn, World worldIn, boolean doors) {
        super(entitylivingIn, worldIn);
        this.nodeProcessor.setCanOpenDoors(doors);
        this.nodeProcessor.setCanEnterDoors(doors);
    }

    protected boolean canNavigate() {
        return this.entity.onGround || this.getCanSwim() && this.isInLiquid() || this.entity.isRiding();
    }

    protected Vec3d getEntityPosition() {
        return new Vec3d(this.entity.posX, (double)this.getPathablePosY(), this.entity.posZ);
    }

    public Path getPathToPos(BlockPos pos) {
        return super.getPathToPos(pos);
    }

    public Path getPathToEntityLiving(Entity entityIn) {
        return this.getPathToPos(new BlockPos(entityIn));
    }

    private int getPathablePosY() {
        if (this.entity.isInWater() && this.getCanSwim()) {
            int i = (int)this.entity.getEntityBoundingBox().minY;
            Block block = this.world.getBlockState(new BlockPos(MathHelper.floor((double)this.entity.posX), i, MathHelper.floor((double)this.entity.posZ))).getBlock();
            int j = 0;
            while (block == Blocks.FLOWING_WATER || block == Blocks.WATER) {
                block = this.world.getBlockState(new BlockPos(MathHelper.floor((double)this.entity.posX), ++i, MathHelper.floor((double)this.entity.posZ))).getBlock();
                if (++j <= 16) continue;
                return (int)this.entity.getEntityBoundingBox().minY;
            }
            return i;
        }
        return (int)(this.entity.getEntityBoundingBox().minY + 0.5);
    }

    public void setBreakDoors(boolean canBreakDoors) {
        this.nodeProcessor.setCanOpenDoors(canBreakDoors);
    }

    public void setEnterDoors(boolean enterDoors) {
        this.nodeProcessor.setCanEnterDoors(enterDoors);
    }

    public boolean getEnterDoors() {
        return this.nodeProcessor.getCanEnterDoors();
    }

    public void setCanSwim(boolean canSwim) {
        this.nodeProcessor.setCanSwim(canSwim);
    }

    public boolean getCanSwim() {
        return this.nodeProcessor.getCanSwim();
    }

    protected net.minecraft.pathfinding.PathFinder getPathFinder() {
        this.nodeProcessor = new WalkNodeProcessor();
        this.nodeProcessor.setCanEnterDoors(true);
        this.nodeProcessor.setCanOpenDoors(true);
        this.nodeProcessor.setCanSwim(true);
        EntityVillageNavigator villageNav = (EntityVillageNavigator)this.entity;
        return new PathFinder(villageNav);
    }

    public PathFinder getVillagerPathFinder() {
        return (PathFinder)this.getPathFinder();
    }

    protected void checkForStuck(Vec3d positionVec3) {
    }

    public boolean setPath(@Nullable Path pathentityIn, double speedIn) {
        this.pathUpdate = System.currentTimeMillis();
        return super.setPath(pathentityIn, speedIn);
    }

    protected void pathFollow() {
        super.pathFollow();
        if (this.currentPath.getCurrentPathIndex() != this.currentPointIndex && !this.currentPath.isFinished()) {
            this.currentPointIndex = this.currentPath.getCurrentPathIndex();
            BasePathingNode pathNode = null;
            if (this.currentPointIndex >= 0 && this.currentPointIndex < this.currentPath.getCurrentPathLength()) {
                PathPoint pathPoint = this.currentPath.getPathPointFromIndex(this.currentPointIndex);
                Village v = ((EntityVillageNavigator)this.entity).getVillage();
                if (pathPoint != null && v != null) {
                    pathNode = v.getPathingGraph().getBaseNode(pathPoint.x, pathPoint.y, pathPoint.z);
                }
            }
            if (pathNode == null || pathNode.getUpdateTick() > this.pathUpdate) {
                this.setPath(null, 1.0);
            }
        }
    }

    protected boolean isDirectPathBetweenPoints(Vec3d posVec31, Vec3d posVec32, int sizeX, int sizeY, int sizeZ) {
        --this.directPathThrottle;
        if (this.directPathThrottle <= 0) {
            this.directPathThrottle = 30 + this.world.rand.nextInt(15);
            Vec3d dir = posVec32.subtract(posVec31).normalize();
            double div = Math.max(Math.abs(dir.x), Math.abs(dir.z));
            double widthX = (double)sizeX / div;
            double widthZ = (double)sizeZ / div;
            dir = dir.scale(0.5);
            Vec3d cur = posVec31;
            while (cur.squareDistanceTo(posVec32) > 1.0) {
                if (!this.isSafeToStandAt(cur, widthX, sizeY, widthZ)) {
                    return false;
                }
                cur = cur.add(dir);
            }
            return true;
        }
        return false;
    }

    private boolean isSafeToStandAt(Vec3d pos, double widthX, double height, double widthZ) {
        double halfX = widthX / 2.0;
        double halfZ = widthZ / 2.0;
        BlockPos corner1 = new BlockPos(pos.x - halfX, pos.y, pos.z - halfZ);
        BlockPos corner2 = new BlockPos(pos.x + halfX, pos.y + height, pos.z + halfZ);
        for (BlockPos blockPos : BlockPos.getAllInBox((BlockPos)corner1, (BlockPos)corner2)) {
            IBlockState blockState = this.world.getBlockState(blockPos);
            Block block = blockState.getBlock();
            if (!block.isPassable((IBlockAccess)this.world, blockPos)) {
                return false;
            }
            if (blockPos.getY() != corner1.getY()) continue;
            PathNodeType pathnodetype = this.nodeProcessor.getPathNodeType((IBlockAccess)this.world, blockPos.getX(), blockPos.getY() - 1, blockPos.getZ(), this.entity, 1, 2, 1, true, true);
            if (pathnodetype == PathNodeType.WATER) {
                return false;
            }
            if (pathnodetype == PathNodeType.LAVA) {
                return false;
            }
            if (pathnodetype == PathNodeType.OPEN) {
                return false;
            }
            pathnodetype = this.nodeProcessor.getPathNodeType((IBlockAccess)this.world, blockPos.getX(), blockPos.getY(), blockPos.getZ(), this.entity, 1, 2, 1, true, true);
            float f = this.entity.getPathPriority(pathnodetype);
            if (f < 0.0f || f >= 8.0f) {
                return false;
            }
            if (pathnodetype != PathNodeType.DAMAGE_FIRE && pathnodetype != PathNodeType.DANGER_FIRE && pathnodetype != PathNodeType.DAMAGE_OTHER) continue;
            return false;
        }
        return true;
    }
}


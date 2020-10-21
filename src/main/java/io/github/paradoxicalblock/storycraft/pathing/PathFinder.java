/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Sets
 *  javax.annotation.Nullable
 *  net.minecraft.block.Block
 *  net.minecraft.block.material.Material
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLiving
 *  net.minecraft.init.Blocks
 *  net.minecraft.pathfinding.Path
 *  net.minecraft.pathfinding.PathFinder
 *  net.minecraft.pathfinding.PathPoint
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.BlockPos$MutableBlockPos
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.world.IBlockAccess
 */
package io.github.paradoxicalblock.storycraft.pathing;

import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.init.Blocks;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.tangotek.tektopia.entities.EntityVillageNavigator;
import net.tangotek.tektopia.pathing.BasePathingNode;
import net.tangotek.tektopia.pathing.PathStep;
import net.tangotek.tektopia.pathing.PathingCell;
import net.tangotek.tektopia.pathing.PathingGraph;
import net.tangotek.tektopia.pathing.PathingNode;

public class PathFinder extends net.minecraft.entity.ai.pathing.PathFinder {
    protected EntityVillageNavigator entity;
    protected IBlockAccess blockAccess;
    protected PriorityQueue<PathStep> openSteps = new PriorityQueue<PathStep>(Comparator.comparingInt(a -> a.getTotalPathDistance()));
    protected Set<PathingNode> closedNodes = new HashSet<PathingNode>();

    public PathFinder(EntityVillageNavigator entityNav) {
        super(null);
        this.entity = entityNav;
        this.blockAccess = entityNav.world;
    }

    public PathingGraph getGraph() {
        if (this.entity.hasVillage()) {
            return this.entity.getVillage().getPathingGraph();
        }
        return null;
    }

    @Nullable
    public Path findPath(IBlockAccess worldIn, EntityLiving entitylivingIn, Entity targetEntity, float maxDistance) {
        return this.findPath(worldIn, entitylivingIn, targetEntity.posX, targetEntity.getEntityBoundingBox().minY, targetEntity.posZ);
    }

    @Nullable
    public Path findPath(IBlockAccess worldIn, EntityLiving entitylivingIn, BlockPos targetPos, float maxDistance) {
        return this.findPath(worldIn, entitylivingIn, (float)targetPos.getX() + 0.5f, (float)targetPos.getY() + 0.5f, (float)targetPos.getZ() + 0.5f);
    }

    private Path findPath(IBlockAccess worldIn, EntityLiving entityLivingIn, double x, double y, double z) {
        this.blockAccess = worldIn;
        PathingGraph graph = this.getGraph();
        if (graph != null) {
            BasePathingNode endNode = graph.getBaseNode(MathHelper.floor((double)x), MathHelper.floor((double)y), MathHelper.floor((double)z));
            PathingNode startNode = this.getStart(graph);
            return this.findPath(worldIn, startNode, endNode);
        }
        return null;
    }

    public Path findPath(IBlockAccess worldIn, PathingNode startNode, PathingNode endNode) {
        int maxLevel;
        if (endNode == null || startNode == null) {
            return null;
        }
        PathStep firstStep = null;
        for (int level = maxLevel = endNode.getTopParent().getCell().level; level >= 0; --level) {
            PathingNode localStart = startNode.getParent(level);
            PathingNode localEnd = endNode.getParent(level);
            firstStep = this.findLevelPath(new PathStep(localStart, null, localEnd, firstStep), localEnd);
        }
        return this.finalizePath(firstStep);
    }

    private PathStep findLevelPath(PathStep startPoint, PathingNode endNode) {
        PathStep current;
        this.openSteps.clear();
        this.closedNodes.clear();
        this.openSteps.add(startPoint);
        while ((current = this.openSteps.poll()) != null) {
            this.closedNodes.add(current.getNode());
            if (current.getNode() == endNode) {
                return current.reverseSteps();
            }
            for (PathingNode connectedNode : current.getNode().connections) {
                if (this.closedNodes.contains(connectedNode)) continue;
                boolean isOpen = false;
                PathStep stepToAdd = null;
                Iterator<PathStep> itr = this.openSteps.iterator();
                while (itr.hasNext()) {
                    PathStep step = itr.next();
                    if (!step.getNode().equals(connectedNode)) continue;
                    if (step.updateDistance(current)) {
                        itr.remove();
                        stepToAdd = step;
                    }
                    isOpen = true;
                    break;
                }
                if (!isOpen) {
                    if (current.getParentStep() == null) {
                        stepToAdd = new PathStep(connectedNode, current, endNode, null);
                    } else if (connectedNode.getParent() == current.getParentStep().getNode()) {
                        stepToAdd = new PathStep(connectedNode, current, endNode, current.getParentStep());
                    } else if (current.getParentStep().getNextStep() != null && connectedNode.getParent() == current.getParentStep().getNextStep().getNode()) {
                        stepToAdd = new PathStep(connectedNode, current, endNode, current.getParentStep().getNextStep());
                    }
                }
                if (stepToAdd == null) continue;
                this.openSteps.add(stepToAdd);
            }
        }
        return null;
    }

    private Path finalizePath(PathStep firstStep) {
        ArrayList<PathPoint> list = new ArrayList<PathPoint>();
        for (PathStep step = firstStep; step != null; step = step.getNextStep()) {
            PathingCell cell = step.getNode().getCell();
            list.add(new PathPoint(cell.x, cell.y, cell.z));
        }
        return new Path(list.toArray((T[])new PathPoint[0]));
    }

    private PathingNode getStart(PathingGraph graph) {
        int i;
        if (this.entity.isInWater()) {
            i = (int)this.entity.getEntityBoundingBox().minY;
            BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos(MathHelper.floor((double)this.entity.posX), i, MathHelper.floor((double)this.entity.posZ));
            Block block = this.blockAccess.getBlockState((BlockPos)blockpos$mutableblockpos).getBlock();
            while (block == Blocks.FLOWING_WATER || block == Blocks.WATER) {
                blockpos$mutableblockpos.setPos(MathHelper.floor((double)this.entity.posX), ++i, MathHelper.floor((double)this.entity.posZ));
                block = this.blockAccess.getBlockState((BlockPos)blockpos$mutableblockpos).getBlock();
            }
        } else if (this.entity.onGround) {
            i = MathHelper.floor((double)(this.entity.getEntityBoundingBox().minY + 0.5));
        } else {
            BlockPos blockpos = new BlockPos((Entity)this.entity);
            while ((this.blockAccess.getBlockState(blockpos).getMaterial() == Material.AIR || this.blockAccess.getBlockState(blockpos).getBlock().isPassable(this.blockAccess, blockpos)) && blockpos.getY() > 0) {
                blockpos = blockpos.down();
            }
            i = blockpos.up().getY();
        }
        BlockPos blockpos2 = new BlockPos((Entity)this.entity);
        BasePathingNode node = graph.getBaseNode(blockpos2.getX(), i, blockpos2.getZ());
        if (node == null) {
            node = graph.getBaseNode(blockpos2.getX(), i + 1, blockpos2.getZ());
        }
        if (node == null) {
            HashSet set = Sets.newHashSet();
            set.add(new BlockPos(this.entity.getEntityBoundingBox().minX, (double)i, this.entity.getEntityBoundingBox().minZ));
            set.add(new BlockPos(this.entity.getEntityBoundingBox().minX, (double)i, this.entity.getEntityBoundingBox().maxZ));
            set.add(new BlockPos(this.entity.getEntityBoundingBox().maxX, (double)i, this.entity.getEntityBoundingBox().minZ));
            set.add(new BlockPos(this.entity.getEntityBoundingBox().maxX, (double)i, this.entity.getEntityBoundingBox().maxZ));
            for (BlockPos blockpos1 : set) {
                node = graph.getNodeYRange(blockpos1.getX(), blockpos1.getY() - 1, blockpos1.getY(), blockpos1.getZ());
                if (node == null) continue;
                return node;
            }
        }
        return node;
    }
}


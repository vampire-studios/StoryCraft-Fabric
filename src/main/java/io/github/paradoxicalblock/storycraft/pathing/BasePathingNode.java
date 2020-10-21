/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.BlockFence
 *  net.minecraft.block.BlockWall
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.entity.player.EntityPlayerMP
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 *  net.minecraftforge.fml.common.network.simpleimpl.IMessage
 */
package io.github.paradoxicalblock.storycraft.pathing;

import net.minecraft.block.BlockState;
import net.minecraft.block.FenceBlock;
import net.minecraft.block.WallBlock;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

public class BasePathingNode
extends PathingNode {
    private final byte clearanceHeight;
    private long updateTick = 0L;

    public BasePathingNode(BlockPos bp, byte ch) {
        super(new PathingCell(bp, (byte) 0));
        this.clearanceHeight = ch;
        this.updateTick = System.currentTimeMillis();
    }

    public byte getClearanceHeight() {
        return this.clearanceHeight;
    }

    public long getUpdateTick() {
        return this.updateTick;
    }

    @Override
    public int updateConnections(World world, PathingCellMap cellMap, PathingGraph graph) {
        this.updateTick = System.currentTimeMillis();
        this.checkConnection(world, cellMap, graph, 1, 0);
        this.checkConnection(world, cellMap, graph, -1, 0);
        this.checkConnection(world, cellMap, graph, 0, 1);
        this.checkConnection(world, cellMap, graph, 0, -1);
        if (this.parent == null) {
            this.parent = new PathingNode(this.getCell().up());
            this.parent.addChild(this);
            graph.addLastNode(this.parent);
        }
        return 0;
    }

    private boolean checkConnection(World world, PathingCellMap cellMap, PathingGraph graph, int x, int z) {
        if (!graph.isInRange(this.getBlockPos().add(x, 0, z))) {
            return false;
        }
        PathingNode connected = this.getConnection(x, z);
        if (connected == null) {
            boolean newNode = false;
            BasePathingNode node = this.getExistingNeighbor(cellMap, x, z);
            if (node == null && (node = this.checkWalkableNeighbor(world, x, z)) != null) {
                newNode = true;
            }
            if (node != null && this.canWalkTo(node)) {
                BasePathingNode.connectNodes(this, node, graph);
                if (newNode) {
                    graph.addFirstNode(node);
                    cellMap.putNode(node, world);
                    return true;
                }
            }
        } else {
            this.checkParentLink(connected);
        }
        return false;
    }

    @Override
    protected void notifyListeners(World world, List<ServerPlayerEntity> listeners) {
//        listeners.forEach(p -> TekVillager.NETWORK.sendTo((IMessage)new PacketPathingNode(new PathingNodeClient(this)), p));
    }

    private BasePathingNode checkWalkableNeighbor(World world, int x, int z) {
        BlockPos bp = this.getBlockPos().add(x, 0, z);
        if (!(BasePathingNode.canWalkOn(world, bp) || BasePathingNode.canWalkOn(world, bp = bp.down()) || BasePathingNode.canWalkOn(world, bp = bp.down()))) {
            bp = null;
        }
        if (bp != null) {
            bp = bp.up();
            byte clearance = 0;
            if (BasePathingNode.isPassable(world, bp) && BasePathingNode.isPassable(world, bp.up(1))) {
                clearance = 2;
                if (BasePathingNode.isPassable(world, bp.up(2))) {
                    clearance = (byte)(clearance + 1);
                }
            }
            if (clearance >= 2) {
                return new BasePathingNode(bp, clearance);
            }
        }
        return null;
    }

    public static boolean isPassable(World world, BlockPos bp) {
        BlockState blockState = world.getBlockState(bp);
        if (blockState.getMaterial().isLiquid()) {
            return false;
        }
        if (blockState.getBlock().isTranslucent(blockState, world, bp)) {
            return true;
        }
        return BasePathingNode.isPortal(world, bp);
    }

    private static boolean isPortal(World world, BlockPos bp) {
        /*if (VillageStructure.isWoodDoor(world, bp)) {
            return true;
        }
        return VillageStructure.isGate(world, bp);*/
        return false;
    }

    public static boolean canWalkOn(World world, BlockPos bp) {
        if (!BasePathingNode.isPassable(world, bp)) {
            BlockState blockState = world.getBlockState(bp);
            if (blockState.getMaterial().isLiquid()) {
                return false;
            }
            return !(blockState.getBlock() instanceof FenceBlock) && !(blockState.getBlock() instanceof WallBlock);
        }
        return false;
    }

    private boolean canWalkTo(BasePathingNode node) {
        return node.getCell().y == this.getCell().y - 1 && node.getClearanceHeight() >= 3 || node.getCell().y == this.getCell().y || node.getCell().y == this.getCell().y + 1 && this.getClearanceHeight() >= 3;
    }

    private BasePathingNode getExistingNeighbor(PathingCellMap cellMap, int x, int z) {
        return cellMap.getNodeYRange(this.getCell().x + x, this.getCell().y - 1, this.getCell().y + 1, this.getCell().z + z);
    }
}


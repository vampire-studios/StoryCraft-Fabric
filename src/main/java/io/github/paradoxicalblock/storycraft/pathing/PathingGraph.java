/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayerMP
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.world.World
 *  net.minecraft.world.chunk.Chunk
 */
package io.github.paradoxicalblock.storycraft.pathing;

import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.tangotek.tektopia.Village;
import net.tangotek.tektopia.pathing.BasePathingNode;
import net.tangotek.tektopia.pathing.PathingCellMap;
import net.tangotek.tektopia.pathing.PathingNode;

public class PathingGraph {
    protected final World world;
    protected final Village village;
    private int nodesVerified = 0;
    private final PathingCellMap baseCellMap;
    private final Deque<PathingNode> nodeProcessQueue = new LinkedList<PathingNode>();
    private boolean initialQueueComplete = false;
    private final List<ServerPlayerEntity> listeners = new ArrayList<ServerPlayerEntity>();

    public PathingGraph(World worldIn, Village v) {
        this.world = worldIn;
        this.village = v;
        this.baseCellMap = new PathingCellMap(120);
    }

    public int nodeCount() {
        return this.baseCellMap.nodeCount();
    }

    public boolean isProcessing() {
        return !this.nodeProcessQueue.isEmpty() || this.baseCellMap.nodeCount() <= 0;
    }

    public void addListener(EntityPlayerMP player) {
        this.listeners.add(player);
        this.baseCellMap.notifyListenerInitial(this.world, player);
    }

    public void removeListener(EntityPlayerMP player) {
        this.listeners.remove((Object)player);
    }

    public void seedVillage(BlockPos bp) {
        byte clearanceHeight = 0;
        if (BasePathingNode.isPassable(this.world, bp) && BasePathingNode.isPassable(this.world, bp.up())) {
            clearanceHeight = 2;
            if (BasePathingNode.isPassable(this.world, bp.up(2))) {
                clearanceHeight = (byte)(clearanceHeight + 1);
            }
        }
        if (clearanceHeight >= 2) {
            BasePathingNode baseNode = new BasePathingNode(bp, clearanceHeight);
            this.baseCellMap.putNode(baseNode, this.world);
            this.nodeProcessQueue.addLast(baseNode);
        }
    }

    public void update() {
        this.processNodeQueue();
    }

    private void processNodeQueue() {
        int throttle = 16000;
        for (int nodesProcessed = 0; !this.nodeProcessQueue.isEmpty() && nodesProcessed < 16000; ++nodesProcessed) {
            PathingNode node = this.nodeProcessQueue.pollFirst();
            if (node == null) continue;
            if (node.isDestroyed()) {
                boolean bl = true;
                continue;
            }
            node.process(this.world, this.baseCellMap, this);
            if (this.listeners.isEmpty()) continue;
            node.notifyListeners(this.world, this.listeners);
        }
        if (this.nodeProcessQueue.isEmpty() && this.baseCellMap.nodeCount() > 1000) {
            this.initialQueueComplete = true;
        }
    }

    public boolean isInitialQueueComplete() {
        return this.initialQueueComplete;
    }

    public boolean isInRange(BlockPos bp) {
        return this.village.isInVillage(bp);
    }

    public void addFirstNode(PathingNode node) {
        if (node.isDestroyed()) {
            return;
        }
        if (!node.isQueued()) {
            node.queue();
            this.nodeProcessQueue.addFirst(node);
        }
    }

    public void addLastNode(PathingNode node) {
        if (node.isDestroyed()) {
            return;
        }
        if (!node.isQueued()) {
            node.queue();
            for (PathingNode child : node.children) {
                assert (child.parent == node);
            }
            this.nodeProcessQueue.addLast(node);
        }
    }

    private void verifyNode(PathingNode node) {
        for (int i = 0; i < 4 - node.getCell().level; ++i) {
            System.out.print("    ");
        }
        System.out.print("->" + node.getCell());
        ++this.nodesVerified;
        if (node.getCell().level == 1 && node.children.size() > 4) {
            System.err.println("Node with > 4 children " + node);
        }
        if (node.getCell().level > 0 && node.children.size() < 1) {
            System.err.println("Level " + node.getCell().level + " with no children");
        }
        System.out.print("      Connections: ");
        for (PathingNode connect : node.connections) {
            System.out.print(connect.cell + "  ");
        }
        System.out.print("\n");
        for (PathingNode child : node.children) {
            if (child.parent != node) {
                System.err.println("child/parent mismatch");
            }
            for (PathingNode childConnect : child.connections) {
                if (childConnect.parent == node || node.isConnected(childConnect.parent)) continue;
                System.err.println("Node " + node + " not connected to neighbor child " + child + " parent " + childConnect.parent);
            }
            this.verifyNode(child);
        }
    }

    public void onBlockUpdate(World world, BlockPos bp) {
        BasePathingNode baseNode = this.baseCellMap.getNodeYRange(bp.getX(), bp.getY() - 2, bp.getY() + 1, bp.getZ());
        while (baseNode != null) {
            this.baseCellMap.removeNode(baseNode, this);
            baseNode.notifyListeners(world, this.listeners);
            baseNode = this.baseCellMap.getNodeYRange(bp.getX(), bp.getY() - 2, bp.getY() + 1, bp.getZ());
        }
        this.baseCellMap.updateNodes(bp.getX() + 1, bp.getY() - 2, bp.getY() + 1, bp.getZ(), this);
        this.baseCellMap.updateNodes(bp.getX() - 1, bp.getY() - 2, bp.getY() + 1, bp.getZ(), this);
        this.baseCellMap.updateNodes(bp.getX(), bp.getY() - 2, bp.getY() + 1, bp.getZ() + 1, this);
        this.baseCellMap.updateNodes(bp.getX(), bp.getY() - 2, bp.getY() + 1, bp.getZ() - 1, this);
        if (this.isInitialQueueComplete()) {
            this.processNodeQueue();
        }
    }

    public void onChunkUnloaded(Chunk chunk) {
    }

    public void onChunkLoaded(Chunk chunk) {
    }

    public boolean isInGraph(BlockPos bp) {
        return this.getBaseNode(bp.getX(), bp.getY(), bp.getZ()) != null;
    }

    public BasePathingNode getBaseNode(int x, int y, int z) {
        return this.baseCellMap.getNode(x, y, z);
    }

    public BasePathingNode getNodeYRange(int x, int y1, int y2, int z) {
        return this.baseCellMap.getNodeYRange(x, y1, y2, z);
    }

    public BasePathingNode getNearbyBaseNode(Vec3d pos, double widthX, double height, double widthZ) {
        BasePathingNode node;
        block1: {
            BlockPos blockPos;
            node = this.getBaseNode((int)pos.x, (int)pos.y, (int)pos.z);
            if (node != null) break block1;
            double halfX = widthX / 2.0;
            double halfZ = widthZ / 2.0;
            BlockPos corner1 = new BlockPos(pos.x - halfX, pos.y - 1.0, pos.z - halfZ);
            BlockPos corner2 = new BlockPos(pos.x + halfX, pos.y + height, pos.z + halfZ);
            Iterator iterator = BlockPos.iterate(corner1, corner2).iterator();
            while (iterator.hasNext() && (node = this.getBaseNode((blockPos = (BlockPos)iterator.next()).getX(), blockPos.getY(), blockPos.getZ())) == null) {
            }
        }
        return node;
    }

    public void debugEdgeNodes(World world) {
        this.baseCellMap.debugEdgeNodes(world);
    }

    public BasePathingNode getEdgeNode(BlockPos origin, Double minDist) {
        return this.baseCellMap.getEdgeNode(origin, minDist);
    }
}


/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.multiplayer.WorldClient
 *  net.minecraft.client.renderer.BufferBuilder
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.ChunkPos
 */
package io.github.paradoxicalblock.storycraft.pathing;

import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3i;

import java.util.HashMap;
import java.util.Map;

public class PathingOverlayChunk {
    private final ChunkPos chunkPos;
    private Map<BlockPos, PathingNodeClient> nodes = new HashMap<BlockPos, PathingNodeClient>();
    private final int RENDER_RADIUS = 30;
    private final int RENDER_RADIUS_SQ = 900;

    public PathingOverlayChunk(ChunkPos cp) {
        this.chunkPos = cp;
    }

    public void putNode(PathingNodeClient node) {
        if (node.isDestroyed) {
            this.nodes.remove((Object)node.pos);
        } else if (this.nodes.put(node.pos, node) != null) {
            node.setAge(400);
        }
    }

    public void renderOverlays(ClientWorld world, BufferBuilder vertexBuffer, double viewX, double viewY, double viewZ) {
        if ((double)this.chunkPos.getStartX() - viewX > 30.0 || viewX - (double)this.chunkPos.getEndX() > 30.0 || (double)this.chunkPos.getStartZ() - viewZ > 30.0 || viewZ - (double)this.chunkPos.getEndZ() > 30.0) {
            return;
        }
        for (PathingNodeClient node : this.nodes.values()) {
            if (!(node.pos.getSquaredDistance(new Vec3i(viewX, viewY, viewZ)) < 900.0)) continue;
            this.renderNode(world, vertexBuffer, node);
        }
    }

    private void renderNode(ClientWorld world, BufferBuilder vertexBuffer, PathingNodeClient node) {
        for (int index = 0; index < 4; ++index) {
            vertexBuffer.vertex(node.getX(index), node.getY(index), node.getZ(index)).color(170, 170, 170, 140).next();
        }
        node.connections.forEach(conn -> {
            double innerEdge = 0.25;
            double outerEdge = 0.75;
            double width = 0.15;
            double gap = 0.03;
            double x = (double)node.pos.getX() + 0.5;
            double z = (double)node.pos.getZ() + 0.5;
            double y = (double)node.pos.getY() + 0.05;
            for (int index = 0; index < 4; ++index) {
                if (conn.xOffset == 0) {
                    if (conn.zOffset == 1) {
                        vertexBuffer.vertex(x - 0.03, y, z + 0.25).color(98, 209, 83, 160).next();
                        vertexBuffer.vertex(x - 0.03 - 0.15, y, z + 0.25).color(98, 209, 83, 160).next();
                        vertexBuffer.vertex(x - 0.03 - 0.15, y + (double)conn.yOffset, z + 0.75).color(230, 230, 110, 160).next();
                        vertexBuffer.vertex(x - 0.03, y + (double)conn.yOffset, z + 0.75).color(230, 230, 110, 160).next();
                        continue;
                    }
                    vertexBuffer.vertex(x + 0.03, y, z - 0.25).color(98, 209, 83, 160).next();
                    vertexBuffer.vertex(x + 0.03 + 0.15, y, z - 0.25).color(98, 209, 83, 160).next();
                    vertexBuffer.vertex(x + 0.03 + 0.15, y + (double)conn.yOffset, z - 0.75).color(230, 230, 110, 160).next();
                    vertexBuffer.vertex(x + 0.03, y + (double)conn.yOffset, z - 0.75).color(230, 230, 110, 160).next();
                    continue;
                }
                if (conn.xOffset == 1) {
                    vertexBuffer.vertex(x + 0.25, y, z + 0.03).color(98, 209, 83, 160).next();
                    vertexBuffer.vertex(x + 0.25, y, z + 0.03 + 0.15).color(98, 209, 83, 160).next();
                    vertexBuffer.vertex(x + 0.75, y + (double)conn.yOffset, z + 0.03 + 0.15).color(230, 230, 110, 160).next();
                    vertexBuffer.vertex(x + 0.75, y + (double)conn.yOffset, z + 0.03).color(230, 230, 110, 160).next();
                    continue;
                }
                vertexBuffer.vertex(x - 0.25, y, z - 0.03).color(98, 209, 83, 160).next();
                vertexBuffer.vertex(x - 0.25, y, z - 0.03 - 0.15).color(98, 209, 83, 160).next();
                vertexBuffer.vertex(x - 0.75, y + (double)conn.yOffset, z - 0.03 - 0.15).color(230, 230, 110, 160).next();
                vertexBuffer.vertex(x - 0.75, y + (double)conn.yOffset, z - 0.03).color(230, 230, 110, 160).next();
            }
        });
    }
}


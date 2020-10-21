/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.entity.EntityPlayerSP
 *  net.minecraft.client.multiplayer.WorldClient
 *  net.minecraft.client.renderer.BufferBuilder
 *  net.minecraft.client.renderer.GlStateManager
 *  net.minecraft.client.renderer.Tessellator
 *  net.minecraft.client.renderer.vertex.DefaultVertexFormats
 *  net.minecraft.util.math.ChunkPos
 *  net.minecraftforge.client.event.RenderWorldLastEvent
 *  net.minecraftforge.common.MinecraftForge
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 */
package io.github.paradoxicalblock.storycraft.pathing;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.ChunkPos;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.HashMap;
import java.util.Map;

public class PathingOverlayRenderer {
    private Map<ChunkPos, PathingOverlayChunk> chunks = new HashMap<ChunkPos, PathingOverlayChunk>();
    private boolean enabled = false;

    public PathingOverlayRenderer() {
        MinecraftForge.EVENT_BUS.register((Object)this);
    }

    public void handleNodeUpdate(PathingNodeClient node) {
        boolean bl = this.enabled = node != null;
        if (this.enabled) {
            ChunkPos chunkPos = new ChunkPos(node.pos);
            PathingOverlayChunk overlayChunk = this.chunks.get((Object)chunkPos);
            if (overlayChunk == null) {
                overlayChunk = new PathingOverlayChunk(chunkPos);
                this.chunks.put(chunkPos, overlayChunk);
            }
            overlayChunk.putNode(node);
        } else {
            this.chunks.clear();
        }
    }

    @SubscribeEvent
    public void renderOverlays(RenderWorldLastEvent event) {
        if (!this.enabled) {
            return;
        }
        MinecraftClient minecraft = MinecraftClient.getInstance();
        ClientWorld world = minecraft.world;
        ClientPlayerEntity player = minecraft.player;
        if (world == null || player == null) {
            return;
        }
        double partialTicks = event.getPartialTicks();
        double viewX = player.lastRenderX + (player.getX() - player.lastRenderX) * partialTicks;
        double viewY = player.lastRenderY + (player.getY() - player.lastRenderY) * partialTicks;
        double viewZ = player.lastRenderZ + (player.getX() - player.lastRenderZ) * partialTicks;
        GlStateManager.pushMatrix();
        GlStateManager.disableTexture();
        GlStateManager.enableAlphaTest();
        GlStateManager.enableBlend();
        BufferBuilder vertexBuffer = Tessellator.getInstance().getBuffer();
        vertexBuffer.begin(7, VertexFormats.POSITION_COLOR);
        vertexBuffer.vertex(-viewX, -viewY, -viewZ);
        this.renderOverlays(world, vertexBuffer, viewX, viewY, viewZ);
        vertexBuffer.vertex(0.0, 0.0, 0.0);
        Tessellator.getInstance().draw();
        GlStateManager.enableTexture();
        GlStateManager.popMatrix();
    }

    private void renderOverlays(ClientWorld world, BufferBuilder vertexBuffer, double viewX, double viewY, double viewZ) {
        this.chunks.values().forEach(c -> c.renderOverlays(world, vertexBuffer, viewX, viewY, viewZ));
    }
}


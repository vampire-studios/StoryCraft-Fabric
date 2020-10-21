/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayerMP
 */
package io.github.paradoxicalblock.storycraft.pathing;

import net.minecraft.server.network.ServerPlayerEntity;

public class PathingGraphListener {
    private ServerPlayerEntity playerMP;

    public PathingGraphListener(ServerPlayerEntity player) {
        this.playerMP = player;
    }

    public void update(PathingGraph graph) {
    }

    public boolean isPlayer(ServerPlayerEntity player) {
        return player == this.playerMP;
    }
}


/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  net.minecraft.util.math.BlockPos
 */
package io.github.paradoxicalblock.storycraft.pathing;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class PathingNodeClient {
    public final BlockPos pos;
    public final boolean isDestroyed;
    public List<PathingNodeClientConnection> connections = new ArrayList<PathingNodeClientConnection>(4);
    private int age = 0;

    public PathingNodeClient(ByteBuf buf) {
        this.pos = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
        this.isDestroyed = buf.readBoolean();
        int count = buf.readInt();
        for (int i = 0; i < count; ++i) {
            PathingNodeClientConnection newConn = new PathingNodeClientConnection(buf.readByte(), buf.readByte(), buf.readByte());
            this.connections.add(newConn);
        }
    }

    public PathingNodeClient(PathingNode node) {
        this.pos = node.getBlockPos();
        this.isDestroyed = node.isDestroyed();
        for (PathingNode connection : node.connections) {
            byte xOffset = (byte)(connection.cell.x - node.cell.x);
            byte yOffset = (byte)(connection.cell.y - node.cell.y);
            byte zOffset = (byte)(connection.cell.z - node.cell.z);
            this.connections.add(new PathingNodeClientConnection(xOffset, yOffset, zOffset));
        }
    }

    public double getX(int index) {
        return index < 2 ? (double)this.pos.getX() + 0.1 : (double)this.pos.getX() + 0.9;
    }

    public double getZ(int index) {
        return index == 0 || index == 3 ? (double)this.pos.getZ() + 0.1 : (double)this.pos.getZ() + 0.9;
    }

    public double getY(int index) {
        return (double)this.pos.getY() + 0.03;
    }

    public int getAge() {
        this.age = Math.max(60, this.age--);
        return this.age > 60 ? 255 : this.age;
    }

    public void setAge(int a) {
        this.age = a;
    }

    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.pos.getX());
        buf.writeInt(this.pos.getY());
        buf.writeInt(this.pos.getZ());
        buf.writeBoolean(this.isDestroyed);
        buf.writeInt(this.connections.size());
        this.connections.forEach(c -> {
            buf.writeByte((int)c.xOffset);
            buf.writeByte((int)c.yOffset);
            buf.writeByte((int)c.zOffset);
        });
    }

    public class PathingNodeClientConnection {
        public final byte xOffset;
        public final byte zOffset;
        public final byte yOffset;

        public PathingNodeClientConnection(byte x, byte y, byte z) {
            this.xOffset = x;
            this.yOffset = y;
            this.zOffset = z;
        }
    }
}


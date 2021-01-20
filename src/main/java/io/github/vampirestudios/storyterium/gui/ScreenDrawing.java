package io.github.vampirestudios.storyterium.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;
import org.lwjgl.opengl.GL11;

public class ScreenDrawing {

    /**
     * Draws a default-sized recessed itemslot panel
     */
    public static void drawBeveledPanel(int x, int y) {
        drawBeveledPanel(x, y, 18, 18, 0xFF373737, 0xFF8b8b8b, 0xFFFFFFFF);
    }

    /**
     * Draws a default-color recessed itemslot panel of variable size
     */
    public static void drawBeveledPanel(int x, int y, int width, int height) {
        drawBeveledPanel(x, y, width, height, 0xFF373737, 0xFF8b8b8b, 0xFFFFFFFF);
    }

    /**
     * Draws a generalized-case beveled panel. Can be inset or outset depending on arguments.
     * @param x				x coordinate of the topleft corner
     * @param y				y coordinate of the topleft corner
     * @param width			width of the panel
     * @param height		height of the panel
     * @param topleft		color of the top/left bevel
     * @param panel			color of the panel area
     * @param bottomright	color of the bottom/right bevel
     */
    public static void drawBeveledPanel(int x, int y, int width, int height, int topleft, int panel, int bottomright) {
        coloredRect(x,             y,              width,     height,     panel); //Center panel
        coloredRect(x,             y,              width - 1, 1,          topleft); //Top shadow
        coloredRect(x,             y + 1,          1,         height - 2, topleft); //Left shadow
        coloredRect(x + width - 1, y + 1,          1,         height - 1, bottomright); //Right hilight
        coloredRect(x + 1,         y + height - 1, width - 1, 1,          bottomright); //Bottom hilight
    }

    /**
     * Draws an untextured rectangle of the specified RGB color.
     */
    public static void coloredRect(int left, int top, int width, int height, int color) {
        if (width <= 0) width = 1;
        if (height <= 0) height = 1;

        float a = (color >> 24 & 255) / 255.0F;
        float r = (color >> 16 & 255) / 255.0F;
        float g = (color >> 8 & 255) / 255.0F;
        float b = (color & 255) / 255.0F;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        RenderSystem.enableBlend();
        RenderSystem.disableTexture();
        RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO);
        buffer.begin(GL11.GL_QUADS, VertexFormats.POSITION_COLOR); //I thought GL_QUADS was deprecated but okay, sure.
        buffer.vertex(left,         top + height, 0.0D).color(r, g, b, a).next();
        buffer.vertex(left + width, top + height, 0.0D).color(r, g, b, a).next();
        buffer.vertex(left + width, top,          0.0D).color(r, g, b, a).next();
        buffer.vertex(left,         top,          0.0D).color(r, g, b, a).next();
        tessellator.draw();
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
    }

    public static void rect(Identifier texture, int left, int top, int width, int height, int color) {
        rect(texture, left, top, width, height, 0, 0, 1, 1, color, 0);
    }

    public static void rect(Identifier texture, int left, int top, int width, int height, float u1, float v1, float u2, float v2, int color) {
        rect(texture, left, top, width, height, u1, v1, u2, v2, color, 0);
    }

    public static void rect(Identifier texture, int left, int top, int width, int height, float u1, float v1, float u2, float v2, int color, int z) {
        MinecraftClient.getInstance().getTextureManager().bindTexture(texture);

        //float scale = 0.00390625F;

        if (width <= 0) width = 1;
        if (height <= 0) height = 1;

        float r = (color >> 16 & 255) / 255.0F;
        float g = (color >> 8 & 255) / 255.0F;
        float b = (color & 255) / 255.0F;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        RenderSystem.enableBlend();
        //GlStateManager.disableTexture2D();
        RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO);
        RenderSystem.color4f(r, g, b, 1.0f);
        buffer.begin(GL11.GL_QUADS, VertexFormats.POSITION_TEXTURE); //I thought GL_QUADS was deprecated but okay, sure.
        buffer.vertex(left,         top + height, z).texture(u1, v2).next();
        buffer.vertex(left + width, top + height, z).texture(u2, v2).next();
        buffer.vertex(left + width, top,          z).texture(u2, v1).next();
        buffer.vertex(left,         top,          z).texture(u1, v1).next();
        tessellator.draw();
        //GlStateManager.enableTexture2D();
        RenderSystem.disableBlend();
    }

    /**
     * Draws an untextured rectangle of the specified RGB color.
     */
    public static void rect(int left, int top, int width, int height, int color) {
        if (width <= 0) width = 1;
        if (height <= 0) height = 1;

        float a = (color >> 24 & 255) / 255.0F;
        float r = (color >> 16 & 255) / 255.0F;
        float g = (color >> 8 & 255) / 255.0F;
        float b = (color & 255) / 255.0F;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture();
        RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO);
        GlStateManager.color4f(r, g, b, a);
        buffer.begin(GL11.GL_QUADS, VertexFormats.POSITION); //I thought GL_QUADS was deprecated but okay, sure.
        buffer.vertex(left, top + height, 0.0D).next();
        buffer.vertex(left + width, top + height, 0.0D).next();
        buffer.vertex(left + width, top, 0.0D).next();
        buffer.vertex(left, top, 0.0D).next();
        tessellator.draw();
        GlStateManager.enableTexture();
        GlStateManager.disableBlend();
    }

    /** Just like colorFill, but reads the alpha part of the color */
    public static void translucentColorFill(int x, int y, int width, int height, int color) {
        float a = ((color >> 24) & 0xFF) / 255f;
        float r = ((color >> 16) & 0xFF) / 255f;
        float g = ((color >>  8) & 0xFF) / 255f;
        float b = ((color      ) & 0xFF) / 255f;
        
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buffer = tess.getBuffer();
        buffer.begin(GL11.GL_QUADS, VertexFormats.POSITION_COLOR);
        buffer.vertex(x, y+height, 0).color(r, g, b, a).next();
        buffer.vertex(x+width, y+height, 0).color(r, g, b, a).next();
        buffer.vertex(x+width, y, 0).color(r, g, b, a).next();
        buffer.vertex(x, y, 0).color(r, g, b, a).next();
        tess.draw();
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }
    
    public static void colorFill(int x, int y, int width, int height, int color) {
        float r = ((color >> 16) & 0xFF) / 255f;
        float g = ((color >>  8) & 0xFF) / 255f;
        float b = ((color      ) & 0xFF) / 255f;
        
        colorFill(x, y, width, height, 0.0f, r, g, b);
    }
    
    public static void colorFill(int x, int y, int width, int height, double z, float r, float g, float b) {
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buffer = tess.getBuffer();
        buffer.begin(GL11.GL_QUADS, VertexFormats.POSITION_COLOR);
        buffer.vertex(x, y+height, z).color(r, g, b, 1.0f).next();
        buffer.vertex(x+width, y+height, z).color(r, g, b, 1.0f).next();
        buffer.vertex(x+width, y, z).color(r, g, b, 1.0f).next();
        buffer.vertex(x, y, z).color(r, g, b, 1.0f).next();
        tess.draw();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }
    
    public static void colorHollowRect(int x, int y, int width, int height, int color) {
        colorFill(x, y, width, 1, color);
        colorFill(x, y+1, 1, height-2, color);
        colorFill(x+width-1, y+1, 1, height-2, color);
        colorFill(x, y+height-1, width, 1, color);
    }
    
    
    public static void textureFill(int x, int y, int width, int height, Identifier tex, float u1, float v1, float u2, float v2) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        MinecraftClient.getInstance().getTextureManager().bindTexture(tex);
        
        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buffer = tess.getBuffer();
        buffer.begin(GL11.GL_QUADS, VertexFormats.POSITION_TEXTURE);
        buffer.vertex(x, y+height, 0.0f).texture(u1, v2).next();
        buffer.vertex(x+width, y+height, 0.0f).texture(u2, v2).next();
        buffer.vertex(x+width, y, 0.0f).texture(u2, v1).next();
        buffer.vertex(x, y, 0.0f).texture(u1, v1).next();
        tess.draw();
    }
    
    
    /**
     * Draws a rectangle on the screen, using as much of the top-left part of the texture that's visible in the width / height.
     * 
     *  <p>Assumes the texture is 256px
     */
    public static void textureFillGui(int x, int y, int width, int height, Identifier tex) {
        textureFillGui(x, y, width, height, tex, 0, 0);
    }
    
    /**
     * Draws a rectangle out of a texture, at one texture-pixel per minecraft gui pixel.
     * 
     * <p>Assumes the texture is 256px
     * 
     * @param x left edge of the rectangle
     * @param y top edge of the rectangle
     * @param width width of the rectangle on the screen
     * @param height height of the rectangle on the screen
     * @param tex the texture identifier to use
     * @param tex_x the x-offset into the texture
     * @param tex_y the y-offset into the texture
     */
    public static void textureFillGui(int x, int y, int width, int height, Identifier tex, int tex_x, int tex_y) {
        float px = 1/256f;
        float u1 = tex_x*px;
        float v1 = tex_y*px;
        float u2 = u1 + (width*px);
        float v2 = v1 + (height*px);
        textureFill(x, y, width, height, tex, u1, v1, u2, v2);
    }
    
    public static void textureFillGui(int x, int y, int width, int height, Identifier tex, int tex_x, int tex_y, int tex_width, int tex_height) {
        float px = 1/256f;
        float u1 = tex_x*px;
        float v1 = tex_y*px;
        float u2 = u1 + (tex_width*px);
        float v2 = v1 + (tex_height*px);
        textureFill(x, y, width, height, tex, u1, v1, u2, v2);
    }
}
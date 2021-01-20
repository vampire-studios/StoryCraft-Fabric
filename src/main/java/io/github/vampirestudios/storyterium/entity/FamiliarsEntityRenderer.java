package io.github.vampirestudios.storyterium.entity;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.vampirestudios.storyterium.util.TextureAssembler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Environment(value = EnvType.CLIENT)
public class FamiliarsEntityRenderer extends LivingEntityRenderer<FamiliarsEntity, PlayerEntityModel<FamiliarsEntity>> {
    public FamiliarsEntityRenderer(EntityRenderDispatcher dispatcher, boolean thinArms) {
        super(dispatcher, new PlayerEntityModel<>(0.0F, thinArms), 0.5F);
    }

    @Override
    public Identifier getTexture(FamiliarsEntity entity) {
        if (this.getRenderManager().textureManager.getTexture(new Identifier("minecraft:dynamic/" + entity.getDataTracker().get(FamiliarsEntity.serverUUID) + "_1")) != null) {
            return new Identifier("minecraft:dynamic/" + entity.getDataTracker().get(FamiliarsEntity.serverUUID) + "_1");
        }
        TextureAssembler assembler = new TextureAssembler(entity);
        BufferedImage imageBase = assembler.createTexture();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        try {
            ImageIO.write(imageBase, "png", stream);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        InputStream is = new ByteArrayInputStream(stream.toByteArray());
        NativeImage base = null;
        try {
            base = NativeImage.read(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
        NativeImageBackedTexture texture = new NativeImageBackedTexture(base);
        assembler.save();
        return this.getRenderManager().textureManager.registerDynamicTexture(entity.getDataTracker().get(FamiliarsEntity.serverUUID), texture);
    }

    @Override
    protected void renderLabelIfPresent(FamiliarsEntity abstractClientPlayerEntity, Text text, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        double d = this.dispatcher.getSquaredDistanceToCamera(abstractClientPlayerEntity);
        matrixStack.push();
        if (d < 100.0D) {
            matrixStack.translate(0.0D, 9.0F * 1.15F * 0.025F, 0.0D);
        }
        super.renderLabelIfPresent(abstractClientPlayerEntity, text, matrixStack, vertexConsumerProvider, i);
        matrixStack.pop();
    }

    @Override
    protected void scale(FamiliarsEntity entity, MatrixStack matrices, float tickDelta) {
        float size = 0.9375F;
        if (entity.isBaby()) {
            size = size * 0.5F;
            this.shadowRadius = 0.25F;
        } else {
            this.shadowRadius = 0.5F;
        }

        RenderSystem.scalef(size, size, size);
    }

}

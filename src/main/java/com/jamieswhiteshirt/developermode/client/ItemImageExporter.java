package com.jamieswhiteshirt.developermode.client;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.GlFramebuffer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.GuiLighting;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.ScreenshotUtils;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.item.ItemStack;
import net.minecraft.resource.ResourceImpl;
import org.lwjgl.opengl.GL11;

import java.io.File;
import java.io.IOException;

public class ItemImageExporter implements AutoCloseable {
	private final int size;
	private final GlFramebuffer framebuffer;
	private final ItemRenderer itemRenderer;

	public ItemImageExporter(int size, ItemRenderer itemRenderer) {
		this.size = size;
		framebuffer = new GlFramebuffer(size, size, true, MinecraftClient.IS_SYSTEM_MAC);
		this.itemRenderer = itemRenderer;

		framebuffer.setClearColor(0.0F, 0.0F, 0.0F, 0.0F);
	}

	public void render(ItemStack stack, File output) {
		framebuffer.clear(MinecraftClient.IS_SYSTEM_MAC);
		framebuffer.beginWrite(true);

		MinecraftClient client = MinecraftClient.getInstance();

		// Fresh matrices
        GlStateManager.matrixMode(GL11.GL_PROJECTION);
        GlStateManager.pushMatrix();
        GlStateManager.loadIdentity();
		GlStateManager.scalef(1.0F, -1.0F, 1.0F);
		GlStateManager.matrixMode(GL11.GL_MODELVIEW);
		GlStateManager.pushMatrix();
		GlStateManager.loadIdentity();

        GuiLighting.enableForItems();
		client.getTextureManager().bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
		client.getTextureManager().getTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX).pushFilter(false, false);
		GlStateManager.enableRescaleNormal();
		GlStateManager.enableAlphaTest();
		GlStateManager.alphaFunc(516, 0.1F);
		GlStateManager.enableBlend();
		BakedModel bakedModel = itemRenderer.getModel(stack);
		if (bakedModel.hasDepthInGui()) {
			GlStateManager.enableLighting();
		} else {
			GlStateManager.disableLighting();
		}
		GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.ONE_MINUS_DST_ALPHA, GlStateManager.DestFactor.DST_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.scalef(2.0F, -2.0F, 1.0F);
        bakedModel.getTransformation().applyGl(ModelTransformation.Type.GUI);
		itemRenderer.renderItemAndGlow(stack, bakedModel);
		GlStateManager.disableAlphaTest();
		GlStateManager.disableRescaleNormal();
		GlStateManager.disableLighting();
		client.getTextureManager().bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
		client.getTextureManager().getTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX).popFilter();
		GuiLighting.disable();

		// Reset matrices
		GlStateManager.matrixMode(GL11.GL_PROJECTION);
		GlStateManager.popMatrix();
        GlStateManager.matrixMode(GL11.GL_MODELVIEW);
        GlStateManager.popMatrix();

		framebuffer.endWrite();
		GlStateManager.viewport(0, 0, client.window.getFramebufferWidth(), client.window.getFramebufferHeight());

        NativeImage nativeImage = new NativeImage(size, size, false);
        GlStateManager.bindTexture(framebuffer.colorAttachment);
        nativeImage.loadFromTextureImage(0, false);

        nativeImage.method_4319();
        ResourceImpl.RESOURCE_IO_EXECUTOR.execute(() -> {
            try {
                nativeImage.writeFile(output);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
	}

	@Override
	public void close() {
		framebuffer.delete();
	}
}

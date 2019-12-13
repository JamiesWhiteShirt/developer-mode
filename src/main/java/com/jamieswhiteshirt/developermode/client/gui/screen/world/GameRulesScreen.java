package com.jamieswhiteshirt.developermode.client.gui.screen.world;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Blocks;
import net.minecraft.client.gui.ParentElement;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.item.ItemStack;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.GameRules;
import org.lwjgl.opengl.GL11;

@Environment(EnvType.CLIENT)
public class GameRulesScreen extends Screen implements ParentElement {
    private final GameRules gameRules;
    private final CreateWorldScreen parent;
    private GameRuleListWidget gameRuleListWidget;

    public GameRulesScreen(GameRules gameRules, CreateWorldScreen parent) {
        super(new TranslatableComponent("developermode.gameRules"));
        this.gameRules = gameRules;
        this.parent = parent;
    }

    @Override
    protected void init() {
        gameRuleListWidget = new GameRuleListWidget(gameRules, this, minecraft);
        children.add(gameRuleListWidget);
        addButton(new ButtonWidget(width / 2 - 155 + 160, height - 29, 150, 20, I18n.translate("gui.done"), button -> minecraft.openScreen(parent)));
    }

    @Override
    public void render(int x, int y, float delta) {
        renderBackground();
        gameRuleListWidget.render(x, y, delta);
        drawCenteredString(font, title.getFormattedText(), width / 2, 8, 16777215);
        super.render(x, y, delta);

        GlStateManager.matrixMode(GL11.GL_MODELVIEW);
        GlStateManager.pushMatrix();
        GlStateManager.loadIdentity();
        GlStateManager.matrixMode(GL11.GL_PROJECTION);
        GlStateManager.pushMatrix();
        GlStateManager.loadIdentity();

        GlStateManager.disableTexture();
        // GlStateManager.disableBlend();
        // GlStateManager.disableDepthTest();
        // GlStateManager.disableCull();
        // GlStateManager.disableAlphaTest();
        // GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBufferBuilder();
        bufferBuilder.begin(GL11.GL_TRIANGLES, VertexFormats.POSITION_COLOR);
        bufferBuilder.vertex(-1.0D, -1.0D, 0.0D).color(255, 255, 255, 255).next();
        bufferBuilder.vertex( 1.0D, -1.0D, 0.0D).color(255, 255, 255, 255).next();
        bufferBuilder.vertex( 1.0D,  1.0D, 0.0D).color(255, 255, 255, 255).next();
        bufferBuilder.vertex(-1.0D, -1.0D, 0.0D).color(255, 255, 255, 255).next();
        bufferBuilder.vertex( 1.0D,  1.0D, 0.0D).color(255, 255, 255, 255).next();
        bufferBuilder.vertex(-1.0D,  1.0D, 0.0D).color(255, 255, 255, 255).next();
        tessellator.draw();

        GlStateManager.enableTexture();

        minecraft.getTextureManager().bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
        minecraft.getTextureManager().getTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX).pushFilter(false, false);
        GlStateManager.enableRescaleNormal();
        GlStateManager.enableAlphaTest();
        GlStateManager.alphaFunc(516, 0.1F);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        itemRenderer.renderItemAndGlow(new ItemStack(Blocks.GRASS), itemRenderer.getModel(new ItemStack(Blocks.GRASS)));

		GlStateManager.disableAlphaTest();
		GlStateManager.disableRescaleNormal();
		GlStateManager.disableLighting();
		minecraft.getTextureManager().bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
		minecraft.getTextureManager().getTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX).popFilter();

        GlStateManager.matrixMode(GL11.GL_MODELVIEW);
        GlStateManager.popMatrix();
        GlStateManager.matrixMode(GL11.GL_PROJECTION);
        GlStateManager.popMatrix();
        GlStateManager.matrixMode(GL11.GL_MODELVIEW);
    }
}

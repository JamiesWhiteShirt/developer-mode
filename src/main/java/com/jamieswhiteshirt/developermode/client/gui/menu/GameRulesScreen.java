package com.jamieswhiteshirt.developermode.client.gui.menu;

import com.jamieswhiteshirt.developermode.client.gui.widget.GameRuleListWidget;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.ParentElement;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.gui.menu.NewLevelScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.world.GameRules;

@Environment(EnvType.CLIENT)
public class GameRulesScreen extends Screen implements ParentElement {
    private final GameRules gameRules;
    private final NewLevelScreen parent;
    private GameRuleListWidget gameRuleListWidget;

    public GameRulesScreen(GameRules gameRules, NewLevelScreen parent) {
        super(new TranslatableTextComponent("developermode.gameRules"));
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
    }
}

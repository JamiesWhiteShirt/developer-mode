package com.jamieswhiteshirt.developermode.client.gui.menu;

import com.jamieswhiteshirt.developermode.client.gui.NestedInputListener;
import com.jamieswhiteshirt.developermode.client.gui.widget.GameRuleListWidget;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.gui.menu.NewLevelScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.world.GameRules;

@Environment(EnvType.CLIENT)
public class GameRulesScreen extends Screen implements NestedInputListener {
    private final GameRules gameRules;
    private final NewLevelScreen parent;
    private GameRuleListWidget gameRuleListWidget;

    public GameRulesScreen(GameRules gameRules, NewLevelScreen parent) {
        super(new TranslatableTextComponent("developermode.gameRules"));
        this.gameRules = gameRules;
        this.parent = parent;
    }

    @Override
    protected void onInitialized() {
        gameRuleListWidget = new GameRuleListWidget(gameRules, this, client);
        listeners.add(gameRuleListWidget);
        addButton(new ButtonWidget(screenWidth / 2 - 155 + 160, screenHeight - 29, 150, 20, I18n.translate("gui.done"), (buttonWidget_1) -> client.openScreen(parent)));
    }

    @Override
    public void render(int x, int y, float delta) {
        drawBackground();
        gameRuleListWidget.render(x, y, delta);
        drawStringCentered(fontRenderer, title.getFormattedText(), screenWidth / 2, 8, 16777215);
        super.render(x, y, delta);
    }
}

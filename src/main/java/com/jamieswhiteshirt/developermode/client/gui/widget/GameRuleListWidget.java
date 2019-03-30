package com.jamieswhiteshirt.developermode.client.gui.widget;

import com.google.common.collect.ImmutableList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.world.GameRules;

import java.util.List;
import java.util.Map;

@Environment(EnvType.CLIENT)
public class GameRuleListWidget extends ElementListWidget<GameRuleListWidget.Entry> {
    private final GameRules gameRules;
    private int nameMaxWidth;

    public GameRuleListWidget(GameRules gameRules, Screen screen, MinecraftClient minecraft) {
        super(minecraft, screen.width + 45, screen.height, 24, screen.height - 32, 20);
        this.gameRules = gameRules;

        for (Map.Entry<String, GameRules.Key> entry : GameRules.getKeys().entrySet()) {
            String name = entry.getKey();
            GameRules.Key key = entry.getValue();
            GameRules.Value value = gameRules.get(name);

            if (value.getType() == GameRules.Type.BOOLEAN) {
                addItem(new BooleanEntry(name, key, value));
            } else {
                addItem(new TextEntry(name, key, value));
            }

            int nameWidth = minecraft.textRenderer.getStringWidth(name);
            if (nameWidth > nameMaxWidth) {
                nameMaxWidth = nameWidth;
            }
        }
    }

    @Override
    protected int getScrollbarPosition() {
        return super.getScrollbarPosition() + 15;
    }

    @Override
    public int getItemWidth() {
        return super.getItemWidth() + 32;
    }

    @Environment(EnvType.CLIENT)
    public class BooleanEntry extends Entry {
        private final ButtonWidget editButton;
        private final ButtonWidget resetButton;

        private BooleanEntry(String name, GameRules.Key key, GameRules.Value value) {
            super(name, key, value);
            this.editButton = new ButtonWidget(0, 0, 75, 20, I18n.translate("edit"), buttonWidget -> this.value.set(String.valueOf(!value.getBoolean()), null));
            this.resetButton = new ButtonWidget(0, 0, 50, 20, I18n.translate("controls.reset"), buttonWidget -> {
                String defaultValue = this.key.createValue().getString();
                this.value.set(defaultValue, null);
            });
        }

        @Override
        public void render(int i, int y, int x, int width, int height, int parentX, int parentY, boolean mouseHover, float delta) {
            client.textRenderer.draw(name, x + 90 - nameMaxWidth, (float)(y + height / 2 - 9 / 2), 0xFFFFFF);
            resetButton.x = x + 190;
            resetButton.y = y;
            resetButton.render(parentX, parentY, delta);
            editButton.x = x + 105;
            editButton.y = y;
            editButton.setMessage(value.getString());

            editButton.render(parentX, parentY, delta);
        }

        @Override
        public List<? extends Element> children() {
            return ImmutableList.of(editButton, resetButton);
        }
    }

    @Environment(EnvType.CLIENT)
    public class TextEntry extends Entry {
        private final TextFieldWidget textField;
        private final ButtonWidget resetButton;

        private TextEntry(String name, GameRules.Key key, GameRules.Value value) {
            super(name, key, value);
            this.textField = new TextFieldWidget(client.textRenderer, 1, 1, 73, 18);
            this.textField.setText(value.getString());
            this.textField.setChangedListener(newValue -> value.set(newValue, null));
            this.resetButton = new ButtonWidget(0, 0, 50, 20, I18n.translate("controls.reset"), buttonWidget_1 -> {
                String defaultValue = this.key.createValue().getString();
                this.textField.setText(defaultValue);
                this.value.set(defaultValue, null);
            });
        }

        @Override
        public void render(int i, int y, int x, int width, int height, int parentX, int parentY, boolean mouseHover, float delta) {
            client.textRenderer.draw(name, x + 90 - nameMaxWidth, (float)(y + height / 2 - 9 / 2), 0xFFFFFF);
            resetButton.x = x + 190;
            resetButton.y = y;
            resetButton.render(parentX, parentY, delta);
            textField.setX(x + 106);
            ((TextFieldWidgetExtension) textField).developermode_setY(y + 1);

            textField.render(parentX, parentY, delta);
        }

        @Override
        public List<? extends Element> children() {
            return ImmutableList.of(textField, resetButton);
        }
    }

    @Environment(EnvType.CLIENT)
    public abstract static class Entry extends ElementListWidget.ElementItem<Entry> {
        protected final String name;
        protected final GameRules.Key key;
        protected final GameRules.Value value;

        private Entry(String name, GameRules.Key key, GameRules.Value value) {
            this.name = name;
            this.key = key;
            this.value = value;
        }
    }
}

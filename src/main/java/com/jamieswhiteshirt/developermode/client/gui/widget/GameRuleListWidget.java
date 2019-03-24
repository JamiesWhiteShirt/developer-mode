package com.jamieswhiteshirt.developermode.client.gui.widget;

import com.jamieswhiteshirt.developermode.client.gui.NestedInputListener;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.InputListener;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.world.GameRules;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Environment(EnvType.CLIENT)
public class GameRuleListWidget extends EntryListWidget<GameRuleListWidget.Entry> implements NestedInputListener {
    private final GameRules gameRules;
    private final MinecraftClient client;
    private int nameMaxWidth;

    public GameRuleListWidget(GameRules gameRules, Screen screen, MinecraftClient minecraftClient_1) {
        super(minecraftClient_1, screen.screenWidth + 45, screen.screenHeight, 24, screen.screenHeight - 32, 20);
        this.gameRules = gameRules;
        this.client = minecraftClient_1;

        for (Map.Entry<String, GameRules.Key> entry : GameRules.getKeys().entrySet()) {
            String name = entry.getKey();
            GameRules.Key key = entry.getValue();
            GameRules.Value value = gameRules.get(name);

            if (value.getType() == GameRules.Type.BOOLEAN) {
                addEntry(new BooleanEntry(name, key, value));
            } else {
                addEntry(new TextEntry(name, key, value));
            }

            int nameWidth = minecraftClient_1.textRenderer.getStringWidth(name);
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
    public int getEntryWidth() {
        return super.getEntryWidth() + 32;
    }

    @Environment(EnvType.CLIENT)
    public class BooleanEntry extends Entry implements NestedInputListener {
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

        public void draw(int int_1, int int_2, int int_3, int int_4, boolean boolean_1, float float_1) {
            int y = getY();
            int x = getX();
            client.textRenderer.draw(name, x + 90 - nameMaxWidth, (float)(y + int_2 / 2 - 9 / 2), 0xFFFFFF);
            resetButton.x = x + 190;
            resetButton.y = y;
            resetButton.render(int_3, int_4, float_1);
            editButton.x = x + 105;
            editButton.y = y;
            editButton.setMessage(value.getString());

            editButton.render(int_3, int_4, float_1);
        }

        @Override
        public List<? extends InputListener> getInputListeners() {
            return Arrays.asList(editButton, resetButton);
        }
    }

    @Environment(EnvType.CLIENT)
    public class TextEntry extends Entry implements NestedInputListener {
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

        public void draw(int int_1, int int_2, int int_3, int int_4, boolean boolean_1, float float_1) {
            int y = getY();
            int x = getX();
            client.textRenderer.draw(name, x + 90 - nameMaxWidth, (float)(y + int_2 / 2 - 9 / 2), 0xFFFFFF);
            resetButton.x = x + 190;
            resetButton.y = y;
            resetButton.render(int_3, int_4, float_1);
            textField.setX(x + 106);
            ((TextFieldWidgetExtension) textField).developermode_setY(y + 1);

            textField.render(int_3, int_4, float_1);
        }

        @Override
        public List<? extends InputListener> getInputListeners() {
            return Arrays.asList(textField, resetButton);
        }
    }

    @Environment(EnvType.CLIENT)
    public abstract static class Entry extends EntryListWidget.Entry<Entry> implements NestedInputListener {
        protected final String name;
        protected final GameRules.Key key;
        protected final GameRules.Value value;
        private boolean active = true;
        @Nullable
        private InputListener focused;

        private Entry(String name, GameRules.Key key, GameRules.Value value) {
            this.name = name;
            this.key = key;
            this.value = value;
        }

        @Override
        public boolean isActive() {
            return active;
        }

        @Override
        public void setActive(boolean active) {
            this.active = active;
        }

        @Override
        public void setFocused(@Nullable InputListener focused) {
            this.focused = focused;
        }

        @Nullable
        @Override
        public InputListener getFocused() {
            return focused;
        }
    }
}

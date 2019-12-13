package com.jamieswhiteshirt.developermode.client.gui.screen.world;

import com.google.common.collect.ImmutableList;
import com.jamieswhiteshirt.developermode.mixin.world.GameRules$IntRuleAccessor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.world.GameRules;

import java.util.List;

@Environment(EnvType.CLIENT)
public class GameRuleListWidget extends ElementListWidget<GameRuleListWidget.Entry> {
    private int nameMaxWidth;

    public GameRuleListWidget(GameRules gameRules, Screen screen, MinecraftClient minecraft) {
        super(minecraft, screen.width + 45, screen.height, 24, screen.height - 32, 20);

        GameRules.forEachType(new GameRules.RuleTypeConsumer() {
            public <T extends GameRules.Rule<T>> void accept(GameRules.RuleKey<T> key, GameRules.RuleType<T> type) {
                String name = key.getName();
                GameRules.Rule<T> rule = gameRules.get(key);

                if (rule instanceof GameRules.BooleanRule) {
                    addEntry(new BooleanEntry(name, (GameRules.BooleanRule) rule, ((GameRules.BooleanRule) type.createRule()).get()));
                } else if (rule instanceof GameRules.IntRule) {
                    addEntry(new IntEntry(name, (GameRules.IntRule) rule, ((GameRules.IntRule) type.createRule()).get()));
                }

                int nameWidth = minecraft.textRenderer.getStringWidth(name);
                if (nameWidth > nameMaxWidth) {
                    nameMaxWidth = nameWidth;
                }
            }
        });
    }

    @Override
    protected int getScrollbarPosition() {
        return super.getScrollbarPosition() + 15;
    }

    @Override
    public int getRowWidth() {
        return super.getRowWidth() + 32;
    }

    @Environment(EnvType.CLIENT)
    public class BooleanEntry extends Entry {
        private final GameRules.BooleanRule rule;
        private final boolean defaultValue;
        private final ButtonWidget editButton;
        private final ButtonWidget resetButton;

        private BooleanEntry(String name, GameRules.BooleanRule rule, boolean defaultValue) {
            super(name);
            this.rule = rule;
            this.defaultValue = defaultValue;
            this.editButton = new ButtonWidget(0, 0, 75, 20, I18n.translate("edit"), buttonWidget -> this.rule.set(!this.rule.get(), null));
            this.resetButton = new ButtonWidget(0, 0, 50, 20, I18n.translate("controls.reset"), buttonWidget -> this.rule.set(this.defaultValue, null));
        }

        @Override
        public void render(int i, int y, int x, int width, int height, int parentX, int parentY, boolean mouseHover, float delta) {
            minecraft.textRenderer.draw(name, x + 90 - nameMaxWidth, (float)(y + height / 2 - 9 / 2), 0xFFFFFF);
            resetButton.x = x + 190;
            resetButton.y = y;
            resetButton.active = rule.get() != defaultValue;
            resetButton.render(parentX, parentY, delta);
            editButton.x = x + 105;
            editButton.y = y;
            editButton.setMessage(String.valueOf(rule.get()));

            editButton.render(parentX, parentY, delta);
        }

        @Override
        public List<? extends Element> children() {
            return ImmutableList.of(editButton, resetButton);
        }
    }

    @Environment(EnvType.CLIENT)
    public class IntEntry extends Entry {
        private final GameRules.IntRule rule;
        private final int defaultValue;
        private final TextFieldWidget textField;
        private final ButtonWidget resetButton;

        private IntEntry(String name, GameRules.IntRule rule, int defaultValue) {
            super(name);
            this.rule = rule;
            this.defaultValue = defaultValue;
            this.textField = new TextFieldWidget(minecraft.textRenderer, 1, 1, 73, 18, I18n.translate("developermode.gameRules.enterRule"));
            this.textField.setText(String.valueOf(rule.get()));
            this.resetButton = new ButtonWidget(0, 0, 50, 20, I18n.translate("controls.reset"), buttonWidget -> {
                this.textField.setText(String.valueOf(defaultValue));
                ((GameRules$IntRuleAccessor) this.rule).developermode_setValue(defaultValue);
                buttonWidget.active = false;
            });
            this.resetButton.active = false;
            this.textField.setChangedListener(newValue -> {
                try {
                    int value = Integer.parseInt(newValue);
                    ((GameRules$IntRuleAccessor) this.rule).developermode_setValue(value);
                    this.resetButton.active = value != defaultValue;
                } catch (NumberFormatException ignored) {
                    this.resetButton.active = true;
                }
            });
        }

        @Override
        public void render(int i, int y, int x, int width, int height, int parentX, int parentY, boolean mouseHover, float delta) {
            minecraft.textRenderer.draw(name, x + 90 - nameMaxWidth, (float)(y + height / 2 - 9 / 2), 0xFFFFFF);
            resetButton.x = x + 190;
            resetButton.y = y;
            resetButton.render(parentX, parentY, delta);
            textField.setX(x + 106);
            textField.y = y + 1;

            textField.render(parentX, parentY, delta);
        }

        @Override
        public List<? extends Element> children() {
            return ImmutableList.of(textField, resetButton);
        }
    }

    @Environment(EnvType.CLIENT)
    public abstract static class Entry extends ElementListWidget.Entry<Entry> {
        protected final String name;

        private Entry(String name) {
            this.name = name;
        }
    }
}

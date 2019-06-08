package com.jamieswhiteshirt.developermode.client.gui.screen.world;

import com.google.common.collect.ImmutableList;
import com.jamieswhiteshirt.developermode.common.world.GameRules$IntRuleExtension;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.world.GameRules;

import java.util.List;
import java.util.Objects;

@Environment(EnvType.CLIENT)
public class GameRuleListWidget extends ElementListWidget {
    private final GameRules gameRules;
    private int nameMaxWidth;
    private CommandContext<ServerCommandSource> dummyCommandContext = new CommandContext<>(new ServerCommandSource(null, null, null, null, 0, null, null, null, null), null, null, null, null, null, null, null, null, false);

    public GameRuleListWidget(GameRules gameRules, Screen screen, MinecraftClient minecraft) {
        super(minecraft, screen.width + 45, screen.height, 24, screen.height - 32, 20);
        this.gameRules = gameRules;

        GameRules.forEach(new GameRules.RuleConsumer() {
            @SuppressWarnings("unchecked")
            @Override
            public <T extends GameRules.Rule<T>> void accept(GameRules.RuleKey<T> key, GameRules.RuleType<T> ruleType) {
                GameRules.Rule<T> rule = gameRules.get(key);
                if (rule instanceof GameRules.BooleanRule) {
                    addEntry(new BooleanEntry((GameRules.RuleKey<GameRules.BooleanRule>) key, (GameRules.RuleType<GameRules.BooleanRule>) ruleType, (GameRules.BooleanRule) rule));
                } else if (rule instanceof GameRules.IntRule) {
                    addEntry(new IntEntry((GameRules.RuleKey<GameRules.IntRule>) key, (GameRules.RuleType<GameRules.IntRule>) ruleType, (GameRules.IntRule) rule));
                }

                int nameWidth = minecraft.textRenderer.getStringWidth(key.getName());
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
    public class BooleanEntry extends Entry<GameRules.BooleanRule> {
        private final boolean defaultValue;
        private final ButtonWidget editButton;
        private final ButtonWidget resetButton;

        private BooleanEntry(GameRules.RuleKey<GameRules.BooleanRule> key, GameRules.RuleType<GameRules.BooleanRule> type, GameRules.BooleanRule rule) {
            super(key, type, rule);
            this.defaultValue = type.newRule().getValue();
            this.editButton = new ButtonWidget(0, 0, 75, 20, I18n.translate("edit"), buttonWidget -> this.rule.setValue(!rule.getValue(), null));
            this.resetButton = new ButtonWidget(0, 0, 50, 20, I18n.translate("controls.reset"), buttonWidget -> {
                this.rule.setValue(defaultValue, null);
            });
        }

        @Override
        public void render(int i, int y, int x, int width, int height, int parentX, int parentY, boolean mouseHover, float delta) {
            minecraft.textRenderer.draw(key.getName(), x + 90 - nameMaxWidth, (float)(y + height / 2 - 9 / 2), 0xFFFFFF);
            resetButton.x = x + 190;
            resetButton.y = y;
            resetButton.active = rule.getValue() != defaultValue;
            resetButton.render(parentX, parentY, delta);
            editButton.x = x + 105;
            editButton.y = y;
            editButton.setMessage(Boolean.toString(rule.getValue()));

            editButton.render(parentX, parentY, delta);
        }

        @Override
        public List<? extends Element> children() {
            return ImmutableList.of(editButton, resetButton);
        }
    }

    @Environment(EnvType.CLIENT)
    public class IntEntry extends Entry<GameRules.IntRule> {
        private final int defaultValue;
        private final TextFieldWidget textField;
        private final ButtonWidget resetButton;

        private IntEntry(GameRules.RuleKey<GameRules.IntRule> key, GameRules.RuleType<GameRules.IntRule> type, GameRules.IntRule rule) {
            super(key, type, rule);
            this.defaultValue = type.newRule().getValue();
            this.textField = new TextFieldWidget(minecraft.textRenderer, 1, 1, 73, 18, I18n.translate("developermode.gameRules.enterValue"));
            this.textField.setText(Integer.toString(rule.getValue()));
            this.textField.setChangedListener(newValue -> {
                try {
                    int intValue = Integer.parseInt(newValue);
                    ((GameRules$IntRuleExtension) rule).developermode_setValue(intValue);
                } catch (NumberFormatException ignored) {
                }
            });
            this.resetButton = new ButtonWidget(0, 0, 50, 20, I18n.translate("controls.reset"), buttonWidget_1 -> {
                ((GameRules$IntRuleExtension) rule).developermode_setValue(defaultValue);
                textField.setText(Integer.toString(defaultValue));
            });
        }

        @Override
        public void render(int i, int y, int x, int width, int height, int parentX, int parentY, boolean mouseHover, float delta) {
            minecraft.textRenderer.draw(key.getName(), x + 90 - nameMaxWidth, (float)(y + height / 2 - 9 / 2), 0xFFFFFF);
            resetButton.x = x + 190;
            resetButton.y = y;
            resetButton.active = !Objects.equals(textField.getText(), Integer.toString(defaultValue));
            resetButton.render(parentX, parentY, delta);
            textField.setX(x + 106);
            textField.y = y + 1;
            boolean isValidValue;
            try {
                Integer.parseInt(textField.getText());
                isValidValue = true;
            } catch (NumberFormatException ignored) {
                isValidValue = false;
            }
            textField.setEditableColor(isValidValue ? 0xE0E0E0 : 0xE00000);

            textField.render(parentX, parentY, delta);
        }

        @Override
        public List<? extends Element> children() {
            return ImmutableList.of(textField, resetButton);
        }
    }

    @Environment(EnvType.CLIENT)
    public abstract static class Entry<T extends GameRules.Rule<T>> extends ElementListWidget.Entry<Entry<T>> {
        protected final GameRules.RuleKey<T> key;
        protected final GameRules.RuleType<T> type;
        protected final T rule;

        private Entry(GameRules.RuleKey<T> key, GameRules.RuleType<T> type, T rule) {
            this.key = key;
            this.type = type;
            this.rule = rule;
        }
    }
}

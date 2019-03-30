package com.jamieswhiteshirt.developermode.mixin.client.gui.menu;

import com.jamieswhiteshirt.developermode.DeveloperMode;
import com.jamieswhiteshirt.developermode.client.DeveloperModeClient;
import com.jamieswhiteshirt.developermode.client.gui.menu.LevelSelectScreenExtension;
import com.jamieswhiteshirt.developermode.client.gui.menu.NewLevelScreenExtension;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.gui.menu.LevelSelectScreen;
import net.minecraft.client.gui.menu.NewLevelScreen;
import net.minecraft.client.gui.widget.LevelListWidget;
import net.minecraft.text.TextComponent;
import net.minecraft.world.level.storage.LevelStorage;
import net.minecraft.world.level.storage.LevelStorageException;
import net.minecraft.world.level.storage.LevelSummary;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.io.File;
import java.util.Optional;

@Mixin(LevelSelectScreen.class)
public abstract class LevelSelectScreenMixin extends Screen implements LevelSelectScreenExtension {
    @Shadow private LevelListWidget levelList;

    protected LevelSelectScreenMixin(TextComponent textComponent_1) {
        super(textComponent_1);
    }

    @ModifyArg(
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/MinecraftClient;openScreen(Lnet/minecraft/client/gui/Screen;)V"
        ),
        method = "method_19944(Lnet/minecraft/client/gui/widget/ButtonWidget;)V"
    )
    private Screen modifyOpenScreen(Screen original) {
        if (DeveloperModeClient.rememberNewWorldSettingsEnabled) {
            File file = new File(MinecraftClient.getInstance().runDirectory, "newWorldSettings.json");
            ((NewLevelScreenExtension) original).developermode_setLevelPropertiesFile(file);
        }
        return original;
    }

    @Override
    public void developermode_openOrCreateLevel(String name) {
        try {
            LevelStorage levelStorage = minecraft.getLevelStorage();
            Optional<LevelSummary> optLevel = levelStorage.getLevelList().stream()
                .filter(level -> level.getName().equals(name))
                .findFirst();
            if (optLevel.isPresent()) {
                ((LevelListWidget) (Object) this).new LevelItem(levelList, optLevel.get(), levelStorage).method_20164();
            } else {
                NewLevelScreen newLevelScreen = new NewLevelScreen(this);
                NewLevelScreenExtension extension = (NewLevelScreenExtension) newLevelScreen;
                if (DeveloperModeClient.rememberNewWorldSettingsEnabled) {
                    File file = new File(minecraft.runDirectory, "newWorldSettings.json");
                    extension.developermode_setLevelPropertiesFile(file);
                }
                extension.developermode_setLevelName(name);
                minecraft.openScreen(newLevelScreen);
            }
        } catch (LevelStorageException e) {
            DeveloperMode.LOGGER.error("Failed to auto load world", e);
        }
    }
}

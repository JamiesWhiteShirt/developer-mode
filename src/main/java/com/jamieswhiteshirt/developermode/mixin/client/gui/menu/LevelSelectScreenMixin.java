package com.jamieswhiteshirt.developermode.mixin.client.gui.menu;

import com.jamieswhiteshirt.developermode.DeveloperMode;
import com.jamieswhiteshirt.developermode.client.DeveloperModeClient;
import com.jamieswhiteshirt.developermode.client.gui.menu.LevelSelectScreenExtension;
import com.jamieswhiteshirt.developermode.client.gui.menu.NewLevelScreenExtension;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.gui.menu.LevelSelectScreen;
import net.minecraft.client.gui.menu.NewLevelScreen;
import net.minecraft.client.gui.widget.LevelListWidget;
import net.minecraft.client.gui.widget.LevelSelectEntryWidget;
import net.minecraft.world.level.storage.LevelStorage;
import net.minecraft.world.level.storage.LevelStorageException;
import net.minecraft.world.level.storage.LevelSummary;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.io.File;
import java.util.Optional;

@Mixin(LevelSelectScreen.class)
public abstract class LevelSelectScreenMixin extends Screen implements LevelSelectScreenExtension {
    @Shadow private LevelListWidget levelList;

    @Override
    public void developermode_openOrCreateLevel(String name) {
        try {
            LevelStorage levelStorage = client.getLevelStorage();
            Optional<LevelSummary> optLevel = levelStorage.getAvailableLevels().stream()
                .filter(level -> level.getName().equals(name))
                .findFirst();
            if (optLevel.isPresent()) {
                new LevelSelectEntryWidget(levelList, optLevel.get(), levelStorage).loadLevel();
            } else {
                NewLevelScreen newLevelScreen = new NewLevelScreen(this);
                NewLevelScreenExtension extension = (NewLevelScreenExtension) newLevelScreen;
                if (DeveloperModeClient.rememberNewWorldSettingsEnabled) {
                    File file = new File(client.runDirectory, "newWorldSettings.json");
                    extension.developermode_setLevelPropertiesFile(file);
                }
                extension.developermode_setLevelName(name);
                client.openScreen(newLevelScreen);
            }
        } catch (LevelStorageException e) {
            DeveloperMode.LOGGER.error("Failed to auto load world", e);
        }
    }
}

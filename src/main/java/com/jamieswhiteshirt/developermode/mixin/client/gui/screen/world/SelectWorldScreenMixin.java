package com.jamieswhiteshirt.developermode.mixin.client.gui.screen.world;

import com.jamieswhiteshirt.developermode.DeveloperMode;
import com.jamieswhiteshirt.developermode.client.DeveloperModeClient;
import com.jamieswhiteshirt.developermode.client.gui.screen.world.SelectWorldScreenExtension;
import com.jamieswhiteshirt.developermode.client.gui.screen.world.CreateWorldScreenExtension;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.screen.world.WorldListWidget;
import net.minecraft.text.Text;
import net.minecraft.world.level.storage.LevelStorage;
import net.minecraft.world.level.storage.LevelStorageException;
import net.minecraft.world.level.storage.LevelSummary;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.io.File;
import java.util.Optional;

@Mixin(SelectWorldScreen.class)
public abstract class SelectWorldScreenMixin extends Screen implements SelectWorldScreenExtension {
    @Shadow private WorldListWidget levelList;

    protected SelectWorldScreenMixin(Text text) {
        super(text);
    }

    @ModifyArg(
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/MinecraftClient;openScreen(Lnet/minecraft/client/gui/screen/Screen;)V"
        ),
        method = "method_19944(Lnet/minecraft/client/gui/widget/ButtonWidget;)V"
    )
    private Screen modifyOpenScreen(Screen original) {
        if (DeveloperModeClient.rememberNewWorldSettingsEnabled) {
            File file = new File(MinecraftClient.getInstance().runDirectory, "newWorldSettings.json");
            ((CreateWorldScreenExtension) original).developermode_setLevelPropertiesFile(file);
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
                levelList.new Entry(levelList, optLevel.get(), levelStorage).play();
            } else {
                CreateWorldScreen createWorldScreen = new CreateWorldScreen(this);
                CreateWorldScreenExtension extension = (CreateWorldScreenExtension) createWorldScreen;
                if (DeveloperModeClient.rememberNewWorldSettingsEnabled) {
                    File file = new File(minecraft.runDirectory, "newWorldSettings.json");
                    extension.developermode_setLevelPropertiesFile(file);
                }
                extension.developermode_setLevelName(name);
                minecraft.openScreen(createWorldScreen);
            }
        } catch (LevelStorageException e) {
            DeveloperMode.LOGGER.error("Failed to auto load world", e);
        }
    }
}

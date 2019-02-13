package com.jamieswhiteshirt.developermode.mixin.client.gui.menu;

import com.jamieswhiteshirt.developermode.DeveloperMode;
import com.jamieswhiteshirt.developermode.client.NewLevelProperties;
import com.jamieswhiteshirt.developermode.client.gui.menu.NewLevelScreenExtension;
import com.jamieswhiteshirt.developermode.common.world.level.LevelInfoExtension;
import net.minecraft.client.gui.menu.NewLevelScreen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.GameRules;
import net.minecraft.world.level.LevelGeneratorType;
import net.minecraft.world.level.LevelInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.*;

@Mixin(NewLevelScreen.class)
public abstract class NewLevelScreenMixin implements NewLevelScreenExtension {
    private File developermode_propertiesFile;
    private GameRules developermode_gameRules = new GameRules();

    @Shadow private String seed;
    @Shadow private int generatorType;
    @Shadow public CompoundTag field_3200;
    @Shadow private boolean structures;
    @Shadow private boolean commandsAllowed;
    @Shadow private String gameMode;
    @Shadow private boolean field_3191;
    @Shadow private String levelName;

    @Inject(
        at = @At("RETURN"),
        method = "onClosed()V"
    )
    private void onClosed(CallbackInfo ci) {
        if (developermode_propertiesFile != null) {
            developermode_writeLevelPropertiesFile();
        }
    }

    @ModifyVariable(
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/LevelInfo;setGeneratorOptions(Lcom/google/gson/JsonElement;)Lnet/minecraft/world/level/LevelInfo;"
        ),
        method = "createLevel()V"
    )
    private LevelInfo modifyLevelInfo(LevelInfo original) {
        LevelInfoExtension extension = (LevelInfoExtension) (Object) original;
        extension.developermode_setGameRules(developermode_gameRules);
        return original;
    }

    @Override
    public void developermode_setLevelPropertiesFile(File file) {
        developermode_propertiesFile = file;
        if (developermode_propertiesFile.exists()) {
            developermode_readLevelPropertiesFile();
        } else {
            developermode_writeLevelPropertiesFile();
        }
    }

    @Override
    public void developermode_setLevelName(String name) {
        levelName = name;
    }

    private void developermode_readLevelPropertiesFile() {
        NewLevelProperties properties = new NewLevelProperties();
        try {
            properties.readFromFile(developermode_propertiesFile);
            developermode_setProperties(properties);
        } catch (IOException e) {
            DeveloperMode.LOGGER.error("Could not read new world settings", e);
        }
    }

    private void developermode_writeLevelPropertiesFile() {
        NewLevelProperties properties = developermode_getProperties();
        try {
            properties.writeToFile(developermode_propertiesFile);
        } catch (IOException e) {
            DeveloperMode.LOGGER.error("Could not write new world settings", e);
        }
    }

    private NewLevelProperties developermode_getProperties() {
        NewLevelProperties properties = new NewLevelProperties();
        properties.randomSeed = seed;
        properties.generatorName = LevelGeneratorType.TYPES[generatorType].getName();
        properties.generatorOptions = field_3200.copy();
        properties.mapFeatures = structures;
        properties.allowCommands = commandsAllowed;
        properties.bonusItems = field_3191;
        properties.gameType = gameMode;
        properties.gameRules = developermode_gameRules;
        return properties;
    }

    private void developermode_setProperties(NewLevelProperties properties) {
        seed = properties.randomSeed;
        LevelGeneratorType levelGeneratorType = LevelGeneratorType.getTypeFromName(properties.generatorName);
        generatorType = levelGeneratorType != null ? levelGeneratorType.getId() : 0;
        field_3200 = properties.generatorOptions.copy();
        structures = properties.mapFeatures;
        commandsAllowed = properties.allowCommands;
        field_3191 = properties.bonusItems;
        gameMode = properties.gameType;
        developermode_gameRules = properties.gameRules;
    }
}

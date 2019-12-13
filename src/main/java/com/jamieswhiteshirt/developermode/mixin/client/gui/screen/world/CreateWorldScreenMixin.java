package com.jamieswhiteshirt.developermode.mixin.client.gui.screen.world;

import com.jamieswhiteshirt.developermode.DeveloperMode;
import com.jamieswhiteshirt.developermode.client.CreateWorldScreenUtil;
import com.jamieswhiteshirt.developermode.client.DeveloperModeClient;
import com.jamieswhiteshirt.developermode.client.NewLevelProperties;
import com.jamieswhiteshirt.developermode.client.gui.screen.world.CreateWorldScreenExtension;
import com.jamieswhiteshirt.developermode.client.gui.screen.world.GameRulesScreen;
import com.jamieswhiteshirt.developermode.common.world.level.LevelInfoExtension;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.Text;
import net.minecraft.world.GameRules;
import net.minecraft.world.level.LevelGeneratorType;
import net.minecraft.world.level.LevelInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;
import java.io.IOException;

@Mixin(CreateWorldScreen.class)
public abstract class CreateWorldScreenMixin extends Screen implements CreateWorldScreenExtension {
    private File developermode_propertiesFile;
    private GameRules developermode_gameRules = new GameRules();
    private ButtonWidget developermode_gameRulesButton;

    @Shadow private String seed;
    @Shadow private int generatorType;
    @Shadow public CompoundTag generatorOptionsTag;
    @Shadow private boolean structures;
    @Shadow private boolean cheatsEnabled;
    @Shadow private boolean bonusChest;
    @Shadow private String levelName;

    protected CreateWorldScreenMixin(Text text) {
        super(text);
    }

    @Inject(
        at = @At("RETURN"),
        method = "removed()V"
    )
    private void removed(CallbackInfo ci) {
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

    @Inject(
        method = "init()V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/screen/world/CreateWorldScreen;setMoreOptionsOpen(Z)V"
        )
    )
    private void init(CallbackInfo ci) {
        developermode_gameRulesButton = this.addButton(new ButtonWidget(2 * this.width / 3 + 5, 187, 80, 20, I18n.translate("developermode.gameRules"), (buttonWidget_1) -> {
            minecraft.openScreen(new GameRulesScreen(developermode_gameRules, (CreateWorldScreen) (Object) this));
        }));
    }

    @Inject(
        method = "setMoreOptionsOpen(Z)V",
        at = @At("TAIL")
    )
    private void setMoreOptionsOpen(boolean moreOptionsOpen, CallbackInfo ci) {
        developermode_gameRulesButton.visible = moreOptionsOpen && DeveloperModeClient.gameRulesGuiEnabled;
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
        properties.generatorOptions = (CompoundTag) generatorOptionsTag.copy();
        properties.mapFeatures = structures;
        properties.allowCommands = cheatsEnabled;
        properties.bonusItems = bonusChest;
        Object currentMode = CreateWorldScreenUtil.getCurrentMode((CreateWorldScreen)(Object) this);
        properties.gameType = CreateWorldScreenUtil.getModeTranslationSuffix(currentMode);
        properties.gameRules = developermode_gameRules;
        return properties;
    }

    private void developermode_setProperties(NewLevelProperties properties) {
        seed = properties.randomSeed;
        LevelGeneratorType levelGeneratorType = LevelGeneratorType.getTypeFromName(properties.generatorName);
        generatorType = levelGeneratorType != null ? levelGeneratorType.getId() : 0;
        generatorOptionsTag = (CompoundTag) properties.generatorOptions.copy();
        structures = properties.mapFeatures;
        cheatsEnabled = properties.allowCommands;
        bonusChest = properties.bonusItems;
        for (Object mode : CreateWorldScreenUtil.MODE_CLASS.getEnumConstants()) {
            if (CreateWorldScreenUtil.getModeTranslationSuffix(mode).equals(properties.gameType)) {
                CreateWorldScreenUtil.setCurrentMode((CreateWorldScreen)(Object) this, mode);
            }
        }
        developermode_gameRules = properties.gameRules;
    }
}

package com.jamieswhiteshirt.developermode.mixin.client.gui.menu;

import com.jamieswhiteshirt.developermode.client.DeveloperModeClient;
import com.jamieswhiteshirt.developermode.client.gui.menu.NewLevelScreenExtension;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.io.File;

@Mixin(targets = "net.minecraft.client.gui.menu.LevelSelectScreen$3")
public abstract class LevelSelectScreen$3Mixin extends ButtonWidget {
    public LevelSelectScreen$3Mixin(int int_1, int int_2, String string_1) {
        super(int_1, int_2, string_1);
    }

    @ModifyArg(
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/MinecraftClient;openScreen(Lnet/minecraft/client/gui/Screen;)V"
        ),
        method = "onPressed(DD)V"
    )
    private Screen modifyOpenScreen(Screen original) {
        if (DeveloperModeClient.rememberNewWorldSettingsEnabled) {
            File file = new File(MinecraftClient.getInstance().runDirectory, "newWorldSettings.json");
            ((NewLevelScreenExtension) original).developermode_setLevelPropertiesFile(file);
        }
        return original;
    }
}

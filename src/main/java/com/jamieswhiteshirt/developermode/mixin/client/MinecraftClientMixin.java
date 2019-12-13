package com.jamieswhiteshirt.developermode.mixin.client;

import com.jamieswhiteshirt.developermode.client.RunArgsExtension;
import com.jamieswhiteshirt.developermode.client.gui.screen.world.SelectWorldScreenExtension;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {
    private String developermode_autoWorld;

    @Inject(
        at = @At("RETURN"),
        method = "<init>(Lnet/minecraft/client/RunArgs;)V"
    )
    private void constructor(RunArgs runArgs, CallbackInfo ci) {
        RunArgsExtension extension = (RunArgsExtension) runArgs;
        developermode_autoWorld = extension.developermode_getAutoWorld();
    }

    @Redirect(
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/MinecraftClient;openScreen(Lnet/minecraft/client/gui/screen/Screen;)V",
            ordinal = 1
        ),
        method = "<init>(Lnet/minecraft/client/RunArgs;)V"
    )
    private void init(MinecraftClient client, Screen original) {
        if (developermode_autoWorld != null) {
            SelectWorldScreen levelSelectScreen = new SelectWorldScreen(original);
            client.openScreen(levelSelectScreen);
            SelectWorldScreenExtension extension = (SelectWorldScreenExtension) levelSelectScreen;
            extension.developermode_openOrCreateLevel(developermode_autoWorld);
        } else {
            client.openScreen(original);
        }
    }
}

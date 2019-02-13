package com.jamieswhiteshirt.developermode.mixin.client.gui;

import com.jamieswhiteshirt.developermode.client.DeveloperModeClient;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.gui.SplashScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(SplashScreen.class)
public abstract class SplashScreenMixin extends Screen {
    @ModifyConstant(
        constant = @Constant(
            floatValue = 1000.F
        ),
        method = "draw(IIF)V"
    )
    private float modifyFadeTime(float originalValue) {
        return DeveloperModeClient.splashFadeTime / 2.0F;
    }

    @ModifyArg(
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/SplashScreen;drawRect(IIIII)V",
            ordinal = 0
        ),
        method = "draw(IIF)V",
        index = 4
    )
    private int backgroundColor(int i) {
        return DeveloperModeClient.theme.getBackgroundColor(i);
    }

    @ModifyArg(
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/SplashScreen;drawRect(IIIII)V",
            ordinal = 0
        ),
        method = "renderProgressBar(IIIIFF)V",
        index = 4
    )
    private int progressBarOutlineColor(int i) {
        return DeveloperModeClient.theme.getProgressBarOutlineColor(i);
    }

    @ModifyArg(
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/SplashScreen;drawRect(IIIII)V",
            ordinal = 1
        ),
        method = "renderProgressBar(IIIIFF)V",
        index = 4
    )
    private int progressBarBackgroundColor(int i) {
        return DeveloperModeClient.theme.getProgressBarBackgroundColor(i);
    }

    @ModifyArg(
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/SplashScreen;drawRect(IIIII)V",
            ordinal = 2
        ),
        method = "renderProgressBar(IIIIFF)V",
        index = 4
    )
    private int progressBarFillColor(int i) {
        return DeveloperModeClient.theme.getProgressBarFillColor(i);
    }
}

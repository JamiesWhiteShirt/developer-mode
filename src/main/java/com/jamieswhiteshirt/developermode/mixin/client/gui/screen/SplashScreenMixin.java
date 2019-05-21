package com.jamieswhiteshirt.developermode.mixin.client.gui.screen;

import com.jamieswhiteshirt.developermode.client.DeveloperModeClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Overlay;
import net.minecraft.client.gui.screen.SplashScreen;
import net.minecraft.resource.ResourceReloadMonitor;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SplashScreen.class)
public abstract class SplashScreenMixin extends Overlay {
    @Mutable @Shadow @Final private boolean field_18219;
    @Shadow @Final private MinecraftClient client;

    @Inject(
        at = @At("RETURN"),
        method = "<init>(Lnet/minecraft/client/MinecraftClient;Lnet/minecraft/resource/ResourceReloadMonitor;Ljava/lang/Runnable;Z)V"
    )
    private void constructor(MinecraftClient minecraftClient_1, ResourceReloadMonitor resourceReloadHandler_1, Runnable runnable_1, boolean boolean_1, CallbackInfo ci) {
        if (DeveloperModeClient.splashFadeTime <= 0) {
            field_18219 = true;
        }
    }

    @ModifyConstant(
        constant = @Constant(
            floatValue = 1000.F
        ),
        method = "render(IIF)V"
    )
    private float modifyFadeTime1(float originalValue) {
        return DeveloperModeClient.splashFadeTime / 2.0F;
    }

    @ModifyConstant(
        constant = @Constant(
            floatValue = 500.F
        ),
        method = "render(IIF)V"
    )
    private float modifyFadeTime2(float originalValue) {
        return DeveloperModeClient.splashFadeTime / 4.0F;
    }

    @ModifyArg(
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/screen/SplashScreen;fill(IIIII)V"
        ),
        method = "render(IIF)V",
        index = 4
    )
    private int backgroundColor(int x, int y, int width, int height, int color) {
        // In case someone else injects drawRect calls in this method
        // We'll assume it's drawing a background if drawRect covers the whole screen exactly
        if (x == 0 && y == 0 && width == client.window.getScaledWidth() && height == client.window.getScaledHeight()) {
            return DeveloperModeClient.theme.getBackgroundColor(color);
        } else {
            return color;
        }
    }

    @ModifyArg(
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/screen/SplashScreen;fill(IIIII)V",
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
            target = "Lnet/minecraft/client/gui/screen/SplashScreen;fill(IIIII)V",
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
            target = "Lnet/minecraft/client/gui/screen/SplashScreen;fill(IIIII)V",
            ordinal = 2
        ),
        method = "renderProgressBar(IIIIFF)V",
        index = 4
    )
    private int progressBarFillColor(int i) {
        return DeveloperModeClient.theme.getProgressBarFillColor(i);
    }
}

package com.jamieswhiteshirt.developermode.mixin.client.gui;

import com.jamieswhiteshirt.developermode.client.DeveloperModeClient;
import net.minecraft.class_4071;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.SplashScreen;
import net.minecraft.resource.ResourceReloadHandler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SplashScreen.class)
public abstract class SplashScreenMixin extends class_4071 {
    @Mutable @Shadow @Final private boolean field_18219;

    @Shadow @Final private MinecraftClient field_18217;

    @Inject(
        at = @At("RETURN"),
        method = "<init>(Lnet/minecraft/client/MinecraftClient;Lnet/minecraft/resource/ResourceReloadHandler;Ljava/lang/Runnable;Z)V"
    )
    private void constructor(MinecraftClient minecraftClient_1, ResourceReloadHandler resourceReloadHandler_1, Runnable runnable_1, boolean boolean_1, CallbackInfo ci) {
        if (DeveloperModeClient.splashFadeTime <= 0) {
            field_18219 = true;
        }
    }

    @ModifyConstant(
        constant = @Constant(
            floatValue = 1000.F
        ),
        method = "method_18326(IIF)V"
    )
    private float modifyFadeTime1(float originalValue) {
        return DeveloperModeClient.splashFadeTime / 2.0F;
    }

    @ModifyConstant(
        constant = @Constant(
            floatValue = 500.F
        ),
        method = "method_18326(IIF)V"
    )
    private float modifyFadeTime2(float originalValue) {
        return DeveloperModeClient.splashFadeTime / 4.0F;
    }

    @ModifyArg(
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/SplashScreen;drawRect(IIIII)V"
        ),
        method = "method_18326(IIF)V",
        index = 4
    )
    private int backgroundColor(int x, int y, int width, int height, int color) {
        // In case someone else injects drawRect calls in this method
        // We'll assume it's drawing a background if drawRect covers the whole screen exactly
        if (x == 0 && y == 0 && width == field_18217.window.getScaledWidth() && height == field_18217.window.getScaledHeight()) {
            return DeveloperModeClient.theme.getBackgroundColor(color);
        } else {
            return color;
        }
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

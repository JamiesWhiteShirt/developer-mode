package com.jamieswhiteshirt.developermode.mixin.client.gui;

import com.jamieswhiteshirt.developermode.client.DeveloperModeClient;
import net.minecraft.class_766;
import net.minecraft.client.gui.MainMenuScreen;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MainMenuScreen.class)
public abstract class MainMenuScreenMixin {
    @Mutable @Shadow @Final private long field_17773;
    @Mutable @Shadow @Final private long field_17772;

    @Inject(
        at = @At("RETURN"),
        method = "<init>(Lnet/minecraft/class_766;J)V"
    )
    private void constructor(class_766 class_766_1, long long_1, CallbackInfo ci) {
        field_17773 = field_17772 + DeveloperModeClient.splashFadeTime / 2;
    }
}

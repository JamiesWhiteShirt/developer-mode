package com.jamieswhiteshirt.developermode.mixin.world.level;

import com.jamieswhiteshirt.developermode.common.world.level.LevelInfoExtension;
import net.minecraft.world.GameRules;
import net.minecraft.world.level.LevelInfo;
import net.minecraft.world.level.LevelProperties;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelProperties.class)
public abstract class LevelPropertiesMixin {
    @Mutable @Shadow @Final private GameRules gameRules;

    @Inject(
        at = @At("RETURN"),
        method = "<init>(Lnet/minecraft/world/level/LevelInfo;Ljava/lang/String;)V"
    )
    private void constructor(LevelInfo levelInfo, String string, CallbackInfo ci) {
        LevelInfoExtension extension = (LevelInfoExtension) (Object) levelInfo;
        GameRules gameRules = extension.developermode_getGameRules();
        if (gameRules != null) {
            this.gameRules = gameRules;
        }
    }
}

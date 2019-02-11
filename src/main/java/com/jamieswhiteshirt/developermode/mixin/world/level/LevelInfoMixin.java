package com.jamieswhiteshirt.developermode.mixin.world.level;

import com.jamieswhiteshirt.developermode.common.world.level.LevelInfoExtension;
import net.minecraft.world.GameRules;
import net.minecraft.world.level.LevelInfo;
import net.minecraft.world.level.LevelProperties;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelInfo.class)
public abstract class LevelInfoMixin implements LevelInfoExtension {
    private GameRules developermode_gameRules;

    @Inject(
        at = @At("RETURN"),
        method = "<init>(Lnet/minecraft/world/level/LevelProperties;)V"
    )
    private void constructor(LevelProperties levelProperties, CallbackInfo ci) {
        developermode_gameRules = levelProperties.getGameRules();
    }

    @Override
    public GameRules developermode_getGameRules() {
        return developermode_gameRules;
    }

    @Override
    public void developermode_setGameRules(GameRules gameRules) {
        this.developermode_gameRules = gameRules;
    }
}

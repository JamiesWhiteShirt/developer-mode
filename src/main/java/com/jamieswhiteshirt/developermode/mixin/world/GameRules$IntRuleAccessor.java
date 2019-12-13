package com.jamieswhiteshirt.developermode.mixin.world;

import net.minecraft.world.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(GameRules.IntRule.class)
public interface GameRules$IntRuleAccessor {
    @Accessor("value")
    void developermode_setValue(int value);
}

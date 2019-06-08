package com.jamieswhiteshirt.developermode.mixin.world;

import com.jamieswhiteshirt.developermode.common.world.GameRules$IntRuleExtension;
import net.minecraft.world.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(GameRules.IntRule.class)
public class GameRules$IntRuleMixin implements GameRules$IntRuleExtension {
    @Shadow private int value;

    @Override
    public void developermode_setValue(int value) {
        this.value = value;
    }
}

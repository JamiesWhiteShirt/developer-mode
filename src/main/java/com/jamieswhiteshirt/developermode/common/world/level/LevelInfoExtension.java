package com.jamieswhiteshirt.developermode.common.world.level;

import net.minecraft.world.GameRules;

public interface LevelInfoExtension {
    GameRules developermode_getGameRules();

    void developermode_setGameRules(GameRules gameRules);
}

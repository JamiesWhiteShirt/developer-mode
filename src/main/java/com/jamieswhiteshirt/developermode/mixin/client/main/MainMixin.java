package com.jamieswhiteshirt.developermode.mixin.client.main;

import com.jamieswhiteshirt.developermode.client.RunArgsExtension;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import net.minecraft.client.RunArgs;
import net.minecraft.client.main.Main;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(Main.class)
public abstract class MainMixin {
    private static OptionSpec<String> worldOption;
    private static String autoWorld;

    @ModifyVariable(
        at = @At(
            value = "INVOKE",
            target = "Ljoptsimple/OptionParser;accepts(Ljava/lang/String;)Ljoptsimple/OptionSpecBuilder;",
            ordinal = 0,
            remap = false
        ),
        method = "main([Ljava/lang/String;)V"
    )
    private static OptionParser addWorldOption(OptionParser original) {
        worldOption = original.accepts("world").withRequiredArg();
        return original;
    }

    @ModifyVariable(
        at = @At(
            value = "INVOKE",
            target = "Ljoptsimple/OptionSet;valuesOf(Ljoptsimple/OptionSpec;)Ljava/util/List;",
            ordinal = 0,
            remap = false
        ),
        method = "main([Ljava/lang/String;)V"
    )
    private static OptionSet getAutoWorldValue(OptionSet optionSet) {
        autoWorld = optionSet.valueOf(worldOption);
        return optionSet;
    }

    @ModifyArg(
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/MinecraftClient;<init>(Lnet/minecraft/client/RunArgs;)V"
        ),
        method = "main([Ljava/lang/String;)V",
        index = 0
    )
    private static RunArgs addAutoWorldRunArg(RunArgs runArgs) {
        RunArgsExtension extension = (RunArgsExtension) runArgs;
        extension.developermode_setAutoWorld(autoWorld);
        worldOption = null;
        autoWorld = null;
        return runArgs;
    }
}

package com.jamieswhiteshirt.developermode.mixin.realms;

import com.jamieswhiteshirt.developermode.client.DeveloperModeClient;
import net.minecraft.realms.RealmsBridge;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(RealmsBridge.class)
public class RealmsBridgeMixin {
    @Redirect(
        method = {
            "getNotificationScreen",
            "switchToRealms"
        },
        at = @At(
            value = "INVOKE",
            target = "Lorg/apache/logging/log4j/Logger;error(Ljava/lang/String;)V"
        ),
        remap = false
    )
    void redirectError1(Logger logger, String message) {
        logger.log(DeveloperModeClient.realmsErrorLogLevel, message);
    }

    @Redirect(
        method = {
            "getNotificationScreen",
            "switchToRealms"
        },
        at = @At(
            value = "INVOKE",
            target = "Lorg/apache/logging/log4j/Logger;error(Ljava/lang/String;Ljava/lang/Throwable;)V"
        ),
        remap = false
    )
    void redirectError2(Logger logger, String message, Throwable t) {
        logger.log(DeveloperModeClient.realmsErrorLogLevel, message, t);
    }
}

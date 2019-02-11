package com.jamieswhiteshirt.developermode.mixin.client.util;

import com.jamieswhiteshirt.developermode.client.DeveloperModeClient;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.WindowEventHandler;
import net.minecraft.client.WindowSettings;
import net.minecraft.client.util.MonitorTracker;
import net.minecraft.client.util.Window;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Window.class)
public abstract class WindowMixin {
    @Shadow @Final private long handle;

    @Inject(
        at = @At("RETURN"),
        method = "<init>(Lnet/minecraft/client/WindowEventHandler;Lnet/minecraft/client/util/MonitorTracker;Lnet/minecraft/client/WindowSettings;Ljava/lang/String;Ljava/lang/String;)V"
    )
    private void constructor(WindowEventHandler windowEventHandler_1, MonitorTracker monitorTracker_1, WindowSettings windowSettings_1, String string_1, String string_2, CallbackInfo ci) {
        int backgroundColor = DeveloperModeClient.theme.getBackgroundColor(0xFFFFFFFF);
        float a = ((backgroundColor >> 24) & 0xFF) / 255.0F;
        float r = ((backgroundColor >> 16) & 0xFF) / 255.0F;
        float g = ((backgroundColor >> 8) & 0xFF) / 255.0F;
        float b = (backgroundColor & 0xFF) / 255.0F;
        GlStateManager.clearColor(r, g, b, a);
        GlStateManager.clear(GL11.GL_COLOR_BUFFER_BIT, false);
        GLFW.glfwSwapBuffers(handle);
    }
}

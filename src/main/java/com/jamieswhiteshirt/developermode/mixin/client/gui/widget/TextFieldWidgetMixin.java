package com.jamieswhiteshirt.developermode.mixin.client.gui.widget;

import com.jamieswhiteshirt.developermode.client.gui.widget.TextFieldWidgetExtension;
import net.minecraft.client.gui.widget.TextFieldWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(TextFieldWidget.class)
public class TextFieldWidgetMixin implements TextFieldWidgetExtension {
    @Shadow private int y;

    @Override
    public void developermode_setY(int y) {
        this.y = y;
    }
}

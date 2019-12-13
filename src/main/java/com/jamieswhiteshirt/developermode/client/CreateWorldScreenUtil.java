package com.jamieswhiteshirt.developermode.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.MappingResolver;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;

import java.lang.reflect.Field;

@Environment(EnvType.CLIENT)
public class CreateWorldScreenUtil {
    private static final String CLASS_INTERMEDIARY_NAME = "net.minecraft.class_525";
    private static final String MODE_CLASS_INTERMEDIARY_NAME = "net.minecraft.class_525$class_4539";
    private static final String CURRENT_MODE_FIELD_INTERMEDIARY_DESC = "Lnet/minecraft/class_525$class_4539;";
    private static final String CURRENT_MODE_FIELD_INTERMEDIARY_NAME = "field_3201";
    private static final String MODE_TRANSLATION_SUFFIX_FIELD_INTERMEDIARY_NAME = "field_20628";
    private static final String MODE_TRANSLATION_SUFFIX_FIELD_INTERMEDIARY_DESC = "Ljava/lang/String;";

    public static final Field CURRENT_MODE_FIELD;
    public static final Class<?> MODE_CLASS;
    public static final Field MODE_TRANSLATION_SUFFIX_FIELD;

    static {
        try {
            MappingResolver mappingResolver = FabricLoader.getInstance().getMappingResolver();
            String modeClassCurrentName = mappingResolver.mapClassName("intermediary", MODE_CLASS_INTERMEDIARY_NAME);
            String currentModeFieldCurrentName = mappingResolver.mapFieldName("intermediary", CLASS_INTERMEDIARY_NAME, CURRENT_MODE_FIELD_INTERMEDIARY_NAME, CURRENT_MODE_FIELD_INTERMEDIARY_DESC);
            String goalTranslationSuffixFieldCurrentName = mappingResolver.mapFieldName("intermediary", MODE_CLASS_INTERMEDIARY_NAME, MODE_TRANSLATION_SUFFIX_FIELD_INTERMEDIARY_NAME, MODE_TRANSLATION_SUFFIX_FIELD_INTERMEDIARY_DESC);
            CURRENT_MODE_FIELD = CreateWorldScreen.class.getDeclaredField(currentModeFieldCurrentName);
            CURRENT_MODE_FIELD.setAccessible(true);
            MODE_CLASS = Class.forName(modeClassCurrentName);
            MODE_TRANSLATION_SUFFIX_FIELD = MODE_CLASS.getDeclaredField(goalTranslationSuffixFieldCurrentName);
            MODE_TRANSLATION_SUFFIX_FIELD.setAccessible(true);
        } catch (ClassNotFoundException | NoSuchFieldException e) {
            throw new Error("Unable to reflect on CreateWorldScreen", e);
        }
    }

    public static void setCurrentMode(CreateWorldScreen screen, Object mode) {
        try {
            CURRENT_MODE_FIELD.set(screen, mode);
        } catch (IllegalAccessException e) {
            throw new Error("Unable to reflect on CreateWorldScreen", e);
        }
    }

    public static Object getCurrentMode(CreateWorldScreen screen) {
        try {
            return CURRENT_MODE_FIELD.get(screen);
        } catch (IllegalAccessException e) {
            throw new Error("Unable to reflect on CreateWorldScreen", e);
        }
    }

    public static String getModeTranslationSuffix(Object mode) {
        try {
            return (String) MODE_TRANSLATION_SUFFIX_FIELD.get(mode);
        } catch (IllegalAccessException e) {
            throw new Error("Unable to reflect on CreateWorldScreen", e);
        }
    }
}

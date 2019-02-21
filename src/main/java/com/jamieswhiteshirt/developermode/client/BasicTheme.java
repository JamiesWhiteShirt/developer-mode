package com.jamieswhiteshirt.developermode.client;

public class BasicTheme implements Theme {
    private final Integer backgroundColor;
    private final Integer progressBarOutlineColor;
    private final Integer progressBarBackgroundColor;
    private final Integer progressBarFillColor;

    private static int applyColorOverride(Integer value, int original) {
        if (value == null) return original;
        return (original & 0xFF000000) | (value & 0xFFFFFF);
    }

    public BasicTheme(Integer backgroundColor, Integer progressBarOutlineColor, Integer progressBarBackgroundColor, Integer progressBarFillColor) {
        this.backgroundColor = backgroundColor;
        this.progressBarOutlineColor = progressBarOutlineColor;
        this.progressBarBackgroundColor = progressBarBackgroundColor;
        this.progressBarFillColor = progressBarFillColor;
    }

    @Override
    public int getBackgroundColor(int original) {
        return applyColorOverride(backgroundColor, original);
    }

    @Override
    public int getProgressBarOutlineColor(int original) {
        return applyColorOverride(progressBarOutlineColor, original);
    }

    @Override
    public int getProgressBarBackgroundColor(int original) {
        return applyColorOverride(progressBarBackgroundColor, original);
    }

    @Override
    public int getProgressBarFillColor(int original) {
        return applyColorOverride(progressBarFillColor, original);
    }
}

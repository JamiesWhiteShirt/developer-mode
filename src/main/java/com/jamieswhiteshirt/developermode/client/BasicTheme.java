package com.jamieswhiteshirt.developermode.client;

public class BasicTheme implements Theme {
    private final Integer backgroundColor;
    private final Integer progressBarOutlineColor;
    private final Integer progressBarBackgroundColor;
    private final Integer progressBarFillColor;

    public BasicTheme(Integer backgroundColor, Integer progressBarOutlineColor, Integer progressBarBackgroundColor, Integer progressBarFillColor) {
        this.backgroundColor = backgroundColor;
        this.progressBarOutlineColor = progressBarOutlineColor;
        this.progressBarBackgroundColor = progressBarBackgroundColor;
        this.progressBarFillColor = progressBarFillColor;
    }

    @Override
    public int getBackgroundColor(int original) {
        return backgroundColor != null ? backgroundColor : original;
    }

    @Override
    public int getProgressBarOutlineColor(int original) {
        return progressBarOutlineColor != null ? progressBarOutlineColor : original;
    }

    @Override
    public int getProgressBarBackgroundColor(int original) {
        return progressBarBackgroundColor != null ? progressBarBackgroundColor : original;
    }

    @Override
    public int getProgressBarFillColor(int original) {
        return progressBarFillColor != null ? progressBarFillColor : original;
    }
}

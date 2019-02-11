package com.jamieswhiteshirt.developermode.client;

import com.jamieswhiteshirt.developermode.DeveloperMode;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.MinecraftClient;

import java.io.*;
import java.util.Properties;

public class DeveloperModeClient implements ClientModInitializer {
    public static Theme theme;
    public static boolean shareOptionsEnabled;
    public static boolean rememberNewWorldSettingsEnabled;
    public static int splashFadeTime;

    private static Integer getColorProperty(Properties properties, String key) {
        String value = properties.getProperty(key);
        if (value != null) {
            try {
                return 0xFF000000 | Integer.parseInt(value, 16);
            } catch (NumberFormatException ignored) {
                DeveloperMode.LOGGER.error("Invalid property value for " + key + " in client config. Expected hexadecimal RRGGBB.");
            }
        }
        return null;
    }

    private static int getIntProperty(Properties properties, String key, int def) {
        String value = properties.getProperty(key);
        if (value != null) {
            try {
                return Integer.parseInt(value, 10);
            } catch (NumberFormatException ignored) {
                DeveloperMode.LOGGER.error("Invalid property value for " + key + " in client config. Expected integer.");
            }
        }
        return def;
    }

    private static boolean getBooleanProperty(Properties properties, String key, boolean def) {
        String value = properties.getProperty(key);
        if (value != null) {
            return Boolean.parseBoolean(value);
        }
        return def;
    }

    @Override
    public void onInitializeClient() {
        File runDirectory = MinecraftClient.getInstance().runDirectory;
        File configDirectory = new File(runDirectory, "config/developer-mode");
        File clientConfig = new File(configDirectory, "client.config.properties");
        Properties properties = new Properties();
        if (configDirectory.exists() || configDirectory.mkdirs()) {
            if (!clientConfig.exists()) {
                try (BufferedReader br = new BufferedReader(new InputStreamReader(DeveloperMode.class.getResourceAsStream("/client.config.properties")))) {
                    try (PrintWriter pw = new PrintWriter(new FileOutputStream(clientConfig))) {
                        String line;
                        while ((line = br.readLine()) != null) {
                            pw.println(line);
                        }
                    } catch (IOException e) {
                        DeveloperMode.LOGGER.error("Could not write default client config", e);
                    }
                } catch (IOException e) {
                    DeveloperMode.LOGGER.error("Could not read default client config", e);
                }
            }

            try (InputStream in = new FileInputStream(clientConfig)) {
                properties.load(in);
            } catch (IOException e) {
                DeveloperMode.LOGGER.error("Could not read client config", e);
            }
        } else {
            DeveloperMode.LOGGER.error("Could not create default client config, going with defaults");
        }

        Integer backgroundColor = getColorProperty(properties, "theme.background.color");
        Integer progressBarOutlineColor = getColorProperty(properties, "theme.progressBar.outline.color");
        Integer progressBarBackgroundColor = getColorProperty(properties, "theme.progressBar.background.color");
        Integer progressBarFillColor = getColorProperty(properties, "theme.progressBar.fill.color");
        theme = new BasicTheme(backgroundColor, progressBarOutlineColor, progressBarBackgroundColor, progressBarFillColor);
        shareOptionsEnabled = getBooleanProperty(properties, "shareOptions.enabled", false);
        rememberNewWorldSettingsEnabled = getBooleanProperty(properties, "rememberNewWorldSettings.enabled", false);
        splashFadeTime = getIntProperty(properties, "splash.fade.time", 1000);
    }
}

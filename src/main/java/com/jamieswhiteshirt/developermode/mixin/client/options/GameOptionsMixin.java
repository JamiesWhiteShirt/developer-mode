package com.jamieswhiteshirt.developermode.mixin.client.options;

import com.jamieswhiteshirt.developermode.DeveloperMode;
import com.jamieswhiteshirt.developermode.client.DeveloperModeClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.GameOptions;
import org.apache.commons.io.IOUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.*;
import java.util.*;
import java.util.prefs.Preferences;
import java.util.stream.Collectors;

@Mixin(GameOptions.class)
public abstract class GameOptionsMixin {
    private static void readOptions(InputStream in, Map<String, String> keyValues) throws IOException {
        List<String> list_1 = IOUtils.readLines(in);
        for (String line : list_1) {
            try {
                // Use more lenient parsing here. GameOptions can deal with the empty ones.
                Iterator<String> iterator = GameOptions.COLON_SPLITTER.limit(2).split(line).iterator();
                keyValues.put(iterator.next(), iterator.next());
            } catch (Exception var10) {
                DeveloperMode.LOGGER.warn("Skipping bad option: {}", line);
            }
        }
    }

    private static void writeOptions(OutputStream out, Map<String, String> keyValues) throws IOException {
        IOUtils.writeLines(keyValues.entrySet().stream().map(entry -> entry.getKey() + ":" + entry.getValue()).collect(Collectors.toList()), System.lineSeparator(), out);
    }

    @Shadow private File optionsFile;

    private File developermode_optionsShareFile = new File(MinecraftClient.getInstance().runDirectory, "options.share.txt");
    private Preferences developermode_globalPreferences = Preferences.userNodeForPackage(DeveloperMode.class).node("globalOptions");

    @Inject(
        at = @At("HEAD"),
        method = "load()V"
    )
    private void load(CallbackInfo ci) {
        if (!DeveloperModeClient.shareOptionsEnabled) return;

        // Read local options file into joined options if it exists
        Map<String, String> joinedOptions = new LinkedHashMap<>();
        if (optionsFile.exists()) {
            try (InputStream in = new FileInputStream(optionsFile)) {
                readOptions(in, joinedOptions);
            } catch (IOException e) {
                DeveloperMode.LOGGER.error("Error reading options file", e);
                return;
            }
        }

        // Read options share file or use the default one
        Map<String, String> optionsShare = new LinkedHashMap<>();
        if (!developermode_optionsShareFile.exists()) {
            try (InputStream in = DeveloperMode.class.getResourceAsStream("/options.share.txt")) {
                readOptions(in, optionsShare);
            } catch (IOException e) {
                DeveloperMode.LOGGER.error("Error reading default options share file", e);
                return;
            }
        } else {
            try (InputStream in = new FileInputStream(developermode_optionsShareFile)) {
                readOptions(in, optionsShare);
            } catch (IOException e) {
                DeveloperMode.LOGGER.error("Error reading default options share file", e);
                return;
            }
        }

        // Set unknown options as local by default
        for (String option : joinedOptions.keySet()) {
            optionsShare.computeIfAbsent(option, opt -> {
                DeveloperMode.LOGGER.warn("Found unknown option {}. This option will be local by default.", opt);
                return "local";
            });
        }

        // Write the options share file in case unknown options were discovered or it didn't already exist
        try (OutputStream out = new FileOutputStream(developermode_optionsShareFile)) {
            writeOptions(out, optionsShare);
        } catch (IOException e) {
            DeveloperMode.LOGGER.error("Error writing options share file", e);
            return;
        }

        // Read global options to joined options
        for (Map.Entry<String, String> optionShare : optionsShare.entrySet()) {
            String key = optionShare.getKey();
            if (optionShare.getValue().equalsIgnoreCase("global")) {
                String globalValue = developermode_globalPreferences.get(key, null);
                if (globalValue != null) {
                    joinedOptions.put(key, globalValue);
                } else {
                    String localValue = joinedOptions.get(key);
                    if (localValue != null) {
                        developermode_globalPreferences.put(key, localValue);
                    }
                }
            }
        }

        // Write the local options file so GameOptions can parse it
        try (OutputStream out = new FileOutputStream(optionsFile)) {
            writeOptions(out, joinedOptions);
        } catch (IOException e) {
            DeveloperMode.LOGGER.error("Error writing options file", e);
        }
    }

    @Inject(
        at = @At("TAIL"),
        method = "write()V"
    )
    private void write(CallbackInfo ci) {
        if (!DeveloperModeClient.shareOptionsEnabled) return;

        // Read the local options file GameOptions made
        Map<String, String> localOptions = new LinkedHashMap<>();
        if (optionsFile.exists()) {
            try (InputStream in = new FileInputStream(optionsFile)) {
                readOptions(in, localOptions);
            } catch (IOException e) {
                DeveloperMode.LOGGER.error("Error reading options file", e);
                return;
            }
        }

        // Read the options share file
        Map<String, String> optionsShare = new LinkedHashMap<>();
        if (developermode_optionsShareFile.exists()) {
            try (InputStream in = new FileInputStream(developermode_optionsShareFile)) {
                readOptions(in, optionsShare);
            } catch (IOException e) {
                DeveloperMode.LOGGER.error("Error reading options share file", e);
                return;
            }
        }

        // Overwrite global options
        for (Map.Entry<String, String> optionShare : optionsShare.entrySet()) {
            String key = optionShare.getKey();
            if (optionShare.getValue().equalsIgnoreCase("true")) {
                developermode_globalPreferences.put(key, localOptions.get(key));
            }
        }
    }
}

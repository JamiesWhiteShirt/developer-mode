# Developer Mode
[![Maven Repository](https://img.shields.io/maven-metadata/v/https/maven.jamieswhiteshirt.com/libs-release/com/jamieswhiteshirt/developer-mode/maven-metadata.xml.svg)](https://maven.jamieswhiteshirt.com/libs-release/com/jamieswhiteshirt/developer-mode/)

A few things to make development easier with [Fabric](https://fabricmc.net/).

## Features

- Configurable loading screen themes: Dark theme by default.
- Configurable loading screen fade time: Instant by default.
- Remember New World settings: Enabled by default.
- Global client options: Enabled for most options by default.
- `--world $WORLDNAME` client program argument to instantly create and/or open a world on launch.

## Usage

To use this mod in your workspace, add the following to your `build.gradle`:

```groovy
repositories {
    maven {url "https://maven.jamieswhiteshirt.com/libs-release/"}
}

dependencies {
    modCompile "com.jamieswhiteshirt:developer-mode:<DEVELOPERMODE_VERSION>"
}
```

## Configuration

### `./config/developer-mode/client.settings.properties`

```properties
# Theme for the loading screen
# Default theme based on Darcula
theme.background.color=2B2B2B
theme.progressBar.outline.color=3C3F41
theme.progressBar.background.color=3C3F41
theme.progressBar.fill.color=7D7D7D

# Share options between environments
shareOptions.enabled=true

# Remember New World settings
rememberNewWorldSettings.enabled=true

# Settings for the fade from splash screen to main menu
splash.fade.time=0
```

### `./newWorldSettings.json`

Automatically synchronized with settings in New World. NOTE: `"gameRules"` can currently only be modified in the file itself.

```json
{
  "randomSeed": "",
  "generatorOptions": {},
  "gameRules": {
    "doTileDrops": "true",
    "doFireTick": "true",
    "maxCommandChainLength": "65536",
    "reducedDebugInfo": "false",
    "naturalRegeneration": "true",
    "disableElytraMovementCheck": "false",
    "doMobLoot": "true",
    "announceAdvancements": "true",
    "keepInventory": "false",
    "doEntityDrops": "true",
    "doLimitedCrafting": "false",
    "mobGriefing": "true",
    "randomTickSpeed": "3",
    "commandBlockOutput": "true",
    "spawnRadius": "10",
    "doMobSpawning": "true",
    "maxEntityCramming": "24",
    "logAdminCommands": "true",
    "spectatorsGenerateChunks": "true",
    "doWeatherCycle": "false",
    "sendCommandFeedback": "true",
    "doDaylightCycle": "false",
    "showDeathMessages": "true"
  },
  "generatorName": "default",
  "gameType": "survival",
  "mapFeatures": 1,
  "allowCommands": 0,
  "bonusItems": 0
}
```

### `./options.share.txt`

Meta-options file. For each option determines whether the option is `global` or `local`.
If global, the option will be saved and loaded from the current user's preferences.
If local, the option will not be saved or loaded from the current user's preferences.

### `--world $WORLDNAME`

Add to the client program arguments to immediately load into a world when loading is complete.
The world name must match a directory name in the saves directory. Example: `--world "New World"`.
If the world does not exist, a New World screen is opened.

## Developing Developer Mode

To get started, refer to the [Fabric documentation](https://fabricmc.net/wiki/setup).

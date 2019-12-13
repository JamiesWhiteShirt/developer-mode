package com.jamieswhiteshirt.developermode.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import com.mojang.datafixers.types.JsonOps;
import net.minecraft.datafixers.NbtOps;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.GameRules;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class NewLevelProperties {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().setLenient().create();

    public String randomSeed;
    public String generatorName;
    public CompoundTag generatorOptions;
    public boolean mapFeatures;
    public boolean allowCommands;
    public boolean bonusItems;
    public String gameType;
    public GameRules gameRules = new GameRules();

    public <T> T serialize(DynamicOps<T> ops) {
        Map<T, T> map = new HashMap<>();
        map.put(ops.createString("randomSeed"), ops.createString(randomSeed));
        map.put(ops.createString("generatorName"), ops.createString(generatorName));
        map.put(ops.createString("generatorOptions"), Dynamic.convert(NbtOps.INSTANCE, ops, generatorOptions));
        map.put(ops.createString("mapFeatures"), ops.createByte(mapFeatures ? (byte) 1 : (byte) 0));
        map.put(ops.createString("allowCommands"), ops.createByte(allowCommands ? (byte) 1 : (byte) 0));
        map.put(ops.createString("bonusItems"), ops.createByte(bonusItems ? (byte) 1 : (byte) 0));
        map.put(ops.createString("gameType"), ops.createString(gameType));
        map.put(ops.createString("gameRules"), Dynamic.convert(NbtOps.INSTANCE, ops, gameRules.toNbt()));
        return ops.createMap(map);
    }

    public <T> void deserialize(Dynamic<T> dynamic) {
        DynamicOps<T> ops = dynamic.getOps();
        randomSeed = dynamic.getElement("randomSeed").flatMap(ops::getStringValue).orElse("");
        generatorName = dynamic.getElement("generatorName").flatMap(ops::getStringValue).orElse("");
        generatorOptions = dynamic.getElement("generatorOptions").map(it -> Dynamic.convert(ops, NbtOps.INSTANCE, it)).flatMap(it -> {
            if (it instanceof CompoundTag) {
                return Optional.of((CompoundTag) it);
            } else {
                return Optional.empty();
            }
        }).orElse(new CompoundTag());
        mapFeatures = dynamic.getElement("mapFeatures").flatMap(ops::getNumberValue).map(it -> it.byteValue() != 0).orElse(false);
        allowCommands = dynamic.getElement("allowCommands").flatMap(ops::getNumberValue).map(it -> it.byteValue() != 0).orElse(false);
        bonusItems = dynamic.getElement("bonusItems").flatMap(ops::getNumberValue).map(it -> it.byteValue() != 0).orElse(false);
        gameType = dynamic.getElement("gameType").flatMap(ops::getStringValue).orElse("");
        gameRules.load(dynamic.getElement("gameRules").map(it -> Dynamic.convert(ops, NbtOps.INSTANCE, it)).flatMap(it -> {
            if (it instanceof CompoundTag) {
                return Optional.of((CompoundTag) it);
            } else {
                return Optional.empty();
            }
        }).orElse(new CompoundTag()));
    }

    public void writeToFile(File file) throws IOException {
        JsonElement element = serialize(JsonOps.INSTANCE);
        try (OutputStream out = new FileOutputStream(file); Writer writer = new OutputStreamWriter(out)) {
            GSON.toJson(element, writer);
            out.flush();
        }
    }

    public void readFromFile(File file) throws IOException {
        try (InputStream in = new FileInputStream(file); Reader reader = new InputStreamReader(in)) {
            JsonElement element = new JsonParser().parse(GSON.newJsonReader(reader));
            deserialize(new Dynamic<>(JsonOps.INSTANCE, element));
        }
    }
}

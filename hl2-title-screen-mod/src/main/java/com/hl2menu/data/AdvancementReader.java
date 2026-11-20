package com.hl2menu.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/** Reads the advancement progress file of the most recently played world, for the Achievements dialog. */
public final class AdvancementReader {
    public record Entry(String category, String name, String id) {}

    public record Result(String worldName, List<Entry> done, int inProgress) {}

    private AdvancementReader() {}

    /** Returns null when no world with advancement data exists. */
    public static Result readLatest() {
        for (SaveScanner.SaveEntry save : SaveScanner.scan()) {
            Path dir = SaveScanner.savesDir().resolve(save.id()).resolve("advancements");
            if (!Files.isDirectory(dir)) {
                continue;
            }
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, "*.json")) {
                Path best = null;
                long bestTime = -1;
                for (Path file : stream) {
                    long time = Files.getLastModifiedTime(file).toMillis();
                    if (time > bestTime) {
                        bestTime = time;
                        best = file;
                    }
                }
                if (best != null) {
                    return parse(save.name(), best);
                }
            } catch (IOException | RuntimeException ignored) {
                // try the next most recent world
            }
        }
        return null;
    }

    private static Result parse(String worldName, Path file) throws IOException {
        JsonObject root;
        try (Reader reader = Files.newBufferedReader(file)) {
            root = JsonParser.parseReader(reader).getAsJsonObject();
        }
        List<Entry> done = new ArrayList<>();
        int inProgress = 0;
        for (Map.Entry<String, JsonElement> e : root.entrySet()) {
            String id = e.getKey();
            if (id.equals("DataVersion") || id.contains(":recipes/") || id.endsWith("/root")
                    || !e.getValue().isJsonObject()) {
                continue;
            }
            JsonObject progress = e.getValue().getAsJsonObject();
            boolean isDone = progress.has("done") && progress.get("done").getAsBoolean();
            if (isDone) {
                done.add(entry(id));
            } else {
                inProgress++;
            }
        }
        done.sort(Comparator.comparing(Entry::category).thenComparing(Entry::name));
        return new Result(worldName, done, inProgress);
    }

    private static Entry entry(String id) {
        String path = id.substring(id.indexOf(':') + 1);
        String[] parts = path.split("/");
        String category = parts.length > 1 ? pretty(parts[0]) : "Misc";
        return new Entry(category, pretty(parts[parts.length - 1]), id);
    }

    private static String pretty(String snake) {
        StringBuilder out = new StringBuilder();
        for (String word : snake.split("_")) {
            if (word.isEmpty()) {
                continue;
            }
            if (out.length() > 0) {
                out.append(' ');
            }
            out.append(word.substring(0, 1).toUpperCase(Locale.ROOT)).append(word.substring(1));
        }
        return out.toString();
    }
}

package com.hl2menu.data;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.fabricmc.loader.api.FabricLoader;

/** Finds the worlds in the saves folder by reading the disk directly, newest first. */
public final class SaveScanner {
    public record SaveEntry(String id, String name, long lastPlayed) {}

    private SaveScanner() {}

    public static Path savesDir() {
        return FabricLoader.getInstance().getGameDir().resolve("saves");
    }

    public static List<SaveEntry> scan() {
        List<SaveEntry> out = new ArrayList<>();
        Path dir = savesDir();
        if (!Files.isDirectory(dir)) {
            return out;
        }
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
            for (Path folder : stream) {
                Path levelDat = folder.resolve("level.dat");
                if (!Files.isRegularFile(levelDat)) {
                    continue;
                }
                String id = folder.getFileName().toString();
                String name = id;
                long last = modified(levelDat);
                try {
                    MiniNbt.LevelInfo info = MiniNbt.readLevelInfo(levelDat);
                    if (info.name() != null && !info.name().isBlank()) {
                        name = info.name();
                    }
                    if (info.lastPlayed() > 0) {
                        last = info.lastPlayed();
                    }
                } catch (IOException | RuntimeException ignored) {
                    // unreadable level.dat: fall back to the folder name and file time
                }
                out.add(new SaveEntry(id, name, last));
            }
        } catch (IOException ignored) {
            // no readable saves folder: show an empty list
        }
        out.sort(Comparator.comparingLong(SaveEntry::lastPlayed).reversed());
        return out;
    }

    private static long modified(Path file) {
        try {
            return Files.getLastModifiedTime(file).toMillis();
        } catch (IOException e) {
            return 0L;
        }
    }
}

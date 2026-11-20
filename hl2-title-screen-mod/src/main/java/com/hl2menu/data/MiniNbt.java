package com.hl2menu.data;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

/**
 * Just enough of an NBT reader to pull the world name and last-played time out of level.dat, with
 * no dependency on Minecraft's own (frequently renamed) NBT classes.
 */
public final class MiniNbt {
    public record LevelInfo(String name, long lastPlayed) {}

    private MiniNbt() {}

    @SuppressWarnings("unchecked")
    public static LevelInfo readLevelInfo(Path levelDat) throws IOException {
        try (DataInputStream in = new DataInputStream(
                new BufferedInputStream(new GZIPInputStream(Files.newInputStream(levelDat))))) {
            int rootType = in.readUnsignedByte();
            if (rootType != 10) {
                throw new IOException("level.dat root is not a compound");
            }
            in.readUTF();
            Map<String, Object> root = (Map<String, Object>) readPayload(in, 10);
            Object data = root.get("Data");
            if (!(data instanceof Map)) {
                throw new IOException("level.dat has no Data compound");
            }
            Map<String, Object> map = (Map<String, Object>) data;
            Object name = map.get("LevelName");
            Object last = map.get("LastPlayed");
            return new LevelInfo(name instanceof String s ? s : null, last instanceof Long l ? l : 0L);
        }
    }

    private static Object readPayload(DataInputStream in, int type) throws IOException {
        switch (type) {
            case 1:
                return in.readByte();
            case 2:
                return in.readShort();
            case 3:
                return in.readInt();
            case 4:
                return in.readLong();
            case 5:
                return in.readFloat();
            case 6:
                return in.readDouble();
            case 7: {
                in.skipNBytes(in.readInt());
                return null;
            }
            case 8:
                return in.readUTF();
            case 9: {
                int elementType = in.readUnsignedByte();
                int length = in.readInt();
                List<Object> list = new ArrayList<>();
                for (int i = 0; i < length; i++) {
                    list.add(readPayload(in, elementType));
                }
                return list;
            }
            case 10: {
                Map<String, Object> map = new HashMap<>();
                while (true) {
                    int childType = in.readUnsignedByte();
                    if (childType == 0) {
                        return map;
                    }
                    String name = in.readUTF();
                    map.put(name, readPayload(in, childType));
                }
            }
            case 11: {
                in.skipNBytes(4L * in.readInt());
                return null;
            }
            case 12: {
                in.skipNBytes(8L * in.readInt());
                return null;
            }
            default:
                throw new IOException("Unknown NBT tag type " + type);
        }
    }
}

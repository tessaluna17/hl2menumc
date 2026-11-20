package com.hl2menu.dialog;

import com.hl2menu.Hl2Fonts;
import com.hl2menu.VanillaBridge;
import com.hl2menu.data.SaveScanner;
import com.hl2menu.screen.Hl2TitleScreen;
import com.hl2menu.ui.HlBox;
import com.hl2menu.ui.HlButton;
import com.hl2menu.ui.HlLabel;
import com.hl2menu.ui.HlList;
import com.hl2menu.ui.Theme;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/** Lists the worlds in the saves folder, newest first. Double-click or Load opens the selected one. */
public final class LoadGameDialog extends Dialog {
    private static final DateTimeFormatter DATE =
            DateTimeFormatter.ofPattern("MMM d, yyyy  HH:mm").withZone(ZoneId.systemDefault());

    private final List<SaveScanner.SaveEntry> saves = SaveScanner.scan();
    private int selected;

    public LoadGameDialog(Hl2TitleScreen host) {
        super(host, "Load Game", 400, 240);
        this.selected = saves.isEmpty() ? -1 : 0;
    }

    @Override
    protected void build() {
        int lx = x + 10;
        int ly = y + 28;
        int lw = dw - 20;
        int lh = dh - 28 - 40;

        if (saves.isEmpty()) {
            add(new HlBox(lx, ly, lw, lh));
            add(new HlLabel("No saved games found.", lx + 12, ly + 24, Theme.TEXT_DIM, Hl2Fonts.MENU));
        } else {
            List<HlList.Row> rows = new ArrayList<>();
            for (SaveScanner.SaveEntry save : saves) {
                rows.add(new HlList.Row(save.name(),
                        DATE.format(Instant.ofEpochMilli(save.lastPlayed())) + "    " + save.id()));
            }
            add(new HlList(lx, ly, lw, lh, 26, rows, selected,
                    i -> selected = i,
                    i -> {
                        selected = i;
                        load();
                    }));
        }

        int by = y + dh - 28;
        add(new HlButton("Load", x + dw - 152, by, 66, 18, this::load).activeWhen(() -> selected >= 0));
        add(new HlButton("Cancel", x + dw - 76, by, 66, 18, host::closeDialog));
    }

    private void load() {
        if (selected < 0 || selected >= saves.size()) {
            return;
        }
        String folder = saves.get(selected).id();
        host.closeDialog();
        VanillaBridge.openWorld(host, folder);
    }
}

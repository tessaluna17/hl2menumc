package com.hl2menu.dialog;

import com.hl2menu.Hl2Fonts;
import com.hl2menu.data.AdvancementReader;
import com.hl2menu.screen.Hl2TitleScreen;
import com.hl2menu.ui.HlBox;
import com.hl2menu.ui.HlButton;
import com.hl2menu.ui.HlLabel;
import com.hl2menu.ui.HlList;
import com.hl2menu.ui.Theme;
import java.util.ArrayList;
import java.util.List;

/** Shows the completed advancements of the most recently played world, HL2 "achievements" style. */
public final class AchievementsDialog extends Dialog {
    private final AdvancementReader.Result result = AdvancementReader.readLatest();

    public AchievementsDialog(Hl2TitleScreen host) {
        super(host, "Achievements", 420, 240);
    }

    @Override
    protected void build() {
        int lx = x + 10;
        if (result == null) {
            add(new HlBox(lx, y + 28, dw - 20, dh - 28 - 40));
            add(new HlLabel("No advancement data found yet.", lx + 12, y + 56, Theme.TEXT_DIM, Hl2Fonts.MENU));
            add(new HlLabel("Play a world, then come back.", lx + 12, y + 74, Theme.TEXT_DIM, Hl2Fonts.MENU));
        } else {
            add(new HlLabel("World: " + result.worldName(), lx + 2, y + 40, Theme.TEXT_DIM, Hl2Fonts.MENU));
            add(new HlLabel("Total Achievements Earned: " + result.done().size(), lx + 2, y + 56, Theme.TEXT, Hl2Fonts.MENU));

            List<HlList.Row> rows = new ArrayList<>();
            for (AdvancementReader.Entry entry : result.done()) {
                rows.add(new HlList.Row(entry.name(), entry.category() + "    " + entry.id()));
            }
            add(new HlList(lx, y + 64, dw - 20, dh - 64 - 40, 26, rows, -1, i -> { }, i -> { }));
        }
        add(new HlButton("Back", x + dw - 76, y + dh - 28, 66, 18, host::closeDialog));
    }
}

package com.hl2menu.dialog;

import com.hl2menu.Hl2Fonts;
import com.hl2menu.VanillaBridge;
import com.hl2menu.screen.Hl2TitleScreen;
import com.hl2menu.ui.HlButton;
import com.hl2menu.ui.HlLabel;
import com.hl2menu.ui.Theme;

public final class QuitDialog extends Dialog {
    public QuitDialog(Hl2TitleScreen host) {
        super(host, "Quit", 300, 100);
    }

    @Override
    protected void build() {
        add(new HlLabel("Are you sure you want to quit?", x + 14, y + 50, Theme.TEXT, Hl2Fonts.MENU));

        int by = y + dh - 28;
        add(new HlButton("Yes", x + dw - 146, by, 66, 18, VanillaBridge::quit));
        add(new HlButton("No", x + dw - 76, by, 66, 18, host::closeDialog));
    }
}

package com.hl2menu.dialog;

import com.hl2menu.Hl2Fonts;
import com.hl2menu.VanillaBridge;
import com.hl2menu.screen.Hl2TitleScreen;
import com.hl2menu.ui.Element;
import com.hl2menu.ui.HlBox;
import com.hl2menu.ui.HlButton;
import com.hl2menu.ui.HlCheck;
import com.hl2menu.ui.HlCycle;
import com.hl2menu.ui.HlLabel;
import com.hl2menu.ui.HlSlider;
import com.hl2menu.ui.HlTab;
import com.hl2menu.ui.Theme;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.DoubleFunction;
import java.util.function.IntFunction;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.sounds.SoundSource;

/**
 * The tabbed HL2 options dialog. Controls edit the real Minecraft options live; OK and Apply save
 * them, Cancel (or Escape / the close box) puts back whatever they were when the dialog opened.
 */
public final class OptionsDialog extends Dialog {
    private enum Tab {
        GAMEPLAY("Gameplay"),
        KEYBOARD("Keyboard"),
        MOUSE("Mouse"),
        AUDIO("Audio"),
        VIDEO("Video");

        final String label;

        Tab(String label) {
            this.label = label;
        }
    }

    private static final int ROW = 22;

    private final Options options = Minecraft.getInstance().options;
    private final Map<OptionInstance<?>, Object> original = new LinkedHashMap<>();
    private Tab tab = Tab.GAMEPLAY;

    public OptionsDialog(Hl2TitleScreen host) {
        super(host, "Options", 440, 250);
    }

    @Override
    protected void build() {
        int tabY = y + 26;
        int tx = x + 10;
        for (Tab t : Tab.values()) {
            add(new HlTab(t.label, tx, tabY, 64, 16, () -> tab == t, () -> {
                tab = t;
                host.relayout();
            }));
            tx += 62;
        }

        int cx = x + 10;
        int cy = tabY + 16;
        int cw = dw - 20;
        int ch = dh - (cy - y) - 36;
        add(new HlBox(cx, cy, cw, ch));

        switch (tab) {
            case GAMEPLAY -> gameplay(cx, cy, cw);
            case KEYBOARD -> keyboard(cx, cy);
            case MOUSE -> mouse(cx, cy, cw);
            case AUDIO -> audio(cx, cy, cw);
            case VIDEO -> video(cx, cy, cw);
        }

        int by = y + dh - 28;
        add(new HlButton("OK", x + dw - 224, by, 66, 18, this::ok));
        add(new HlButton("Cancel", x + dw - 150, by, 66, 18, this::cancel));
        add(new HlButton("Apply", x + dw - 76, by, 66, 18, options::save));
    }

    private void gameplay(int cx, int cy, int cw) {
        int rx = cx + 12;
        int rw = cw - 24;
        add(intSlider("Field of view", options.fov(), 30, 110, 1, v -> v + "°", rx, cy + 10, rw, 110, 50));
        add(check("View bobbing", options.bobView(), rx, cy + 10 + ROW, rw));
        add(check("Show subtitles", options.showSubtitles(), rx, cy + 10 + 2 * ROW, rw));
    }

    private void keyboard(int cx, int cy) {
        int rx = cx + 12;
        add(new HlLabel("Key bindings are edited on Minecraft's Controls screen.", rx, cy + 24, Theme.TEXT, Hl2Fonts.MENU));
        add(new HlLabel("Choose Controls there to rebind keys.", rx, cy + 42, Theme.TEXT_DIM, Hl2Fonts.MENU));
        add(new HlButton("Controls...", rx, cy + 56, 110, 18, () -> {
            host.closeDialog();
            VanillaBridge.openVanillaOptions(host);
        }));
    }

    private void mouse(int cx, int cy, int cw) {
        add(doubleSlider("Sensitivity", options.mouseSensitivity(), v -> Math.round(v * 200) + "%",
                cx + 12, cy + 10, cw - 24, 110, 50));
    }

    private void audio(int cx, int cy, int cw) {
        SoundSource[] sources = SoundSource.values();
        int colW = (cw - 36) / 2;
        for (int i = 0; i < sources.length; i++) {
            SoundSource source = sources[i];
            int col = i % 2;
            int row = i / 2;
            add(doubleSlider(pretty(source.getName()), options.getSoundSourceOptionInstance(source),
                    v -> Math.round(v * 100) + "%",
                    cx + 12 + col * (colW + 12), cy + 8 + row * ROW, colW, 64, 38));
        }
    }

    private void video(int cx, int cy, int cw) {
        int rx = cx + 12;
        int rw = cw - 24;
        add(doubleSlider("Brightness", options.gamma(), v -> Math.round(v * 100) + "%",
                rx, cy + 8, rw, 110, 56));
        add(intSlider("Render distance", options.renderDistance(), 2, 32, 1, v -> v + " chunks",
                rx, cy + 8 + ROW, rw, 110, 56));
        add(intSlider("Max framerate", options.framerateLimit(), 10, 260, 10,
                v -> v >= 260 ? "Unlimited" : v + " fps", rx, cy + 8 + 2 * ROW, rw, 110, 56));

        OptionInstance<Boolean> fullscreen = options.fullscreen();
        track(fullscreen);
        add(new HlCycle("Display mode", rx, cy + 8 + 3 * ROW, rw, 20, 110,
                () -> fullscreen.get() ? "Full screen" : "Windowed",
                () -> fullscreen.set(!fullscreen.get())));
        add(check("Vertical sync", options.enableVsync(), rx, cy + 8 + 4 * ROW, rw));
        add(new HlButton("Advanced...", rx + rw - 110, cy + 8 + 5 * ROW, 110, 18, () -> {
            host.closeDialog();
            VanillaBridge.openVanillaOptions(host);
        }));
    }

    // ---- option bindings -------------------------------------------------------------------

    private <T> void track(OptionInstance<T> option) {
        original.putIfAbsent(option, option.get());
    }

    private Element doubleSlider(String label, OptionInstance<Double> option, DoubleFunction<String> format,
                                 int ex, int ey, int ew, int labelWidth, int valueWidth) {
        track(option);
        return new HlSlider(label, ex, ey, ew, 20, labelWidth, valueWidth,
                () -> option.get(),
                n -> option.set(n),
                () -> format.apply(option.get()));
    }

    private Element intSlider(String label, OptionInstance<Integer> option, int min, int max, int step,
                              IntFunction<String> format, int ex, int ey, int ew, int labelWidth, int valueWidth) {
        track(option);
        return new HlSlider(label, ex, ey, ew, 20, labelWidth, valueWidth,
                () -> (option.get() - min) / (double) (max - min),
                n -> {
                    int value = min + (int) Math.round(n * (max - min) / (double) step) * step;
                    option.set(Math.max(min, Math.min(max, value)));
                },
                () -> format.apply(option.get()));
    }

    private Element check(String label, OptionInstance<Boolean> option, int ex, int ey, int ew) {
        track(option);
        return new HlCheck(label, ex, ey, ew, 20, () -> option.get(), v -> option.set(v));
    }

    private static String pretty(String name) {
        return name.isEmpty() ? name : name.substring(0, 1).toUpperCase(Locale.ROOT) + name.substring(1);
    }

    // ---- OK / Cancel -----------------------------------------------------------------------

    private void ok() {
        options.save();
        host.closeDialog();
    }

    @Override
    public void cancel() {
        restore();
        host.closeDialog();
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void restore() {
        for (Map.Entry<OptionInstance<?>, Object> entry : original.entrySet()) {
            ((OptionInstance) entry.getKey()).set(entry.getValue());
        }
    }
}

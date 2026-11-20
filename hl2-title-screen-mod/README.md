# Half-Life 2 Menu (Fabric, Minecraft 26.3)

Replaces Minecraft's title screen with a Half-Life 2 style main menu and rebuilds the menus behind it as grey VGUI dialogs. Nothing uses Minecraft's pixel font: all text is drawn with bundled TrueType fonts (DejaVu Sans, a close relative of Verdana).

Client-side only. Needs Fabric Loader 0.19.5+ and Java 25. **Fabric API is not required.**

## What you get

| Menu entry | What it does |
|---|---|
| NEW GAME | HL2-style dialog, then opens Minecraft's world creation screen |
| LOAD GAME | List of your saves (name, last played, folder), newest first. Double-click or Load opens the world |
| ACHIEVEMENTS | Completed advancements of your most recently played world, with a total count |
| OPTIONS | Tabbed dialog (Gameplay / Keyboard / Mouse / Audio / Video). Sliders and checkboxes edit your real Minecraft settings live; OK/Apply save, Cancel undoes. "Controls..." and "Advanced..." open Minecraft's own screens for everything not covered |
| QUIT | "Are you sure you want to quit?" confirmation |

The regular Minecraft panorama stays as the background, with a dark vignette on the left like the HL2 menu. Escape closes the open dialog. Other screens (pause menu, in-game menus, world creation itself) are left as vanilla.

## Status: not compiled against the real game yet

The code was written against the 26.x API as documented by Fabric and checked for type errors against stubs of those APIs. It has **not** been built against the real Minecraft 26.3 jars, so a compile error in one of the few version-sensitive spots below is possible.

## Build the JAR

**Option A - on your PC** (needs JDK 25):

    ./gradlew build        (Windows: gradlew.bat build)

The mod is `build/libs/hl2menu-1.0.0.jar` (not the `-sources` one).

**Option B - no setup:** put this folder in a GitHub repository and push. The included workflow builds it and attaches `hl2menu-jar` to the run (Actions tab).

**Option C - if Gradle can't resolve the plugin version:** generate a fresh project at https://fabricmc.net/develop for 26.3, copy this project's `src/` folder into it, and build that.

Then drop the JAR into `.minecraft/mods`.

## If it doesn't compile or crashes on launch

Everything version-sensitive is isolated:

- `VanillaBridge.java` - the calls into vanilla flows (create world, open save, options screen)
- `Hl2Fonts.java` - one line that attaches a font to a text style
- `Hl2TitleScreen.java` - the "input" block at the bottom (mouse and key event signatures)
- `mixin/GuiSetScreenMixin.java` - hooks `Gui.setScreen` (26.2+; it used to be on `Minecraft`)
- `dialog/OptionsDialog.java` - uses the standard `Options` getters (`fov()`, `gamma()`, `renderDistance()` ...)

Paste the compiler error and the fix is usually one line.

## Tweaking the look

- Colours and the text baseline offset: `ui/Theme.java` (if text sits a pixel high or low, change `BASELINE`)
- Fonts and sizes: `assets/hl2menu/font/*.json`; replace `hl2_sans*.ttf` to use another typeface
- Title and menu placement: `computeLayout()` in `Hl2TitleScreen.java` (percentages of screen size)

Fonts: DejaVu Sans, see `assets/hl2menu/FONT-LICENSE.txt`.

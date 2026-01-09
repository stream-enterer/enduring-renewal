package com.tann.dice.gameplay.save.settings.option;

import com.tann.dice.platform.control.desktop.DesktopControl;
import com.tann.dice.screens.dungeon.DungeonScreen;
import com.tann.dice.screens.dungeon.panels.book.Book;
import com.tann.dice.screens.dungeon.panels.book.page.stuffPage.APIUtils;
import com.tann.dice.screens.titleScreen.TitleScreen;
import com.tann.dice.statics.bullet.BulletStuff;
import com.tann.dice.statics.sound.Sounds;
import com.tann.dice.statics.sound.music.MusicManager;
import com.tann.dice.util.Tann;
import com.tann.dice.util.TannFont;
import com.tann.dice.util.lang.Words;

public class OptionLib {
   public static final BOption LANDSCAPE_LOCK = new BOption("landscape lock", "lock phone into landscape mode even if rotated to portrait") {
      @Override
      protected void manualSelectAction() {
         OptionUtils.mayRequireFullAppRestart();
      }
   };
   public static final BOption HASH_ICONS = new BOption("icons", "show full iconography in # section where possible");
   public static final BOption HASH_HIDDEN = new BOption("show hidden", "show hidden modifiers in the modifier list");
   public static final BOption REMOVE_SAVE_BUTT = new BOption("remove this!", "removes the joke save button") {
      @Override
      protected void manualSelectAction() {
         OptionUtils.mayRequireQuitLoad(false);
      }
   };
   public static final BOption FAST_ENEMY_TURNS = new BOption("fast enemy turns", "3x speed for enemy rolling and combat animations");
   public static final BOption COMPLEX_HARD_EASY = new BOption(
      "complex [orange]hard[yellow]/[green]easy", "All easy/hard difficulties use a point-buy system similar to unfair+"
   );
   public static final BOption MYRIAD_OFFERS = new BOption(
      "myriad offers",
      "For difficulty modifier offers: [green]+" + Tann.percentFormat(0.5F) + " options[cu] but [purple]+" + Tann.percentFormat(0.15F) + " required value"
   );
   public static final BOption PRE_RANDOMISE = new BOption(
      "0.16667", "Ensure precisely 1/6 chance for each side to land [grey]by rotating to a random side before rolling"
   );
   public static final BOption GENERATED_HEROES = new BOption(
      "5% generated [yellow]heroes[cu]", "Each starting hero and levelup option has a chance to be replaced with a random generated hero"
   );
   public static final BOption GENERATED_ITEMS = new BOption(
      "5% generated [grey]items", "Item rewards have a chance to be replaced with a badly-balanced generated item"
   );
   public static final BOption GENERATED_MONSTERS = new BOption("5% generated [orange]monsters", "Try it, you'll see them");
   public static final BOption WILD_MODIFIERS = new BOption("5% wild [purple]modifiers", "They appear in difficulty offers etc");
   public static final BOption INCREASE_PERCENTAGE = new BOption("5% -> 20%", "all '5%' options are now more likely");
   public static final BOption DISABLE_CHAT = new BOption("disable chat", "Disable all speech bubbles from heroes");
   public static final BOption AUTO_FLEE = new BOption("auto-flee", "Monsters will always be allowed to flee if they want");
   public static final BOption TINY_PASTE = new BOption("tiny paste", "makes pastes smaller");
   public static final BOption LEVELUP_HORUS_ONLY = new BOption(
      "horus levelup", "For unknown heroes, levelup will try to equip them with an item. This falls back to +4hp and +1 to all sides which is a bit more sane."
   );
   public static final BOption GENERATE_50 = new BOption("api2 20", "Try to make 20 of each thing on api-2, instead of 6");
   public static final BOption LONGTAP_END = new BOption(
      "longtap end", "tap+hold or right-click on reroll button to 'confirm'. I added this to help me play one-handed on a phone."
   );
   public static final BOption HIDE_SPINNERS = new BOption(
      "hide spinning dice", "Hides UI spinny dice. This could render weirdly on certain machines, or cause lag."
   );
   public static final BOption DISABLE_2D_3D_IMAGE = new BOption(
      "disable optimisation 1",
      "An optimisation I made to reduce texture swaps, though I'm concerned it could cause visual glitches on Android maybe. It's easy to toggle at runtime so you can see if it did anything too!"
   );
   public static final BOption DISABLE_3D_DISPOSE = new BOption("disable dispose", "stop disposing 3d assets, maybe this causes a crash on android?");
   public static final BOption DIE_PANEL_TINY = new BOption("tiny DiePanel", "force tiny dice panels (also improves performance a bit)");
   public static final BOption FPS_COUNTER = new BOption("fps counter", "Show FPS on screen");
   public static final BOption PHASE_DISPLAY = new BOption("phase display", "Show phase on screen if any");
   public static final BOption COLORBLIND_POISON = new BOption("colorblind poison", "poisoned hp shows as [pink]pink[cu] instead of [green]green[cu]");
   public static final BOption SHOW_STAT_POPUPS = new BOption("show stat updates");
   public static final BOption AVOID_SETTING_LOCALE_EN = new BOption("avoid locale set");
   public static final BOption MOD_CALC = new BOption("show modcalc");
   public static final BOption SHOW_GRB = new BOption("show grb");
   public static final BOption SHOW_PREDICTION = new BOption("show prediction");
   public static final BOption DISABLE_API_ORDER = new BOption("disable API sort");
   public static final BOption PRINT_PIPE = new BOption("log pipe stuff", "prints recursion & string");
   public static final BOption DISABLE_PIPE_CACHE = new BOption("disable pipe cache", "");
   public static final BOption DISABLE_IMG_CACHE = new BOption("disable img cache", "");
   public static final BOption IMG_CREATIONS = new BOption("img creations", "");
   public static final BOption UNUSED_1 = new BOption("unused1", "");
   public static final BOption UNUSED_2 = new BOption("unused2", "");
   public static final BOption BYPASS_UNLOCKS = new BOption("bypass unlocks", "Unlock everything, though you can still earn achievements as normal") {
      @Override
      protected String warnString() {
         boolean moveScreen = !(com.tann.dice.Main.getCurrentScreen() instanceof TitleScreen);
         String mvs = moveScreen ? "You will be returned to the title screen" : null;
         if (this.c()) {
            return moveScreen ? mvs : null;
         } else {
            return "Locked things are [purple]more complex, [orange]not more powerful.[cu][n][nh][grey](this doesn't affect achievements and you can undo it"
               + (mvs == null ? "" : ", also " + mvs.toLowerCase())
               + ")";
         }
      }

      @Override
      protected void manualSelectAction() {
         com.tann.dice.Main.unlockManager().resetUnlocks();
         com.tann.dice.Main.self().setScreen(new TitleScreen());
         String s = this.c()
            ? "[text][yellow]100% unlocked[cu][n][nh]The game will be a lot more [purple]complex[cu] now[n][nh]Remember you can undo this if it's too much!"
            : "Unlocks synced with [yellow]" + Words.plural("achievement");
         com.tann.dice.Main.getCurrentScreen().showDialog(s);
         Sounds.playSound(this.c() ? Sounds.deathBig : Sounds.magic);
      }
   };
   public static final BOption SHOW_COPY = new BOption(
      "copy button",
      com.tann.dice.Main.self().control.getSelectTapString() + " on the faint triangle bottom left of a hero, monster, modifier, item to copy to clipboard."
   ) {
      @Override
      protected void manualSelectAction() {
         APIUtils.refreshCopyButton();
      }
   };
   public static final BOption SHOW_TIMER = new BOption("show timer", "Timer for speedrunning, time is calculated from the date you started the run") {
      @Override
      protected void manualSelectAction() {
         DungeonScreen ds = DungeonScreen.getCurrentScreenIfDungeon(false);
         if (ds != null) {
            ds.refreshTimer();
         }
      }
   };
   public static final BOption SHOW_CLOCK = new BOption("show clock", "A few people saying the game made them late for work") {
      @Override
      protected void manualSelectAction() {
         DungeonScreen ds = DungeonScreen.getCurrentScreenIfDungeon(false);
         if (ds != null) {
            ds.refreshClock();
         }
      }
   };
   public static final BOption SHOW_RARITY = new BOption(
      "show rarity",
      "Many things in the game have rarity. It's not intended to be an exciting thing, just to balance out distributions and stop annoying/weird things from showing up too much"
   ) {
      @Override
      protected void manualSelectAction() {
         OptionUtils.mayRequireQuitLoad();
      }
   };
   public static final BOption ROMAN_MODE = new BOption("roman numerals", "[notranslate]alea iacta est") {
      @Override
      protected void manualSelectAction() {
         OptionUtils.mayRequireQuitLoad();
      }
   };
   public static final BOption CUSTOM_REARRANGE = new BOption("custom rearrange", "rearrange modifiers in custom mode") {
      @Override
      protected void manualSelectAction() {
         if (com.tann.dice.Main.getCurrentScreen() instanceof TitleScreen) {
            OptionUtils.remakeUIAfterTogglingOption();
         }
      }
   };
   public static final BOption CRAZY_UI = new BOption("crazy UI", "panels burn away etc") {
      @Override
      protected void manualSelectAction() {
         if (++OptionUtils.crazyTaps >= 15) {
            Sounds.playSound(Sounds.pipSmall);
            TannFont.GLOBAL_GLITCH = OptionLib.CRAZY_UI.c();
         }
      }
   };
   public static final BOption DISABLE_MARQUEE = new BOption("disable <marquee>", "show ... instead of scrolling text") {
      @Override
      protected void manualSelectAction() {
         OptionUtils.mayRequireQuitLoad();
      }
   };
   public static final BOption ALWAYS_SHOW_TARG_BUTTON = new BOption(
      "target button", "always show the red targeting button at the top to show monster targeting"
   ) {
      @Override
      protected void manualSelectAction() {
         if (com.tann.dice.Main.getCurrentScreen() instanceof DungeonScreen) {
            OptionUtils.remakeUIAfterTogglingOption();
         }
      }
   };
   public static final BOption SEARCH_BUTT = new BOption("search button", "show a search button a bit like pins") {
      @Override
      protected void manualSelectAction() {
         OptionUtils.mayRequireQuitLoad();
      }
   };
   public static final BOption TRIPLE_CHAT = new BOption("triple chat", "hero chats happen more frequently");
   public static final BOption TEXTMOD_COMPLEX = new BOption("textmod [purple]+more", "Show more textmod apis, the new ones are purple") {
      @Override
      protected void manualSelectAction() {
         if (Book.inBook()) {
            OptionUtils.remakeUIAfterTogglingOption();
         }
      }
   };
   public static final BOption SMARTPHONE_CONTROLS = new BOption("smartphone controls", "Use long tap for right-click on desktop, maybe useful for steamdeck") {
      @Override
      public boolean isValid() {
         return com.tann.dice.Main.self().control instanceof DesktopControl;
      }
   };
   public static final BOption SHOW_PLAYING_POPUP = new BOption("music popups", "show the name of the track when it is played");
   public static final BOption SHOW_LEVEL_DIFF = new BOption("level val");
   public static final BOption SHOW_RENDERCALLS = new BOption("show renders") {
      @Override
      public void setValue(boolean value, boolean manual) {
         super.setValue(value, manual);
         com.tann.dice.Main.self().setupProfiler();
      }
   };
   public static final ChOption DYING_FLASH = new ChOption("dying flash", "Alternate effects for dying heroes.", "dark", "border", "old") {
      @Override
      protected void manualSelectAction() {
         if (this.c() == 2) {
            OptionUtils.showSelectingChoptionWarning("old");
         }
      }
   };
   public static final ChOption STATIC_INCOMING_DEBUFF = new ChOption("incoming debuffs", "Alternate effect for incoming debuffs.", "faded", "plus", "old") {
      @Override
      protected void manualSelectAction() {
         if (this.c() == 2) {
            OptionUtils.showSelectingChoptionWarning("old");
         }
      }
   };
   public static final ChOption CR_INDICATOR = new ChOption("CR indicator", "?", "none", "pulsate", "random");
   public static final ChOption RENDER_MODE = new ChOption(
      "render mode",
      "S&D has very little animation. Much CPU/battery can be saved by skipping re-rendering when nothing is happening. cr0 and cr0.1 are similar, cr1 forces a render each frame.",
      "cr0.1",
      "cr0",
      "cr1"
   ) {
      @Override
      protected void manualSelectAction() {
         com.tann.dice.Main.self().crManuallyAltered();
      }
   };
   public static final ChOption MUSIC_LOAD_TYPE = new ChOption(
      "music load", "load music differently, maybe this was causing an issue - preload requires app restart", "thread", "preload", "lazy"
   );
   public static final ChOption GAP = new ChOption(
      "gap", "Make a gap for something like a stream avatar or video. Not available in portrait.", "none", "bot", "top"
   ) {
      @Override
      protected void manualSelectAction() {
         if (com.tann.dice.Main.getCurrentScreen() instanceof DungeonScreen) {
            OptionUtils.remakeUIAfterTogglingOption();
         }
      }

      @Override
      public boolean isValid() {
         return !com.tann.dice.Main.isPortrait();
      }
   };
   public static final ChOption FONT = new ChOption(
      "font",
      "which font to use, TannFont has significantly better performance",
      "[notranslate]TannFont",
      "[notranslate]Tuffy",
      "[notranslate]Gargle",
      "[notranslate]Karma Suture",
      "[notranslate]Mesmerize"
   ) {
      @Override
      public void setValue(int index, boolean manual) {
         int oldIndex = this.selectedIndex;
         super.setValue(index, manual);
         if (manual && oldIndex == 0 && this.selectedIndex != 0) {
            OptionUtils.showHdFontsPerformanceWarning();
         }
      }

      @Override
      protected void manualSelectAction() {
         OptionUtils.remakeUIAfterTogglingOption();
      }
   };
   public static final ChOption LANGUAGE = new ChOption(
      "language",
      "what language to use",
      "default",
      "[notranslate][light]en",
      "[notranslate][orange]es",
      "[notranslate][yellow]pt",
      "[notranslate][blurple]fr",
      "[notranslate][green]it",
      "[notranslate][blue]ru"
   ) {
      @Override
      protected void manualSelectAction() {
         com.tann.dice.Main.self().setLanguageFromOption();
         com.tann.dice.Main.self().setupScale(true);
      }
   };
   public static final ChOption BUTT_STYLE = new ChOption("Button Style", null, "Rounded", "Sides", "Corners", "SimpleSquare") {
      @Override
      protected void manualSelectAction() {
         OptionUtils.mayRequireQuitLoad(false);
      }
   };
   public static final ChOption AFFECT_LOOP = new ChOption(
      "music loop",
      "some tracks loop one or twice, this affects that[n][grey][tick]- normal loops[n]X- no loops[n][infinite]- loop 999 times",
      "[tick]",
      "[notranslate]X",
      "[infinite]"
   ) {
      @Override
      protected void manualSelectAction() {
         MusicManager.userRequestNextSong();
      }
   };
   public static final ChOption MUSIC_SELECTION = new ChOption(
      "music selection", "[yellow]DJ:[cu] plays music that fits where you are[n][yellow]Shuffle:[cu] picks from all enabled tracks", "DJ", "Shuffle"
   ) {
      @Override
      protected void manualSelectAction() {
         MusicManager.checkSongIsValid(true);
      }
   };
   public static final ChOption ROLL_SPEED = new ChOption(
      "roll speed", "Affect dice roll speed (and some other things)", "[notranslate]1x", "[notranslate]1.5x", "[notranslate]2x"
   );
   public static final ChOption DICE_SIZE = new ChOption(
      "[tinyDice] size",
      "Change size of dice",
      "[notranslate]1x",
      "[notranslate]1.1x",
      "[notranslate]1.2x",
      "[notranslate]0.9x",
      "[notranslate]0.8x",
      "[notranslate]0.7x"
   ) {
      @Override
      protected void manualSelectAction() {
         if (DungeonScreen.get() != null) {
            OptionUtils.remakeUIAfterTogglingOption();
         } else {
            BulletStuff.init();
         }
      }
   };
   public static final FlOption sfx = new FlOption("sfx") {
      @Override
      protected void manualSelectAction() {
         if (com.tann.dice.Main.frames % 15 == 0) {
            Sounds.playSound(Sounds.pip);
         }
      }
   };
   public static final FlOption music = new FlOption("music") {
      @Override
      public float getDefaultValue() {
         return 0.15F;
      }
   };
}

package com.tann.dice.gameplay.save.settings.option;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Entries;
import com.badlogic.gdx.utils.ObjectMap.Entry;
import com.tann.dice.gameplay.battleTest.Difficulty;
import com.tann.dice.gameplay.effect.targetable.ability.ui.AbilityHolder;
import com.tann.dice.gameplay.phase.Phase;
import com.tann.dice.gameplay.phase.PhaseManager;
import com.tann.dice.gameplay.phase.gameplay.DamagePhase;
import com.tann.dice.gameplay.phase.gameplay.EnemyRollingPhase;
import com.tann.dice.screens.dungeon.DungeonScreen;
import com.tann.dice.screens.dungeon.DungeonUtils;
import com.tann.dice.statics.Images;
import com.tann.dice.statics.bullet.BulletStuff;
import com.tann.dice.statics.sound.Sounds;
import com.tann.dice.statics.sound.music.JukeboxUtils;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Pixl;
import com.tann.dice.util.Tann;
import com.tann.dice.util.TannLog;
import com.tann.dice.util.listener.TannListener;
import com.tann.dice.util.ui.TextWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class OptionUtils {
   public static final float MYRIAD_BONUS_TIER = 0.15F;
   public static final float MYRIAD_BONUS_AMT_MULTIPLIER = 0.5F;
   private static final float CHANCE_LOW = 0.05F;
   static final String PERC_LOW = "5%";
   private static final float CHANCE_HIGH = 0.2F;
   static final String PERC_HIGH = "20%";
   public static int crazyTaps = 0;
   private static List<Option> list;
   private static final float FAST_ENEMY_TURNS_MULT = 0.5F;

   public static boolean isContinuous() {
      return OptionLib.RENDER_MODE.c() == 2;
   }

   public static boolean shouldForceRenderThread() {
      return OptionLib.RENDER_MODE.c() == 0;
   }

   public static boolean shouldBeCrazyUi() {
      return OptionLib.FONT.c() == 0 && OptionLib.CRAZY_UI.c();
   }

   public static boolean disableMarqueeFromCR() {
      return false;
   }

   public static float getRollSpeedMultiplier(boolean player) {
      return getBaseRollSpeedMultiplier() * (player ? 1.0F : baseEnemyAnim());
   }

   private static float getBaseRollSpeedMultiplier() {
      switch (OptionLib.ROLL_SPEED.c()) {
         case 1:
            return 0.6666F;
         case 2:
            return 0.5F;
         default:
            return 1.0F;
      }
   }

   public static float unkAnim() {
      PhaseManager pm = PhaseManager.get();
      if (pm != null) {
         Phase p = pm.getPhase();
         if (p instanceof EnemyRollingPhase || p instanceof DamagePhase) {
            return enemyAnim();
         }
      }

      return 1.0F;
   }

   public static float enemyAnim() {
      return baseEnemyAnim() * ((1.0F + getBaseRollSpeedMultiplier()) / 2.0F);
   }

   private static float baseEnemyAnim() {
      return OptionLib.FAST_ENEMY_TURNS.c() ? 0.5F : 1.0F;
   }

   public static float buttonAnim() {
      float base = 0.3F;
      switch (OptionLib.ROLL_SPEED.c()) {
         case 3:
            base *= 0.3F;
         default:
            return base;
      }
   }

   public static boolean playRollSfx(boolean player) {
      return getRollSpeedMultiplier(player) > 0.3F;
   }

   public static boolean skipForwardsBackForEnemies() {
      return enemyAnim() < 0.3F;
   }

   public static float backgroundMoveMult() {
      return getBaseRollSpeedMultiplier();
   }

   public static float dangerButtonSpeed() {
      return 1.0F;
   }

   public static boolean shouldPreRandomise() {
      return OptionLib.PRE_RANDOMISE.c() || BulletStuff.forcePrerandomiseDueToBounds();
   }

   public static boolean shouldShowFlashyEndTurn() {
      return com.tann.dice.Main.self().settings.getTutorialProgress() < 0.45F;
   }

   public static boolean dyingCrossTitle() {
      return false;
   }

   public static boolean dyingSkullHp() {
      return true;
   }

   public static boolean dyingSkullConfirm() {
      return true;
   }

   public static boolean cornerSkullsConfirm() {
      return false;
   }

   public static boolean pulsatingDyingFlash() {
      return OptionLib.DYING_FLASH.c() == 2;
   }

   public static int getRealModifierAmt(Difficulty d) {
      int amt = d.getBaseAmt();
      if (d != Difficulty.Normal && OptionLib.MYRIAD_OFFERS.c()) {
         return Tann.randomRound(amt * 1.5F);
      } else {
         return !OptionLib.COMPLEX_HARD_EASY.c() || d != Difficulty.Easy && d != Difficulty.Hard ? amt : amt + 1;
      }
   }

   public static int getRealModifierAddUpTo(int addUpTo) {
      return !OptionLib.MYRIAD_OFFERS.c() ? addUpTo : Math.round(addUpTo - Math.abs(addUpTo) * 0.15F);
   }

   public static int affectLoops(int baseLoops) {
      switch (OptionLib.AFFECT_LOOP.c()) {
         case 0:
         default:
            return baseLoops;
         case 1:
            return 1;
         case 2:
            return 999;
      }
   }

   public static Actor makeLockButton() {
      Actor lockButton = DungeonUtils.makeBasicButton(Images.unlocked);
      lockButton.addListener(new TannListener() {
         @Override
         public boolean action(int button, int pointer, float x, float y) {
            Pixl p = new Pixl(3, 4).border(Colours.purple);
            p.text("Unlocks are currently [purple]bypassed[cu][n]Would you like to re-enable?");
            p.row();
            p.actor(OptionLib.BYPASS_UNLOCKS.makeUnlockActor(true));
            Actor a = p.pix();
            Sounds.playSound(Sounds.pip);
            com.tann.dice.Main.getCurrentScreen().push(a);
            Tann.center(a);
            return true;
         }
      });
      return lockButton;
   }

   static void remakeUIAfterTogglingOption() {
      com.tann.dice.Main.self().setupScale(true);
   }

   public static boolean shouldShowCopy() {
      return OptionLib.SHOW_COPY.c();
   }

   public static float genChance() {
      return OptionLib.INCREASE_PERCENTAGE.c() ? 0.2F : 0.05F;
   }

   static void mayRequireQuitLoad() {
      mayRequireQuitLoad(true);
   }

   static void mayRequireQuitLoad(boolean inGameOnly) {
      if (inGameOnly) {
         DungeonScreen ds = DungeonScreen.getCurrentScreenIfDungeon(false);
         if (ds == null) {
            return;
         }
      }

      AbilityHolder.showInfo("may require quit/load", Colours.purple);
   }

   static void mayRequireFullAppRestart() {
      com.tann.dice.Main.getCurrentScreen().showDialog("[pink]may require app restart to take effect");
   }

   public static void showSelectingChoptionWarning(String op) {
      showMiscWarning("[orange]Caution[cu][n]The option '[light]" + op + "'[cu] has [orange]worse performance");
   }

   static void showHdFontsPerformanceWarning() {
      showMiscWarning("[purple]Warning[n][cu]HD Fonts may be [red][b]laggy[b][n][grey](TannFont is fast)");
   }

   public static void showMiscWarning(String msg) {
      Actor warn = new Pixl(2, 3).border(Colours.grey).text(msg, (int)(com.tann.dice.Main.width * 0.6F)).pix();
      com.tann.dice.Main.getCurrentScreen().pushAndCenter(warn, 0.8F);
   }

   public static void loadValuesFrom(List<String> enabledBooleans, ObjectMap<String, Integer> chopValues, ObjectMap<String, Float> flopValues) {
      for (Option option : list) {
         option.reset();
      }

      for (String s : enabledBooleans) {
         Option bo = byName(s);
         if (!(bo instanceof BOption)) {
            TannLog.log("Error finding booleanoption with name " + s, TannLog.Severity.error);
         } else {
            ((BOption)bo).setValue(true, false);
         }
      }

      Entries var7 = chopValues.iterator();

      while (var7.hasNext()) {
         Entry<String, Integer> chopValue = (Entry<String, Integer>)var7.next();
         Option chop = byName((String)chopValue.key);
         if (!(chop instanceof ChOption)) {
            TannLog.log("Error finding ChOption with name " + (String)chopValue.key, TannLog.Severity.error);
         } else {
            ((ChOption)chop).setValue((Integer)chopValue.value, false);
         }
      }

      var7 = flopValues.iterator();

      while (var7.hasNext()) {
         Entry<String, Float> flopValue = (Entry<String, Float>)var7.next();
         Option chop = byName((String)flopValue.key);
         if (!(chop instanceof FlOption)) {
            TannLog.log("Error finding ChOption with name " + (String)flopValue.key, TannLog.Severity.error);
         } else {
            ((FlOption)chop).setValue((Float)flopValue.value, false);
         }
      }
   }

   private static Option byName(String name) {
      for (Option bo : list) {
         if (bo.name.equals(name)) {
            return bo;
         }
      }

      return null;
   }

   public static List<Option> getAll() {
      return list;
   }

   public static void init() {
      list = new ArrayList<>();

      for (OptionUtils.EscBopType value : OptionUtils.EscBopType.values()) {
         list.addAll(value.getOptions());
      }
   }

   public static Color poisonCol() {
      return OptionLib.COLORBLIND_POISON.c() ? Colours.pink : Colours.green;
   }

   public static String poisonTag() {
      return TextWriter.getTag(poisonCol());
   }

   public static float getDiceAdjust() {
      switch (OptionLib.DICE_SIZE.c()) {
         case 0:
         default:
            return 0.0F;
         case 1:
            return 0.5F;
         case 2:
            return 1.0F;
         case 3:
            return -0.4F;
         case 4:
            return -0.8F;
         case 5:
            return -1.2F;
      }
   }

   public static boolean shouldForceDiceRand() {
      return getDiceAdjust() > 0.0F;
   }

   public static void setLocaleEn() {
      if (!OptionLib.AVOID_SETTING_LOCALE_EN.c()) {
         Locale.setDefault(Locale.ENGLISH);
      }
   }

   public static enum EscBopType {
      Top(false),
      Misc(false),
      Gameplay(true),
      UI(true),
      Modding(true),
      Music(true),
      Debug(false);

      public final boolean shownInOptions;

      private EscBopType(boolean shownInOptions) {
         this.shownInOptions = shownInOptions;
      }

      public List<Option> getOptions() {
         switch (this) {
            case Top:
               return Arrays.asList(OptionLib.sfx, OptionLib.music);
            case Gameplay:
               return Arrays.asList(
                  OptionLib.BYPASS_UNLOCKS,
                  OptionLib.GENERATED_HEROES,
                  OptionLib.GENERATED_ITEMS,
                  OptionLib.GENERATED_MONSTERS,
                  OptionLib.WILD_MODIFIERS,
                  OptionLib.INCREASE_PERCENTAGE,
                  OptionLib.COMPLEX_HARD_EASY,
                  OptionLib.MYRIAD_OFFERS,
                  OptionLib.PRE_RANDOMISE
               );
            case Modding:
               return Arrays.asList(
                  OptionLib.TEXTMOD_COMPLEX,
                  OptionLib.CUSTOM_REARRANGE,
                  OptionLib.DISABLE_MARQUEE,
                  OptionLib.TINY_PASTE,
                  OptionLib.LEVELUP_HORUS_ONLY,
                  OptionLib.GENERATE_50
               );
            case UI:
               List<Option> options = new ArrayList<>(
                  Arrays.asList(
                     OptionLib.FAST_ENEMY_TURNS,
                     OptionLib.AUTO_FLEE,
                     OptionLib.SHOW_TIMER,
                     OptionLib.SHOW_CLOCK,
                     OptionLib.SHOW_RARITY,
                     OptionLib.ALWAYS_SHOW_TARG_BUTTON,
                     OptionLib.SEARCH_BUTT,
                     OptionLib.TRIPLE_CHAT,
                     OptionLib.ROMAN_MODE,
                     OptionLib.CRAZY_UI,
                     OptionLib.LONGTAP_END,
                     OptionLib.HIDE_SPINNERS,
                     OptionLib.SMARTPHONE_CONTROLS,
                     OptionLib.COLORBLIND_POISON,
                     OptionLib.LANGUAGE,
                     OptionLib.ROLL_SPEED,
                     OptionLib.DICE_SIZE,
                     OptionLib.GAP,
                     OptionLib.FONT,
                     OptionLib.DYING_FLASH,
                     OptionLib.STATIC_INCOMING_DEBUFF
                  )
               );
               if (Gdx.app.getType() == ApplicationType.iOS) {
                  options.remove(OptionLib.GAP);
               }

               if (Gdx.app.getType() == ApplicationType.iOS) {
                  options.add(OptionLib.LANDSCAPE_LOCK);
               }

               return options;
            case Misc:
               return Arrays.asList(OptionLib.HASH_ICONS, OptionLib.HASH_HIDDEN, OptionLib.REMOVE_SAVE_BUTT, OptionLib.SHOW_COPY);
            case Debug:
               return Arrays.asList(
                  OptionLib.CR_INDICATOR,
                  OptionLib.RENDER_MODE,
                  OptionLib.SHOW_STAT_POPUPS,
                  OptionLib.SHOW_LEVEL_DIFF,
                  OptionLib.PRINT_PIPE,
                  OptionLib.SHOW_RENDERCALLS,
                  OptionLib.MOD_CALC,
                  OptionLib.SHOW_GRB,
                  OptionLib.SHOW_PREDICTION,
                  OptionLib.DISABLE_API_ORDER,
                  OptionLib.BUTT_STYLE,
                  OptionLib.DISABLE_CHAT,
                  OptionLib.DISABLE_PIPE_CACHE,
                  OptionLib.DISABLE_IMG_CACHE,
                  OptionLib.IMG_CREATIONS,
                  OptionLib.DISABLE_2D_3D_IMAGE,
                  OptionLib.DISABLE_3D_DISPOSE,
                  OptionLib.MUSIC_LOAD_TYPE,
                  OptionLib.FPS_COUNTER,
                  OptionLib.PHASE_DISPLAY,
                  OptionLib.DIE_PANEL_TINY,
                  OptionLib.AVOID_SETTING_LOCALE_EN,
                  OptionLib.UNUSED_1,
                  OptionLib.UNUSED_2
               );
            case Music:
               return Arrays.asList(OptionLib.SHOW_PLAYING_POPUP, OptionLib.AFFECT_LOOP, OptionLib.MUSIC_SELECTION);
            default:
               return Arrays.asList();
         }
      }

      public Color getCol() {
         switch (this) {
            case Gameplay:
               return Colours.orange;
            case Modding:
               return Colours.red;
            case UI:
               return Colours.text;
            case Misc:
            case Debug:
            default:
               return Colours.pink;
            case Music:
               return JukeboxUtils.SOUND_COL;
         }
      }
   }
}

package com.tann.dice.screens.titleScreen;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.battleTest.Difficulty;
import com.tann.dice.gameplay.content.ent.group.Party;
import com.tann.dice.gameplay.content.ent.group.PartyLayoutType;
import com.tann.dice.gameplay.content.ent.type.HeroCol;
import com.tann.dice.gameplay.content.ent.type.lib.HeroTypeLib;
import com.tann.dice.gameplay.content.gen.pipe.entity.hero.PipeHero;
import com.tann.dice.gameplay.context.DungeonContext;
import com.tann.dice.gameplay.context.config.ContextConfig;
import com.tann.dice.gameplay.context.config.difficultyConfig.DifficultyConfig;
import com.tann.dice.gameplay.mode.Mode;
import com.tann.dice.gameplay.mode.creative.pastey.PasteMode;
import com.tann.dice.gameplay.mode.meta.folder.FolderType;
import com.tann.dice.gameplay.progress.chievo.unlock.Feature;
import com.tann.dice.gameplay.progress.chievo.unlock.UnUtil;
import com.tann.dice.gameplay.save.SaveState;
import com.tann.dice.gameplay.save.antiCheese.AntiCheeseRerollInfo;
import com.tann.dice.gameplay.save.antiCheese.AnticheeseData;
import com.tann.dice.gameplay.trigger.global.heroLevelupAffect.HeroGenType;
import com.tann.dice.screens.Screen;
import com.tann.dice.screens.dungeon.DungeonScreen;
import com.tann.dice.screens.dungeon.panels.tutorial.TutorialManager;
import com.tann.dice.statics.sound.Sounds;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Pixl;
import com.tann.dice.util.Tann;
import com.tann.dice.util.TannLog;
import com.tann.dice.util.listener.TannListener;
import com.tann.dice.util.ui.TextWriter;
import com.tann.dice.util.ui.choice.ChoiceDialog;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class GameStart {
   public static void start(DungeonContext dungeonContext) {
      Sounds.playSound(Sounds.confirm);
      ContextConfig cc = dungeonContext.getContextConfig();
      if (!anticheeseOverride(cc)) {
         if (!tutStartOverride(cc)) {
            DungeonScreen ds = new DungeonScreen(dungeonContext);
            com.tann.dice.Main.self().setScreen(ds);
         }
      }
   }

   public static void startWithPLTChoice(final ContextConfig cc, final AntiCheeseRerollInfo acri, boolean countsAsLoss) {
      rerollAnticheeseHack(cc);
      if (!shouldUsePartyLayout(cc)) {
         basicStartConfirmIfNecessary(cc, new Runnable() {
            @Override
            public void run() {
               GameStart.start(cc.makeContext(acri));
            }
         }, false);
      } else {
         startWithPLTChoice(cc, PipeHero.getGenType(cc.getModeGlobals()), acri, acri != null, countsAsLoss);
      }
   }

   private static void rerollAnticheeseHack(ContextConfig cc) {
      if (cc instanceof DifficultyConfig
         && ((DifficultyConfig)cc).getDifficulty() == Difficulty.Normal
         && UnUtil.isLocked(Feature.NORMAL_TWEAKS)
         && cc.getAnticheese() != null
         && cc.getAnticheese().canReroll()) {
         cc.anticheeseReroll();
      }
   }

   private static void startWithPLTChoice(
      final ContextConfig cc, final HeroGenType hgt, final AntiCheeseRerollInfo acri, final boolean reroll, final boolean countsAsLoss
   ) {
      com.tann.dice.Main.getCurrentScreen().popAllMedium();
      Pixl p = new Pixl(10, 8).border(Colours.grey);
      p.text("Choose party layout").row();

      for (final PartyLayoutType type : getTypesFromContext(cc, acri)) {
         Actor a = type.visualise();
         p.actor(a);
         a.addListener(new TannListener() {
            @Override
            public boolean action(int button, int pointer, float x, float y) {
               if (!countsAsLoss && (reroll || !cc.hasSave())) {
                  GameStart.startWithPLTChosen(cc, hgt, acri, type, reroll);
               } else {
                  GameStart.basicStartConfirmIfNecessary(cc, new Runnable() {
                     @Override
                     public void run() {
                        GameStart.startWithPLTChosen(cc, hgt, acri, type, false);
                     }
                  }, countsAsLoss);
               }

               return true;
            }
         });
      }

      Actor a = p.pix();
      a = Tann.makeScrollpaneIfNecessaryHori(a);
      com.tann.dice.Main.getCurrentScreen().push(a, 0.8F);
      Tann.center(a);
      Sounds.playSound(Sounds.pip);
   }

   private static void basicStartConfirmIfNecessary(final ContextConfig cc, final Runnable actuallyStart, boolean streakReset) {
      if (cc.hasSave() && !cc.saveAtRunEnd()) {
         String msg = streakReset
            ? "[n][red]This will count as a loss because you have already rerolled once without playing. Continue?"
            : "[red]Warning[cu], this will overwrite your in-progress game.[n]Are you sure?";
         ChoiceDialog choiceDialog = new ChoiceDialog(null, Arrays.asList(new TextWriter(msg, 120)), ChoiceDialog.ChoiceNames.YesCancel, new Runnable() {
            @Override
            public void run() {
               if (!cc.skipStats()) {
                  try {
                     SaveState oldSave = SaveState.load(cc.getGeneralSaveKey());
                     DungeonContext old = oldSave.dungeonContext;
                     old.logDefeatBackground(oldSave);
                  } catch (Exception var3) {
                     TannLog.log("Failed to merge stats: " + var3.getClass().getSimpleName() + ": " + var3.getMessage(), TannLog.Severity.error);
                     var3.printStackTrace();
                  }
               }

               cc.clearSave();
               actuallyStart.run();
            }
         }, null);
         com.tann.dice.Main.getCurrentScreen().push(choiceDialog, 0.5F);
         Tann.center(choiceDialog);
         Sounds.playSound(Sounds.pip);
      } else {
         actuallyStart.run();
      }
   }

   public static boolean shouldUsePartyLayout(ContextConfig cc) {
      return !UnUtil.isLocked(Feature.PARTY_LAYOUT_CHOICE)
         && !cc.mode.disablePartyLayout()
         && (cc.getAnticheese() == null || cc.getAnticheese().getSaveState() == null || currentlyPlayingProbably(cc.mode));
   }

   private static boolean currentlyPlayingProbably(Mode mode) {
      Screen s = com.tann.dice.Main.getCurrentScreen();
      if (s instanceof DungeonScreen) {
         DungeonScreen ds = (DungeonScreen)s;
         return ds.getDungeonContext().getContextConfig().mode == mode;
      } else {
         return false;
      }
   }

   public static List<PartyLayoutType> getTypesFromContext(ContextConfig cc, AntiCheeseRerollInfo acri) {
      FolderType ft = cc.mode.getFolderType();
      if (ft != FolderType.cursed && ft != FolderType.creative) {
         if (UnUtil.isLocked(HeroCol.blue)) {
            return Arrays.asList(PartyLayoutType.CornCob, PartyLayoutType.Force, PartyLayoutType.RNG);
         } else {
            String finalPart = "";
            if (cc.skipStats()) {
               finalPart = Tann.randomString(5);
            } else {
               finalPart = cc.getWins() + ":" + cc.getLosses();
            }

            String hashString = com.tann.dice.Main.getSettings().getHighscoreIdentifier() + cc.mode.getName() + cc.describeConfig() + finalPart;
            AnticheeseData acd = cc.getAnticheese();
            if (acd != null) {
               hashString = hashString + acd;
            }

            int hash = hashString.hashCode();
            PartyLayoutType[] pool = PartyLayoutType.exceptBasic();
            if (acri != null) {
               PartyLayoutType plt = acri.getLayout();
               if (plt != null) {
                  int index = Tann.indexOf(pool, plt);
                  if (index != -1 && index != pool.length - 1) {
                     PartyLayoutType[] newtmp = new PartyLayoutType[pool.length - 1];
                     System.arraycopy(pool, 0, newtmp, 0, newtmp.length);
                     newtmp[index] = pool[pool.length - 1];
                     pool = newtmp;
                  }
               }
            }

            List<PartyLayoutType> rc = Tann.randomChanced(pool, 2, Tann.makeStdRandom(hash));
            rc.add(0, PartyLayoutType.Basic);
            return rc;
         }
      } else {
         return UnUtil.isLocked(HeroTypeLib.byName("housecat"))
            ? Arrays.asList(PartyLayoutType.Basic, PartyLayoutType.Force)
            : Arrays.asList(
               PartyLayoutType.Basic,
               PartyLayoutType.Force,
               PartyLayoutType.Greens,
               PartyLayoutType.Magical,
               PartyLayoutType.Mountain,
               PartyLayoutType.Defensive
            );
      }
   }

   public static void startWithPLTChosen(ContextConfig cc, HeroGenType hgt, AntiCheeseRerollInfo acri, PartyLayoutType plt, boolean reroll) {
      ContextConfig.resetCache();
      if (reroll) {
         cc.anticheeseReroll();
      }

      DungeonContext dc;
      if (acri == null) {
         dc = cc.makeContext(acri, Party.generate(cc.getLevelOffset(), hgt, plt, new ArrayList<>()));
      } else {
         dc = cc.makeContext(acri, Party.generate(cc.getLevelOffset(), hgt, plt, acri.getOldHeroes()));
         dc.setAntiCheeseRerollModifiers(acri.getOldOptions());
      }

      DungeonScreen ds = new DungeonScreen(dc);
      com.tann.dice.Main.self().setScreen(ds);
   }

   private static boolean tutStartOverride(ContextConfig cc) {
      if (com.tann.dice.Main.getSettings().hasAttemptedLevel()) {
         return false;
      } else {
         if (cc instanceof DifficultyConfig) {
            DifficultyConfig diffCon = (DifficultyConfig)cc;
            String pasteOverride = TutorialManager.getTutOverride(diffCon);
            if (pasteOverride != null) {
               PasteMode.attemptToStartFromString(pasteOverride, cc);
               return true;
            }
         }

         return false;
      }
   }

   private static boolean anticheeseOverride(ContextConfig cc) {
      String anticheese = cc.getAnticheeseKey();
      if (anticheese != null) {
         AnticheeseData saved = com.tann.dice.Main.getSettings().getSavedAnticheese(anticheese);
         if (saved != null && saved.getSaveState() != null) {
            SaveState ss = saved.getSaveState().toState();
            if (!cc.antiCheeseHeroes()) {
               ss.dungeonContext.setParty(cc.getStartingParty(PartyLayoutType.random(), AntiCheeseRerollInfo.makeBlank()));
            }

            ss.start();
            return true;
         }
      }

      return false;
   }
}

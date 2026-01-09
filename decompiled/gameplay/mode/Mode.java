package com.tann.dice.gameplay.mode;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.tann.dice.gameplay.battleTest.Zone;
import com.tann.dice.gameplay.context.DungeonContext;
import com.tann.dice.gameplay.context.config.ContextConfig;
import com.tann.dice.gameplay.leaderboard.Leaderboard;
import com.tann.dice.gameplay.leaderboard.LeaderboardBlob;
import com.tann.dice.gameplay.mode.chooseParty.ChoosePartyMode;
import com.tann.dice.gameplay.mode.creative.WishMode;
import com.tann.dice.gameplay.mode.creative.custom.CustomMode;
import com.tann.dice.gameplay.mode.creative.pastey.PasteMode;
import com.tann.dice.gameplay.mode.cursey.BlursedMode;
import com.tann.dice.gameplay.mode.cursey.BlurtraMode;
import com.tann.dice.gameplay.mode.cursey.BlyptraMode;
import com.tann.dice.gameplay.mode.cursey.CurseMode;
import com.tann.dice.gameplay.mode.cursey.CurseModeHyper;
import com.tann.dice.gameplay.mode.cursey.CurseModeUltra;
import com.tann.dice.gameplay.mode.debuggy.BalanceMode;
import com.tann.dice.gameplay.mode.debuggy.CustomFightMode;
import com.tann.dice.gameplay.mode.debuggy.DebugMode;
import com.tann.dice.gameplay.mode.debuggy.EmptyMode;
import com.tann.dice.gameplay.mode.debuggy.PickMode;
import com.tann.dice.gameplay.mode.debuggy.SavesMode;
import com.tann.dice.gameplay.mode.general.AlternateHeroesMode;
import com.tann.dice.gameplay.mode.general.ClassicMode;
import com.tann.dice.gameplay.mode.general.DemoMode;
import com.tann.dice.gameplay.mode.general.DreamMode;
import com.tann.dice.gameplay.mode.general.GenerateHeroesMode;
import com.tann.dice.gameplay.mode.general.InstantMode;
import com.tann.dice.gameplay.mode.general.LootMode;
import com.tann.dice.gameplay.mode.general.RaidMode;
import com.tann.dice.gameplay.mode.general.ShortcutMode;
import com.tann.dice.gameplay.mode.general.nightmare.NightmareMode;
import com.tann.dice.gameplay.mode.meta.folder.BasicFolderMode;
import com.tann.dice.gameplay.mode.meta.folder.FolderType;
import com.tann.dice.gameplay.mode.meta.folder.RootFolder;
import com.tann.dice.gameplay.modifier.Modifier;
import com.tann.dice.gameplay.phase.endPhase.statsPanel.GameEndStatsPanel;
import com.tann.dice.gameplay.progress.chievo.AchLib;
import com.tann.dice.gameplay.progress.chievo.Achievement;
import com.tann.dice.gameplay.progress.chievo.unlock.UnUtil;
import com.tann.dice.gameplay.progress.chievo.unlock.Unlockable;
import com.tann.dice.gameplay.save.RunHistory;
import com.tann.dice.gameplay.save.SaveState;
import com.tann.dice.gameplay.trigger.global.Global;
import com.tann.dice.screens.dungeon.panels.book.page.ledgerPage.AchievementIconView;
import com.tann.dice.screens.titleScreen.GameStart;
import com.tann.dice.statics.ImageUtils;
import com.tann.dice.statics.Images;
import com.tann.dice.statics.sound.Sounds;
import com.tann.dice.util.Colours;
import com.tann.dice.util.ImageActor;
import com.tann.dice.util.LockOverlay;
import com.tann.dice.util.Pixl;
import com.tann.dice.util.Tann;
import com.tann.dice.util.listener.TannListener;
import com.tann.dice.util.saves.Prefs;
import com.tann.dice.util.tp.TP;
import com.tann.dice.util.ui.ScrollPane;
import com.tann.dice.util.ui.TextWriter;
import com.tann.dice.util.ui.standardButton.StandardButton;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public abstract class Mode implements Unlockable {
   private static List<Mode> allModes = new ArrayList<>();
   private static List<Mode> playableModes = new ArrayList<>();
   public static ClassicMode CLASSIC;
   public static DebugMode DEBUG;
   public static CurseMode CURSE;
   public static BlursedMode BLURSED;
   public static BlurtraMode BLURTRA;
   public static BlyptraMode BLYPTRA;
   public static CurseModeUltra CURSED_ULTRA;
   public static ChoosePartyMode CHOOSE_PARTY;
   public static InstantMode INSTANT;
   public static ShortcutMode SHORTCUT;
   public static DemoMode DEMO;
   public static CurseModeHyper CURSE_HYPER;
   public static LootMode LOOT;
   public static GenerateHeroesMode GENERATE_HEROES;
   public static AlternateHeroesMode ALTERNATE_HEROES;
   public static RaidMode RAID;
   public static PasteMode PASTE;
   public static BalanceMode BALANCE;
   public static PickMode PICK;
   public static CustomFightMode CUSTOM_FIGHT;
   public static CustomMode CUSTOM;
   public static DreamMode DREAM;
   public static RootFolder ROOT;
   public static NightmareMode NIGHTMARE;
   public static WishMode WISH;
   public static SavesMode SAVES;
   public static EmptyMode EMPTY;
   protected final String name;
   private List<ContextConfig> configs;

   protected Mode(String name) {
      this.name = name;
   }

   public static void init() {
      allModes = new ArrayList<>();
      playableModes = new ArrayList<>();
      addMode(DEMO = new DemoMode());
      addMode(CLASSIC = new ClassicMode());
      addMode(SHORTCUT = new ShortcutMode());
      addMode(CHOOSE_PARTY = new ChoosePartyMode());
      addMode(LOOT = new LootMode());
      addMode(RAID = new RaidMode());
      addMode(GENERATE_HEROES = new GenerateHeroesMode());
      addMode(ALTERNATE_HEROES = new AlternateHeroesMode());
      addMode(NIGHTMARE = new NightmareMode());
      addMode(DREAM = new DreamMode());
      addMode(PASTE = new PasteMode());
      addMode(CUSTOM = new CustomMode());
      addMode(CURSE = new CurseMode());
      addMode(BLURSED = new BlursedMode());
      addMode(CURSE_HYPER = new CurseModeHyper());
      addMode(CURSED_ULTRA = new CurseModeUltra());
      addMode(BLURTRA = new BlurtraMode());
      addMode(BLYPTRA = new BlyptraMode());
      addMode(INSTANT = new InstantMode());
      addMode(BALANCE = new BalanceMode());
      addMode(PICK = new PickMode());
      addMode(CUSTOM_FIGHT = new CustomFightMode());
      addMode(WISH = new WishMode());
      addMode(SAVES = new SavesMode());
      addMode(EMPTY = new EmptyMode());
      addMode(DEBUG = new DebugMode());

      for (Mode m : BasicFolderMode.makeAll()) {
         addMode(m);
      }

      addMode(ROOT = new RootFolder());
   }

   public Color getColour() {
      return Colours.pink;
   }

   private static void addMode(Mode mode) {
      allModes.add(mode);
      if (mode.isPlayable()) {
         playableModes.add(mode);
      }
   }

   public static List<Mode> getAllModes() {
      return allModes;
   }

   protected static List<Mode> getAllModesOmni() {
      List<Mode> pm = new ArrayList<>(getPlayableModes());

      for (int i = pm.size() - 1; i >= 0; i--) {
         if (pm.get(i).getFolderType() != null || pm.get(i).isDebug()) {
            pm.remove(i);
         }
      }

      return pm;
   }

   public static List<Mode> getPlayableModes() {
      return playableModes;
   }

   public static List<Mode> getAllSaveBearingModes() {
      List<Mode> tmp = new ArrayList<>();

      for (Mode m : allModes) {
         if (m.getSaveKey() != null) {
            tmp.add(m);
         }
      }

      return tmp;
   }

   public static List<ContextConfig> getAllSaveBearingConfigs() {
      List<ContextConfig> contextConfigs = new ArrayList<>();

      for (Mode m : getAllSaveBearingModes()) {
         contextConfigs.addAll(m.getConfigs());
      }

      return contextConfigs;
   }

   public Actor makeStartGameDisplay() {
      List<String> descList = new ArrayList<>();
      String[] lines = this.getDescriptionLines();
      if (lines != null) {
         descList.addAll(Arrays.asList(lines));
      }

      if (this.skipStats()) {
         descList.add("no stats");
      }

      if (this.getConfigs().size() > 0) {
         ContextConfig cc = this.configs.get(0);

         for (Modifier m : cc.getStartingModifiers()) {
            for (Global gt : m.getGlobals()) {
               String desc = gt.describeForMode();
               if (desc != null) {
                  descList.add(desc);
               }
            }
         }

         for (Global gtx : cc.getModeGlobals()) {
            String desc = gtx.describeForMode();
            if (desc != null) {
               descList.add(desc);
            }
         }
      }

      if (descList.size() == 0) {
         descList.add("no description?");
      }

      return this.makeStandardDisplay(this.getColour(), this.name, descList);
   }

   public List<ContextConfig> getConfigs() {
      if (this.configs == null) {
         this.configs = this.makeAllConfigs();
      }

      return this.configs;
   }

   protected List<Actor> getLeftOfTitleActors() {
      return new ArrayList<>();
   }

   public String[] getDescriptionLines() {
      return new String[0];
   }

   protected abstract List<ContextConfig> makeAllConfigs();

   public StandardButton makeModeSelectButton() {
      boolean locked = UnUtil.isLocked(this);
      String text = locked ? "      " : this.getTextButtonName();
      StandardButton tb = new StandardButton(text);
      if (locked) {
         new LockOverlay(tb, false);
      }

      return tb;
   }

   public abstract String getSaveKey();

   public boolean hasSave() {
      return this.getSaveKey() == null ? false : Prefs.getString(this.getSaveKey(), null) != null;
   }

   public boolean isPlayable() {
      return this.getFolderType() != FolderType.debug && this.getFolderType() != FolderType.unfinished;
   }

   public final Actor makeStartGameCard() {
      return this.makeStartGameCard(this.getConfigs());
   }

   public Actor makeStartGameCard(List<ContextConfig> all) {
      ContextConfig first = all.get(0);
      List<ContextConfig> unlocked = new ArrayList<>(all);

      for (int i = unlocked.size() - 1; i >= 0; i--) {
         if (unlocked.get(i).isLocked()) {
            unlocked.remove(i);
         }
      }

      Actor example = first.makeStartButton(true);
      boolean big = example != null && com.tann.dice.Main.width * 0.87F > unlocked.size() * example.getWidth();
      int gap = (int)((big ? 6 : 4) * (com.tann.dice.Main.isPortrait() ? 1.5F : 1.0F));
      Pixl main = new Pixl(gap);
      main.row(0);

      for (int ix = 0; ix < unlocked.size(); ix++) {
         if (ix == 10) {
            main.row();
         }

         final ContextConfig cc = unlocked.get(ix);
         Pixl p = new Pixl(0);
         if (!cc.skipStats()) {
            p.actor(this.makeWinsActor(cc));
         }

         p.row(4);
         StandardButton button = cc.makeStartButton(big);
         p.actor(button);
         button.setRunnable(new Runnable() {
            @Override
            public void run() {
               Mode.this.onStartButtonPress(cc);
            }
         });
         if (com.tann.dice.Main.isPortrait()) {
            main.actor(p.pix(), com.tann.dice.Main.width * 0.75F);
         } else {
            main.actor(p.pix());
         }
      }

      Actor loadButton = SaveState.getLoadButton(first.getGeneralSaveKey());
      if (loadButton != null) {
         main.row().actor(loadButton);
      }

      return main.pix();
   }

   protected void onStartButtonPress(ContextConfig cc) {
      GameStart.startWithPLTChoice(cc, null, false);
   }

   public Actor makeWinsActor(final ContextConfig config) {
      int wins = config.getWins();
      TextureRegion wreath = Images.wreath;
      Actor a;
      if (wins == 0) {
         a = new Actor();
         a.setSize(wreath.getRegionWidth(), wreath.getRegionHeight());
      } else {
         Group g = Tann.makeGroup();
         ImageActor ia = new ImageActor(wreath);
         g.setSize(ia.getWidth(), ia.getHeight());
         g.addActor(ia);
         if (wins > 1) {
            TextWriter tw = new TextWriter("[yellow]" + wins);
            g.addActor(tw);
            tw.setPosition((int)(g.getWidth() / 2.0F - tw.getWidth() / 2.0F), (int)(g.getHeight() / 2.0F - tw.getHeight() / 2.0F));
         }

         a = g;
      }

      if (config.getWins() > 0) {
         a.addListener(new TannListener() {
            @Override
            public boolean info(int button, float x, float y) {
               int winsx = config.getWins();
               int losses = config.getLosses();
               if (winsx + losses == 0) {
                  return true;
               } else {
                  Pixl p = new Pixl(2, 3).border(Colours.yellow);
                  p.text(config.getEndTitle()).row().text(Mode.getWinsString(winsx, losses)).row();
                  int currentStreak = config.getStreak(false);
                  int bestStreak = config.getStreak(true);
                  if (bestStreak > 0) {
                     p.text("[purple]Streak: " + currentStreak + " (best " + bestStreak + ")");
                  }

                  Actor ax = Mode.this.getHistoryButtonIfRelevant(config);
                  if (ax != null) {
                     p.row().actor(ax);
                  }

                  for (Leaderboard leaderboard : LeaderboardBlob.all) {
                     if (!UnUtil.isLocked(leaderboard)) {
                        boolean valid = leaderboard.validForModeInfo(config);
                        if (valid) {
                           p.actor(leaderboard.makeInfoActor(TextWriter.getTag(leaderboard.getCol()) + "Leaderboard"));
                        }
                     }
                  }

                  Group g = p.pix(8);
                  com.tann.dice.Main.getCurrentScreen().pushAndCenter(g);
                  return true;
               }
            }
         });
      }

      return a;
   }

   public static String getWinsString(int wins, int losses) {
      String colTag = wins > 0 ? "[green]" : "[text]";
      float ratio = (float)wins / (wins + losses);
      String ratioString = Math.round(ratio * 100.0F) + "%";
      return "[notranslate]" + colTag + com.tann.dice.Main.t("Wins: ") + wins + "/" + (losses + wins) + " (" + ratioString + ")";
   }

   private Actor getHistoryButtonIfRelevant(List<ContextConfig> configs) {
      final List<RunHistory> runs = new ArrayList<>();

      for (ContextConfig config : configs) {
         runs.addAll(com.tann.dice.Main.self().masterStats.getRunHistoryStore().getRuns(config));
      }

      if (runs.size() > 0) {
         Collections.sort(runs, new Comparator<RunHistory>() {
            public int compare(RunHistory o1, RunHistory o2) {
               return (int)(o1.getDate() - o2.getDate());
            }
         });
         StandardButton tb = new StandardButton("History");
         tb.setRunnable(new Runnable() {
            @Override
            public void run() {
               Actor runsActor = new Pixl(3, 3).border(Colours.grey).actor(RunHistory.makeGroup(runs)).pix();
               ScrollPane sp = Tann.makeScrollpane(runsActor);
               sp.setSize(runsActor.getWidth() + 6.0F, (int)Math.min(runsActor.getHeight(), com.tann.dice.Main.height * 0.9F));
               com.tann.dice.Main.getCurrentScreen().pushAndCenter(sp);
            }
         });
         return tb;
      } else {
         return null;
      }
   }

   private Actor getHistoryButtonIfRelevant(ContextConfig config) {
      return this.getHistoryButtonIfRelevant(Arrays.asList(config));
   }

   public List<Actor> getEndInfo(DungeonContext context, int previousFurthestReached, boolean victory) {
      List<Actor> result = new ArrayList<>();
      String reached = context.getLevelProgressString(true);
      if (reached != null) {
         result.add(new TextWriter(reached));
      }

      if (victory && previousFurthestReached == context.getCurrentLevelNumber()) {
         int currentStreak = context.getContextConfig().getStreak(false);
         int bestStreak = context.getContextConfig().getStreak(true);
         result.add(new TextWriter("[text]Current streak: " + currentStreak));
         result.add(new TextWriter("[text]Best streak: " + bestStreak));
      } else if (previousFurthestReached > 1 && previousFurthestReached < 20) {
         result.add(new TextWriter("[text]Previous best: " + previousFurthestReached));
         if (previousFurthestReached < context.getCurrentLevelNumber()) {
            result.add(new TextWriter("[yellow](new record!)"));
         }
      }

      for (Leaderboard l : LeaderboardBlob.all) {
         if (!UnUtil.isLocked(l) && l.validForEndCard(context.getContextConfig()) && l.canSubmitBetterScore()) {
            result.add(l.getSubmitHighscoreButton());
            break;
         }
      }

      return result;
   }

   public List<Actor> getEndOptions(final DungeonContext dungeonContext, boolean victory) {
      List<Actor> result = new ArrayList<>();
      StandardButton quit = new StandardButton(dungeonContext.getVictoryString());
      quit.setRunnable(new Runnable() {
         @Override
         public void run() {
            Sounds.playSound(Sounds.pip);
            ContextConfig cc = dungeonContext.getContextConfig();
            cc.clearSave();
            cc.quitAction();
         }
      });
      result.add(quit);
      result.add(GameEndStatsPanel.makeStatsButton(dungeonContext, victory));
      return result;
   }

   public static List<TP<Zone, Integer>> getStandardLevelTypes() {
      return Arrays.asList(new TP<>(Zone.Forest, 4), new TP<>(Zone.Dungeon, 4), new TP<>(Zone.Catacombs, 4), new TP<>(Zone.Lair, 4), new TP<>(Zone.Pit, 4));
   }

   public static int[] getStandardBossLevels() {
      return new int[]{4, 8, 12, 16, 20};
   }

   @Override
   public Actor makeUnlockActor(boolean big) {
      return com.tann.dice.Main.demo
         ? new Pixl(0, 3).text(this.getTextButtonName()).row(3).text("[grey](full game only)").pix()
         : new Pixl(0, 3).text(this.getTextButtonName()).pix();
   }

   @Override
   public TextureRegion getAchievementIcon() {
      return null;
   }

   @Override
   public String getAchievementIconString() {
      return TextWriter.getTag(this.getColour()) + "M";
   }

   public String getName() {
      return this.name;
   }

   public String getTextButtonName() {
      return TextWriter.getTag(this.getColour()) + this.getName() + "[cu]";
   }

   public void showModeInfo() {
      if (!this.skipMetaInfoOnNameClick()) {
         Sounds.playSound(Sounds.pip);
         Actor a = this.makeMoreInfoActor();
         com.tann.dice.Main.getCurrentScreen().push(a, 0.8F);
         Tann.center(a);
      }
   }

   private Actor makeMoreInfoActor() {
      int WIDTH = (int)Math.min(130.0F, com.tann.dice.Main.width * 0.9F);
      List<ContextConfig> cc = this.getConfigs();
      Pixl p = new Pixl(5, 5).border(this.getColour());
      p.text(this.getTextButtonName() + " [text]mode").row();
      String extraDesc = this.getExtraDescription();
      if (extraDesc != null) {
         p.text(extraDesc, WIDTH).row();
      }

      if (!this.skipStats()) {
         int wins = 0;
         int losses = 0;

         for (ContextConfig config : cc) {
            wins += config.getWins();
            losses += config.getLosses();
         }

         p.text(getWinsString(wins, losses)).row();
         Actor a = this.getHistoryButtonIfRelevant(cc);
         if (a != null) {
            p.actor(a).row();
         }
      }

      List<Actor> leaderboardActors = new ArrayList<>();

      for (Leaderboard leaderboard : LeaderboardBlob.all) {
         if (!UnUtil.isLocked(leaderboard)) {
            boolean valid = false;

            for (ContextConfig contextConfig : cc) {
               valid |= leaderboard.validForModeInfo(contextConfig);
            }

            if (valid) {
               leaderboardActors.add(leaderboard.makeInfoActor());
            }
         }
      }

      if (!leaderboardActors.isEmpty()) {
         Pixl lp = new Pixl(3, 4).border(Colours.blue).text("[blue]Leaderboards").row();

         for (Actor leaderboardActor : leaderboardActors) {
            lp.actor(leaderboardActor, WIDTH);
         }

         p.actor(lp.pix()).row();
      }

      leaderboardActors = new ArrayList<>();

      for (Achievement achievement : AchLib.getAll()) {
         if (achievement.isChallenge() && achievement.isCompletable() && achievement.forSpecificMode(this)) {
            leaderboardActors.add(achievement);
         }
      }

      Collections.sort(leaderboardActors, new Comparator<Achievement>() {
         public int compare(Achievement o1, Achievement o2) {
            return (int)Math.signum(o1.getDifficulty() - o2.getDifficulty());
         }
      });
      if (!leaderboardActors.isEmpty()) {
         Pixl lp = new Pixl(3, 2).border(Colours.orange).text("[orange]challenges").row();

         for (Achievement achievementx : leaderboardActors) {
            lp.actor(new AchievementIconView(achievementx), WIDTH);
         }

         p.actor(lp.pix());
      }

      return p.pix();
   }

   protected String getExtraDescription() {
      return null;
   }

   protected Actor makeStandardDisplay(Color color, String title, List<String> descriptionsLines) {
      String tag = TextWriter.getTag(color);
      String prefix = descriptionsLines.size() == 1 ? "" : "- ";
      Pixl descPix = new Pixl(2, 3).border(Colours.dark, color, 1);
      int textMaxWidth = this.shortText() ? 75 : 140;
      List<String> translatedDescriptionLines = new ArrayList<>(descriptionsLines.size());

      for (String s : descriptionsLines) {
         translatedDescriptionLines.add(com.tann.dice.Main.t(s));
      }

      descPix.multiText(Tann.prefixAll(translatedDescriptionLines, "[notranslate][text]" + prefix), textMaxWidth);
      List<Actor> acs = this.extraDescActors();
      if (acs.size() > 0) {
         descPix.row();

         for (Actor extraDescActor : acs) {
            descPix.actor(extraDescActor, textMaxWidth);
         }
      }

      Actor tw = descPix.pix(8);
      tw.setTouchable(Touchable.childrenOnly);
      Pixl topPix = new Pixl(8);
      float mw = com.tann.dice.Main.width * 0.9F;
      if (!com.tann.dice.Main.isPortrait()) {
         for (Actor a : this.getLeftOfTitleActors()) {
            topPix.actor(a, mw);
         }
      }

      if (!com.tann.dice.Main.demo) {
         TextWriter modeNameActor = new TextWriter("[b]" + tag + title, 5000, color, 4);
         Pixl pp = new Pixl().actor(modeNameActor);
         if (!this.skipShowTitleDesc()) {
            tw.addListener(new TannListener() {
               @Override
               public boolean action(int button, int pointer, float x, float y) {
                  Mode.this.showModeInfo();
                  return true;
               }
            });
            pp.row(-1).actor(tw);
         }

         Actor titleActor = pp.pix();
         titleActor.setTouchable(Touchable.childrenOnly);
         topPix.actor(titleActor);
         modeNameActor.addListener(new TannListener() {
            @Override
            public boolean action(int button, int pointer, float x, float y) {
               Mode.this.showModeInfo();
               return true;
            }
         });
      }

      if (com.tann.dice.Main.isPortrait()) {
         topPix.row();

         for (Actor a : this.getLeftOfTitleActors()) {
            topPix.actor(a, mw);
         }
      }

      Actor topActor = topPix.pix();
      topActor.setTouchable(Touchable.childrenOnly);
      Pixl p = new Pixl();
      p.actor(topActor);
      Actor a = this.makeStartGameCard();
      if (a != null) {
         a.setTouchable(Touchable.childrenOnly);
         int aWidth = (int)a.getWidth();
         int aHeight = (int)a.getHeight();
         int maxWidth = (int)(com.tann.dice.Main.width * 0.98F);
         int maxHeight = com.tann.dice.Main.height - 60;
         if (aWidth > maxWidth) {
            a = Tann.makeScrollpane(a);
            a.setSize(maxWidth, aHeight + 6);
         } else if (aHeight > maxHeight) {
            a = Tann.makeScrollpane(a);
            a.setSize(aWidth + 6, maxHeight - 30);
         }

         p.row(5).actor(a);
      }

      a = p.pix();
      a.setTouchable(Touchable.childrenOnly);
      return a;
   }

   private boolean shortText() {
      return this.getLeftOfTitleActors().size() > 0;
   }

   protected List<Actor> extraDescActors() {
      return new ArrayList<>();
   }

   public boolean skipShowTitleDesc() {
      return false;
   }

   public boolean displayPopup() {
      return true;
   }

   public static Mode getModeFromString(String name) {
      for (Mode m : allModes) {
         if (m.getName().equals(name)) {
            return m;
         }
      }

      return null;
   }

   public static TextureRegion getBackgroundFromFolderType(FolderType ft) {
      if (ft == null) {
         return ImageUtils.loadExtBig("ui/titleTemple");
      } else {
         switch (ft) {
            case creative:
               return ImageUtils.loadExtBig("ui/titleChurch");
            case cursed:
               return ImageUtils.loadExtBig("ui/titleLighthouse");
            case debug:
            case crappy:
            case unfinished:
               return ImageUtils.loadExtBig("ui/titleOld");
            default:
               return ImageUtils.loadExtBig("ui/titleTemple");
         }
      }
   }

   public TextureRegion getBackground() {
      return getBackgroundFromFolderType(this.getFolderType());
   }

   public boolean basicTitleBackground() {
      return false;
   }

   public boolean showMinimap() {
      return true;
   }

   public boolean skipStats() {
      return false;
   }

   public boolean skipFromMainList() {
      if (this.isDebug()) {
         return true;
      } else {
         return !this.isPlayable() ? true : this.getFolderType() != null;
      }
   }

   public boolean isDebug() {
      return false;
   }

   public Mode getParent() {
      FolderType ft = this.getFolderType();
      if (ft != null) {
         return BasicFolderMode.get(ft);
      } else {
         Mode m = ROOT;
         return UnUtil.isLocked(m) ? null : m;
      }
   }

   public boolean skipUnlockNotify() {
      return false;
   }

   public FolderType getFolderType() {
      return null;
   }

   public long getBannedCollisionBits() {
      return 0L;
   }

   public boolean disablePartyLayout() {
      return false;
   }

   public boolean skipShowBoss() {
      return false;
   }

   public boolean skipMetaInfoOnNameClick() {
      return !com.tann.dice.Main.getSettings().hasAttemptedLevel() ? true : this.getFolderType() == FolderType.creative;
   }

   public String getRawSave() {
      if (!this.hasSave()) {
         return null;
      } else {
         String sk = this.getSaveKey();
         return sk == null ? null : Prefs.getString(sk, null);
      }
   }
}

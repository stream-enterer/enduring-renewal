package com.tann.dice.gameplay.context;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.tann.dice.gameplay.battleTest.Zone;
import com.tann.dice.gameplay.content.ent.Hero;
import com.tann.dice.gameplay.content.ent.group.Party;
import com.tann.dice.gameplay.content.ent.type.HeroType;
import com.tann.dice.gameplay.content.ent.type.lib.HeroTypeLib;
import com.tann.dice.gameplay.content.item.Item;
import com.tann.dice.gameplay.content.item.ItemLib;
import com.tann.dice.gameplay.context.config.ContextConfig;
import com.tann.dice.gameplay.context.config.ContextConfigUtils;
import com.tann.dice.gameplay.fightLog.FightLog;
import com.tann.dice.gameplay.fightLog.Snapshot;
import com.tann.dice.gameplay.level.Level;
import com.tann.dice.gameplay.mode.Mode;
import com.tann.dice.gameplay.mode.creative.WishMode;
import com.tann.dice.gameplay.modifier.Modifier;
import com.tann.dice.gameplay.modifier.ModifierLib;
import com.tann.dice.gameplay.modifier.ModifierType;
import com.tann.dice.gameplay.modifier.ModifierUtils;
import com.tann.dice.gameplay.modifier.SmallModifierPanel;
import com.tann.dice.gameplay.phase.Phase;
import com.tann.dice.gameplay.phase.PhaseManager;
import com.tann.dice.gameplay.phase.endPhase.runEnd.RunEndPhase;
import com.tann.dice.gameplay.phase.levelEndPhase.LevelEndPhase;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.ChoicePhase;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.ItemRewardUtils;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.choosable.Choosable;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.choosable.ChoosableUtils;
import com.tann.dice.gameplay.progress.StatSnapshot;
import com.tann.dice.gameplay.progress.stats.DungeonStatManager;
import com.tann.dice.gameplay.progress.stats.stat.Stat;
import com.tann.dice.gameplay.progress.stats.stat.StatLib;
import com.tann.dice.gameplay.progress.stats.stat.pickRate.BitStat;
import com.tann.dice.gameplay.progress.stats.stat.pickRate.PickStat;
import com.tann.dice.gameplay.save.DungeonContextData;
import com.tann.dice.gameplay.save.LevelData;
import com.tann.dice.gameplay.save.SaveState;
import com.tann.dice.gameplay.save.antiCheese.AnticheeseData;
import com.tann.dice.gameplay.save.settings.option.BOption;
import com.tann.dice.gameplay.save.settings.option.OptionLib;
import com.tann.dice.gameplay.trigger.global.Global;
import com.tann.dice.gameplay.trigger.global.GlobalAddHero;
import com.tann.dice.gameplay.trigger.global.changeHero.GlobalChangeHeroAll;
import com.tann.dice.gameplay.trigger.global.changeHero.GlobalChangeHeroPos;
import com.tann.dice.gameplay.trigger.global.pool.monster.GlobalClearPoolMonster;
import com.tann.dice.gameplay.trigger.global.weird.GlobalTemporary;
import com.tann.dice.gameplay.trigger.personal.Personal;
import com.tann.dice.screens.Screen;
import com.tann.dice.screens.dungeon.DungeonScreen;
import com.tann.dice.screens.dungeon.DungeonUtils;
import com.tann.dice.screens.dungeon.panels.entPanel.choosablePanel.ModifierPanel;
import com.tann.dice.statics.sound.Sounds;
import com.tann.dice.util.Colours;
import com.tann.dice.util.ImageActor;
import com.tann.dice.util.Pixl;
import com.tann.dice.util.Tann;
import com.tann.dice.util.TannLog;
import com.tann.dice.util.lang.Words;
import com.tann.dice.util.listener.TannListener;
import com.tann.dice.util.tp.TP;
import com.tann.dice.util.ui.ScrollPane;
import com.tann.dice.util.ui.TextWriter;
import com.tann.dice.util.ui.resolver.ModifierResolver;
import com.tann.dice.util.ui.standardButton.StandardButton;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public final class DungeonContext {
   private Party party;
   private List<Modifier> currentModifiers = new ArrayList<>();
   private List<DungeonValue> dungeonValues = new ArrayList<>();
   private int startLevel;
   private int seed;
   int currentLevel;
   Level thisLevel;
   List<Level> previousLevels = new ArrayList<>();
   ContextConfig contextConfig;
   DungeonStatManager statsManager;
   private boolean checkedItems;
   public Long lockedSecondsTaken;
   public long extraSeconds;
   public long thisStartTime;
   private static final int MAX_ITEMS = 36;
   private static final int MAX_LEVELUP_OPTIONS = 36;
   List<Global> cachedModifierGlobals;
   private List<Modifier> antiCheeseRerollModifiers = new ArrayList<>();

   public DungeonContext(ContextConfig contextConfig, Party party) {
      this(contextConfig, party, 1);
   }

   public DungeonContext(ContextConfig contextConfig, Party party, int currentLevelNumber) {
      this(contextConfig, party, currentLevelNumber, null);
   }

   public DungeonContext(ContextConfig contextConfig, Party party, int currentLevelNumber, Level currentLevel) {
      this(contextConfig, party, currentLevelNumber, currentLevel, new ArrayList<>());
   }

   public DungeonContext(ContextConfig contextConfig, Party party, int currentLevelNumber, Level currentLevel, List<DungeonValue> dvs) {
      this.contextConfig = contextConfig;
      this.dungeonValues = dvs;
      this.party = party;
      this.currentLevel = currentLevelNumber;
      this.startLevel = currentLevelNumber;
      this.thisLevel = currentLevel;
      this.reSeed();
      this.checkedItems = party.getItems(false).size() == 0;
      this.statsManager = new DungeonStatManager(this);
      if (currentLevel == null) {
         this.onFirstInit();
      }
   }

   private void onFirstInit() {
      boolean skipinit = this.contextConfig.skipFirstPartyInit();

      for (Modifier m : this.contextConfig.getStartingModifiers()) {
         this.addModifier(m, !skipinit);
      }

      if (!skipinit) {
         this.party.onFirstInit(this);
      }
   }

   public boolean skipStats() {
      return this.contextConfig.skipStats();
   }

   private void reSeed() {
      this.seed = (int)(Math.random() * 1000.0);
   }

   public Party getParty() {
      return this.party;
   }

   public int getTotalLength() {
      int base = this.contextConfig.getTotalLength();

      for (Global modifierGlobal : this.getModifierGlobalsIncludingLinked()) {
         base = modifierGlobal.affectTotalLength(base);
      }

      return base;
   }

   public Level getCurrentLevel() {
      if (this.thisLevel == null) {
         this.thisLevel = this.contextConfig.makeNextLevel(this.currentLevel, this.previousLevels, this);
      }

      return this.thisLevel;
   }

   public boolean isAtLastLevel() {
      return this.currentLevel >= this.getTotalLength();
   }

   public void addPhasesFromCurrentLevel(List<Phase> phases) {
      for (Global gt : new ArrayList<>(this.getModifierGlobalsIncludingLinked())) {
         phases.addAll(gt.getPhases(this));
         gt.affectPhasesPost(this.currentLevel, this, phases);
      }
   }

   public List<Choosable> getLootForPreviousLevel() {
      int lvl = this.getCurrentMod20LevelNumber();
      int itemQuality = ItemLib.getStandardItemQualityFor(lvl);
      return this.getLootForPreviousLevel(itemQuality);
   }

   public List<Choosable> getLootForPreviousLevel(int itemQuality) {
      int attempts = 10;
      int amtLoot = this.getNumLootItems() + 1;

      for (int i = 0; i < 10; i++) {
         List<Choosable> result = this.generateLoot(itemQuality, amtLoot, true);
         List<Item> items = ChoosableUtils.fetchItems(result);
         if (!ItemLib.collides(items)) {
            return result;
         }
      }

      TannLog.error("Failed to generate uncolliding set");
      return this.generateLoot(itemQuality, amtLoot, true);
   }

   public int getNumLootItems() {
      int base = 2;

      for (Global allActiveGlobal : this.getAllActiveGlobalsUncached()) {
         base = allActiveGlobal.affectLootQuantity(base);
      }

      return Math.min(36, base);
   }

   public int getLevelupOptions(int baseChoices) {
      for (Global allActiveGlobal : this.getAllActiveGlobalsUncached()) {
         baseChoices = allActiveGlobal.affectLevelupChoices(baseChoices);
      }

      return Math.min(36, baseChoices);
   }

   private List<Choosable> generateLoot(int itemQuality, int amt, boolean lastRandom) {
      itemQuality = this.getLootBonusLevel(itemQuality);
      if (itemQuality < -7) {
         return new ArrayList<>(this.generateFailsafeItems(itemQuality, amt));
      } else {
         List<Choosable> result = new ArrayList<>();
         List<Item> alreadyAdded = new ArrayList<>();
         int genAmt = amt;
         if (lastRandom) {
            genAmt = amt - 1;
         }

         List<Item> items = ItemLib.randomWithExactQuality(genAmt, itemQuality, this, alreadyAdded);
         alreadyAdded.addAll(items);
         result.addAll(items);
         if (lastRandom) {
            result.add(ItemRewardUtils.getFinalReward(itemQuality, this));
         }

         return result;
      }
   }

   private List<Item> generateFailsafeItems(int itemQuality, int amt) {
      List<Item> items = new ArrayList<>();

      for (int i = 0; i < amt; i++) {
         items.add(ItemLib.byName("dead crow.tier." + itemQuality + ".n." + Tann.randomString(5)));
      }

      return items;
   }

   public List<Global> getModifierGlobals() {
      if (this.cachedModifierGlobals == null) {
         this.cachedModifierGlobals = new ArrayList<>();
         this.cachedModifierGlobals.addAll(this.contextConfig.getModeGlobals());

         for (Modifier se : this.currentModifiers) {
            this.cachedModifierGlobals.addAll(se.getGlobals());
         }
      }

      return this.cachedModifierGlobals;
   }

   public List<Global> getAllActiveGlobals(boolean hero, boolean link) {
      List<Global> result = new ArrayList<>(this.getModifierGlobals());
      if (hero) {
         for (Hero h : this.party.getHeroes()) {
            for (Personal pt : h.getBlankState().getActivePersonals()) {
               Global gt = pt.getGlobalFromPersonalTrigger();
               if (gt != null) {
                  result.add(gt);
               }
            }
         }
      }

      if (link) {
         Snapshot.addLinked(result, this.currentLevel, this, -1);
      }

      return result;
   }

   public List<Global> getModifierGlobalsIncludingLinked() {
      return this.getAllActiveGlobals(false, true);
   }

   public List<Global> getAllActiveGlobalsUncached() {
      return this.getAllActiveGlobals(true, true);
   }

   public int getLootBonusLevel(int itemQuality) {
      int result = itemQuality;

      for (Global g : this.getAllActiveGlobalsUncached()) {
         result = g.affectGlobalLootQuality(result);
      }

      return result;
   }

   public int getCurrentLevelNumber() {
      return this.currentLevel;
   }

   public void nextLevel() {
      this.cachedModifierGlobals = null;
      this.currentLevel++;
      if (this.thisLevel != null) {
         this.previousLevels.add(this.getCurrentLevel());

         while (this.previousLevels.size() > 2) {
            this.previousLevels.remove(0);
         }
      }

      this.thisLevel = this.contextConfig.makeNextLevel(this.currentLevel, this.previousLevels, this);
   }

   public String getVictoryString() {
      return "Quit";
   }

   public DungeonContextData toData() {
      List<LevelData> previousData = new ArrayList<>();

      for (Level l : this.previousLevels) {
         previousData.add(new LevelData(l));
      }

      return new DungeonContextData(
         this.contextConfig.classNameSerialise(),
         this.contextConfig.serialise(),
         this.party.toSave(),
         this.currentLevel,
         this.startLevel,
         this.getCurrentModifiersStrings(),
         this.getNonZeroStats(),
         this.getTimeTakenSeconds(),
         new LevelData(this.thisLevel),
         previousData,
         this.seed,
         this.checkedItems,
         this.dungeonValues
      );
   }

   public Map<String, Integer> getNonZeroStatMap() {
      Map<String, Integer> result = new HashMap<>();

      for (Stat s : this.getNonZeroStats()) {
         result.put(s.getName(), s.getValue());
      }

      return result;
   }

   public List<TP<String, String>> getStatChanges(Map<String, Integer> prevNonZeroMap) {
      if (prevNonZeroMap == null) {
         System.out.println("no previous map?");
         return new ArrayList<>();
      } else {
         Map<String, Integer> current = this.getNonZeroStatMap();
         List<TP<String, String>> result = new ArrayList<>();

         for (Entry<String, Integer> ent : current.entrySet()) {
            Integer prev = prevNonZeroMap.get(ent.getKey());
            if (!ent.getValue().equals(prev)) {
               if (prev == null) {
                  result.add(new TP<>(ent.getKey(), "0->" + ent.getValue()));
               } else {
                  result.add(new TP<>(ent.getKey(), prev + "->" + ent.getValue()));
               }
            }
         }

         if (result.size() > 0) {
            System.out.println(result);
         }

         return result;
      }
   }

   public List<String> getCurrentModifiersStrings() {
      List<String> result = new ArrayList<>();

      for (Modifier m : this.getCurrentModifiers()) {
         result.add(m.getSaveString());
      }

      return result;
   }

   public static DungeonContext fromData(DungeonContextData dcd) {
      ContextConfig cc = ContextConfigUtils.fromJson(dcd.cc, dcd.c);
      DungeonContext dc = new DungeonContext(cc, new Party(dcd.p), dcd.n, dcd.l.toLevel(), dcd.dv);
      dc.seed = dcd.seed;
      dc.startLevel = dcd.sl;
      dc.currentModifiers = ModifierUtils.deserialiseList(dcd.m);
      dc.extraSeconds = dcd.extraTime;
      dc.setCheckedItems(dcd.checkedItems);

      for (LevelData ld : dcd.pl) {
         dc.previousLevels.add(ld.toLevel());
      }

      StatLib.mergeStats(dc.getStatsManager().getAllStats(), dcd.stats, true);
      return dc;
   }

   public void addModifiers(List<Modifier> modifiers) {
      for (Modifier m : modifiers) {
         this.addModifier(m);
      }
   }

   public void addModifier(Modifier modifier) {
      this.addModifier(modifier, true);
   }

   public void addModifier(Modifier modifier, boolean activateOnPick) {
      this.currentModifiers.add(modifier);
      this.cachedModifierGlobals = null;
      if (activateOnPick) {
         modifier.onPickEffects(this);
      }
   }

   public List<Modifier> getCurrentModifiers() {
      return this.currentModifiers;
   }

   public ContextConfig getContextConfig() {
      return this.contextConfig;
   }

   public void logVictory() {
      this.end(true, false);
   }

   public void logDefeatBackground(SaveState saveState) {
      FightLog f = saveState.makeFightLog();
      this.logDefeatBackground(f.makeSnapshot());
   }

   public void logDefeatBackground(StatSnapshot snapshot) {
      if (!this.skipStats()) {
         this.statsManager.endOfFight(snapshot, false);
         this.end(false, true);
      }
   }

   public void logDefeat() {
      this.end(false, false);
   }

   private void end(boolean victory, boolean background) {
      this.setupFinalTime();
      if (!this.skipStats()) {
         this.statsManager.endOfRun(this, victory, background);
         com.tann.dice.Main.self().masterStats.endOfRun(this, victory, background);
      }

      this.getContextConfig().afterDefeatAction();
   }

   private void setupFinalTime() {
      this.lockedSecondsTaken = this.getTimeTakenSeconds();
   }

   public Phase onWinLevel() {
      for (int i = this.currentModifiers.size() - 1; i >= 0; i--) {
         Modifier m = this.currentModifiers.get(i);

         for (Global global : m.getGlobals()) {
            if (global instanceof GlobalTemporary) {
               this.currentModifiers.remove(i);
               break;
            }
         }
      }

      int reachedLevel = this.currentLevel - this.startLevel + 2;
      if (reachedLevel == 3) {
         this.getContextConfig().reachedLevelThree();
      }

      if (reachedLevel == 10 && !this.skipStats()) {
         AnticheeseData.reachedLevelTen();
      }

      if (this.isAtLastLevel()) {
         this.getContextConfig().clearAnticheese();
         return new RunEndPhase(true);
      } else {
         this.nextLevel();
         return new LevelEndPhase();
      }
   }

   public List<Stat> getNonZeroStats() {
      return StatLib.getNonZeroStats(this.getStatsManager().getAllStats());
   }

   public DungeonStatManager getStatsManager() {
      return this.statsManager;
   }

   public long getFinalTimeSeconds() {
      return this.lockedSecondsTaken == null ? -1L : this.lockedSecondsTaken;
   }

   public long getTimeTakenSeconds() {
      if (this.getFinalTimeSeconds() != -1L) {
         return this.getFinalTimeSeconds();
      } else {
         return this.thisStartTime == 0L ? this.extraSeconds : (System.currentTimeMillis() - this.thisStartTime) / 1000L + this.extraSeconds;
      }
   }

   public String getLevelProgressString(boolean endReached) {
      if (endReached && this.currentLevel == 20) {
         return null;
      } else {
         String ratio = this.currentLevel + "/" + Words.getMaybeInfinityString(Math.max(this.currentLevel, this.getTotalLength()));
         return "[text]Fight " + ratio;
      }
   }

   public List<TP<Zone, Integer>> getLevelTypes() {
      Zone overrideZone = null;

      for (Global g : this.getAllActiveGlobalsUncached()) {
         Zone p = g.getOverrideZone();
         if (p != null) {
            overrideZone = p;
         }
      }

      if (overrideZone != null) {
         return Arrays.asList(new TP<>(overrideZone, 20));
      } else {
         List<TP<Zone, Integer>> override = this.contextConfig.getOverrideLevelTypes(this);
         return override != null ? override : this.contextConfig.getDefaultLevelTypes(this);
      }
   }

   public int getSeed() {
      return this.seed;
   }

   public boolean isBossFight() {
      return this.contextConfig.isBoss(this.currentLevel);
   }

   public void clearForLoop() {
      this.getParty().curseReset(this);

      for (Global gt : this.getModifierGlobalsIncludingLinked()) {
         if (gt instanceof GlobalAddHero || gt instanceof GlobalChangeHeroPos || gt instanceof GlobalChangeHeroAll) {
            gt.onPick(this);
         }

         List<Item> e = gt.getStartingItems(this);
         if (e != null) {
            this.getParty().addItems(e);
         }
      }

      List<Stat> statsToRefresh = new ArrayList<>();

      for (Stat s : this.getNonZeroStats()) {
         if (s instanceof PickStat) {
            statsToRefresh.add(s);
         }
      }

      com.tann.dice.Main.self().masterStats.mergeStats(statsToRefresh);
      this.statsManager.clearStats(statsToRefresh);
      if (DungeonScreen.get() != null) {
         DungeonScreen.get().getFightLog().resetDueToFiddling();
      }

      this.reSeed();
   }

   public int getNumberOfCurses() {
      int total = 0;

      for (Modifier m : this.getCurrentModifiers()) {
         if (m.getMType() == ModifierType.Curse) {
            total++;
         }
      }

      return total;
   }

   public Actor makeHashButton() {
      ImageActor hash = new ImageActor(DungeonUtils.getBaseImage());
      hash.addListener(new TannListener() {
         @Override
         public boolean action(int button, int pointer, float x, float y) {
            Sounds.playSound(Sounds.pip);
            DungeonContext.this.showHashContents();
            return true;
         }
      });
      Group g = Tann.makeGroup(hash);
      TextWriter twoCh = new TextWriter(this.contextConfig.getTwoCharactersMax());
      twoCh.setTouchable(Touchable.disabled);
      g.addActor(twoCh);
      Tann.center(twoCh);
      return g;
   }

   public void showHashContents() {
      Screen s = com.tann.dice.Main.getCurrentScreen();
      s.popAllMedium();
      Group g = this.makeInsideHash();
      if (this.isTooBig(g)) {
         g = Tann.makeScrollpane(g);
      }

      g.addListener(new TannListener() {
         @Override
         public boolean action(int button, int pointer, float x, float y) {
            return true;
         }
      });
      com.tann.dice.Main.getCurrentScreen().push(g, true, true, false, 0.7F);
      Tann.center(g);
   }

   private boolean isTooBig(Group g) {
      return g.getWidth() > com.tann.dice.Main.width || g.getHeight() > com.tann.dice.Main.height;
   }

   private Group makeInsideHash() {
      BOption bopt = OptionLib.HASH_ICONS;
      boolean bigPanel = bopt.c();
      Pixl p = new Pixl(2, 3).border(Colours.grey);
      int turnNumber = DungeonScreen.get().getFightLog().getSnapshot(FightLog.Temporality.Present).getTurn();
      p.text(this.getContextConfig().getEndTitle())
         .row()
         .text("[notranslate]" + com.tann.dice.Main.t(this.getLevelProgressString(false)) + " " + com.tann.dice.Main.t("Turn " + turnNumber));
      int currentStreak = this.getContextConfig().getStreak(false);
      if (currentStreak > 0) {
         p.row().text("[yellow]Current streak " + currentStreak);
      }

      if (DungeonScreen.isWish() && this.canWishMod()) {
         p.row().actor(this.makeWishAddCurse());
      }

      List<Modifier> rawMods = this.getCurrentModifiers();
      List<Modifier> mods = new ArrayList<>(rawMods);
      boolean anyHidden;
      if (!OptionLib.HASH_HIDDEN.c()) {
         for (int i = mods.size() - 1; i >= 0; i--) {
            if (mods.get(i).isHidden()) {
               mods.remove(i);
            }
         }

         anyHidden = mods.size() != rawMods.size();
      } else {
         anyHidden = false;

         for (int ix = mods.size() - 1; ix >= 0; ix--) {
            if (mods.get(ix).isHidden()) {
               anyHidden = true;
            }
         }
      }

      if (mods.size() > 0) {
         boolean alwaysBig = mods.size() <= 2;
         boolean tinyPanels = !alwaysBig && !bigPanel;
         String tag = TextWriter.getTag(ModifierUtils.colourFor(mods));
         String text = "[notranslate][text]"
            + com.tann.dice.Main.t(mods.size() + " active " + tag + Words.plural(ModifierUtils.describeList(mods), mods.size()))
            + (mods.size() < 2 ? "" : " [text](" + tag + ModifierUtils.sumTiers(mods, true) + "[cu])");
         p.row(6).text(text);
         if (!alwaysBig) {
            p.gap(15);
            Actor a = bopt.makeComplexEscMenuActor(new Runnable() {
               @Override
               public void run() {
                  DungeonContext.this.showHashContents();
               }
            });
            p.actor(a);
         }

         if (anyHidden) {
            p.gap(15).actor(OptionLib.HASH_HIDDEN.makeComplexEscMenuActor(new Runnable() {
               @Override
               public void run() {
                  DungeonContext.this.showHashContents();
               }
            }));
         }

         p.row();
         Pixl modifierPixl = new Pixl(0);
         int align = 1;
         Pixl modColumn = null;

         for (int ixx = 0; ixx < mods.size(); ixx++) {
            if (ixx % 5 == 0) {
               if (ixx > 0) {
                  modifierPixl.actor(modColumn.pix(align));
               }

               modColumn = new Pixl(2);
            }

            Modifier mod = mods.get(ixx);
            Group a;
            if (tinyPanels) {
               SmallModifierPanel smp = new SmallModifierPanel(mod);
               smp.addBasicListener();
               a = smp;
            } else {
               a = new ModifierPanel(mod, false);
            }

            modColumn.actor(a);
            if (ixx < mods.size() - 1) {
               modColumn.row();
            }
         }

         if (modColumn != null) {
            modifierPixl.actor(modColumn.pix(align));
         }

         Actor allModifiers = modifierPixl.pix(2);
         if (tinyPanels) {
            ScrollPane sp = Tann.makeScrollpane(allModifiers);
            boolean needsScroll = allModifiers.getWidth() > sp.getWidth();
            sp.setHeight((int)Math.min(allModifiers.getHeight() + (needsScroll ? 6 : 0), com.tann.dice.Main.height * 0.9F));
            p.actor(sp);
         } else {
            p.actor(allModifiers);
         }
      } else if (anyHidden) {
         p.gap(15).actor(OptionLib.HASH_HIDDEN.makeComplexEscMenuActor(new Runnable() {
            @Override
            public void run() {
               DungeonContext.this.showHashContents();
            }
         }));
      }

      Group g = p.pix();
      if (this.party.getPLT() != null) {
         int gap = 2;
         Actor ax = this.party.getPLT().visualiseTiny();
         g.addActor(ax);
         ax.setPosition(gap, g.getHeight() - ax.getHeight() - gap);
      }

      return g;
   }

   private Actor makeWishAddCurse() {
      StandardButton sb = new StandardButton(Mode.WISH.getTextButtonName());
      sb.addListener(new TannListener() {
         @Override
         public boolean action(int button, int pointer, float x, float y) {
            (new ModifierResolver() {
               public void resolve(Modifier modifier) {
                  if (!DungeonContext.this.canWishMod()) {
                     Sounds.playSound(Sounds.error);
                  } else {
                     if (modifier.getName().equalsIgnoreCase("wish")) {
                        modifier = WishMode.makeGenie();
                     }

                     DungeonContext.this.addModifier(modifier);
                     Sounds.playSound(modifier.getTier() < 0 ? Sounds.poison : Sounds.magic);
                     DungeonScreen.get().save();
                     com.tann.dice.Main.getCurrentScreen().popAllMedium();
                  }
               }
            }).activate(Mode.WISH.wishFor("modifier", Colours.purple));
            return true;
         }
      });
      return sb;
   }

   private boolean canWishMod() {
      PhaseManager pm = PhaseManager.get();
      Phase p = pm.getPhase();
      return p instanceof LevelEndPhase || p instanceof ChoicePhase;
   }

   public Zone getCurrentZone() {
      return this.contextConfig.getTypeForLevel(this.currentLevel, this);
   }

   public boolean isCheckedItems() {
      return this.checkedItems;
   }

   public void setCheckedItems(boolean checkedItems) {
      this.checkedItems = checkedItems;
   }

   public void levelupFromLevelupPhaseChoice(HeroType heroType, int index) {
      this.party.levelUpTo(heroType, index);
   }

   public List<HeroType> makeSeenHeroTypes() {
      return this.makeSeenHeroTypes(null);
   }

   public List<HeroType> makeSeenHeroTypes(Boolean rejected) {
      List<HeroType> seen = new ArrayList<>();
      Map<String, Integer> map = this.getNonZeroStatMap();

      for (HeroType ht : HeroTypeLib.getMasterCopy()) {
         if (map.get(PickStat.nameFor(ht)) != null) {
            Integer value = map.get(PickStat.nameFor(ht));
            if (value != null) {
               if (rejected == null) {
                  seen.add(ht);
               } else if (BitStat.val(value, rejected) > 0) {
                  seen.add(ht);
               }
            }
         }
      }

      return seen;
   }

   public void setContextConfig(ContextConfig contextConfig) {
      this.contextConfig = contextConfig;
   }

   public void specialCachedAchievementCheck() {
      DungeonScreen ds = DungeonScreen.get();
      if (ds != null) {
         List<Stat> nonZeroCopy = StatLib.copy(this.getNonZeroStats());
         StatLib.mergeStats(nonZeroCopy, ds.getStoredMergedList());
         com.tann.dice.Main.self().masterStats.updateAfterSaveForStats(StatLib.makeStatsMap(nonZeroCopy));
      }
   }

   public void setAntiCheeseRerollModifiers(List<Modifier> antiCheeseRerollModifiers) {
      this.antiCheeseRerollModifiers = antiCheeseRerollModifiers;
   }

   public List<Modifier> getAvoidModifiers() {
      List<Modifier> avoid = new ArrayList<>();
      avoid.addAll(this.antiCheeseRerollModifiers);
      avoid.addAll(this.contextConfig.getAvoidModifiers());
      return avoid;
   }

   public boolean isFirstLevel() {
      return this.getCurrentLevelNumber() == this.startLevel;
   }

   public void setParty(Party startingParty) {
      this.party = startingParty;
   }

   public void startTimer() {
      this.thisStartTime = System.currentTimeMillis();
   }

   public boolean allowInventory() {
      for (Hero h : this.getParty().getHeroes()) {
         if (!h.getBlankState().skipEquipScreen()) {
            return true;
         }
      }

      return false;
   }

   public long getBannedCollisionBits() {
      return this.getBannedCollisionBits(true);
   }

   public long getBannedCollisionBits(boolean spellRequiresTwo) {
      return this.getContextConfig().mode.getBannedCollisionBits() | this.party.getBannedCollisionBits(spellRequiresTwo);
   }

   public int getCurrentMod20LevelNumber() {
      int numLevels = this.getContextConfig().getTotalDifferentLevels();
      return (this.currentLevel - 1) % numLevels + 1;
   }

   public boolean isWishable() {
      for (int i = 0; i < this.getModifierGlobals().size(); i++) {
         if (this.getModifierGlobals().get(i).canWish()) {
            return true;
         }
      }

      return false;
   }

   public List<Modifier> makeSeenModifiers() {
      List<Modifier> seen = new ArrayList<>();
      Map<String, Integer> map = this.getNonZeroStatMap();

      for (String s : map.keySet()) {
         Modifier m = ModifierLib.byName(s);
         if (!m.isMissingno()) {
            seen.add(m);
         }
      }

      LevelEndPhase lep = (LevelEndPhase)PhaseManager.get().find(LevelEndPhase.class);
      if (lep != null) {
         for (Phase p : lep.getNestedPhases()) {
            if (p instanceof ChoicePhase) {
               ChoicePhase cp = (ChoicePhase)p;

               for (Choosable ch : cp.getOptions()) {
                  if (ch instanceof Modifier) {
                     seen.add((Modifier)ch);
                  }
               }
            }
         }
      }

      return seen;
   }

   public List<Item> makeSeenItems() {
      List<Item> seen = new ArrayList<>();
      Map<String, Integer> map = this.getNonZeroStatMap();

      for (Item eq : ItemLib.getMasterCopy()) {
         if (map.get(PickStat.nameFor(eq)) != null) {
            seen.add(eq);
         }
      }

      LevelEndPhase lep = (LevelEndPhase)PhaseManager.get().find(LevelEndPhase.class);
      if (lep != null) {
         for (Phase p : lep.getNestedPhases()) {
            if (p instanceof ChoicePhase) {
               ChoicePhase cp = (ChoicePhase)p;

               for (Choosable ch : cp.getOptions()) {
                  if (ch instanceof Item) {
                     seen.add((Item)ch);
                  }
               }
            }
         }
      }

      return seen;
   }

   public void onLose(Choosable lose) {
      if (lose instanceof Modifier) {
         this.currentModifiers.remove(lose);
         this.cachedModifierGlobals = null;
      }
   }

   public void addValue(DungeonValue delta) {
      for (DungeonValue dungeonValue : this.dungeonValues) {
         if (dungeonValue.getKey().equalsIgnoreCase(delta.getKey())) {
            dungeonValue.add(delta.getValue());
            return;
         }
      }

      this.dungeonValues.add(delta);
   }

   public Integer getValue(String key) {
      switch (key) {
         case "heroes":
            return this.party.getHeroes().size();
         case "items":
            return this.party.getItems(null).size();
         case "modifiers":
            return this.getCurrentModifiers().size();
         case "fight":
            return this.getCurrentLevelNumber();
         default:
            for (DungeonValue dungeonValue : this.dungeonValues) {
               if (dungeonValue.getKey().equalsIgnoreCase(key)) {
                  return dungeonValue.getValue();
               }
            }

            return null;
      }
   }

   public void addTime(float secs) {
      this.extraSeconds = (long)((float)this.extraSeconds + secs);
   }

   public boolean isBugged() {
      for (Modifier currentModifier : this.getCurrentModifiers()) {
         if (currentModifier.isMissingno()) {
            return true;
         }
      }

      return false;
   }

   public boolean isMonsterPooling() {
      List<Global> globs = this.getModifierGlobalsIncludingLinked();

      for (int i = 0; i < globs.size(); i++) {
         if (globs.get(i) instanceof GlobalClearPoolMonster) {
            return true;
         }
      }

      return false;
   }
}

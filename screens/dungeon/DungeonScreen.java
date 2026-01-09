package com.tann.dice.screens.dungeon;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.tann.dice.gameplay.content.ent.Ent;
import com.tann.dice.gameplay.content.ent.Hero;
import com.tann.dice.gameplay.content.ent.Monster;
import com.tann.dice.gameplay.content.ent.die.Die;
import com.tann.dice.gameplay.content.ent.die.EntDie;
import com.tann.dice.gameplay.content.ent.group.Party;
import com.tann.dice.gameplay.content.ent.type.lib.MonsterTypeLib;
import com.tann.dice.gameplay.context.DungeonContext;
import com.tann.dice.gameplay.context.config.ContextConfig;
import com.tann.dice.gameplay.effect.eff.Eff;
import com.tann.dice.gameplay.effect.eff.EffBill;
import com.tann.dice.gameplay.effect.targetable.DieTargetable;
import com.tann.dice.gameplay.effect.targetable.SimpleTargetable;
import com.tann.dice.gameplay.effect.targetable.ability.ui.AbilityHolder;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.fightLog.FightLog;
import com.tann.dice.gameplay.fightLog.Snapshot;
import com.tann.dice.gameplay.fightLog.command.SimpleCommand;
import com.tann.dice.gameplay.fightLog.event.entState.ChatStateEvent;
import com.tann.dice.gameplay.fightLog.event.snapshot.SnapshotEvent;
import com.tann.dice.gameplay.fightLog.listener.SnapshotChangeListener;
import com.tann.dice.gameplay.fightLog.listener.VictoryLossListener;
import com.tann.dice.gameplay.level.Level;
import com.tann.dice.gameplay.mode.creative.pastey.PasteMode;
import com.tann.dice.gameplay.phase.Phase;
import com.tann.dice.gameplay.phase.PhaseManager;
import com.tann.dice.gameplay.phase.endPhase.runEnd.RunEndPhase;
import com.tann.dice.gameplay.phase.gameplay.EnemyRollingPhase;
import com.tann.dice.gameplay.phase.gameplay.PlayerRollingPhase;
import com.tann.dice.gameplay.phase.gameplay.SurrenderPhase;
import com.tann.dice.gameplay.phase.gameplay.TargetingPhase;
import com.tann.dice.gameplay.phase.levelEndPhase.LevelEndPanel;
import com.tann.dice.gameplay.phase.levelEndPhase.LevelEndPhase;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.ChoicePhase;
import com.tann.dice.gameplay.progress.MasterStats;
import com.tann.dice.gameplay.progress.stats.stat.Stat;
import com.tann.dice.gameplay.save.LoadCrashException;
import com.tann.dice.gameplay.save.SaveState;
import com.tann.dice.gameplay.save.SaveStateData;
import com.tann.dice.gameplay.save.antiCheese.AntiCheeseRerollInfo;
import com.tann.dice.gameplay.save.antiCheese.AnticheeseData;
import com.tann.dice.gameplay.save.settings.option.OptionLib;
import com.tann.dice.gameplay.save.settings.option.OptionUtils;
import com.tann.dice.screens.Screen;
import com.tann.dice.screens.dungeon.background.BackgroundHolder;
import com.tann.dice.screens.dungeon.background.Dust;
import com.tann.dice.screens.dungeon.panels.ConfirmButton;
import com.tann.dice.screens.dungeon.panels.EntContainer;
import com.tann.dice.screens.dungeon.panels.ExplanelReposition;
import com.tann.dice.screens.dungeon.panels.Explanel.EntPanelInventory;
import com.tann.dice.screens.dungeon.panels.Explanel.Explanel;
import com.tann.dice.screens.dungeon.panels.Explanel.InfoPanel;
import com.tann.dice.screens.dungeon.panels.threeD.DieRenderer;
import com.tann.dice.screens.dungeon.panels.time.Clock;
import com.tann.dice.screens.dungeon.panels.time.SpeedrunTimer;
import com.tann.dice.screens.dungeon.panels.tutorial.TutorialManager;
import com.tann.dice.screens.generalPanels.InventoryPanel;
import com.tann.dice.screens.generalPanels.PartyManagementPanel;
import com.tann.dice.screens.shaderFx.FXContainer;
import com.tann.dice.screens.titleScreen.GameStart;
import com.tann.dice.screens.titleScreen.TitleScreen;
import com.tann.dice.statics.Images;
import com.tann.dice.statics.bullet.BulletStuff;
import com.tann.dice.statics.sound.Sounds;
import com.tann.dice.test.util.TestRunner;
import com.tann.dice.util.Chrono;
import com.tann.dice.util.Colours;
import com.tann.dice.util.DebugUtilsUseful;
import com.tann.dice.util.Draw;
import com.tann.dice.util.Pixl;
import com.tann.dice.util.Rectactor;
import com.tann.dice.util.Tann;
import com.tann.dice.util.TannFont;
import com.tann.dice.util.TannLog;
import com.tann.dice.util.TannStageUtils;
import com.tann.dice.util.VersionUtils;
import com.tann.dice.util.lang.Words;
import com.tann.dice.util.listener.TannListener;
import com.tann.dice.util.saves.Prefs;
import com.tann.dice.util.tp.TP;
import com.tann.dice.util.ui.Button;
import com.tann.dice.util.ui.action.PixAction;
import com.tann.dice.util.ui.standardButton.StandardButton;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DungeonScreen extends Screen implements ExplanelReposition, VictoryLossListener, SnapshotChangeListener {
   private static DungeonScreen self;
   public EntContainer hero;
   public EntContainer enemy;
   FightLog fightLog;
   public TargetingManager targetingManager;
   public RollManager rollManager;
   public PartyManagementPanel partyManagementPanel;
   Party party;
   DungeonContext dungeonContext;
   public Group rollGroup = Tann.makeGroup(getBotButtWidth(), getBottomButtonHeight());
   public Button diceRollButton;
   public Button undoButton;
   public ConfirmButton confirmButton;
   public ConfirmButton doneRollingButton;
   public AbilityHolder abilityHolder;
   public Group optionsButtonsGroup;
   private boolean loading;
   TutorialManager tutorialManager;
   public StandardButton hiddenInventoryButton;
   int undosInARow = 0;
   private final List<Stat> storedMergedList;
   private SpeedrunTimer tt;
   private Clock clock;
   private final String textReroll;
   private final int textRerollWidth;
   boolean tabbed;
   long lastSaved = -1L;
   Map<String, Integer> storedStats;
   Boolean allDiceUsed = null;
   private BackgroundHolder bgh;
   private List<Actor> cleanupActors = new ArrayList<>();
   private Actor undoActor;
   int lastTurn = 0;
   int stateIndex = 0;
   public static final int BASE_BOTTOM_BUTTON_HEIGHT = 29;
   public static boolean tinyPasting;

   public static DungeonScreen get() {
      return self;
   }

   public DungeonScreen(DungeonContext dungeonContext) {
      this(dungeonContext, null, null, null, null);
      String cheeseKey = dungeonContext.getContextConfig().getAnticheeseKey();
      if (cheeseKey != null) {
         this.saveAntiCheese();
      }
   }

   public DungeonScreen(DungeonContext dungeonContext, List<String> commandState, String sideSate, List<String> phases, String json) {
      this.textReroll = com.tann.dice.Main.t("Reroll");
      this.textRerollWidth = TannFont.font.getWidth(this.textReroll);
      SaveState.updateFacadeRenderingStatus(json);
      self = this;
      this.dungeonContext = dungeonContext;
      this.party = dungeonContext.getParty();
      ContextConfig.resetCache();
      PhaseManager.get().clearListeners();
      Sounds.setSoundEnabled(true);
      this.storedMergedList = new ArrayList<>(com.tann.dice.Main.self().masterStats.createMergedStats(dungeonContext.getContextConfig().mode).values());
      MasterStats.clearMergedStats();
      if (commandState == null && sideSate == null) {
         this.fightLog = new FightLog(dungeonContext);
      } else {
         this.loadFromCommandState(commandState, sideSate, json);
      }

      this.fightLog.registerSnapshotListener(this, FightLog.Temporality.Visual);
      this.fightLog.registerVictoryLossListener(this);
      this.fightLog.registerStatUpdate(dungeonContext.getStatsManager());
      this.fightLog.registerStatUpdate(com.tann.dice.Main.self().masterStats);
      this.rollManager = new RollManager(this.fightLog);
      this.targetingManager = new TargetingManager(this.fightLog);
      this.partyManagementPanel = new PartyManagementPanel(this.fightLog);
      this.addActor(new DieRenderer());
      this.enemy = new EntContainer(false);
      this.addActor(this.enemy);
      this.hero = new EntContainer(true);
      this.addActor(this.hero);
      this.tutorialManager = new TutorialManager(this);
      this.addActor(this.tutorialManager.tutorialHolder);
      this.fightLog.registerSnapshotListener(this.tutorialManager, FightLog.Temporality.Visual);
      this.fightLog.registerStatUpdate(this.tutorialManager);
      PhaseManager.get().registerPhaseListen(this.tutorialManager);
      int spellWidth = (int)(this.getWidth() - getBotButtWidth() * 2);
      if (OptionLib.GAP.c() != 0) {
         spellWidth = (int)(spellWidth * 0.56F);
         if (OptionLib.GAP.c() == 2) {
            spellWidth = (int)Math.min((float)spellWidth, this.getWidth() - getBotButtWidth() * 2 - this.enemy.getWidth() - 10.0F);
         }
      }

      this.abilityHolder = new AbilityHolder(spellWidth, this.fightLog);
      this.addActor(this.abilityHolder);
      this.abilityHolder.setPosition(getBotButtWidth(), -this.abilityHolder.getHeight());
      this.abilityHolder.toBack();
      this.slideSpellHolder(AbilityHolder.TuckState.OffScreen, false);
      this.setupButtons();
      this.addListener(new InputListener() {
         public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
            if (!event.isHandled()) {
               DungeonScreen.this.bottomClick();
            }

            return super.touchDown(event, x, y, pointer, button);
         }
      });
      this.bgh = new BackgroundHolder();
      this.bgh.populate(dungeonContext.getLevelTypes());
      this.refreshStoredStats();
      if (this.loading) {
         Snapshot s = this.fightLog.getSnapshot(FightLog.Temporality.Present);
         List<Ent> entitiesForBulletDiceList = new ArrayList<>(s.getHeroesAliveAtStartOfTurn());
         entitiesForBulletDiceList.addAll(s.getAliveMonsterEntities());
         Tann.uniquify(entitiesForBulletDiceList);
         BulletStuff.refreshEntities(entitiesForBulletDiceList);
         this.fightLog.tick();
         PhaseManager.get().clearPhases();
         PhaseManager.get().deserialise(phases);
         int bgStartIndex = dungeonContext.getCurrentLevelNumber() - 1;
         this.bgh.setStartIndex(bgStartIndex);
         this.enemy.assumeHoldsDie();
         Sounds.setSoundEnabled(true);
         this.act(0.001F);
         this.fightLog.updateAllTemporalities();
         this.specialSaveConsiderations(sideSate);
         this.loading = false;
         dungeonContext.addTime(5.0F);
      } else {
         this.bgh.setStartIndex(dungeonContext.getCurrentLevelNumber() - 2);
         this.progressBackground();
         this.startGame(json != null);
      }

      this.fightLog.updateAllTemporalities();
      dungeonContext.startTimer();
      this.refreshTimer();
      this.refreshClock();
      this.addStreamerRects();
   }

   private void specialSaveConsiderations(String sideSate) {
      if (FightLog.isSpecialSave(sideSate, true)) {
         boolean activated = this.fightLog.triggerAllHeroOnLandDueToSave();
         if (activated) {
            this.save();
         }
      }

      if (FightLog.isSpecialSave(sideSate, false)) {
         boolean activated = this.fightLog.triggerAllMonsterOnLandDueToSave();
         if (activated) {
            this.save();
         }
      }
   }

   private void addStreamerRects() {
      if (OptionLib.GAP.c() != 0) {
         boolean top = OptionLib.GAP.c() == 2;
         int x;
         int y;
         int w;
         int h;
         if (top) {
            x = (int)(this.confirmButton.getX() + this.confirmButton.getWidth());
            w = com.tann.dice.Main.width - x;
            y = (int)(this.enemy.getY() + this.enemy.getHeight());
            h = com.tann.dice.Main.height - y;
         } else {
            x = (int)(this.confirmButton.getX() + this.confirmButton.getWidth());
            w = com.tann.dice.Main.width - x;
            y = 0;
            h = (int)(com.tann.dice.Main.height - this.enemy.getHeight());
         }

         Color c = Colours.withAlpha(Colours.dark, 0.15F).cpy();
         Rectactor ra = new Rectactor(w, h, null, c);
         ra.setTouchable(Touchable.disabled);
         ra.setPosition(x, y);
         this.addActor(ra);
         ra.toBack();
      }
   }

   private void loadFromCommandState(List<String> commandState, String sideSate, String json) {
      this.loading = true;
      BulletStuff.reset();
      Sounds.setSoundEnabled(false);

      try {
         this.fightLog = new FightLog(
            this.party.getHeroes(),
            MonsterTypeLib.monsterList(this.dungeonContext.getCurrentLevel().getMonsterList()),
            commandState,
            sideSate,
            this.dungeonContext
         );
      } catch (Exception var8) {
         TannLog.log("failed to load save properly from " + json, TannLog.Severity.error);
         this.showDialog("Failed to load save[n]Things may be [red][sin]broken[sin][n][text]Restarting from start of battle");
         var8.printStackTrace();
         this.loading = false;
         List<String> var9 = null;
         String var10 = null;

         try {
            this.fightLog = new FightLog(this.dungeonContext);
            this.fightLog.setFailed(true);

            for (Hero h : this.party.getHeroes()) {
               h.getDie().setSide(0);
            }

            Sounds.setSoundEnabled(true);
         } catch (Exception var7) {
            clearStaticReference();
            throw new LoadCrashException(var7);
         }
      }
   }

   public void startGame(boolean skipContextPhases) {
      PhaseManager pm = PhaseManager.get();
      pm.clearPhases();
      this.startLevel(skipContextPhases);
   }

   @Override
   public void loss() {
      this.enemy.assumeHoldsDie();
      this.hero.assumeHoldsDie();
      PhaseManager.get().clearPhases();
      PhaseManager.get().pushPhase(new RunEndPhase(false));
   }

   @Override
   public void victory() {
      this.enemy.assumeHoldsDie();
      this.hero.assumeHoldsDie();
      this.enemy.clearAfterInPlace();
      BulletStuff.clearAllDice();
      get().popAllLight();
      List<Phase> phases = PhaseManager.get().clearPhasesAndReturnEndable();
      Phase p = this.getDungeonContext().onWinLevel();
      if (p instanceof LevelEndPhase) {
         LevelEndPhase lep = (LevelEndPhase)p;

         for (Phase phase : phases) {
            lep.addPhase(phase);
         }
      }

      PhaseManager.get().pushPhase(p);
   }

   public void confirmClicked(boolean fromClick) {
      if (!this.fightLog.getSnapshot(FightLog.Temporality.Present).isVictory()) {
         if (fromClick) {
            this.popAllLight();
         }

         Phase phase = PhaseManager.get().getPhase();
         phase.confirmClicked(fromClick);
      }
   }

   @Override
   public void preDraw(Batch batch) {
      batch.setColor(Colours.dark);
      Draw.fillRectangle(batch, this.getX(), this.getY(), this.getWidth(), this.getHeight());
      batch.setColor(Colours.z_white);
      this.renderBackground(batch);
      List<Ent> aliveEntities = this.fightLog.getSnapshot(FightLog.Temporality.Visual).getAliveEntities();
      int i = 0;

      for (int aliveEntitiesSize = aliveEntities.size(); i < aliveEntitiesSize; i++) {
         Ent de = aliveEntities.get(i);
         de.getEntPanel().drawBackground(batch);
      }
   }

   @Override
   public void postDraw(Batch batch) {
      for (int i = 0; i < this.enemy.getEntities().size(); i++) {
         this.enemy.getEntities().get(i).getEntPanel().postDraw(batch);
      }

      for (int i = 0; i < this.hero.getEntities().size(); i++) {
         this.hero.getEntities().get(i).getEntPanel().postDraw(batch);
      }
   }

   public static void drawVersionPixel(Batch batch) {
      int y = Gdx.graphics.getHeight();
      y--;
      batch.setColor(VersionUtils.VERSION_COL);
      Draw.pixel(batch, 0, y);
      batch.setColor(com.tann.dice.Main.self().control.getCol());
      Draw.pixel(batch, 1, y);
   }

   @Override
   public void preTick(float delta) {
   }

   @Override
   public void postTick(float delta) {
   }

   @Override
   public void keyPress(int keycode) {
      Phase p = PhaseManager.get().getPhase();
      switch (keycode) {
         case 32:
            DebugUtilsUseful.debug();
         case 47:
         default:
            break;
         case 61:
            if (p instanceof PlayerRollingPhase || p instanceof TargetingPhase) {
               for (Ent entity : this.hero.getEntities()) {
                  entity.getEntPanel().setArrowIntensity(1.0F, -1.0F);
               }

               this.tabbed = true;
            }
      }

      if (p != null) {
         p.keyPress(keycode);
      }
   }

   public void killAllEnemies() {
      if (!this.getFightLog().getSnapshot(FightLog.Temporality.Present).isVictory()) {
         this.getFightLog().addCommand(new SimpleCommand(null, new SimpleTargetable(null, new EffBill().group().kill().bEff())), false);
      }
   }

   @Override
   public Screen copy() {
      if (SaveState.hasSave(this.dungeonContext.getContextConfig().getGeneralSaveKey())) {
         SaveState s = SaveState.load(this.dungeonContext.getContextConfig().getGeneralSaveKey());
         if (s != null) {
            return s.makeDungeonScreen();
         }
      }

      return new TitleScreen();
   }

   private SaveState makeSaveState() {
      try {
         return new SaveState(this.dungeonContext, this.fightLog.serialiseCommands(), this.fightLog.serialiseSides(), PhaseManager.get().serialise());
      } catch (Exception var2) {
         var2.printStackTrace();
         TannLog.log("Failed to save: " + var2.getMessage());
         return null;
      }
   }

   public void mildSave() {
      this.save();
   }

   public void save() {
      this.lastSaved = System.currentTimeMillis();
      this.handleStatChanges();
      SaveState ss = this.makeSaveState();
      if (ss != null) {
         ss.save();
      }
   }

   private void handleStatChanges() {
      if (OptionLib.SHOW_STAT_POPUPS.c()) {
         List<TP<String, String>> changes = this.dungeonContext.getStatChanges(this.storedStats);
         this.refreshStoredStats();

         for (TP<String, String> tp : changes) {
            Actor a = new Pixl(3, 3).border(Colours.purple).text("[grey]" + tp.a + " : " + tp.b).pix();
            this.addPopup(a);
         }
      }
   }

   private void refreshStoredStats() {
      if (OptionLib.SHOW_STAT_POPUPS.c()) {
         this.storedStats = this.dungeonContext.getNonZeroStatMap();
      }
   }

   public void somethingChanged() {
      this.allDiceUsed = null;
      this.targetingManager.anythingChanged();
   }

   public boolean checkAllDiceUsedCached() {
      if (this.allDiceUsed == null) {
         this.allDiceUsed = this.checkAllDiceUsed(false);
      }

      return this.allDiceUsed;
   }

   public int numUnusedDice(boolean allowSkipInadvisable) {
      int result = 0;
      List<? extends Ent> entities = this.fightLog.getActiveEntities(true);
      Snapshot present = this.fightLog.getSnapshot(FightLog.Temporality.Present);

      for (int i = 0; i < entities.size(); i++) {
         EntDie d = entities.get(i).getDie();
         DieTargetable dt = d.getTargetable();
         EntState es = present.getState(d.ent);
         if (es.canUse() && this.targetingManager.isUsable(dt, allowSkipInadvisable)) {
            Eff e = dt.getDerivedEffects();
            if ((!allowSkipInadvisable || !e.allowAutoskip()) && (!allowSkipInadvisable || !es.isInadvisable(e))) {
               result++;
            }
         }
      }

      return result;
   }

   public boolean checkAllDiceUsed(boolean allowSkipBadKeywords) {
      return this.numUnusedDice(allowSkipBadKeywords) == 0;
   }

   @Override
   public void act(float delta) {
      this.fightLog.tick();
      this.handleTab();
      super.act(delta);
      PhaseManager.get().tick(delta);
      this.bgh.act(delta);
   }

   private void handleTab() {
      if (this.tabbed && !Gdx.input.isKeyPressed(61)) {
         this.tabbed = false;

         for (Ent entity : this.hero.getEntities()) {
            entity.getEntPanel().setArrowIntensity(0.0F, -1.0F);
         }
      }
   }

   public void startLevel(boolean skipContextPhases) {
      MasterStats.clearMergedStats();
      BulletStuff.reset();
      this.getFightLog().resetForNewFight();
      Level l = this.dungeonContext.getCurrentLevel();
      List<Monster> monsters = MonsterTypeLib.monsterList(l.getMonsterList());
      Float diffD = l.getDiffD();
      if (OptionLib.SHOW_LEVEL_DIFF.c() && diffD != null) {
         this.addPopup(new Pixl(4, 4).border(Colours.orange).text(l.diffDeltaString()).pix());
      }

      List<Hero> heroes = this.party.getHeroes();
      this.getFightLog().setup(heroes, monsters);
      BulletStuff.refreshEntities(this.fightLog.getSnapshot(FightLog.Temporality.Present).getAliveEntities());
      PhaseManager pm = PhaseManager.get();
      if (!skipContextPhases && this.dungeonContext.isFirstLevel()) {
         List<Phase> phases = new ArrayList<>();
         this.dungeonContext.addPhasesFromCurrentLevel(phases);

         for (Phase p : phases) {
            pm.pushPhase(p);
         }
      }

      if (this.party.hasAnyItems() && !pm.has(LevelEndPhase.class)) {
         LevelEndPhase toAdd = new LevelEndPhase(true);
         if (pm.has(ChoicePhase.class)) {
            pm.pushPhaseAfter(toAdd, ChoicePhase.class);
         } else {
            pm.pushPhase(toAdd);
         }
      }

      pm.pushPhase(new EnemyRollingPhase());
      this.save();
      if (com.tann.dice.Main.self().control.useBackups()) {
         Prefs.backupSave();
      }
   }

   private void renderBackground(Batch batch) {
      if (this.bgh != null) {
         this.bgh.draw(batch, 1.0F);
      }
   }

   public void progressBackground() {
      if (!TestRunner.isTesting()) {
         Vector2 dist = this.bgh.progress();
         this.enemy.setInPlace(false);
         this.enemy.slideIn(dist, new Runnable() {
            @Override
            public void run() {
               DungeonScreen.this.enemy.setInPlace(true);
            }
         });
      }
   }

   public DungeonContext getDungeonContext() {
      return this.dungeonContext;
   }

   public FightLog getFightLog() {
      return this.fightLog;
   }

   @Override
   public void repositionExplanel(Explanel exp) {
      if (com.tann.dice.Main.isPortrait()) {
         exp.setPosition((int)(com.tann.dice.Main.width / 2 - exp.getWidth() / 2.0F), InfoPanel.getPortraitPanelY() + exp.getExtraBelowExtent());
      } else {
         exp.setPosition((int)(com.tann.dice.Main.width / 2 - exp.getWidth() / 2.0F), (int)(com.tann.dice.Main.height / 2 - exp.getHeight() / 2.0F));
      }
   }

   public void addCleanupActor(Actor a) {
      this.cleanupActors.add(a);
   }

   public void removeAllEffects() {
      for (Actor a : this.cleanupActors) {
         a.remove();
      }

      this.cleanupActors.clear();
   }

   private void bottomClick() {
      if (com.tann.dice.Main.scale != 0) {
         if (this.stackContains(Explanel.class) || this.stackContains(EntPanelInventory.class)) {
            Sounds.playSound(Sounds.pop);
         }

         if (!this.popAllLight()) {
            if (!this.pop(InventoryPanel.class)) {
               if (!this.targetingManager.deselectTargetable()) {
                  if (!BulletStuff.isMouseOnDice() && Dust.allowActor(this.getActorUnderMouse())) {
                     this.makeDust();
                  }
               }
            }
         }
      }
   }

   public void slideButton(Actor button, boolean in, boolean instant, float delay) {
      delay *= OptionUtils.dangerButtonSpeed();
      button.clearActions();
      if (get().isLoading()) {
         instant = true;
      }

      Action move = PixAction.moveTo((int)button.getX(), in ? 0 : (int)(-button.getHeight()) - 30, instant ? 0.0F : OptionUtils.buttonAnim(), Chrono.i);
      if (delay > 0.0F && !instant) {
         button.addAction(Actions.delay(delay, move));
      } else {
         button.addAction(move);
      }
   }

   public void slideButton(Actor button, boolean in, boolean instant) {
      this.slideButton(button, in, instant, 0.0F);
   }

   private void setupButtons() {
      this.diceRollButton = new Button(getBotButtWidth(), getBottomButtonHeight(), null, new Runnable() {
         @Override
         public void run() {
            DungeonScreen.this.popAllLight();
            DungeonScreen.this.rollManager.requestPlayerRoll();
         }
      }, new Runnable() {
         @Override
         public void run() {
            if (OptionLib.LONGTAP_END.c()) {
               DungeonScreen.this.popAllLight();
               DungeonScreen.this.confirmClicked(true);
            }
         }
      }) {
         @Override
         public void draw(Batch batch, float parentAlpha) {
            super.draw(batch, parentAlpha);
            if (!com.tann.dice.Main.getSettings().isHasRolled()) {
               com.tann.dice.Main.requestRendering();
               boolean anyRollingOrUnlocking = false;

               for (Ent de : DungeonScreen.this.fightLog.getSnapshot(FightLog.Temporality.Present).getAliveHeroEntities()) {
                  Die.DieState state = de.getDie().getState();
                  if (state == Die.DieState.Rolling || state == Die.DieState.Unlocking) {
                     anyRollingOrUnlocking = true;
                     break;
                  }
               }

               if (!anyRollingOrUnlocking) {
                  Tann.drawPatch(
                     batch,
                     this,
                     this.getPatch(),
                     Colours.shiftedTowards(Colours.dark, Colours.green, com.tann.dice.Main.pulsateFactor() / 2.0F),
                     Colours.grey,
                     1
                  );
               }
            }

            int border = 2;
            TextureRegion pairODice = Images.reroll;
            int guessTextWidth = DungeonScreen.this.textRerollWidth;
            int imageWidth = pairODice.getRegionWidth();
            int gap = (int)((this.getWidth() - guessTextWidth - imageWidth - border * 2) / 3.0F);
            batch.setColor(Colours.z_white);
            int portSep = 10;
            if (com.tann.dice.Main.isPortrait()) {
               batch.draw(
                  pairODice,
                  (int)(this.getX() + this.getWidth() / 2.0F - pairODice.getRegionWidth() / 2),
                  (int)(this.getY() + this.getHeight() / 2.0F - pairODice.getRegionHeight() / 2) + 10
               );
            } else {
               batch.draw(pairODice, (int)(this.getX() + border + gap), (int)(this.getY() + this.getHeight() / 2.0F - pairODice.getRegionHeight() / 2));
            }

            Snapshot present = DungeonScreen.this.getFightLog().getSnapshot(FightLog.Temporality.Present);
            int rolls = present.getRolls();
            int maxRolls = present.getMaxRolls();
            String rollText = rolls + "/" + maxRolls;
            batch.setColor(Colours.light);
            if (rolls == 0) {
               batch.setColor(Colours.red);
            }

            int start = border + gap * 2 + pairODice.getRegionWidth();
            int textWidth = com.tann.dice.Main.self().translator.shouldTranslate()
               ? Math.max(DungeonScreen.this.textRerollWidth, TannFont.font.getWidth(rollText))
               : TannFont.font.getWidth(rollText);
            int lineGap = 10;
            if (com.tann.dice.Main.isPortrait()) {
               TannFont.font
                  .drawString(batch, DungeonScreen.this.textReroll, (int)(this.getX() + this.getWidth() / 2.0F), (int)(this.getY() + 22.0F - 10.0F), 1);
               TannFont.font.drawString(batch, rollText, (int)(this.getX() + this.getWidth() / 2.0F), (int)(this.getY() + 30.0F - 10.0F), 1);
            } else {
               TannFont.font
                  .drawString(batch, DungeonScreen.this.textReroll, start + textWidth / 2, (int)(this.getY() + this.getHeight() / 2.0F + lineGap / 2), 1);
               TannFont.font.drawString(batch, rollText, start + textWidth / 2, (int)(this.getY() + this.getHeight() / 2.0F - lineGap / 2), 1);
            }
         }
      };
      this.diceRollButton.setInputBorder(com.tann.dice.Main.self().control.getConfirmButtonThumbpadRadius(), 0);
      this.diceRollButton.setSpecialBorderRight(false);
      this.rollGroup.addActor(this.diceRollButton);
      this.rollGroup.setTransform(false);
      this.addActor(this.rollGroup);
      this.slideButton(this.rollGroup, false, false);
      this.confirmButton = new ConfirmButton(getBotButtWidth(), getBottomButtonHeight());
      this.confirmButton.setState(ConfirmButton.ConfirmState.UsingDice);
      this.confirmButton.setRunnable(new Runnable() {
         @Override
         public void run() {
            if (!com.tann.dice.Main.getSettings().hasAttemptedLevel() && DungeonScreen.this.numUnusedDice(true) >= 2) {
               String translated = com.tann.dice.Main.t("First, use your dice");
               DungeonScreen.this.showDialog(Tann.halveString(translated), Colours.red);
               Sounds.playSound(Sounds.error);
            } else {
               DungeonScreen.this.popAllLight();
               DungeonScreen.this.confirmClicked(true);
            }
         }
      }, new Runnable() {
         @Override
         public void run() {
            if (OptionLib.LONGTAP_END.c()) {
               DungeonScreen.this.popAllLight();
               DungeonScreen.this.requestUndo();
            }
         }
      });
      if (isWish()) {
         this.confirmButton
            .addListener(
               new TannListener() {
                  @Override
                  public boolean info(int button, float x, float y) {
                     if (DungeonScreen.this.confirmButton.getConfirmState() != ConfirmButton.ConfirmState.UsingDice
                        && DungeonScreen.this.confirmButton.getConfirmState() != ConfirmButton.ConfirmState.AllDiceUsed) {
                        return super.info(button, x, y);
                     } else {
                        DungeonScreen.this.killAllEnemies();
                        return true;
                     }
                  }
               }
            );
      }

      this.confirmButton.setInputBorder(com.tann.dice.Main.self().control.getConfirmButtonThumbpadRadius(), 1);
      this.addActor(this.confirmButton);
      this.confirmButton.setPosition(this.getWidth() - this.confirmButton.getWidth(), -this.diceRollButton.getHeight());
      if (OptionLib.GAP.c() > 0) {
         this.confirmButton.setX(this.abilityHolder.getX() + this.abilityHolder.getWidth());
      }

      this.slideButton(this.confirmButton, false, false);
      this.confirmButton.setSpecialBorderRight(true);
      this.doneRollingButton = new ConfirmButton(getBotButtWidth(), getBottomButtonHeight());
      this.doneRollingButton.setState(ConfirmButton.ConfirmState.RollingDice);
      this.doneRollingButton.setRunnable(new Runnable() {
         @Override
         public void run() {
            DungeonScreen.this.popAllLight();
            DungeonScreen.this.confirmClicked(true);
         }
      }, new Runnable() {
         @Override
         public void run() {
            if (OptionLib.LONGTAP_END.c()) {
               DungeonScreen.this.popAllLight();
               DungeonScreen.this.rollManager.requestPlayerRoll();
            }
         }
      });
      this.doneRollingButton.setInputBorder(com.tann.dice.Main.self().control.getConfirmButtonThumbpadRadius(), 1);
      this.addActor(this.doneRollingButton);
      this.doneRollingButton.setPosition(this.getWidth() - this.doneRollingButton.getWidth(), -this.diceRollButton.getHeight());
      if (OptionLib.GAP.c() > 0) {
         this.doneRollingButton.setX(this.abilityHolder.getX() + this.abilityHolder.getWidth());
      }

      this.slideButton(this.doneRollingButton, false, false);
      this.doneRollingButton.setSpecialBorderRight(true);
      this.undoButton = new Button(getBotButtWidth(), getBottomButtonHeight(), null, new Runnable() {
         @Override
         public void run() {
            DungeonScreen.this.requestUndo();
         }
      }, new Runnable() {
         @Override
         public void run() {
            if (OptionLib.LONGTAP_END.c()) {
               DungeonScreen.this.popAllLight();
               DungeonScreen.this.confirmClicked(true);
            }
         }
      }) {
         @Override
         public void draw(Batch batch, float parentAlpha) {
            super.draw(batch, parentAlpha);
            DungeonScreen.this.undoActor
               .setPosition(
                  this.getX() + this.getWidth() / 2.0F - DungeonScreen.this.undoActor.getWidth() / 2.0F,
                  this.getY() + this.getHeight() / 2.0F - DungeonScreen.this.undoActor.getHeight() / 2.0F
               );
            DungeonScreen.this.undoActor.draw(batch, parentAlpha);
         }
      };
      this.undoButton.setSpecialBorderRight(false);
      this.setupUndoActor();
      this.undoButton.setBorder(Colours.dark, Colours.purple);
      this.addActor(this.undoButton);
      this.undoButton.setPosition(0.0F, -this.diceRollButton.getHeight());
      this.slideButton(this.undoButton, false, false);
      this.undoButton.setInputBorder(com.tann.dice.Main.self().control.getConfirmButtonThumbpadRadius(), 0);
      this.refreshTopButtonsPanel();
      this.hiddenInventoryButton = new StandardButton("[orange]Inventory", Colours.orange);
      this.hiddenInventoryButton.setRunnable(new Runnable() {
         @Override
         public void run() {
            if (PhaseManager.get().getPhase().showCornerInventory()) {
               LevelEndPanel.showPartyPanel();
            } else {
               Sounds.playSound(Sounds.error);
            }
         }
      });
      this.addActor(this.hiddenInventoryButton);
      this.hiddenInventoryButton.setPosition(0.0F, -this.hiddenInventoryButton.getHeight());
   }

   private void setupUndoActor() {
      if (this.undoActor != null) {
         this.undoActor.remove();
      }

      Phase phase = PhaseManager.get().getPhase();
      boolean undoToReroll = !this.fightLog.canUndo() && phase instanceof TargetingPhase;
      boolean showRerolls = undoToReroll || phase instanceof TargetingPhase;
      this.undoActor = makeUndoActor(undoToReroll, showRerolls ? ((TargetingPhase)phase).getUnusedRolls() : 0);
   }

   public static Actor makeUndoActor(boolean undoToReroll, int rolls) {
      Pixl pix = new Pixl(3);
      Pixl textPix = new Pixl(1);
      String pr = Words.plural("roll", rolls);
      if (undoToReroll) {
         textPix.row();
         String tag = rolls == 0 ? "[red]" : "[grey]";
         String rollString = rolls + "";
         rollString = rollString + " " + pr;
         if (OptionLib.GAP.c() > 0) {
            rollString = rolls + "";
         }

         textPix.row().text(tag + rollString);
      } else {
         textPix.text("Undo");
         if (rolls > 0) {
            if (com.tann.dice.Main.self().translator.shouldTranslate()) {
               textPix.row().text("[grey]" + rolls + " " + pr);
            } else {
               textPix.row().text("[grey](" + rolls + " " + pr + ")");
            }
         }
      }

      if (com.tann.dice.Main.isPortrait()) {
         pix.image(Images.undo, Colours.light).row(5).actor(textPix.pix());
      } else {
         pix.image(Images.undo, Colours.light).gap(5).actor(textPix.pix());
      }

      return pix.pix();
   }

   public void toggleHiddenInventory(boolean show) {
      if (this.getDungeonContext().allowInventory()) {
         int gap = 4;
         this.hiddenInventoryButton.addAction(Actions.moveTo(gap, show ? gap : -this.hiddenInventoryButton.getHeight(), 0.3F, Chrono.i));
      }
   }

   public void requestUndo() {
      boolean fightLogUndid = this.fightLog.undo(false);
      this.popAllLight();
      if (fightLogUndid) {
         this.undosInARow++;
         this.getTutorialManager().onAction(TutorialManager.TutorialAction.Undo, this.undosInARow);
         this.getDungeonContext().getStatsManager().onUndo(this.undosInARow);
         if (this.undosInARow > 3 && this.undosInARow % 4 == 0) {
            this.externalChatEvent(ChatStateEvent.Undizzy);
         }

         this.mildSave();
      } else {
         Phase current = PhaseManager.get().getPhase();
         if (!(current instanceof TargetingPhase)) {
            return;
         }

         TargetingPhase tp = (TargetingPhase)current;
         int rolls = tp.getUnusedRolls();
         if (rolls == 0) {
            Sounds.playSound(Sounds.error);
            return;
         }

         this.undosInARow++;
         PhaseManager.get().removePhaseClass(TargetingPhase.class);
         PhaseManager.get().pushPhase(new PlayerRollingPhase(rolls));
         Sounds.playSound(Sounds.flap);
         BulletStuff.resetAlignment();
      }
   }

   public void nonUndo() {
      this.undosInARow = 0;
   }

   public void refreshTopButtonsPanel() {
      if (this.optionsButtonsGroup != null) {
         this.optionsButtonsGroup.remove();
      }

      this.optionsButtonsGroup = DungeonUtils.makeButtonsGroup(this.dungeonContext, this.fightLog, this);
      this.addActor(this.optionsButtonsGroup);
      DungeonUtils.placeButtonsGroup(this.optionsButtonsGroup, com.tann.dice.Main.isPortrait());
      TannStageUtils.putBehindAlwaysOnTop(this.optionsButtonsGroup);
   }

   public boolean isLoading() {
      return this.loading;
   }

   public TutorialManager getTutorialManager() {
      return this.tutorialManager;
   }

   public static void clearStaticReference() {
      BulletStuff.reset();
      PhaseManager.get().clearPhases();
      self = null;
   }

   @Override
   public void snapshotChanged(FightLog.Temporality temporality, Snapshot newSnapshot) {
      switch (temporality) {
         case Visual:
            List<SnapshotEvent> events = newSnapshot.getEvents();
            if (!this.isLoading()) {
               for (int i = this.stateIndex; i < events.size(); i++) {
                  SnapshotEvent se = events.get(i);
                  se.act(this.abilityHolder);
               }
            }

            this.stateIndex = events.size();
            this.enemy.setEntities(newSnapshot, newSnapshot.getEntities(false, null), newSnapshot.getReinforcements());
            this.hero.setEntities(newSnapshot, newSnapshot.getEntities(true, null), new ArrayList<>());
         default:
            this.setupUndoActor();
            this.somethingChanged();
            int newTurn = newSnapshot.getTurn();
            if (newTurn != this.lastTurn) {
               this.refreshTopButtonsPanel();
            }

            this.lastTurn = newTurn;
      }
   }

   public void slideSpellHolder(AbilityHolder.TuckState tuckState, boolean instant) {
      if (get().isLoading()) {
         instant = true;
      }

      this.abilityHolder.tuck(tuckState, instant);
   }

   public void restart(boolean countsAsLoss) {
      this.restart(countsAsLoss, AntiCheeseRerollInfo.makeBlank());
   }

   public void restart(boolean countsAsLoss, AntiCheeseRerollInfo antiCheeseRerollInfo) {
      if (countsAsLoss) {
         try {
            this.dungeonContext.logDefeatBackground(this.fightLog.makeSnapshot());
         } catch (Exception var4) {
            TannLog.log("Failed to merge stats: " + var4.getMessage(), TannLog.Severity.error);
            var4.printStackTrace();
         }
      }

      this.dungeonContext.getContextConfig().clearSave();
      GameStart.start(this.dungeonContext.getContextConfig().makeContext(antiCheeseRerollInfo));
   }

   @Override
   public String getReportString() {
      return this.reportStringSave(true);
   }

   public String reportStringSave(boolean mini) {
      if (this.getDungeonContext() != null) {
         String s = Prefs.getString(get().getDungeonContext().getContextConfig().getGeneralSaveKey(), null);
         if (s == null) {
            return "invalid save";
         } else {
            SaveStateData ssd = (SaveStateData)com.tann.dice.Main.getJson().fromJson(SaveStateData.class, s);
            if (mini) {
               ssd.trimContextDataForReport();
            }

            String result = com.tann.dice.Main.getJson(mini).toJson(ssd);
            if (mini) {
               result = PasteMode.encloseBackticks(result);
            }

            return result;
         }
      } else {
         return null;
      }
   }

   public void enterPhase(Phase phase) {
      this.toggleHiddenInventory(phase.showCornerInventory());
      this.setupUndoActor();
   }

   public void onLock() {
      this.getTutorialManager().onLock(get().getFightLog().getSnapshot(FightLog.Temporality.Present).getEntities(true, false));
      if (this.allHeroesLockedOrLocking() && PhaseManager.get().getPhase() instanceof PlayerRollingPhase) {
         this.confirmClicked(false);
      }
   }

   private boolean allHeroesLockedOrLocking() {
      for (Hero h : this.fightLog.getSnapshot(FightLog.Temporality.Present).getAliveHeroEntities()) {
         Die.DieState st = h.getDie().getState();
         if (!st.isLockedOrLocking()) {
            return false;
         }
      }

      return true;
   }

   public boolean anyDeathAnimationsOngoing() {
      return this.hero.anyDeathAnimationsOngoing() || this.enemy.anyDeathAnimationsOngoing();
   }

   public void makeDust() {
      Sounds.playSound(Sounds.dust);
      Dust.addDust(this);
   }

   public List<Stat> getStoredMergedList() {
      return this.storedMergedList;
   }

   private void saveAntiCheese() {
      AnticheeseData existing = this.dungeonContext.getContextConfig().getAnticheese();
      String s = this.reportStringSave(false);
      if (existing == null) {
         AnticheeseData acd = new AnticheeseData(s);
         this.dungeonContext.getContextConfig().saveAnticheese(acd);
      } else {
         existing.setSaveState(s);
         this.dungeonContext.getContextConfig().saveAnticheese(existing);
      }
   }

   public void resetFromSetup() {
      TannStageUtils.clearActorsOfType(this, FXContainer.class);
   }

   public static boolean checkActive(Ent de) {
      return get() != null && get().getFightLog() != null ? get().getFightLog().getActiveEntities().contains(de) : false;
   }

   @Override
   public void afterSet() {
      PhaseManager.get().activateCurrentPhase();
   }

   public void refreshTimer() {
      if (this.tt != null) {
         this.tt.remove();
      }

      if (OptionLib.SHOW_TIMER.c()) {
         this.addActor(this.tt = new SpeedrunTimer(this.dungeonContext));
         this.tt.toBack();
         this.tt.setY((int)(com.tann.dice.Main.height - this.tt.getHeight() - 3.0F));
         this.tt
            .setX(
               com.tann.dice.Main.isPortrait()
                  ? com.tann.dice.Main.width / 2 + 4
                  : this.optionsButtonsGroup.getX() + this.optionsButtonsGroup.getWidth() + 2.0F
            );
      }
   }

   public void refreshClock() {
      if (this.clock != null) {
         this.clock.remove();
      }

      if (OptionLib.SHOW_CLOCK.c()) {
         this.addActor(this.clock = new Clock());
         this.clock.toBack();
         this.clock
            .setX(
               com.tann.dice.Main.isPortrait()
                  ? com.tann.dice.Main.width / 2 - this.clock.getWidth() - 4.0F
                  : this.optionsButtonsGroup.getX() - this.clock.getWidth() - 4.0F
            );
         this.clock.setY((int)(com.tann.dice.Main.height - this.clock.getHeight() - 3.0F));
      }
   }

   public static DungeonContext getCurrentContextIfInGame() {
      DungeonScreen ds = getCurrentScreenIfDungeon(true);
      if (ds == null) {
         return null;
      } else {
         DungeonContext dc = ds.getDungeonContext();
         return dc == null ? null : dc;
      }
   }

   public static DungeonScreen getCurrentScreenIfDungeon(boolean allowLoading) {
      Screen s = com.tann.dice.Main.getCurrentScreen();
      if (allowLoading) {
         s = get();
      }

      return s instanceof DungeonScreen ? (DungeonScreen)s : null;
   }

   public void manualFlee() {
      this.popAllMedium();
      if (!com.tann.dice.Main.self().masterStats.allowFlee()) {
         List<EntState> states = this.getFightLog().getSnapshot(FightLog.Temporality.Present).getStates(false, false);
         this.showDialog("[purple]" + SurrenderPhase.describeGroupOfEnemies(states) + " " + Words.plural("does", states.size()) + " not allow you to flee");
      } else {
         if (this.canFleePhase()) {
            PhaseManager.get().getPhase().deactivate();
            this.getFightLog().addCommand(new SimpleCommand(null, new SimpleTargetable(null, new EffBill().friendly().group().flee().bEff())), false);
         } else {
            this.showDialog("[red]cannot flee here...");
         }
      }
   }

   public boolean canFleePhase() {
      return PhaseManager.get().getPhase().canFlee() && !this.fightLog.getSnapshot(FightLog.Temporality.Present).isEnd();
   }

   public static int getBotButtWidth() {
      float base = 84.0F;
      if (com.tann.dice.Main.isPortrait()) {
         return (int)(base * 0.5F);
      } else {
         return OptionLib.GAP.c() > 0 ? (int)(base * 0.7F) : (int)base;
      }
   }

   public static int getBottomButtonHeight() {
      return (int)(29.0F * (com.tann.dice.Main.isPortrait() ? 2.0F : 1.0F));
   }

   @Override
   public boolean skipMonkey() {
      return PhaseManager.get().getPhase() instanceof EnemyRollingPhase;
   }

   public EntContainer getContainer(boolean player) {
      return player ? this.hero : this.enemy;
   }

   public void allHeroDiceLanded() {
      this.abilityHolder.allDiceLanded();
   }

   public void heroDiceRolled() {
      this.abilityHolder.heroDiceRolled();
   }

   public static boolean isWish() {
      return self != null && self.dungeonContext != null && self.dungeonContext.isWishable();
   }

   private void externalChatEvent(ChatStateEvent cse) {
      if (!this.hero.getEntities().isEmpty()) {
         cse.actWithChance(Tann.pick(this.hero.getEntities()).getEntPanel());
      }
   }

   public String tryTinyPaste() {
      tinyPasting = true;
      SaveState ss = this.makeSaveState();
      if (ss == null) {
         tinyPasting = false;
         return null;
      } else {
         String s = com.tann.dice.Main.getJson(true).toJson(ss.toData().trimContextDataForReport());
         tinyPasting = false;
         return s == null ? null : PasteMode.encloseBackticks(s);
      }
   }

   @Override
   public void showDialog(String s, Color border) {
      if (this.abilityHolder != null) {
         this.abilityHolder.addWisp(s);
      } else {
         super.showDialog(s, border);
      }
   }

   @Override
   public boolean needsExtraRender() {
      return TannStageUtils.active(this.clock) || TannStageUtils.active(this.tt);
   }
}

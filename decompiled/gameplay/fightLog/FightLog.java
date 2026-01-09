package com.tann.dice.gameplay.fightLog;

import com.tann.dice.gameplay.content.ent.Ent;
import com.tann.dice.gameplay.content.ent.Hero;
import com.tann.dice.gameplay.content.ent.Monster;
import com.tann.dice.gameplay.content.ent.die.Die;
import com.tann.dice.gameplay.content.ent.die.EntDie;
import com.tann.dice.gameplay.content.ent.type.lib.MonsterTypeLib;
import com.tann.dice.gameplay.context.DungeonContext;
import com.tann.dice.gameplay.effect.eff.Eff;
import com.tann.dice.gameplay.effect.eff.EffType;
import com.tann.dice.gameplay.effect.targetable.Targetable;
import com.tann.dice.gameplay.fightLog.command.Command;
import com.tann.dice.gameplay.fightLog.command.DieCommand;
import com.tann.dice.gameplay.fightLog.command.EndTurnCommand;
import com.tann.dice.gameplay.fightLog.command.StartTurnCommand;
import com.tann.dice.gameplay.fightLog.listener.SnapshotChangeListener;
import com.tann.dice.gameplay.fightLog.listener.VictoryLossListener;
import com.tann.dice.gameplay.phase.PhaseManager;
import com.tann.dice.gameplay.phase.endPhase.runEnd.RunEndPhase;
import com.tann.dice.gameplay.progress.StatSnapshot;
import com.tann.dice.gameplay.progress.stats.StatUpdate;
import com.tann.dice.gameplay.progress.stats.stat.Stat;
import com.tann.dice.gameplay.trigger.global.Global;
import com.tann.dice.screens.dungeon.DungeonScreen;
import com.tann.dice.screens.dungeon.RollManager;
import com.tann.dice.statics.bullet.BulletStuff;
import com.tann.dice.statics.sound.Sounds;
import com.tann.dice.test.util.TestRunner;
import com.tann.dice.util.Tann;
import com.tann.dice.util.TannLog;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FightLog {
   private Set<FightLog.Temporality> updatedTemporalities = new HashSet<>();
   private Map<Command, Snapshot> snapshotMap = new HashMap<>();
   private Snapshot baseSnapshot = new Snapshot(this);
   private List<Command> pastCommands = new ArrayList<>();
   private List<Command> futureCommands = new ArrayList<>();
   boolean awaitingTurnOver;
   final DungeonContext dungeonContext;
   List<StatUpdate> statUpdates = new ArrayList<>();
   Command visualCommand;
   Map<FightLog.Temporality, List<SnapshotChangeListener>> snapshotListeners = new HashMap<>();
   List<VictoryLossListener> victoryLossListeners = new ArrayList<>();
   boolean endNotified;
   private List<Command> commandHistory = new ArrayList<>();
   private boolean failed;
   private static final int MAX_COMMANDS = 2000;
   private static final int MAX_ENTITIES = 1000;

   public FightLog(DungeonContext dungeonContext) {
      this.dungeonContext = dungeonContext;
   }

   public FightLog(List<Hero> startingHeroes, List<Monster> startingMonsters, List<String> commandData, String sideState, DungeonContext context) {
      this(context);
      this.setup(startingHeroes, startingMonsters);
      List<String> playerCommands = new ArrayList<>();
      List<String> enemyCommands = new ArrayList<>();

      for (int i = 0; i < commandData.size(); i++) {
         String commandString = commandData.get(i);
         Command.CommandType type = Command.getType(commandString);
         switch (type) {
            case Start:
            case Simple:
            default:
               break;
            case Hero:
               playerCommands.add(commandString);
               break;
            case Monster:
               enemyCommands.add(commandString);
               break;
            case End:
               EndTurnCommand etc = new EndTurnCommand(commandString);
               if (!etc.player) {
                  this.enemyTurn();
                  this.resetTurn(true);
                  this.addSavedCommands(enemyCommands, false);
                  this.addSavedCommands(playerCommands, true);
                  playerCommands.clear();
                  enemyCommands.clear();
               }
         }
      }

      for (Command c : this.pastCommands) {
         c.forceImpacted();
      }

      List<Ent> entities = this.getSnapshot(FightLog.Temporality.Present).getEntities(null, null);
      if (isSpecialSave(sideState)) {
         sideState = sideState.substring(1);
      }

      if (entities.size() == sideState.length()) {
         for (int i = 0; i < entities.size(); i++) {
            EntDie d = entities.get(i).getDie();
            d.setSide(sideState.charAt(i) - '0');
         }
      } else {
         System.out.println("failed to deserialise sides");
      }

      this.updateAllTemporalities();
   }

   public boolean triggerAllHeroOnLandDueToSave() {
      boolean activated = false;
      List<EntState> presentStates = new ArrayList<>(this.getSnapshot(FightLog.Temporality.Present).getStates(true, false));
      Collections.shuffle(presentStates);

      for (EntState e : presentStates) {
         Die d = e.getEnt().getDie();
         if (d.getState() != Die.DieState.Locked) {
            activated |= ((Hero)e.getEnt()).stoppedGameplayImplications();
         }
      }

      return activated;
   }

   public boolean triggerAllMonsterOnLandDueToSave() {
      for (EntState e : this.getSnapshot(FightLog.Temporality.Present).getStates(false, false)) {
         ((Monster)e.getEnt()).onLockGameplayImplications();
      }

      return true;
   }

   public void setup(List<Hero> heroes, List<Monster> monsters) {
      DungeonScreen ds = DungeonScreen.get();
      if (ds != null) {
         DungeonScreen.get().resetFromSetup();
      }

      this.commandHistory.clear();
      this.futureCommands.clear();
      this.pastCommands.clear();
      this.snapshotMap.clear();
      this.baseSnapshot = new Snapshot(this);

      for (Global gt : this.baseSnapshot.getGlobals()) {
         gt.affectStartMonsters(monsters);
      }

      for (Hero h : heroes) {
         h.setRealFightLog(this);
      }

      for (Monster m : monsters) {
         m.setRealFightLog(this);
      }

      this.setHeroes(heroes);
      this.setEnemies(monsters);
      this.baseSnapshot.setupCombat();
      this.recalculateToFuture();
      this.updateAllTemporalities();
   }

   public void resetForNewFight() {
      this.endNotified = false;
      this.updatedTemporalities.clear();
      this.snapshotMap.clear();
      this.baseSnapshot = new Snapshot(this);
      this.commandHistory.clear();
      this.pastCommands.clear();
      this.futureCommands.clear();
      this.recalculateToFuture();
   }

   public void setEnemies(List<Monster> monsters) {
      this.baseSnapshot.addEntities(monsters);
   }

   public void setHeroes(List<Hero> heroes) {
      this.baseSnapshot.addEntities(heroes);
   }

   public void resetTurn(boolean skipUpdate) {
      if (!skipUpdate && !this.dungeonContext.skipStats()) {
         StatSnapshot ss = this.makeSnapshot();

         for (StatUpdate su : this.statUpdates) {
            su.updateEndOfRound(ss);
         }
      }

      this.awaitingTurnOver = false;
      this.baseSnapshot = this.getSnapshot(FightLog.Temporality.Future).copy();
      this.commandHistory.addAll(this.pastCommands);
      this.pastCommands.clear();
      this.futureCommands.clear();
      this.addCommand(new StartTurnCommand(), false);
      this.addCommand(new EndTurnCommand(true), true);
      this.addCommand(new EndTurnCommand(false), true);
      this.recalculateToFuture();
      this.updateAllTemporalities();
      if (!skipUpdate && !this.checkEnd()) {
         BulletStuff.refreshEntities(this.getSnapshot(FightLog.Temporality.Present).getAliveEntities());
         PhaseManager.get().popPhase();
      }
   }

   private void manageOnSaveEffects(Snapshot prePresent, Command command, List<Hero> previouslyAlive) {
      if (!(command instanceof DieCommand) || command.getSource().isPlayer()) {
         List<Hero> nowFutureAlive = this.getSnapshot(FightLog.Temporality.Future).getAliveHeroEntities();
         boolean didSomething = false;
         if (!nowFutureAlive.equals(previouslyAlive)) {
            EntState saverState = null;
            if (command.getSource() != null) {
               saverState = this.getState(FightLog.Temporality.Present, command.getSource());
            }

            for (int i = 0; i < nowFutureAlive.size(); i++) {
               Hero saved = nowFutureAlive.get(i);
               if (!previouslyAlive.contains(saved) && (saverState == null || saverState.canSee(saved))) {
                  if (saverState != null) {
                     didSomething |= saverState.onRescue(saved, command);
                  }

                  didSomething |= command.onRescue(saved, command.getSource(), this.getSnapshot(FightLog.Temporality.Present), prePresent);
               }
            }
         }

         if (didSomething) {
            this.getSnapshot(FightLog.Temporality.Present).checkHpLimits(command.getSource(), command);
            this.recalculateToFuture();
            this.manageOnSaveEffects(prePresent, command, nowFutureAlive);
         }
      }
   }

   private void manageOnKillEffects(Snapshot prePresent, Command command, List<Ent> previouslyAlive) {
      List<Ent> nowAlive = this.getSnapshot(FightLog.Temporality.Present).getAliveEntities();
      boolean didSomething = false;
      if (!nowAlive.equals(previouslyAlive)) {
         EntState killerState = null;
         if (command.getSource() != null) {
            killerState = this.getState(FightLog.Temporality.Present, command.getSource());
         }

         int kills = 0;

         for (int i = 0; i < previouslyAlive.size(); i++) {
            Ent maybeKilled = previouslyAlive.get(i);
            if (!nowAlive.contains(maybeKilled)) {
               EntState demise = this.getState(FightLog.Temporality.Present, maybeKilled);
               if (demise == null || !demise.isFled()) {
                  kills++;
                  if (killerState != null) {
                     didSomething |= killerState.onKill(command, maybeKilled);
                  }
               }
            }
         }

         if (kills > 0) {
            didSomething |= command.onKill(command.getSource(), prePresent, this.getSnapshot(FightLog.Temporality.Present));
         }

         if (killerState != null) {
            killerState.onTotalKills(kills);
         }
      }

      if (didSomething) {
         this.getSnapshot(FightLog.Temporality.Present).checkHpLimits(command.getSource(), command);
         this.recalculateToFuture();
         this.manageOnKillEffects(prePresent, command, nowAlive);
      }
   }

   public void recalculateToFuture() {
      Snapshot current = this.getSnapshot(FightLog.Temporality.Present);

      for (int i = 0; i < this.futureCommands.size(); i++) {
         Command c = this.futureCommands.get(i);
         EntState sourceState = current.getState(c.getSource());
         if (sourceState != null && sourceState.skipTurn()) {
            this.snapshotMap.put(c, current);
         } else {
            current = current.copy();
            c.enact(current);
            this.snapshotMap.put(c, current);
         }
      }

      this.updatedTemporalities.add(FightLog.Temporality.Future);
   }

   public void tick() {
      if (!this.endNotified) {
         Command mostRecentlyImpacted = null;
         int i = 0;

         for (int pastCommandsSize = this.pastCommands.size(); i < pastCommandsSize; i++) {
            Command c = this.pastCommands.get(i);
            if (c.skipped()) {
               mostRecentlyImpacted = c;
            } else {
               c.checkImpacted();
               if (!c.getImpacted()) {
                  break;
               }

               mostRecentlyImpacted = c;
            }
         }

         if (mostRecentlyImpacted != this.visualCommand) {
            this.visualCommand = mostRecentlyImpacted;
            this.updatedTemporalities.add(FightLog.Temporality.Visual);
         }

         this.manageSnapshotNotify();

         for (int ix = 0; ix < this.pastCommands.size(); ix++) {
            Command c = this.pastCommands.get(ix);
            if (!c.getFinishedAnimating()) {
               boolean playerSource = c.getSource() == null || c.getSource().isPlayer();
               if (!c.getStartedAnimating()) {
                  c.startAnimation(this.getSnapshotBefore(c));
               }

               if (!playerSource || c instanceof EndTurnCommand) {
                  return;
               }
            }
         }

         this.checkEnd();
         if (this.awaitingTurnOver) {
            this.resetTurn(false);
         }
      }
   }

   public void instantCatchup() {
      for (Command pastCommand : this.pastCommands) {
         pastCommand.overrideSkip();
      }
   }

   private void updateStats(Snapshot previousFuture) {
      if (this.pastCommands.size() > 1) {
         StatSnapshot ss = this.makeSnapshot(previousFuture);
         List<Global> afterGlobals = ss.afterCommand.getGlobals();

         for (int i = 0; i < afterGlobals.size(); i++) {
            Global g = afterGlobals.get(i);
            g.statSnapshotCheck(ss);
         }

         if (!this.dungeonContext.skipStats()) {
            Map<String, Stat> statMap = this.getContext().getStatsManager().getStatsMap();
            if (ss != null) {
               for (StatUpdate su : this.getStatUpdates()) {
                  su.updateAfterCommand(ss, statMap);
               }
            }
         }
      }
   }

   public StatSnapshot makeSnapshot() {
      return this.makeSnapshot(this.getSnapshot(FightLog.Temporality.Future));
   }

   private StatSnapshot makeSnapshot(Snapshot previousFuture) {
      Command origin = null;
      if (this.pastCommands.size() > 0) {
         origin = this.pastCommands.get(this.pastCommands.size() - 1);
         return new StatSnapshot(
            origin,
            this.pastCommands,
            this.futureCommands,
            this.getSnapshot(FightLog.Temporality.StartOfTurn),
            this.getSnapshotBefore(origin),
            this.getSnapshotAfter(origin),
            this.getSnapshot(FightLog.Temporality.Future),
            previousFuture,
            this.dungeonContext
         );
      } else {
         return new StatSnapshot(
            null,
            this.pastCommands,
            this.futureCommands,
            this.getSnapshot(FightLog.Temporality.StartOfTurn),
            null,
            this.getSnapshot(FightLog.Temporality.Future),
            this.getSnapshot(FightLog.Temporality.Future),
            previousFuture,
            this.dungeonContext
         );
      }
   }

   public List<StatUpdate> getStatUpdates() {
      return this.statUpdates;
   }

   public void registerStatUpdate(StatUpdate su) {
      this.statUpdates.add(su);
   }

   public Snapshot getSnapshot() {
      return this.getSnapshot(FightLog.Temporality.Present);
   }

   public Snapshot getSnapshot(FightLog.Temporality temporality) {
      return this.getSnapshotInternal(temporality);
   }

   private Snapshot getSnapshotInternal(FightLog.Temporality temporality) {
      switch (temporality) {
         case Base:
            return this.baseSnapshot;
         case StartOfTurn:
            Command lastStart = this.visualCommand;

            for (Command c : this.pastCommands) {
               if (c instanceof StartTurnCommand) {
                  lastStart = c;
               }
            }

            return this.snapshotMap.get(lastStart);
         case Visual:
            if (this.visualCommand != null) {
               Snapshot visual = this.snapshotMap.get(this.visualCommand);
               if (visual != null) {
                  return visual;
               }
            }

            return this.baseSnapshot;
         case Present:
            if (this.pastCommands.size() != 0) {
               Snapshot presentSnapshot = this.snapshotMap.get(this.pastCommands.get(this.pastCommands.size() - 1));
               if (presentSnapshot != null) {
                  return presentSnapshot;
               }
            }

            return this.baseSnapshot;
         case Future:
            if (this.futureCommands.size() == 0) {
               return this.getSnapshotInternal(FightLog.Temporality.Present);
            } else {
               Snapshot futureSnapshot = this.snapshotMap.get(this.futureCommands.get(this.futureCommands.size() - 1));
               if (futureSnapshot == null) {
                  return this.getSnapshotInternal(FightLog.Temporality.Present);
               }

               return futureSnapshot;
            }
         default:
            throw new RuntimeException("Attempting to get a snapshot with temporality " + temporality);
      }
   }

   public EntState get(Ent ent, FightLog.Temporality temporality) {
      return this.getSnapshot(temporality).getState(ent);
   }

   public void addCommand(Command command, boolean future) {
      if (!this.invalidState()) {
         Snapshot present = this.getSnapshot(FightLog.Temporality.Present);
         if (!present.isEnd()) {
            Snapshot futureCopy = this.getSnapshot(FightLog.Temporality.Future).copy();
            command.preEnact(present);
            command.lockSave(present);
            List<Hero> previouslyAliveHeroes = this.getSnapshot(FightLog.Temporality.Future).getAliveHeroEntities();
            List<Ent> previouslyAliveEntities = this.getSnapshot(FightLog.Temporality.Present).getAliveEntities();
            this.updatedTemporalities.add(FightLog.Temporality.Future);
            if (future) {
               if (command instanceof EndTurnCommand) {
                  this.futureCommands.add(command);
               } else {
                  this.futureCommands.add(this.futureCommands.size() - 1, command);
                  this.sortFutureActions();
               }
            } else {
               Snapshot newCurrent = this.getSnapshot(FightLog.Temporality.Present).copy();
               command.enact(newCurrent);
               this.pastCommands.add(command);
               this.snapshotMap.put(command, newCurrent);
               this.updatedTemporalities.add(FightLog.Temporality.Present);
            }

            this.getSnapshot(FightLog.Temporality.Present).somethingChangedAllEntities();
            this.recalculateToFuture();
            this.manageOnSaveEffects(present, command, previouslyAliveHeroes);
            this.manageOnKillEffects(present, command, previouslyAliveEntities);
            if (!future) {
               this.updateStats(futureCopy);
            }

            this.updateAllTemporalities();
            this.markNotUndo();
         }
      }
   }

   private boolean invalidState() {
      if (this.tooManyCommands()) {
         this.cmdsErr();
         return true;
      } else if (this.tooManyEntities()) {
         this.entitiesErr();
         return true;
      } else {
         return false;
      }
   }

   public void somethingChangedAllSnapshots() {
      this.getSnapshot(FightLog.Temporality.Base).somethingChangedAllEntities();
      this.getSnapshot(FightLog.Temporality.Visual).somethingChangedAllEntities();
      this.getSnapshot(FightLog.Temporality.Present).somethingChangedAllEntities();
      this.getSnapshot(FightLog.Temporality.Future).somethingChangedAllEntities();
   }

   private void markNotUndo() {
      DungeonScreen ds = DungeonScreen.get();
      if (ds != null) {
         ds.nonUndo();
      }
   }

   public void addCommand(Targetable targetable, Ent target, boolean future) {
      if (target != null && !target.isPlayer() && targetable.getDerivedEffects(this.getSnapshot(FightLog.Temporality.Present)).getType() == EffType.Damage) {
         com.tann.dice.Main.getSettings().setHasSworded(true);
      }

      Command c = Command.create(targetable, target);
      this.addCommand(c, future);
   }

   private void sortFutureActions() {
      Collections.sort(this.futureCommands, new Comparator<Command>() {
         public int compare(Command o1, Command o2) {
            Ent ent1 = o1.getSource();
            Ent ent2 = o2.getSource();
            if (ent1 != null && ent2 != null) {
               List<Ent> entities = FightLog.this.getSnapshot(FightLog.Temporality.Present).getAliveEntities();
               return entities.indexOf(ent1) - entities.indexOf(ent2);
            } else {
               return 0;
            }
         }
      });
   }

   public void enemyTurn() {
      this.awaitingTurnOver = true;
      this.pastCommands.addAll(this.futureCommands);
      this.futureCommands.clear();
      this.updatedTemporalities.add(FightLog.Temporality.Present);
   }

   public boolean undo(boolean force) {
      if (!force && !this.canUndo()) {
         System.err.println("Can't undo, no past actions remaining");
         return false;
      } else if (this.pastCommands.isEmpty()) {
         return false;
      } else {
         Sounds.playSound(Sounds.undo);
         Command topCommand = this.pastCommands.get(this.pastCommands.size() - 1);
         this.pastCommands.remove(topCommand);
         topCommand.undo();
         this.recalculateToFuture();
         this.somethingChangedAllSnapshots();
         this.updateAllTemporalities();
         return true;
      }
   }

   private Command getTopCommandToUndo() {
      return this.pastCommands.size() == 0 ? null : this.pastCommands.get(this.pastCommands.size() - 1);
   }

   public List<? extends Ent> getActiveEntities(boolean player) {
      return this.getSnapshot(FightLog.Temporality.Present).getAliveEntities(player);
   }

   public List<? extends Ent> getActiveEntities() {
      return this.getSnapshot(FightLog.Temporality.Present).getAliveEntities();
   }

   List<Command> getAllCommands() {
      List<Command> result = new ArrayList<>();
      result.addAll(this.pastCommands);
      result.addAll(this.futureCommands);
      return result;
   }

   public boolean checkEnd() {
      if (PhaseManager.get().getPhase() instanceof RunEndPhase) {
         return false;
      } else if (this.endNotified) {
         return true;
      } else {
         int i = 0;

         for (int pastCommandsSize = this.pastCommands.size(); i < pastCommandsSize; i++) {
            Command c = this.pastCommands.get(i);
            if (!c.getFinishedAnimating()) {
               return false;
            }
         }

         if (this.getSnapshot(FightLog.Temporality.Visual).isVictory()) {
            this.onVictory();
            return true;
         } else if (this.getSnapshot(FightLog.Temporality.Visual).isLoss()) {
            this.onDefeat();
            return true;
         } else {
            return false;
         }
      }
   }

   private void onVictory() {
      this.notifyEndOfFight(true);
      this.getSnapshot(FightLog.Temporality.Present).endLevel();
      if (!this.dungeonContext.isAtLastLevel()) {
         this.futureCommands.clear();
         this.pastCommands.clear();
         this.commandHistory.clear();
         List<Hero> heroes = this.getContext().getParty().getHeroes();
         this.baseSnapshot = new Snapshot(this);
         this.baseSnapshot.addEntities(heroes);
         this.baseSnapshot.setupCombat();
         this.snapshotMap.clear();
         this.recalculateToFuture();
         this.updateAllTemporalities();
      }

      this.notifyVictory();
   }

   private void onDefeat() {
      this.notifyEndOfFight(false);
      this.notifyLoss();
   }

   private void notifyEndOfFight(boolean victory) {
      this.awaitingTurnOver = false;
      if (!this.dungeonContext.getContextConfig().skipStats()) {
         StatSnapshot ss = this.makeSnapshot();

         for (StatUpdate su : this.statUpdates) {
            su.endOfFight(ss, victory);
         }
      }
   }

   public boolean canUndo() {
      Command c = this.getTopCommandToUndo();
      return c != null && c.canUndo();
   }

   public EntState getState(FightLog.Temporality temporality, Ent ent) {
      return this.getSnapshot(temporality).getState(ent);
   }

   public boolean anyHidingVisual() {
      return this.getSnapshot(FightLog.Temporality.Visual).anyHidingEnemies();
   }

   public void registerSnapshotListener(SnapshotChangeListener snapshotChangeListener, FightLog.Temporality... requestedTemporailities) {
      for (FightLog.Temporality t : requestedTemporailities) {
         List<SnapshotChangeListener> a = this.snapshotListeners.get(t);
         if (a == null) {
            a = new ArrayList<>();
            this.snapshotListeners.put(t, a);
         }

         if (!a.contains(snapshotChangeListener)) {
            a.add(snapshotChangeListener);
         }
      }
   }

   private void manageSnapshotNotify() {
      if (!TestRunner.isTesting()) {
         for (FightLog.Temporality t : this.updatedTemporalities) {
            Snapshot s = this.getSnapshot(t);
            if (s != null) {
               for (Ent de : s.getEntities(null, null)) {
                  EntState es = s.getState(de);
                  de.setState(t, es);
               }

               if (this.snapshotListeners.get(t) != null) {
                  for (SnapshotChangeListener scl : this.snapshotListeners.get(t)) {
                     scl.snapshotChanged(t, this.getSnapshot(t));
                  }
               }
            }
         }

         this.updatedTemporalities.clear();
      }
   }

   public void registerVictoryLossListener(VictoryLossListener vll) {
      this.victoryLossListeners.add(vll);
   }

   private void notifyVictory() {
      this.endNotified = true;

      for (VictoryLossListener vll : this.victoryLossListeners) {
         vll.victory();
      }
   }

   private void notifyLoss() {
      this.endNotified = true;

      for (VictoryLossListener vll : this.victoryLossListeners) {
         vll.loss();
      }
   }

   public Snapshot getSnapshotAfter(Command command) {
      return this.snapshotMap.get(command);
   }

   public Snapshot getSnapshotBefore(Command command) {
      Command prev = this.getCommandBefore(command);
      return prev == null ? this.getSnapshot(FightLog.Temporality.Base) : this.snapshotMap.get(prev);
   }

   private Command getCommandBefore(Command command) {
      if (command == null) {
         throw new RuntimeException("uhoh null command");
      } else {
         int pastIndex = this.pastCommands.indexOf(command);
         if (pastIndex == 0) {
            return this.commandHistory.size() > 0 ? this.commandHistory.get(this.commandHistory.size() - 1) : null;
         } else if (pastIndex > 0) {
            return this.pastCommands.get(pastIndex - 1);
         } else {
            int futureIndex = this.futureCommands.indexOf(command);
            if (futureIndex == 0) {
               return this.pastCommands.get(this.pastCommands.size() - 1);
            } else if (futureIndex > 0) {
               return this.futureCommands.get(futureIndex - 1);
            } else {
               int historyIndex = this.commandHistory.indexOf(command);
               if (historyIndex > 0) {
                  return this.commandHistory.get(historyIndex - 1);
               } else {
                  return historyIndex == 0 ? null : null;
               }
            }
         }
      }
   }

   private void addSavedCommands(List<String> commandStrings, boolean player) {
      for (String s : commandStrings) {
         Snapshot present = this.getSnapshot(FightLog.Temporality.Present);
         this.addCommand(Command.load(s, present), !player);
      }
   }

   public static boolean isSpecialSave(String sideState) {
      return isSpecialSave(sideState, true) || isSpecialSave(sideState, false);
   }

   public static boolean isSpecialSave(String sideState, boolean hero) {
      return sideState != null && sideState.startsWith(hero ? "h" : "m");
   }

   private static String getSpecialSaveSidesStart() {
      if (RollManager.predictionSavePlayer == null) {
         return "";
      } else {
         return RollManager.predictionSavePlayer ? "h" : "m";
      }
   }

   public String serialiseSides() {
      String result = getSpecialSaveSidesStart();

      for (Ent de : this.getSnapshot(FightLog.Temporality.Present).getEntities(null, null)) {
         int sideIndex = de.getDie().getSideIndex();
         if (sideIndex == -1) {
            if (!de.isPlayer()) {
               sideIndex = 0;
            } else {
               sideIndex = Tann.randomInt(6);
            }
         }

         result = result + sideIndex;
      }

      return result;
   }

   public List<String> serialiseCommands() {
      List<String> result = new ArrayList<>();
      List<Command> commands = this.getAllCommandsIncludingHistory();
      int loopEnd = commands.size();

      for (int i = 0; i < loopEnd; i++) {
         Command c = commands.get(i);
         if (c.getLockedSave() == null) {
            TannLog.log("Error, null command save");
         }

         if (!c.getLockedSave().equals(Command.SKIP)) {
            result.add(c.getLockedSave());
         }
      }

      return result;
   }

   private List<Command> getAllCommandsIncludingHistory() {
      List<Command> all = new ArrayList<>();
      all.addAll(this.commandHistory);
      all.addAll(this.pastCommands);
      all.addAll(this.futureCommands);
      return all;
   }

   public DungeonContext getContext() {
      return this.dungeonContext;
   }

   public void refreshPresentBaseStats() {
      this.getSnapshot(FightLog.Temporality.Base).updateAllBaseStats();
      this.getSnapshot(FightLog.Temporality.Present).updateAllBaseStats();
      this.recalculateToFuture();
   }

   public void updateOutOfCombat() {
      this.baseSnapshot = this.baseSnapshot.copy();
      this.baseSnapshot.updateAllBaseStats();
      if (this.pastCommands.size() > 0) {
         TannLog.log("Updating out of combat with past commands uhoh", TannLog.Severity.error);
      }

      if (this.getSnapshot(FightLog.Temporality.Present) != this.baseSnapshot
         || this.getSnapshot(FightLog.Temporality.Future) != this.baseSnapshot
         || this.getSnapshot(FightLog.Temporality.Visual) != this.baseSnapshot) {
         TannLog.log("Updating out of combat with snapshots working uhoh uhoh", TannLog.Severity.error);
      }

      this.updateAllTemporalities();
   }

   public void updateAllTemporalities() {
      this.updatedTemporalities.addAll(Arrays.asList(FightLog.Temporality.values()));
      this.manageSnapshotNotify();
   }

   public void maybeStart() {
      if (this.pastCommands.size() == 0) {
         this.resetTurn(true);
      }
   }

   public Command getCommandFromSnapshot(Snapshot newSnapshot) {
      for (Command c : this.snapshotMap.keySet()) {
         if (this.snapshotMap.get(c) == newSnapshot) {
            return c;
         }
      }

      return null;
   }

   public DieCommand getDieCommandFromAtOrBeforeSnapshot(Snapshot s) {
      int index = this.getIndexOfCommandFromSnapshot(s);
      if (index == -1) {
         index = this.pastCommands.size() - 1;
      }

      for (int i = index; i >= 0; i--) {
         Command pastCommand = this.pastCommands.get(i);
         if (pastCommand instanceof DieCommand) {
            return (DieCommand)pastCommand;
         }
      }

      return null;
   }

   private int getIndexOfCommandFromSnapshot(Snapshot s) {
      for (Command c : this.snapshotMap.keySet()) {
         if (this.snapshotMap.get(c) == s) {
            return this.pastCommands.indexOf(c);
         }
      }

      return -1;
   }

   public void enemiesSurrendered() {
      Snapshot pres = this.getSnapshot(FightLog.Temporality.Present);
      pres.allFlee();
      this.updateAllTemporalities();
   }

   public Eff getContemporaneousEffect(DieCommand lastTargetableCommand) {
      if (lastTargetableCommand == null) {
         return null;
      } else {
         Snapshot s = this.getSnapshotBefore(lastTargetableCommand);
         return s == null ? null : lastTargetableCommand.dt.getDerivedEffect(s);
      }
   }

   public void resetDueToFiddling() {
      this.resetDueToFiddling(this.dungeonContext.getParty().getHeroes(), MonsterTypeLib.monsterList(this.dungeonContext.getCurrentLevel().getMonsterList()));
   }

   public void resetDueToFiddling(List<Hero> heroes, List<Monster> monsters) {
      this.setup(heroes, monsters);
      BulletStuff.refreshEntities(this.getSnapshot(FightLog.Temporality.Present).getAliveEntities());
   }

   public boolean isVictoryAssured() {
      return this.getSnapshot(FightLog.Temporality.Future).isVictory();
   }

   public void setFailed(boolean failed) {
      this.failed = failed;
   }

   public boolean isFailed() {
      return this.failed;
   }

   public DieCommand getFirstAttackAfter(Snapshot snapshot, Ent ent) {
      Command sourceCommand = this.getCommandFromSnapshot(snapshot);
      if (sourceCommand == null) {
         return getFirstAttack(this.futureCommands, ent, 0);
      } else {
         List<List<Command>> cmdsl = Arrays.asList(this.pastCommands, this.futureCommands);

         for (int llIndex = 0; llIndex < cmdsl.size(); llIndex++) {
            List<Command> cmds = cmdsl.get(llIndex);
            int indexInList = cmds.indexOf(sourceCommand);
            int startIndex = indexInList;
            if (indexInList >= 0) {
               startIndex = indexInList + 1;
            } else if (cmds == this.futureCommands && this.pastCommands.contains(sourceCommand)) {
               startIndex = 0;
            }

            if (startIndex != -1) {
               DieCommand possible = getFirstAttack(cmds, ent, startIndex);
               if (possible != null) {
                  return possible;
               }
            }
         }

         return null;
      }
   }

   private static DieCommand getFirstAttack(List<Command> list, Ent ent, int startIndex) {
      for (int i = startIndex; i < list.size(); i++) {
         Command possible = list.get(i);
         if (possible instanceof DieCommand) {
            DieCommand dc = (DieCommand)possible;
            if (dc.getSource() == ent) {
               return dc;
            }
         }
      }

      return null;
   }

   private boolean tooManyCommands() {
      return this.commandHistory.size() + this.pastCommands.size() + this.futureCommands.size() > 2000;
   }

   private boolean tooManyEntities() {
      return this.getSnapshot().getStates(null, null).size() > 1000;
   }

   private void cmdsErr() {
      this.err("error, too many commands [red]'you lose'[cu]");
   }

   private void entitiesErr() {
      this.err("error, too many entities [red]'you lose'[cu]");
   }

   private void err(String msg) {
      Sounds.playSound(Sounds.error);
      TannLog.error(msg);
      if (DungeonScreen.get() != null) {
         DungeonScreen.get().showDialog(msg);
      }
   }

   public static enum Temporality {
      Base,
      Visual,
      Present,
      Future,
      StartOfTurn;
   }
}

package com.tann.dice.screens.dungeon.panels.tutorial;

import com.tann.dice.gameplay.content.ent.Ent;
import com.tann.dice.gameplay.content.ent.die.Die;
import com.tann.dice.gameplay.effect.eff.Eff;
import com.tann.dice.gameplay.effect.eff.EffType;
import com.tann.dice.gameplay.effect.targetable.Targetable;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.fightLog.FightLog;
import com.tann.dice.gameplay.fightLog.Snapshot;
import com.tann.dice.gameplay.fightLog.command.AbilityCommand;
import com.tann.dice.gameplay.fightLog.command.DieCommand;
import com.tann.dice.gameplay.fightLog.command.TargetableCommand;
import com.tann.dice.gameplay.progress.StatSnapshot;
import com.tann.dice.platform.control.Control;
import com.tann.dice.util.lang.Words;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public abstract class TutorialQuest extends TutorialItem {
   private final String text;

   public TutorialQuest(String text) {
      this(0, text);
   }

   public TutorialQuest(int priority, String text) {
      super(priority);
      this.text = text;
   }

   @Override
   protected String getDisplayText() {
      return "[notranslateall][white]" + (this.complete ? "[checkboxTicked]" : "[checkbox]") + "[cu] " + com.tann.dice.Main.t(this.text);
   }

   public String toString() {
      return "Quest: " + this.text;
   }

   protected static Collection<? extends TutorialQuest> makeRollingPhase() {
      List<TutorialQuest> result = new ArrayList<>();
      String infoTap = com.tann.dice.Main.self().control.getInfoTapString();
      String selectTap = com.tann.dice.Main.self().control.getSelectTapString();
      result.add(new TutorialQuest("Reroll your dice") {
         @Override
         public void onRoll(List<Ent> heroes) {
            this.markCompleted();
            super.onRoll(heroes);
         }
      });
      result.add(new TutorialQuest(infoTap + " a dice to learn what it does") {
         @Override
         public void onAction(TutorialManager.TutorialAction type, Object arg) {
            if (type == TutorialManager.TutorialAction.DieInfo) {
               this.markCompleted();
            }
         }
      });
      result.add(new TutorialQuest(selectTap + " a dice to lock it") {
         @Override
         public void onLock(List<Ent> heroes) {
            int locked = 0;

            for (Ent de : heroes) {
               if (de.getDie().getState().isLockedOrLocking()) {
                  locked++;
               }
            }

            if (locked >= 1) {
               this.markCompleted();
            }
         }
      });
      result.add(new TutorialQuest(2, "Reroll 2 dice only") {
         @Override
         public void onRoll(List<Ent> heroes) {
            int rolling = 0;

            for (Ent de : heroes) {
               if (de.getDie().getState() == Die.DieState.Rolling) {
                  rolling++;
               }
            }

            if (rolling == 2) {
               this.markCompleted();
            }

            super.onRoll(heroes);
         }
      });
      result.add(new TutorialQuest(5, com.tann.dice.Main.self().control.getSelectTapString() + " a monster to see their attack") {
         @Override
         public void onAction(TutorialManager.TutorialAction type, Object arg) {
            if (type == TutorialManager.TutorialAction.SelectMonster) {
               this.markCompleted();
            }
         }
      });
      return result;
   }

   protected static Collection<? extends TutorialQuest> makeTargetingPhase() {
      Control c = com.tann.dice.Main.self().control;
      String infoTap = c.getInfoTapString();
      List<TutorialQuest> result = new ArrayList<>();
      result.addAll(c.getExtraTargetingPhaseQuests());
      result.add(new TutorialQuest(0, c.getSelectTapString() + " on a hero to use their dice") {
         @Override
         public void newStatsSnapshot(StatSnapshot ss) {
            if (ss.origin instanceof DieCommand) {
               this.markCompleted();
            }
         }
      });
      final TutorialQuest shielding = new TutorialQuest(3, "[grey]Shield[cu] some incoming damage ([yellow][hp][cu])") {
         @Override
         public void newStatsSnapshot(StatSnapshot ss) {
            if (ss.origin instanceof TargetableCommand) {
               int futureBlocked = 0;

               for (EntState es : ss.future.getStates(true, null)) {
                  futureBlocked += es.getDamageBlocked();
               }

               if (futureBlocked >= 1) {
                  this.markCompleted();
               }
            }
         }

         @Override
         public boolean isValid(FightLog fightLog) {
            return fightLog.getSnapshot(FightLog.Temporality.Present).getAvailable(EffType.Shield) > 0;
         }
      };
      result.add(shielding);
      result.add(new TutorialQuest(4, "Defeat " + Words.entName(false, false)) {
         @Override
         public void newStatsSnapshot(StatSnapshot ss) {
            if (ss.beforeCommand.getStates(false, true).size() < ss.afterCommand.getStates(false, true).size()) {
               this.markCompleted();
            }
         }

         @Override
         public boolean isValid(FightLog fightLog) {
            return fightLog.getSnapshot(FightLog.Temporality.Present).getKillableMonsters() > 0;
         }
      });
      result.add(new TutorialQuest(2, "Gain [blue]mana[cu] ([white][p][mana][p][cu])") {
         @Override
         public void newStatsSnapshot(StatSnapshot ss) {
            if (ss.afterCommand.getTotalMana() > ss.beforeCommand.getTotalMana()) {
               this.markCompleted();
            }
         }

         @Override
         public boolean isValid(FightLog fightLog) {
            return fightLog.getSnapshot(FightLog.Temporality.Present).getAvailable(EffType.Mana) > 0;
         }
      });
      result.add(new TutorialQuest(4, "Cast a [blue]spell") {
         @Override
         public void newStatsSnapshot(StatSnapshot ss) {
            if (ss.origin instanceof AbilityCommand) {
               this.markCompleted();
            }
         }

         @Override
         public boolean isValid(FightLog fightLog) {
            Snapshot present = fightLog.getSnapshot(FightLog.Temporality.Present);
            return present.getTotalMana() + present.getAvailable(EffType.Mana) >= 2;
         }
      });
      result.add(new TutorialQuest(5, "Use a hero, then undo") {
         @Override
         public boolean isValid(FightLog fightLog) {
            return fightLog.getContext().getTimeTakenSeconds() > 600L && TutorialQuest.isUndoValid(fightLog);
         }

         @Override
         public void onAction(TutorialManager.TutorialAction type, Object arg) {
            if (type == TutorialManager.TutorialAction.Undo) {
               this.markCompleted();
            }
         }
      });
      result.add(
         new TutorialQuest(9, "Undo 3 actions in a row") {
            @Override
            public boolean isValid(FightLog fightLog) {
               return fightLog.getSnapshot(FightLog.Temporality.Present).getAvailable(EffType.Mana) > 2
                  && fightLog.getContext().getTimeTakenSeconds() > 2400L
                  && TutorialQuest.isUndoValid(fightLog);
            }

            @Override
            public void onAction(TutorialManager.TutorialAction type, Object arg) {
               if (type == TutorialManager.TutorialAction.Undo && (Integer)arg == 3) {
                  this.markCompleted();
               }
            }
         }
      );
      result.add(
         new TutorialQuest(7, "Save a hero from dying by [grey]shielding[cu] incoming damage ([yellow][hp][cu])") {
            @Override
            public void newStatsSnapshot(StatSnapshot ss) {
               if (TutorialQuest.savedBy(EffType.Shield, ss)) {
                  this.markCompleted();
               }
            }

            @Override
            public boolean isValid(FightLog fightLog) {
               return !shielding.isComplete()
                  ? false
                  : fightLog.getSnapshot(FightLog.Temporality.Present).canSaveAHero(EffType.Shield, fightLog.getSnapshot(FightLog.Temporality.Future));
            }
         }
      );
      TutorialQuest healingSave = new TutorialQuest(8, "Save a hero from dying by [red]healing[cu] them") {
         @Override
         public void newStatsSnapshot(StatSnapshot ss) {
            if (TutorialQuest.savedBy(EffType.Heal, ss)) {
               this.markCompleted();
            }
         }

         @Override
         public boolean isValid(FightLog fightLog) {
            return fightLog.getSnapshot(FightLog.Temporality.Present).canSaveAHero(EffType.Heal, fightLog.getSnapshot(FightLog.Temporality.Future));
         }
      };
      result.add(healingSave);
      result.add(new TutorialQuest(3, infoTap + " " + Words.entName(false, false) + " to see all their sides") {
         @Override
         public void onAction(TutorialManager.TutorialAction type, Object arg) {
            if (type == TutorialManager.TutorialAction.MonsterPanelInfo) {
               this.markCompleted();
            }
         }

         @Override
         public boolean isValid(FightLog fightLog) {
            for (Ent de : fightLog.getActiveEntities(false)) {
               if (!de.isPlayer() && de.entType.traits.size() > 0) {
                  return true;
               }
            }

            return false;
         }
      });
      return result;
   }

   private static boolean savedBy(EffType effType, StatSnapshot ss) {
      if (ss.future.getStates(true, true).size() >= ss.previousFuture.getStates(true, true).size()) {
         return false;
      } else {
         if (ss.origin instanceof TargetableCommand) {
            Targetable t = ((TargetableCommand)ss.origin).targetable;
            Eff e = t.getDerivedEffects(ss.beforeCommand);
            if (t.isPlayer() && e.hasType(effType, false)) {
               return true;
            }
         }

         return false;
      }
   }

   private static boolean isUndoValid(FightLog f) {
      return f.getSnapshot(FightLog.Temporality.Present).getRolls() == 0;
   }

   public static Collection<? extends TutorialItem> makeLevelEndPhase() {
      List<TutorialQuest> result = new ArrayList<>();
      result.addAll(Arrays.asList(new TutorialQuest(-999, "Swap two items on a hero.[n][grey]Equipped items apply their effects in order: 1 then 2") {
         @Override
         public void onAction(TutorialManager.TutorialAction type, Object arg) {
            if (type == TutorialManager.TutorialAction.SwapItems) {
               this.markCompleted();
            }

            super.onAction(type, arg);
         }

         @Override
         public boolean isValid(FightLog fightLog) {
            int numItems = fightLog.getContext().getParty().getItems(null).size();
            return numItems > 5;
         }
      }));
      return result;
   }
}

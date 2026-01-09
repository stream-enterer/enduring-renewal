package com.tann.dice.screens.dungeon;

import com.tann.dice.gameplay.content.ent.Ent;
import com.tann.dice.gameplay.content.ent.Hero;
import com.tann.dice.gameplay.content.ent.die.Die;
import com.tann.dice.gameplay.content.ent.die.EntDie;
import com.tann.dice.gameplay.effect.targetable.ability.ui.AbilityHolder;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.fightLog.FightLog;
import com.tann.dice.gameplay.fightLog.Snapshot;
import com.tann.dice.gameplay.phase.Phase;
import com.tann.dice.gameplay.phase.PhaseManager;
import com.tann.dice.gameplay.save.settings.option.OptionLib;
import com.tann.dice.gameplay.save.settings.option.OptionUtils;
import com.tann.dice.gameplay.trigger.global.Global;
import com.tann.dice.statics.bullet.BulletStuff;
import com.tann.dice.statics.bullet.RollFx;
import com.tann.dice.statics.sound.Sounds;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Pixl;
import com.tann.dice.util.Tann;
import com.tann.dice.util.TannLog;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RollManager {
   FightLog fightLog;
   public static Boolean predictionSavePlayer;

   public RollManager(FightLog fightLog) {
      this.fightLog = fightLog;
   }

   public boolean allDiceStopped() {
      return this.allDiceNotState(Die.DieState.Rolling) && this.allDiceNotState(Die.DieState.Unlocking);
   }

   public boolean allDiceStoppedEnoughToSave() {
      return this.allDiceNotState(Die.DieState.Rolling);
   }

   public boolean allDiceNotState(Die.DieState ds) {
      for (Ent de : this.fightLog.getSnapshot(FightLog.Temporality.Present).getAliveEntities()) {
         Die.DieState state = de.getDie().getState();
         if (state == ds) {
            return false;
         }
      }

      return true;
   }

   public boolean requestPlayerRoll() {
      if (BulletStuff.isLined()) {
         BulletStuff.toggleDiceScatter();
         return false;
      } else {
         BulletStuff.resetAlignment();
         Snapshot present = this.fightLog.getSnapshot(FightLog.Temporality.Present);
         if (present.getRolls() <= 0) {
            return false;
         } else if (!PhaseManager.get().getPhase().canRoll()) {
            return false;
         } else if (present.isEnd()) {
            return false;
         } else {
            if (!com.tann.dice.Main.getSettings().isHasRolled()) {
               com.tann.dice.Main.getSettings().setHasRolled(true);
            }

            List<EntDie> diceToRoll = this.getHeroDiceAvailableToRoll();
            if (diceToRoll.isEmpty()) {
               Sounds.playSound(Sounds.error);
            }

            List<Ent> entitiesToRoll = new ArrayList<>();

            for (EntDie d : diceToRoll) {
               entitiesToRoll.add(d.ent);
            }

            if (entitiesToRoll.size() == 0) {
               Sounds.playSound(Sounds.error);
               return false;
            } else {
               List<Hero> alives = present.getAliveHeroEntities();

               for (Global gt : present.getGlobals()) {
                  String error = gt.getRollError(entitiesToRoll, alives.size());
                  if (error != null) {
                     this.showRollError(error);
                     return false;
                  }
               }

               DungeonScreen ds = DungeonScreen.get();
               present.spendRoll();
               this.roll(false, true);
               ds.tutorialManager.onRoll(present.getEntities(true, false));
               return true;
            }
         }
      }
   }

   public List<EntDie> getHeroDiceAvailableToRoll() {
      Phase p = PhaseManager.get().getPhase();
      if (p != null && p.canRoll()) {
         Snapshot present = this.fightLog.getSnapshot(FightLog.Temporality.Present);
         List<Hero> alives = present.getAliveHeroEntities();
         List<EntDie> dice = new ArrayList<>();
         if (this.anyDiceDangerousState()) {
            return new ArrayList<>();
         } else {
            for (Ent de : alives) {
               Die.DieState state = de.getDie().getState();
               if (state == Die.DieState.Stopped) {
                  dice.add(de.getDie());
               }
            }

            return dice;
         }
      } else {
         return new ArrayList<>();
      }
   }

   public boolean anyDiceDangerousState() {
      Snapshot present = this.fightLog.getSnapshot(FightLog.Temporality.Present);
      if (present == null) {
         return false;
      } else {
         for (Ent de : present.getAliveHeroEntities()) {
            Die.DieState state = de.getDie().getState();
            if (state == Die.DieState.Rolling || state == Die.DieState.Unlocking) {
               return true;
            }
         }

         return false;
      }
   }

   private void showRollError(String error) {
      Sounds.playSound(Sounds.error);
      AbilityHolder.showInfo(error, Colours.red);
   }

   public void firstRoll(boolean player) {
      Snapshot present = this.fightLog.getSnapshot(FightLog.Temporality.Present);

      for (Ent de : present.getAliveEntities(player)) {
         if (de.getDie().getState() != Die.DieState.Stopped) {
            return;
         }
      }

      this.roll(true, player);
   }

   public void roll(boolean firstRoll, boolean player) {
      com.tann.dice.Main.requestRendering();
      List<? extends Ent> entList = this.fightLog.getSnapshot(FightLog.Temporality.Present).getAliveEntities(player);
      List<EntDie> toRoll = new ArrayList<>();

      for (Ent de : entList) {
         if (de.getDie().getState() == Die.DieState.Stopped) {
            toRoll.add(de.getDie());
         }
      }

      DungeonScreen ds = DungeonScreen.get();
      long rollSeed = (long)(Math.random() * 9.99999999E8);
      List<Integer> predictedOutcome = BulletStuff.predictAndReset(toRoll, firstRoll, rollSeed, player);
      if (OptionLib.SHOW_PREDICTION.c()) {
         String infoString = "";

         for (int i = 0; i < predictedOutcome.size(); i++) {
            infoString = infoString + toRoll.get(i).getSide(predictedOutcome.get(i));
            if (i < predictedOutcome.size() - 1) {
               infoString = infoString + "[n]";
            }
         }

         if (ds != null) {
            ds.addPopup(new Pixl(3, 3).border(Colours.grey).text(infoString).pix());
         }
      }

      for (int ix = 0; ix < toRoll.size(); ix++) {
         EntDie ed = toRoll.get(ix);
         if (predictedOutcome.size() == toRoll.size()) {
            ed.setSideOverride(predictedOutcome.get(ix));
         } else {
            TannLog.error("prediction error");
            ed.setSideOverride(Tann.randomInt(6));
         }
      }

      predictionSavePlayer = player;
      if (ds != null) {
         ds.save();
      }

      predictionSavePlayer = null;
      Random r = Tann.makeStdRandom(rollSeed);

      for (EntDie entDie : toRoll) {
         entDie.roll(firstRoll, r);
      }

      if (player && !this.fightLog.getContext().skipStats()) {
         this.fightLog.getContext().getStatsManager().updateDiceRolled(toRoll.size());
      }

      if (player && ds != null) {
         ds.heroDiceRolled();
      }

      BulletStuff.resetAlignment();
      if (OptionUtils.playRollSfx(player)) {
         RollFx.addRollSFX(toRoll.size(), firstRoll, false, player);
      }
   }

   public void resetForRoll(boolean player) {
      this.resetForRoll(player, true, null, null);
   }

   public void resetForRoll(boolean player, boolean autoRoll, Integer lockState, Snapshot present) {
      this.resetForRoll(player, autoRoll, lockState, present, false, false);
   }

   public void resetForRoll(final boolean player, final boolean autoRoll, Integer lockState, Snapshot present, boolean specialFirstTurn, boolean fromSave) {
      List<? extends Ent> entities = this.fightLog.getSnapshot(FightLog.Temporality.Present).getAliveEntities(player);

      for (int i = 0; i < entities.size(); i++) {
         final Ent de = entities.get(i);
         final EntDie die = de.getDie();
         boolean autolock = false;
         if (player && present != null) {
            EntState ps = present.getState(de);
            autolock = ps != null && ps.isAutoLock();
         }

         die.setupInitialLocation();
         if (autolock && !autoRoll) {
            die.setState(Die.DieState.Locked);
         } else {
            if (autoRoll) {
               de.getDie().reset();
            }

            boolean shouldReturn = lockState == null || (lockState & 1 << i) == 0;
            if (shouldReturn) {
               final float interpSpeed = this.getInterpSpeedForAutoReturnToPlay(autoRoll, autoRoll, specialFirstTurn, fromSave);
               float delay = getDelayForAutoReturn(specialFirstTurn, i);
               if (delay > 0.0F) {
                  Tann.delay(delay, new Runnable() {
                     @Override
                     public void run() {
                        de.getEntPanel().holdsDie = false;
                        die.returnToPlay(new Runnable() {
                           @Override
                           public void run() {
                              if (autoRoll) {
                                 RollManager.this.firstRoll(player);
                              }
                           }
                        }, autoRoll, interpSpeed);
                     }
                  });
               } else {
                  de.getEntPanel().holdsDie = false;
                  die.returnToPlay(new Runnable() {
                     @Override
                     public void run() {
                        if (autoRoll) {
                           RollManager.this.firstRoll(player);
                        }
                     }
                  }, autoRoll, interpSpeed);
               }
            } else {
               die.setState(Die.DieState.Locked);
            }
         }
      }
   }

   private static float getDelayForAutoReturn(boolean specialFirstTurn, int index) {
      return specialFirstTurn ? index * 0.3F : 0.0F;
   }

   private float getInterpSpeedForAutoReturnToPlay(boolean applyNewQuaternion, boolean autoRoll, boolean specialFirstTurn, boolean fromSave) {
      float base = Die.getBaseInterpSpeed();
      if (specialFirstTurn) {
         return base * 1.8F * (com.tann.dice.Main.isPortrait() ? 1.4F : 1.0F);
      } else if (fromSave) {
         return 0.0F;
      } else if (applyNewQuaternion) {
         return base * 1.4F;
      } else {
         return autoRoll ? base * 2.0F : base;
      }
   }
}

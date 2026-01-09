package com.tann.dice.gameplay.phase.gameplay;

import com.tann.dice.gameplay.content.ent.Ent;
import com.tann.dice.gameplay.content.ent.die.Die;
import com.tann.dice.gameplay.content.ent.die.EntDie;
import com.tann.dice.gameplay.effect.targetable.ability.ui.AbilityHolder;
import com.tann.dice.gameplay.fightLog.FightLog;
import com.tann.dice.gameplay.fightLog.Snapshot;
import com.tann.dice.gameplay.phase.Phase;
import com.tann.dice.gameplay.phase.PhaseManager;
import com.tann.dice.screens.dungeon.DungeonScreen;
import com.tann.dice.screens.dungeon.RollManager;
import com.tann.dice.screens.dungeon.panels.ConfirmButton;
import com.tann.dice.screens.dungeon.panels.book.page.helpPage.HelpType;
import com.tann.dice.screens.dungeon.panels.tutorial.TutorialHolder;
import com.tann.dice.statics.bullet.BulletStuff;
import com.tann.dice.statics.sound.Sounds;
import com.tann.dice.util.Tann;
import java.util.List;

public class PlayerRollingPhase extends Phase {
   Integer overrideRolls;
   Integer lockState;
   boolean specialE;
   public static final float UNDO_DELAY = 0.75F;
   boolean autosaved;

   public PlayerRollingPhase() {
   }

   public PlayerRollingPhase(int overrideRolls) {
      this.overrideRolls = overrideRolls;
   }

   public PlayerRollingPhase(String saved) {
      this();
      if (!saved.isEmpty()) {
         this.autosaved = true;
         String[] split = saved.split(";");
         if (split[0].equalsIgnoreCase("e")) {
            this.specialE = true;
         } else {
            this.overrideRolls = Integer.parseInt(split[0]);
         }

         this.lockState = Integer.parseInt(split[1]);
      }
   }

   @Override
   public void activate() {
      RollManager rm = DungeonScreen.get().rollManager;
      DungeonScreen ds = DungeonScreen.get();
      Snapshot present = ds.getFightLog().getSnapshot(FightLog.Temporality.Present);
      if (this.overrideRolls != null) {
         present.setOverrideRolls(this.overrideRolls);
      }

      rm.resetForRoll(true, this.overrideRolls == null, this.lockState, present, this.specialE, this.fromSave);
      boolean instant = this.fromSave && !this.specialE;
      ds.slideButton(ds.rollGroup, true, instant, 0.75F);
      ds.slideButton(ds.doneRollingButton, false, instant, 0.75F);
      ds.slideButton(ds.doneRollingButton, true, instant);
      ds.slideButton(ds.confirmButton, false, instant);
      ds.slideButton(ds.undoButton, false, instant);
      ds.slideSpellHolder(AbilityHolder.TuckState.Tucked, false);
      DungeonScreen.get().save();
   }

   @Override
   public void tick(float delta) {
      DungeonScreen ds = DungeonScreen.get();
      if (com.tann.dice.Main.frames % 10 == 0) {
         FightLog f = this.getFightLog();
         int newDead = f.getSnapshot(FightLog.Temporality.Present).getAliveHeroEntities().size()
            - f.getSnapshot(FightLog.Temporality.Future).getAliveHeroEntities().size();
         ds.doneRollingButton
            .setState(BulletStuff.allDiceLockedOrLocking() ? ConfirmButton.ConfirmState.AllDiceLocked : ConfirmButton.ConfirmState.RollingDice, newDead);
      }

      if (this.calcRolls() == 0) {
         com.tann.dice.Main.requestRendering();
      }
   }

   @Override
   public void deactivate() {
      DungeonScreen ds = DungeonScreen.get();
      Snapshot present = ds.getFightLog().getSnapshot(FightLog.Temporality.Present);
      PhaseManager.get().pushPhase(new TargetingPhase(present.getRolls()));
   }

   @Override
   public boolean canRoll() {
      return true;
   }

   @Override
   public HelpType getHelpType() {
      return HelpType.Rolling;
   }

   @Override
   public void confirmClicked(boolean fromClick) {
      FightLog fightLog = this.getFightLog();
      boolean allGood = true;

      for (Ent h : fightLog.getSnapshot(FightLog.Temporality.Present).getAliveHeroEntities()) {
         EntDie d = h.getDie();
         if (d.getSideIndex() == -1) {
            allGood = false;
         }
      }

      if (!allGood) {
         Sounds.playSound(Sounds.error);
      } else {
         for (Ent hx : fightLog.getSnapshot(FightLog.Temporality.Present).getAliveHeroEntities()) {
            EntDie d = hx.getDie();
            d.slideToPanel();
            Sounds.playSound(Sounds.lock);
         }

         if (fromClick) {
            Sounds.playSound(Sounds.confirm);
         }

         PhaseManager.get().popPhase(PlayerRollingPhase.class);
         DungeonScreen.get().save();
      }
   }

   @Override
   public String serialise() {
      String start = "0";
      return !this.autosaved && this.overrideRolls == null && RollManager.predictionSavePlayer == null
         ? start
         : start + this.calcRolls() + ";" + this.packLockedState();
   }

   private int calcRolls() {
      DungeonScreen ds = DungeonScreen.get();
      int rolls = 0;
      if (ds != null) {
         rolls = ds.getFightLog().getSnapshot(FightLog.Temporality.Present).getRolls();
      }

      return rolls;
   }

   private int packLockedState() {
      int result = 0;
      List<Ent> heroes = this.getFightLog().getSnapshot(FightLog.Temporality.Present).getEntities(true, false);

      for (int i = 0; i < heroes.size(); i++) {
         Ent de = heroes.get(i);
         Die.DieState ds = de.getDie().getState();
         boolean locked = ds.isLockedOrLocking();
         if (locked) {
            result += 1 << i;
         }
      }

      return result;
   }

   @Override
   public void positionTutorial(TutorialHolder tutorialHolder) {
      tutorialHolder.setPosition(com.tann.dice.Main.width - tutorialHolder.getWidth() - 2.0F, com.tann.dice.Main.height);
      Tann.slideIn(tutorialHolder, Tann.TannPosition.Top, TutorialHolder.getTopGap());
   }

   @Override
   public boolean highlightDice() {
      return true;
   }

   @Override
   public boolean keyPress(int keycode) {
      if (this.checkForHeroPress(keycode)) {
         return true;
      } else {
         switch (keycode) {
            case 46:
               DungeonScreen.get().rollManager.requestPlayerRoll();
               return true;
            case 62:
            case 66:
            case 160:
               DungeonScreen.get().confirmClicked(true);
               return true;
            default:
               return false;
         }
      }
   }

   private boolean checkForHeroPress(int keycode) {
      DungeonScreen ds = DungeonScreen.get();
      int digit = Tann.getDigit(keycode);
      if (digit >= 0 && digit <= 9) {
         List<Ent> heroes = this.getFightLog().getSnapshot(FightLog.Temporality.Present).getEntities(true, false);
         if (digit < heroes.size()) {
            Ent selected = heroes.get(digit);
            ds.targetingManager.clicked(selected, true);
            return true;
         }
      }

      return false;
   }

   @Override
   public boolean isDuringCombat() {
      return true;
   }

   @Override
   public boolean canSave() {
      return true;
   }

   @Override
   public boolean canFlee() {
      return true;
   }

   @Override
   public boolean disallowRescale() {
      DungeonScreen ds = DungeonScreen.get();
      if (ds == null) {
         return false;
      } else {
         RollManager rm = ds.rollManager;
         return rm == null ? false : rm.anyDiceDangerousState();
      }
   }

   public void setAutosaved() {
      this.autosaved = true;
   }

   @Override
   public boolean showTargetButton() {
      return true;
   }
}

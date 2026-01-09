package com.tann.dice.gameplay.content.ent;

import com.badlogic.gdx.graphics.Color;
import com.tann.dice.gameplay.content.ent.die.Die;
import com.tann.dice.gameplay.content.ent.die.side.EntSide;
import com.tann.dice.gameplay.content.ent.type.MonsterType;
import com.tann.dice.gameplay.effect.Trait;
import com.tann.dice.gameplay.effect.eff.EffType;
import com.tann.dice.gameplay.effect.targetable.DieTargetable;
import com.tann.dice.gameplay.fightLog.FightLog;
import com.tann.dice.gameplay.save.settings.option.OptionUtils;
import com.tann.dice.screens.dungeon.DungeonScreen;
import com.tann.dice.screens.dungeon.panels.entPanel.EntPanelCombat;
import com.tann.dice.statics.bullet.BulletStuff;
import com.tann.dice.statics.sound.Sounds;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Tann;
import java.util.List;

public class Monster extends Ent {
   MonsterType monsterType;
   static final float lockDelay = 0.4F;

   public Monster(MonsterType type) {
      super(type);

      for (EntSide es : this.sides) {
         if (es.getBaseEffect().getType() == EffType.Buff) {
            es.getBaseEffect().getBuff().skipFirstTick();
         }
      }

      this.monsterType = type;
   }

   @Override
   public boolean isPlayer() {
      return false;
   }

   @Override
   public int getPixelSize() {
      return this.size.getPixels();
   }

   @Override
   public void stopped() {
      super.stopped();
      if (!BulletStuff.isSimulating()) {
         if (this.getState(FightLog.Temporality.Present).getCurrentSideState().getCalculatedEffect().getType() == EffType.Blank) {
            Sounds.playSound(Sounds.lock);
            this.getDie().removeFromPhysics();
            this.getDie().slideToPanel();
         }

         if (this.allStoppedOrLocky()) {
            this.triggerLocking();
         }
      }
   }

   private void triggerLocking() {
      float delayAdd = 0.4F;
      delayAdd *= OptionUtils.getRollSpeedMultiplier(false);
      float delay = 0.0F;
      List<? extends Ent> enemies = this.getFightLog().getActiveEntities(false);

      for (int i = 0; i < enemies.size(); i++) {
         final Ent de = enemies.get(i);
         Die.DieState ds = de.getDie().getState();
         if (ds == Die.DieState.Stopped) {
            Sounds.playSoundDelayed(Sounds.lock, 1.0F, 1.0F, delay);
            Tann.delay(delay, new Runnable() {
               @Override
               public void run() {
                  if (!de.getDie().isDisposed()) {
                     de.getDie().removeFromPhysics();
                     de.getDie().slideToPanel();
                  }
               }
            });
            delay += delayAdd;
         }
      }
   }

   private boolean allStoppedOrLocky() {
      for (Ent de : this.getFightLog().getActiveEntities(false)) {
         Die.DieState ds = de.getDie().getState();
         if (ds != Die.DieState.Stopped && ds != Die.DieState.Locked && ds != Die.DieState.Locking) {
            return false;
         }
      }

      return true;
   }

   @Override
   public void locked() {
      this.onLockGameplayImplications();
   }

   public void onLockGameplayImplications() {
      if (!BulletStuff.isSimulating()) {
         DieTargetable dt = this.die.getTargetable();
         Ent target = DungeonScreen.get().targetingManager.getRandomTargetForEnemy(dt);
         this.getFightLog().addCommand(dt, target, true);
         EntPanelCombat ep = this.getDie().ent.getEntPanel();
         ep.setArrowIntensity(1.0F, 0.75F);
      }
   }

   @Override
   public void deathSound() {
      if (this.monsterType.deathSound != null) {
         Sounds.playSound(this.monsterType.deathSound);
      }
   }

   @Override
   public Color getColour() {
      return Colours.purple;
   }

   public MonsterType getEntType() {
      return this.monsterType;
   }

   public void debugChangeTo(MonsterType type) {
      this.entType = type;
      this.name = type.getName(false);
      this.traits = type.traits.toArray(new Trait[0]);
      this.size = type.size;
      this.setSides(type.sides);
      this.updateOutOfCombat();
      this.getDie().resetSides();
   }
}

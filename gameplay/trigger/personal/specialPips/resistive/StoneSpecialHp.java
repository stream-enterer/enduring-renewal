package com.tann.dice.gameplay.trigger.personal.specialPips.resistive;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.tann.dice.gameplay.content.ent.type.EntType;
import com.tann.dice.gameplay.effect.eff.Eff;
import com.tann.dice.gameplay.effect.targetable.Targetable;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.fightLog.Snapshot;
import com.tann.dice.gameplay.fightLog.event.entState.PanelHighlightEvent;
import com.tann.dice.gameplay.fightLog.event.snapshot.SoundSnapshotEvent;
import com.tann.dice.gameplay.trigger.personal.specialPips.SpecialHp;
import com.tann.dice.gameplay.trigger.personal.specialPips.pipLoc.PipLoc;
import com.tann.dice.statics.Images;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Tann;
import com.tann.dice.util.tp.TP;

public class StoneSpecialHp extends SpecialHp {
   public StoneSpecialHp(PipLoc loc) {
      super(loc);
   }

   @Override
   public TP<TextureRegion, Color> getPipTannple(boolean big) {
      return new TP<>(big ? Images.hp_girder : Images.hp_small, Colours.grey);
   }

   @Override
   protected String describe() {
      return "must be removed individually";
   }

   @Override
   public Integer alterTakenDamage(int damage, Eff eff, Snapshot snapshot, EntState self, Targetable targetable) {
      if (damage == 0) {
         return 0;
      } else {
         int hp = self.getHp();
         int minTriggerPip = self.getMinTriggerPipHp();
         int nextStonePosition = -1;
         int[] pipLocations = this.pipLoc.getLocs(self.getMaxHp());

         for (int i = pipLocations.length - 1; i >= 0; i--) {
            int pip = pipLocations[i];
            if (pip < minTriggerPip) {
               nextStonePosition = pip;
               break;
            }
         }

         if (nextStonePosition == -1) {
            return super.alterTakenDamage(damage, eff, snapshot, self, targetable);
         } else if (hp == nextStonePosition + 1) {
            if (damage > 1 && hp > 1) {
               this.addClinks(self);
            }

            return 1;
         } else {
            int nextPreThreshold = hp - nextStonePosition - 1;
            if (damage > nextPreThreshold) {
               this.addClinks(self);
            }

            return Math.min(damage, nextPreThreshold);
         }
      }
   }

   private void addClinks(EntState self) {
      self.getSnapshot().addEvent(SoundSnapshotEvent.clink);
      self.addEvent(PanelHighlightEvent.stoneSkin);
   }

   @Override
   public float affectStrengthCalc(float total, float avgRawValue, EntType type) {
      return total;
   }

   @Override
   public float affectTotalHpCalc(float hp, EntType entType) {
      float total = 0.0F;
      int[] pipLocs = this.getPips(entType);

      for (int pipLoc : pipLocs) {
         if (pipLoc != hp - 1.0F && pipLoc != 0 && !Tann.contains(pipLocs, pipLoc + 1)) {
            total++;
         } else {
            total += 0.5F;
         }
      }

      float guessHp = hp + total * 2.0F;
      float alpha = Math.min(1.0F, guessHp / 40.0F);
      float increaseFactor = Interpolation.linear.apply(1.5F, 3.9F, alpha);
      return hp + total * increaseFactor;
   }

   @Override
   public float getPriority() {
      return 100.0F;
   }
}

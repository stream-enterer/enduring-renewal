package com.tann.dice.gameplay.trigger.personal.specialPips.resistive;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
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
import com.tann.dice.util.tp.TP;

public class ToughSpecialHp extends SpecialHp {
   final int str;

   public ToughSpecialHp(int str, PipLoc loc) {
      super(loc);
      this.str = str;
   }

   @Override
   public TP<TextureRegion, Color> getPipTannple(boolean big) {
      return new TP<>(big ? Images.hp_reverse : Images.hp_small, Colours.grey);
   }

   @Override
   protected String describe() {
      return "takes " + this.str + " damage at once to remove";
   }

   @Override
   public Integer alterTakenDamage(int damage, Eff eff, Snapshot snapshot, EntState self, Targetable targetable) {
      if (damage == 0) {
         return 0;
      } else {
         int hp = self.getHp();
         int minTriggerPip = self.getMinTriggerPipHp();
         int tmpDmg = damage;
         int dealt = 0;

         for (int i = 0; i < tmpDmg; i++) {
            int dmgLoc = hp - i - 1;
            if (dmgLoc >= minTriggerPip) {
               dealt++;
            } else {
               if (this.pipLoc.isActive(dmgLoc, self.getMaxHp())) {
                  tmpDmg -= this.str - 1;
                  if (i >= tmpDmg) {
                     break;
                  }
               }

               dealt++;
            }
         }

         if (dealt == 0) {
            this.addClinks(self);
         }

         return dealt;
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
      int[] pipLocs = this.getPips(entType);
      float increaseFactor = (float)(Math.pow(this.str, 1.2F) - 1.0);
      return hp + pipLocs.length * increaseFactor;
   }

   @Override
   public float getPriority() {
      return -30.0F;
   }
}

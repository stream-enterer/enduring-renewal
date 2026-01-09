package com.tann.dice.gameplay.trigger.personal.specialPips.resistive;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.tann.dice.gameplay.content.ent.type.EntType;
import com.tann.dice.gameplay.effect.eff.Eff;
import com.tann.dice.gameplay.effect.targetable.DieTargetable;
import com.tann.dice.gameplay.effect.targetable.Targetable;
import com.tann.dice.gameplay.effect.targetable.ability.spell.Spell;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.fightLog.Snapshot;
import com.tann.dice.gameplay.fightLog.event.entState.TextEvent;
import com.tann.dice.gameplay.fightLog.event.snapshot.SoundSnapshotEvent;
import com.tann.dice.gameplay.trigger.Collision;
import com.tann.dice.gameplay.trigger.personal.specialPips.SpecialHp;
import com.tann.dice.gameplay.trigger.personal.specialPips.pipLoc.PipLoc;
import com.tann.dice.statics.Images;
import com.tann.dice.util.Colours;
import com.tann.dice.util.tp.TP;

public class ResistSpecialHp extends SpecialHp {
   final ResistSpecialHp.DamageType damageType;

   public ResistSpecialHp(ResistSpecialHp.DamageType damageType, PipLoc loc) {
      super(loc);
      this.damageType = damageType;
   }

   @Override
   public TP<TextureRegion, Color> getPipTannple(boolean big) {
      return new TP<>(big ? Images.hp_girder : Images.hp_small, this.damageType.color);
   }

   @Override
   protected String describe() {
      return "must be removed by " + this.damageType.desc + " damage";
   }

   @Override
   public Integer alterTakenDamage(int damage, Eff eff, Snapshot snapshot, EntState self, Targetable targetable) {
      switch (this.damageType) {
         case Spell:
            if (targetable instanceof Spell) {
               return damage;
            }
            break;
         case DiceOnly:
            if (targetable instanceof DieTargetable) {
               return damage;
            }
      }

      int hp = self.getHp();
      int minTriggerPip = self.getMinTriggerPipHp();
      int nextPipPosition = this.getNextPipLocation(minTriggerPip, self.getMaxHp());
      if (nextPipPosition == -1) {
         return damage;
      } else {
         int nextPreThreshold = hp - nextPipPosition - 1;
         if (damage > nextPreThreshold) {
            this.addClinks(self);
         }

         return Math.min(damage, nextPreThreshold);
      }
   }

   private void addClinks(EntState self) {
      self.getSnapshot().addEvent(SoundSnapshotEvent.clink);
      self.addEvent(TextEvent.Immune);
   }

   @Override
   public float affectStrengthCalc(float total, float avgRawValue, EntType type) {
      return total;
   }

   @Override
   public float affectTotalHpCalc(float hp, EntType entType) {
      return hp + this.getPips(entType).length * 1.0F;
   }

   @Override
   public long getCollisionBits(Boolean player) {
      long s = super.getCollisionBits(player);
      switch (this.damageType) {
         case Spell:
            return s | Collision.SPELL | Collision.PHYSICAL_DAMAGE;
         case DiceOnly:
            return s | Collision.PHYSICAL_DAMAGE;
         default:
            return s;
      }
   }

   public static enum DamageType {
      Spell(Colours.blue, "spell"),
      DiceOnly(Colours.light, "dice");

      final Color color;
      final String desc;

      private DamageType(Color color, String desc) {
         this.color = color;
         this.desc = desc;
      }
   }
}

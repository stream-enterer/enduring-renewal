package com.tann.dice.gameplay.effect;

import com.tann.dice.gameplay.effect.eff.Eff;
import com.tann.dice.gameplay.effect.eff.TargetingType;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.trigger.personal.Cleansed;
import com.tann.dice.gameplay.trigger.personal.Dodge;
import com.tann.dice.gameplay.trigger.personal.Personal;
import com.tann.dice.gameplay.trigger.personal.Stunned;
import com.tann.dice.gameplay.trigger.personal.immunity.DamageImmunity;
import com.tann.dice.gameplay.trigger.personal.merge.Merge;
import com.tann.dice.util.tp.TP;

public class Buff {
   public int turns;
   public Personal personal;
   boolean expired;
   private boolean skipFirstTick;

   public Buff(int turns, Personal personal) {
      this.turns = turns;
      this.personal = personal.transformForBuff();
      this.personal.buff = this;
   }

   public Buff(Personal personal) {
      this(-1, personal);
   }

   public void turn() {
      this.personal.clearDescCache();
      if (!this.skipFirstTick) {
         if (this.turns > 0) {
            this.turns--;
         }

         if (this.turns == 0) {
            this.expire();
         }
      }
   }

   public void unskip() {
      this.skipFirstTick = false;
   }

   public void skipFirstTick() {
      this.skipFirstTick = true;
   }

   private void expire() {
      this.expired = true;
   }

   public Buff copy() {
      Buff b = new Buff(this.turns, this.personal);
      if (this.skipFirstTick) {
         b.skipFirstTick();
      }

      return b;
   }

   public String toNiceString(Eff source) {
      return this.toNiceString(source, true, true);
   }

   public String toNiceString(Eff source, boolean withTurnsString, boolean preTranslate) {
      TargetingType tt = source.getTargetingType();
      String giveText;
      if (tt == TargetingType.Self) {
         if (this.personal instanceof DamageImmunity) {
            giveText = "Become " + this.personal.describeForSelfBuff().toLowerCase();
         } else {
            giveText = this.personal.describeForSelfBuff();
         }
      } else {
         giveText = this.personal.describeForGiveBuff(source);
         if (tt == TargetingType.Group) {
            giveText = giveText.replaceAll("target's", source.isFriendly() ? "allied" : "enemy");
         }
      }

      if (this.isInfinite()) {
         return preTranslate ? "[notranslate]" + com.tann.dice.Main.t(giveText) + " " + com.tann.dice.Main.t("this fight") : giveText + " this fight";
      } else if (!withTurnsString) {
         return preTranslate ? "[notranslate]" + com.tann.dice.Main.t(giveText) : giveText;
      } else {
         return preTranslate
            ? "[notranslate]" + com.tann.dice.Main.t(giveText) + com.tann.dice.Main.t(this.getTurnsString())
            : giveText + this.getTurnsString();
      }
   }

   public String getTurnsString() {
      if (this.isInfinite()) {
         return "";
      } else if (this.turns == 0 || this.turns == 1 && !this.skipFirstTick) {
         return !(this.personal instanceof Stunned) && !(this.personal instanceof Dodge) ? " this turn" : "";
      } else {
         return this.turns == 1 && this.skipFirstTick ? " next turn" : " for " + this.turns + " turns";
      }
   }

   public Cleansed.CleanseType getCleanseType() {
      return this.personal.getCleanseType();
   }

   public boolean expired() {
      return this.expired;
   }

   public boolean canMerge(Buff buff) {
      return !(this.personal instanceof Merge)
         ? false
         : ((Merge)this.personal).canMerge(buff.personal) && this.turns == buff.turns && this.skipFirstTick == buff.skipFirstTick;
   }

   public void merge(Buff buff) {
      if (!this.canMerge(buff)) {
         throw new RuntimeException("Invalid buff merger");
      } else {
         if (this.personal instanceof Merge) {
            Merge cpy = ((Merge)this.personal).copy();
            cpy.merge(buff.personal);
            cpy.clearDescCache();
            this.personal = cpy;
         }
      }
   }

   public boolean isRecommendedTarget(EntState sourceState, EntState targetPresent, EntState targetFuture) {
      return this.personal.isRecommended(sourceState, targetPresent, targetFuture);
   }

   public float getEffectTier(int pips, int tier) {
      return this.personal.getEffectTier(pips, tier);
   }

   public TP<Integer, Boolean> cleanseBy(int cleanseAmt) {
      TP<Integer, Boolean> result = this.personal.cleanseBy(cleanseAmt);
      this.personal.clearDescCache();
      return result;
   }

   public boolean isInfinite() {
      return this.turns == -1;
   }
}

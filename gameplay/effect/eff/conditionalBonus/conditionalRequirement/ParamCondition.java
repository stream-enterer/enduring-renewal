package com.tann.dice.gameplay.effect.eff.conditionalBonus.conditionalRequirement;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.effect.eff.Eff;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.fightLog.Snapshot;
import com.tann.dice.statics.Images;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Pixl;
import com.tann.dice.util.ui.HpGrid;

public class ParamCondition implements ConditionalRequirement {
   final ParamCondition.ParamConType pct;
   final int val;
   final boolean source;

   public boolean isSource() {
      return this.source;
   }

   public ParamCondition(ParamCondition.ParamConType pct, int val, boolean source) {
      this.pct = pct;
      this.val = val;
      this.source = source;
   }

   public ParamCondition(ParamCondition.ParamConType pct, int val) {
      this(pct, val, true);
   }

   @Override
   public boolean isValid(Snapshot s, EntState sourceState, EntState targetState, Eff eff) {
      EntState chk = this.source ? sourceState : targetState;
      switch (this.pct) {
         case ExactlyHp:
            return chk.getHp() == this.val;
         case OrMoreHp:
            return chk.getHp() >= this.val;
         case OrLessHp:
            return chk.getHp() <= this.val;
         case OrLessMaxHp:
            return chk.getMaxHp() <= this.val;
         case OrMoreMaxHp:
            return chk.getMaxHp() >= this.val;
         case ExactlyMaxHp:
            return chk.getMaxHp() == this.val;
         default:
            throw new RuntimeException("unimp: " + this.pct);
      }
   }

   @Override
   public boolean preCalculate() {
      return this.source;
   }

   @Override
   public String getInvalidString(Eff eff) {
      return null;
   }

   @Override
   public String describe(Eff eff) {
      switch (this.pct) {
         case ExactlyHp:
            return "with exactly " + this.val + " hp";
         case OrMoreHp:
            return "with " + this.val + " or more hp";
         case OrLessHp:
            return "with " + this.val + " or less hp";
         case OrLessMaxHp:
            return "with " + this.val + " or less max hp";
         case OrMoreMaxHp:
            return "with " + this.val + " or more max hp";
         case ExactlyMaxHp:
            return "with exactly " + this.val + " max hp";
         default:
            return "unset: " + this.pct;
      }
   }

   @Override
   public String getBasicString() {
      return this.describe(null);
   }

   @Override
   public Actor getRestrictionActor() {
      int next5 = (this.val + 5) / 5 * 5;
      switch (this.pct) {
         case ExactlyHp:
            return HpGrid.make(this.val, next5);
         case OrMoreHp:
            return new Pixl().image(Images.ge, Colours.text).gap(2).actor(HpGrid.make(this.val, next5)).pix();
         case OrLessHp:
            return new Pixl().image(Images.lte, Colours.text).gap(2).actor(HpGrid.make(this.val, next5)).pix();
         case OrLessMaxHp:
            return new Pixl().text("[grey]max[n]<=").gap(2).actor(HpGrid.make(this.val, this.val)).pix();
         case OrMoreMaxHp:
            return new Pixl().text("[grey]max[n]>=").gap(2).actor(HpGrid.make(this.val, this.val)).pix();
         case ExactlyMaxHp:
            return new Pixl().text("[grey]max[p][p]=").gap(2).actor(HpGrid.make(this.val, this.val)).pix();
         default:
            return new Pixl().text("unimp: " + this.pct + "/" + this.val).pix();
      }
   }

   @Override
   public boolean isPlural() {
      return true;
   }

   public ParamCondition getSwapped() {
      return new ParamCondition(this.pct, this.val, !this.source);
   }

   public String hyphenTag() {
      return this.val + "";
   }

   public static enum ParamConType {
      ExactlyHp,
      OrMoreHp,
      OrMoreMaxHp,
      OrLessMaxHp,
      ExactlyMaxHp,
      OrLessHp;
   }
}

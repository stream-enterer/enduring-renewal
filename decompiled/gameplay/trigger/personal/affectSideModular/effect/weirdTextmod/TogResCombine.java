package com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.weirdTextmod;

import com.tann.dice.gameplay.effect.eff.Eff;
import com.tann.dice.gameplay.effect.eff.EffBill;
import com.tann.dice.gameplay.effect.eff.EffUtils;
import com.tann.dice.gameplay.effect.eff.conditionalBonus.conditionalRequirement.CombinedRequirement;
import com.tann.dice.gameplay.effect.eff.conditionalBonus.conditionalRequirement.ConditionalRequirement;
import com.tann.dice.gameplay.effect.eff.conditionalBonus.conditionalRequirement.ORRequirement;
import com.tann.dice.gameplay.effect.eff.conditionalBonus.conditionalRequirement.XORRequirement;

public class TogResCombine extends LeftTextmodToggle {
   final int index;

   public TogResCombine(int index) {
      this.index = index;
   }

   @Override
   public String describe() {
      return "merge restrictions from left side using " + this.reqDesc();
   }

   @Override
   public Eff alterEff(Eff e, Eff left) {
      ConditionalRequirement cr = this.affect(e, left);
      return cr == null ? null : new EffBill(e).unrestrict().restrict(cr).bEff();
   }

   private ConditionalRequirement affect(Eff e, Eff left) {
      ConditionalRequirement mine = EffUtils.getRestrictionsForTog(e).get(0);
      ConditionalRequirement theirs = EffUtils.getRestrictionsForTog(left).get(0);
      return this.comb(mine, theirs);
   }

   private String reqDesc() {
      switch (this.index) {
         case 0:
            return "AND";
         case 1:
            return "OR";
         case 2:
            return "XOR";
         default:
            return "??";
      }
   }

   private ConditionalRequirement comb(ConditionalRequirement mine, ConditionalRequirement theirs) {
      switch (this.index) {
         case 0:
            return new CombinedRequirement(mine, theirs);
         case 1:
            return new ORRequirement(mine, theirs);
         case 2:
            return new XORRequirement(mine, theirs);
         default:
            return null;
      }
   }
}

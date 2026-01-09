package com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.weirdTextmod;

import com.tann.dice.gameplay.effect.eff.Eff;
import com.tann.dice.gameplay.effect.eff.EffBill;
import com.tann.dice.gameplay.effect.eff.EffUtils;
import com.tann.dice.gameplay.effect.eff.conditionalBonus.conditionalRequirement.ConditionalRequirement;
import com.tann.dice.gameplay.effect.eff.conditionalBonus.conditionalRequirement.NotRequirement;
import com.tann.dice.gameplay.effect.eff.keyword.KUtils;
import java.util.List;

public class TogResCh extends BasicTextmodToggle {
   final int index;

   public TogResCh(int index) {
      this.index = index;
   }

   @Override
   public String describe() {
      return "alter the targeting restriction " + this.index;
   }

   @Override
   public Eff alterEff(Eff e) {
      List<ConditionalRequirement> restt = EffUtils.getRestrictionsForTog(e);
      if (restt.size() != 1) {
         return null;
      } else {
         ConditionalRequirement tr = this.affect(restt.get(0));
         return new EffBill(e).unrestrict().restrict(tr).bEff();
      }
   }

   private ConditionalRequirement affect(ConditionalRequirement tr) {
      switch (this.index) {
         case 0:
            return new NotRequirement(tr);
         case 1:
            return KUtils.getSwapped(tr);
         default:
            return tr;
      }
   }
}

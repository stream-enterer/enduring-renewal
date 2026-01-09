package com.tann.dice.gameplay.trigger.personal.eff;

import com.tann.dice.gameplay.effect.eff.Eff;
import com.tann.dice.gameplay.effect.eff.EffUtils;
import com.tann.dice.gameplay.effect.eff.conditionalBonus.conditionalRequirement.ConditionalRequirement;
import com.tann.dice.gameplay.effect.eff.conditionalBonus.conditionalRequirement.GSCConditionalRequirement;
import com.tann.dice.gameplay.effect.targetable.SimpleTargetable;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.trigger.personal.Personal;
import com.tann.dice.gameplay.trigger.personal.linked.stateCondition.GenericStateCondition;

public class StartOfTurnSelf extends PersonalEffContainer {
   final Eff[] effs;
   ConditionalRequirement req;

   public StartOfTurnSelf(Eff... effs) {
      this((ConditionalRequirement)null, effs);
   }

   public StartOfTurnSelf(GenericStateCondition gsc, Eff... effs) {
      this(new GSCConditionalRequirement(gsc), effs);
   }

   public StartOfTurnSelf(ConditionalRequirement req, Eff... effs) {
      super(effs);
      this.effs = effs;
      this.req = req;
   }

   @Override
   public String getImageName() {
      switch (this.effs[0].getType()) {
         case Mana:
            return "tap";
         case Shield:
            switch (this.effs[0].getValue()) {
               case 1:
               case 3:
               default:
                  return "shield";
               case 2:
                  return "shield2";
               case 4:
                  return "shield4";
            }
         case Heal:
            return "red2";
         default:
            return super.getImageName();
      }
   }

   @Override
   public boolean showInEntPanelInternal() {
      return true;
   }

   @Override
   public void startOfTurn(EntState self, int turn) {
      if (this.req == null || this.req.isValid(self.getSnapshot(), self, null, null)) {
         for (Eff e : this.effs) {
            if (e.needsTarget()) {
               self.getSnapshot().target(self.getEnt(), new SimpleTargetable(self.getEnt(), e), false);
            } else {
               self.getSnapshot().target(null, new SimpleTargetable(self.getEnt(), e), false);
            }
         }
      }
   }

   @Override
   public String describeForSelfBuff() {
      String startOfTurn = "[notranslate]" + com.tann.dice.Main.t("At the start of each turn, ");
      String result = Eff.hyphenInsteadOfNewline(EffUtils.describe(this.effs)).toLowerCase();
      if (this.req != null && this.req.describe(null) != null) {
         result = result + " " + com.tann.dice.Main.t(this.req.describe(null).toLowerCase());
      }

      result = result.replaceAll("target's sides", "my sides");
      return startOfTurn + result;
   }

   @Override
   public Personal genMult(int mult) {
      Eff[] cpy = new Eff[this.effs.length];

      for (int i = 0; i < cpy.length; i++) {
         cpy[i] = EndOfTurnEff.gme(this.effs[i], mult);
         if (cpy[i] == null) {
            return null;
         }
      }

      return new StartOfTurnSelf(this.req, cpy);
   }
}

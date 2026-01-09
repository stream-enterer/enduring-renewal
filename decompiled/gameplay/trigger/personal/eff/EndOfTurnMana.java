package com.tann.dice.gameplay.trigger.personal.eff;

import com.tann.dice.gameplay.effect.eff.Eff;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.fightLog.Snapshot;
import com.tann.dice.gameplay.fightLog.event.snapshot.ManaGainEvent;
import com.tann.dice.util.lang.Words;

public class EndOfTurnMana extends PersonalEffContainer {
   private final boolean orMore;
   private final int manaRequired;
   private final Eff eff;

   public EndOfTurnMana(int manaRequired, boolean orMore, Eff eff) {
      super(eff);
      this.manaRequired = manaRequired;
      this.orMore = orMore;
      this.eff = eff;
   }

   @Override
   public boolean showInEntPanelInternal() {
      return true;
   }

   @Override
   public String getImageName() {
      switch (this.manaRequired) {
         case 0:
            return "chaliceEmpty";
         case 3:
            return "chaliceOverflow";
         default:
            return super.getImageName();
      }
   }

   @Override
   public String describeForSelfBuff() {
      return "If you have "
         + this.manaRequired
         + (this.orMore ? " or more" : "")
         + " "
         + Words.manaString()
         + " at the end of the turn, "
         + this.eff.describe().toLowerCase();
   }

   @Override
   public void endOfTurn(EntState entState) {
      Snapshot snapshot = entState.getSnapshot();
      int mana = snapshot.getTotalMana();
      if (!this.orMore || mana >= this.manaRequired) {
         if (this.orMore || mana == this.manaRequired) {
            snapshot.addEvent(new ManaGainEvent(this.eff.getValue(), "chalice"));
            snapshot.untargetedUse(this.eff, entState.getEnt());
         }
      }
   }
}

package com.tann.dice.gameplay.trigger.personal;

import com.tann.dice.gameplay.effect.eff.Eff;
import com.tann.dice.gameplay.fightLog.EntState;

public class ShieldsRemaining extends Personal {
   Eff self;
   boolean forEach;

   public ShieldsRemaining(Eff self, boolean forEach) {
      this.self = self;
      this.forEach = forEach;
   }

   @Override
   public boolean showInEntPanelInternal() {
      return true;
   }

   @Override
   public String getImageName() {
      return "ironSkin";
   }

   @Override
   public void endOfTurn(EntState entState) {
      int shieldsRemaining = entState.getShields();
      if (shieldsRemaining > 0) {
         Eff toHit = this.self;
         if (this.forEach) {
            toHit = this.self.copy();
            toHit.setValue(shieldsRemaining);
         }

         useEffMaybeUntargeted(entState, toHit);
      }
   }

   public static void useEffMaybeUntargeted(EntState src, Eff eff) {
      if (eff.needsTarget()) {
         src.hit(eff, src.getEnt());
      } else {
         src.getSnapshot().untargetedUse(eff, src.getEnt());
      }
   }

   @Override
   public String describeForSelfBuff() {
      String result = "At the end of the turn: ";
      return this.forEach
         ? result + this.self.toString().toLowerCase() + " for each remaining shield"
         : result + " if I have any remaining shields, " + this.self.toString().toLowerCase();
   }
}

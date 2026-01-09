package com.tann.dice.gameplay.trigger.global.linked;

import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.fightLog.Snapshot;
import com.tann.dice.gameplay.trigger.Collision;
import com.tann.dice.gameplay.trigger.personal.Personal;
import java.util.List;

public class GlobalTopNonMagic extends GlobalLinked {
   public final Personal personal;

   public GlobalTopNonMagic(Personal personal) {
      super(personal);
      this.personal = personal;
   }

   @Override
   public String describeForSelfBuff() {
      String end = this.personal.describeForSelfBuff();
      return "The top non-magic hero:[n]" + end;
   }

   @Override
   public Personal getLinkedPersonal(EntState entState) {
      if (!entState.getEnt().isPlayer()) {
         return super.getLinkedPersonal(entState);
      } else {
         return !this.isValid(entState) ? null : this.personal;
      }
   }

   private boolean isValid(EntState entState) {
      Snapshot s = entState.getSnapshot();
      List<EntState> ents = s.getStates(true, false);

      for (int i = 0; i < ents.size(); i++) {
         EntState e = ents.get(i);
         if ((e.getEnt().entType.getCollisionBits() & Collision.SPELL) == 0L) {
            if (e == entState) {
               return true;
            }

            return false;
         }
      }

      return false;
   }
}

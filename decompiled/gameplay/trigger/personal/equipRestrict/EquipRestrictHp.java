package com.tann.dice.gameplay.trigger.personal.equipRestrict;

import com.tann.dice.gameplay.content.ent.Ent;
import com.tann.dice.gameplay.content.ent.Hero;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.fightLog.FightLog;
import com.tann.dice.gameplay.fightLog.Snapshot;
import java.util.List;

public class EquipRestrictHp extends EquipRestrict {
   @Override
   public String describeForSelfBuff() {
      return "[purple]Highest-hp hero only[cu]";
   }

   @Override
   public boolean unequip(Ent ent) {
      if (!(ent instanceof Hero)) {
         return true;
      } else {
         Hero h = (Hero)ent;
         EntState es = h.getState(FightLog.Temporality.Present);
         if (es == null) {
            return false;
         } else {
            Snapshot s = es.getSnapshot();
            if (s == null) {
               return false;
            } else {
               List<Ent> as = s.getEntities(true, null);
               int myHp = ent.entType.hp;

               for (Ent de : as) {
                  if (de.entType.hp > myHp) {
                     return true;
                  }
               }

               return false;
            }
         }
      }
   }
}

package com.tann.dice.screens.dungeon.panels.hourglass;

import com.tann.dice.gameplay.fightLog.Snapshot;
import com.tann.dice.gameplay.trigger.global.scaffolding.turnRequirement.TurnRequirement;
import java.util.List;

public class HourglassElement {
   public final TurnRequirement turnReq;
   public String message;
   public final HourglassTime hourglassTime;

   public HourglassElement(TurnRequirement turnReq, String message, HourglassTime hourglassTime) {
      this.turnReq = turnReq;
      this.message = message;
      this.hourglassTime = hourglassTime;
   }

   public List<Integer> getTurns(int turn) {
      return this.turnReq.nextTurnsAfter(turn);
   }

   public boolean hourglassShouldBeHighlit(Snapshot present) {
      int turn = present.getTurn();
      List<Integer> its = this.getTurns(turn);
      if (its == null) {
         return false;
      } else {
         for (Integer it : its) {
            int compare = this.hourglassTime == HourglassTime.START ? it - 1 : it;
            if (turn == compare) {
               return true;
            }
         }

         return false;
      }
   }

   public String getRealMessage() {
      return this.message;
   }
}

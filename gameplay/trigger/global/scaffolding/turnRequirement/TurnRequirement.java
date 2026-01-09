package com.tann.dice.gameplay.trigger.global.scaffolding.turnRequirement;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.util.Pixl;
import java.util.ArrayList;
import java.util.List;

public abstract class TurnRequirement {
   static final int futureCheck = 3;

   public abstract boolean isValid(int var1);

   public abstract String describe();

   public Actor makePanelActor() {
      return new Pixl().text("[text]" + this.describe(), 35).pix();
   }

   public final List<Integer> nextTurnsAfter(int turn) {
      List<Integer> results = new ArrayList<>();

      for (int i = turn; i < turn + 3; i++) {
         if (this.isValid(i)) {
            results.add(i);
         }
      }

      return results.size() == 0 ? null : results;
   }

   public String hyphenTag() {
      return null;
   }
}

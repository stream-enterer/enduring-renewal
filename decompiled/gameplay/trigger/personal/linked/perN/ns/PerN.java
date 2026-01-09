package com.tann.dice.gameplay.trigger.personal.linked.perN.ns;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.fightLog.Snapshot;

public abstract class PerN {
   public abstract Actor makePanelActor();

   public abstract int getAmt(Snapshot var1, EntState var2);

   public abstract String describe();

   public long getCollisionBits(Boolean player) {
      return 0L;
   }

   public String describePer() {
      return "per " + this.describe();
   }
}

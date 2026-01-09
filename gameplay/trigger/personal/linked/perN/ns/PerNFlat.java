package com.tann.dice.gameplay.trigger.personal.linked.perN.ns;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.fightLog.Snapshot;
import com.tann.dice.util.ui.TextWriter;

public class PerNFlat extends PerN {
   final int n;

   public PerNFlat(int n) {
      this.n = n;
   }

   @Override
   public Actor makePanelActor() {
      return new TextWriter("[pink]" + this.n);
   }

   @Override
   public int getAmt(Snapshot snapshot, EntState entState) {
      return this.n;
   }

   @Override
   public String describePer() {
      return "x" + this.n;
   }

   @Override
   public String describe() {
      return this.n + "";
   }
}

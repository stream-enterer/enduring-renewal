package com.tann.dice.gameplay.trigger.personal.specificMonster;

import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.fightLog.Snapshot;
import com.tann.dice.gameplay.fightLog.event.snapshot.SnapshotTextEvent;
import com.tann.dice.gameplay.modifier.Modifier;
import com.tann.dice.gameplay.trigger.personal.Personal;
import com.tann.dice.gameplay.trigger.personal.item.GainModifier;
import com.tann.dice.util.Tann;

public class Vase extends Personal {
   final Modifier toGain;

   public Vase(Modifier c) {
      this.toGain = c;
   }

   @Override
   public boolean showInEntPanelInternal() {
      return true;
   }

   @Override
   public String getImageName() {
      return "blight";
   }

   @Override
   public String describeForSelfBuff() {
      return "Upon death, gain '" + Tann.makeEllipses(this.toGain.getName(true)) + "' [grey](after combat)";
   }

   @Override
   public void onDeath(EntState self, Snapshot snapshot) {
      self.addBuff(new GainModifier(this.toGain));
      snapshot.addEvent(new SnapshotTextEvent("[yellow]+1 modifier [text](after combat)"));
   }
}

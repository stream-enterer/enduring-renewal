package com.tann.dice.gameplay.trigger.personal.specificMonster;

import com.tann.dice.gameplay.effect.targetable.SimpleTargetable;
import com.tann.dice.gameplay.effect.targetable.ability.Ability;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.fightLog.Snapshot;
import com.tann.dice.gameplay.fightLog.event.snapshot.SnapshotTextEvent;
import com.tann.dice.gameplay.trigger.personal.Personal;

public class Orb extends Personal {
   final Ability castOnDeath;

   public Orb(Ability c) {
      this.castOnDeath = c;
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
      return "Upon death, " + this.castOnDeath.describe();
   }

   @Override
   public void onDeath(EntState self, Snapshot snapshot) {
      snapshot.target(null, new SimpleTargetable(self.getEnt(), this.castOnDeath.getBaseEffect()), false);
      snapshot.addEvent(new SnapshotTextEvent("On death: " + this.castOnDeath.getTitle()));
   }
}

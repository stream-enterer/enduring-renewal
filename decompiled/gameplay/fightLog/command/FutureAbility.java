package com.tann.dice.gameplay.fightLog.command;

import com.tann.dice.gameplay.content.ent.Ent;
import com.tann.dice.gameplay.effect.eff.Eff;
import com.tann.dice.gameplay.effect.targetable.SimpleTargetable;
import com.tann.dice.gameplay.effect.targetable.Targetable;
import com.tann.dice.gameplay.fightLog.Snapshot;

public class FutureAbility {
   public final Targetable targetable;
   public final Ent target;

   public FutureAbility(Targetable targetable, Ent target, Snapshot snapshot) {
      Eff e = targetable.getDerivedEffects(snapshot);
      e.clearKeywords();
      this.targetable = new SimpleTargetable(targetable.getSource(), e);
      this.target = target;
   }
}

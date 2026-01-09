package com.tann.dice.gameplay.effect.targetable;

import com.tann.dice.gameplay.content.ent.Ent;
import com.tann.dice.gameplay.effect.eff.Eff;
import com.tann.dice.gameplay.fightLog.Snapshot;
import java.util.List;

public interface Targetable {
   Eff getBaseEffect();

   Eff getDerivedEffects();

   Eff getDerivedEffects(Snapshot var1);

   boolean isUsable(Snapshot var1);

   Ent getSource();

   boolean isPlayer();

   void afterUse(Snapshot var1, Eff var2, List<Integer> var3);

   void beforeUse(Snapshot var1, Eff var2, List<Integer> var3);
}

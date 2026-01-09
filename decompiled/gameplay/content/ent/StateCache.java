package com.tann.dice.gameplay.content.ent;

import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.fightLog.FightLog;
import com.tann.dice.util.Tann;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StateCache {
   final List<FightLog.Temporality> tracked;
   final Map<FightLog.Temporality, EntState> map;
   final Ent ent;

   public StateCache(Ent ent, FightLog.Temporality... tracked) {
      this.tracked = Arrays.asList(tracked);
      Tann.clearDupes(this.tracked);
      this.ent = ent;
      this.map = new HashMap<>();
      this.update();
   }

   public boolean update() {
      boolean changed = false;

      for (int i = 0; i < this.tracked.size(); i++) {
         FightLog.Temporality t = this.tracked.get(i);
         EntState currentState = this.ent.getState(t);
         EntState old = this.map.get(t);
         if (old != currentState) {
            this.map.put(t, currentState);
            changed = true;
         }
      }

      return changed;
   }

   public EntState get(FightLog.Temporality temporality) {
      return this.map.get(temporality);
   }
}

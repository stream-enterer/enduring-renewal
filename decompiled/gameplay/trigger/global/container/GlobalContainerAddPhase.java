package com.tann.dice.gameplay.trigger.global.container;

import com.tann.dice.gameplay.context.DungeonContext;
import com.tann.dice.gameplay.fightLog.Snapshot;
import com.tann.dice.gameplay.phase.Phase;
import com.tann.dice.gameplay.trigger.global.Global;
import java.util.ArrayList;
import java.util.List;

public class GlobalContainerAddPhase extends Global {
   final List<Global> addPhases;

   public GlobalContainerAddPhase(List<Global> addPhases) {
      this.addPhases = addPhases;
   }

   @Override
   public List<Phase> getPhases(DungeonContext dungeonContext) {
      List<Phase> result = new ArrayList<>();
      List<Global> globs = new ArrayList<>(this.addPhases);
      Snapshot.addLinked(globs, dungeonContext.getCurrentLevelNumber(), dungeonContext, 0);

      for (int i = 0; i < globs.size(); i++) {
         result.addAll(globs.get(i).getPhases(dungeonContext));
      }

      return result;
   }

   @Override
   public long getCollisionBits(Boolean player) {
      long bit = 0L;

      for (int i = 0; i < this.addPhases.size(); i++) {
         bit |= this.addPhases.get(i).getCollisionBits(player);
      }

      return bit;
   }

   @Override
   public String describeForSelfBuff() {
      return "not implemented yet.. ContainerAddPhase";
   }
}

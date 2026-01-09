package com.tann.dice.gameplay.phase.levelEndPhase;

import com.tann.dice.gameplay.phase.Phase;
import java.util.ArrayList;
import java.util.List;

public class LevelEndData {
   List<String> ps = new ArrayList<>();

   public LevelEndData() {
   }

   public LevelEndData(List<Phase> phases) {
      for (Phase p : phases) {
         this.ps.add(p.serialise());
      }
   }

   public List<Phase> makePhases() {
      List<Phase> result = new ArrayList<>();

      for (String s : this.ps) {
         result.add(Phase.deserialise(s));
      }

      return result;
   }
}

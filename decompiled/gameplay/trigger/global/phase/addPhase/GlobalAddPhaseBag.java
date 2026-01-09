package com.tann.dice.gameplay.trigger.global.phase.addPhase;

import com.tann.dice.gameplay.context.DungeonContext;
import com.tann.dice.gameplay.context.config.event.EventGenerator;
import com.tann.dice.gameplay.phase.Phase;
import com.tann.dice.gameplay.trigger.global.Global;
import com.tann.dice.gameplay.trigger.global.scaffolding.levelRequirement.LevelRequirement;
import com.tann.dice.gameplay.trigger.global.scaffolding.levelRequirement.LevelRequirementHash;
import com.tann.dice.util.Tann;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GlobalAddPhaseBag extends Global {
   private static final int MAX_PHASES = 4;
   private static final boolean log = false;
   final List<EventGenerator> adds;
   final float targetTotalStrength;

   public GlobalAddPhaseBag(List<EventGenerator> adds, float targetTotalStrength) {
      this.adds = adds;
      this.targetTotalStrength = targetTotalStrength;

      for (int i = 0; i < adds.size(); i++) {
         LevelRequirement lr = adds.get(i).lr;
         if (lr instanceof LevelRequirementHash) {
            ((LevelRequirementHash)lr).setSeedOffset(i);
         }
      }
   }

   @Override
   public List<Phase> getPhases(DungeonContext dungeonContext) {
      List<EventGenerator> actives = this.getActives(dungeonContext);
      List<Phase> result = new ArrayList<>();

      for (EventGenerator active : actives) {
         if (active.lr.validFor(dungeonContext)) {
            result.addAll(active.pg.get(dungeonContext));
         }
      }

      return result;
   }

   public List<EventGenerator> getActives(DungeonContext dc) {
      Random r = Tann.makeStdRandom(dc.getSeed());
      int attempts = 50;

      for (int i = 0; i < 50; i++) {
         List<EventGenerator> rs = this.attemptToGetActives(r, dc);
         if (rs != null) {
            return rs;
         }
      }

      return new ArrayList<>();
   }

   private List<EventGenerator> attemptToGetActives(Random r, DungeonContext dc) {
      List<EventGenerator> results = new ArrayList<>();
      ArrayList<EventGenerator> cpy = new ArrayList<>();
      float currentStrength = 0.0F;
      float maxOvershoot = 0.3F;
      List<Integer> takenLevels = new ArrayList<>();

      for (int i = 0; i < 100; i++) {
         if (cpy.isEmpty()) {
            cpy = new ArrayList<>(this.adds);
         }

         EventGenerator rs = this.get(cpy, r);
         cpy.remove(rs);
         LevelRequirement lr = rs.lr;
         if (lr instanceof LevelRequirementHash) {
            int lv = ((LevelRequirementHash)lr).getChallengeLevel(dc);
            if (takenLevels.contains(lv)) {
               return null;
            }

            takenLevels.add(lv);
         }

         float str = rs.getStrength(dc);
         if (currentStrength + str > this.targetTotalStrength + 0.3F) {
            return null;
         }

         results.add(rs);
         currentStrength += str;
         if (currentStrength >= this.targetTotalStrength) {
            return results;
         }

         if (results.size() >= 4) {
            return null;
         }
      }

      return results;
   }

   public EventGenerator get(List<EventGenerator> lst, Random r) {
      float total = 0.0F;

      for (EventGenerator add : lst) {
         total += add.chance;
      }

      float rnd = r.nextFloat() * total;

      for (EventGenerator t : lst) {
         rnd -= t.chance;
         if (rnd <= 0.0F) {
            return t;
         }
      }

      throw new RuntimeException("oops?");
   }
}

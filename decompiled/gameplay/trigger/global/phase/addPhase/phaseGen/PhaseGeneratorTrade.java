package com.tann.dice.gameplay.trigger.global.phase.addPhase.phaseGen;

import com.tann.dice.gameplay.context.DungeonContext;
import com.tann.dice.gameplay.modifier.Modifier;
import com.tann.dice.gameplay.modifier.ModifierLib;
import com.tann.dice.gameplay.modifier.ModifierPickContext;
import com.tann.dice.gameplay.modifier.ModifierPickUtils;
import com.tann.dice.gameplay.modifier.modBal.TierUtils;
import com.tann.dice.gameplay.phase.Phase;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.choosable.Choosable;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.choosable.ChoosableUtils;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.trade.TradePhase;
import com.tann.dice.util.TannLog;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class PhaseGeneratorTrade extends PhaseGenerator {
   @Override
   public List<Phase> generate(DungeonContext dc) {
      TradePhase tp = new TradePhase(this.make(dc));
      return Arrays.asList(tp);
   }

   private List<Choosable> make(DungeonContext dc) {
      return this.makeRandomEqualChoosable(dc);
   }

   private List<Choosable> makeRandomEqualChoosable(DungeonContext dc) {
      List<Choosable> result = new ArrayList<>(this.makeRandomEqual(dc));
      ModifierLib.getCache().decacheChoosables(result);
      Collections.sort(result, new Comparator<Choosable>() {
         public int compare(Choosable o1, Choosable o2) {
            return Float.compare(o2.getModTier(), o1.getModTier());
         }
      });
      return result;
   }

   private List<Choosable> makeRandomEqual(DungeonContext dc) {
      long start = System.currentTimeMillis();
      int attempts = 200;
      int maxElements = 3;
      List<Modifier> library = new ArrayList<>();
      List<Choosable> result = new ArrayList<>();

      for (int i = 0; i < 200; i++) {
         result.clear();
         if (library.isEmpty()) {
            library = this.makeLibrary(dc);
         }

         result.add(library.remove(0));
         int tier = totalTier(result);

         for (int li = 0; li < library.size(); li++) {
            Modifier m = library.get(li);
            if (m.getTier() == -tier) {
               result.add(m);
               break;
            }
         }

         Boolean b = this.validList(result);
         if (b != null && b) {
            TannLog.log("Trade took " + (System.currentTimeMillis() - start) + "ms and " + (i + 1) + " attempts");
            return result;
         }
      }

      TannLog.error("failed to find trade with 200/3");
      return new ArrayList<>();
   }

   private List<Modifier> makeLibrary(DungeonContext dc) {
      int libSize = 50;
      List<Modifier> library = ModifierPickUtils.generateModifiers(-20, 20, libSize, ModifierPickContext.Trade, dc);

      for (int i1 = library.size() - 1; i1 >= 0; i1--) {
         Modifier m = library.get(i1);
         if (m.getTier() == 0) {
            library.remove(i1);
         }
      }

      return library;
   }

   private static int totalTier(List<Choosable> result) {
      int tier = 0;

      for (int i = 0; i < result.size(); i++) {
         tier += result.get(i).getTier();
      }

      return tier;
   }

   private Boolean validList(List<Choosable> result) {
      if (ChoosableUtils.anyCollides(result)) {
         return false;
      } else if (result.size() < 2) {
         return null;
      } else {
         boolean anyBig = false;

         for (int i = 0; i < result.size(); i++) {
            if (Math.abs(result.get(i).getModTier()) >= 1.0F) {
               anyBig = true;
               break;
            }
         }

         if (!anyBig) {
            return null;
         } else {
            return Math.abs(TierUtils.totalModTier(result)) <= 0.2F ? true : null;
         }
      }
   }
}

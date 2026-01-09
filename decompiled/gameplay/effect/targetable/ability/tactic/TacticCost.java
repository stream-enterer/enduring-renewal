package com.tann.dice.gameplay.effect.targetable.ability.tactic;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.effect.eff.Eff;
import com.tann.dice.gameplay.effect.eff.keyword.Keyword;
import com.tann.dice.gameplay.fightLog.EntSideState;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.fightLog.Snapshot;
import com.tann.dice.gameplay.phase.PhaseManager;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.ChoicePhase;
import com.tann.dice.util.Pixl;
import com.tann.dice.util.Tann;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TacticCost {
   final List<TacticCostType> costs;

   public TacticCost(List<TacticCostType> costs) {
      this.costs = costs;
   }

   public TacticCost(TacticCostType... costs) {
      this(Arrays.asList(costs));
   }

   public TacticCost(TacticCostType t, int amt) {
      this(makeList(t, amt));
   }

   private static List<TacticCostType> makeList(TacticCostType t, int amt) {
      List<TacticCostType> result = new ArrayList<>();

      for (int i = 0; i < amt; i++) {
         result.add(t);
      }

      return result;
   }

   public List<Actor> makeActors(Snapshot snapshot) {
      List<Actor> result = new ArrayList<>();
      List<TacticCostType> fulfilledCosts = this.getFulfilledCosts(snapshot);
      if (PhaseManager.get().getPhase() instanceof ChoicePhase) {
         fulfilledCosts.clear();
      }

      Map<TacticCostType, Integer> map = this.getCostAmtsUnwise();
      List<TacticCostType> ordered = new ArrayList<>(this.costs);
      Tann.uniquify(ordered);

      for (TacticCostType costType : ordered) {
         int amt = map.get(costType);
         if (amt > 2) {
            boolean fulf = Tann.countInList(costType, fulfilledCosts) >= amt;
            result.add(new Pixl().actor(costType.getActor(fulf)).gap(-1).text("[text]x" + map.get(costType)).pix());
         } else {
            for (int i = 0; i < amt; i++) {
               result.add(costType.getActor(fulfilledCosts.remove(costType)));
            }
         }
      }

      return result;
   }

   public boolean isUsable(Snapshot snapshot) {
      return this.getFulfilledCosts(snapshot).size() == this.costs.size();
   }

   public List<EntState> getHeroesWhoWantToBeUsedForTactic(Snapshot snapshot) {
      List<EntState> result = new ArrayList<>();
      ArrayList<TacticCostType> cpy = new ArrayList<>(this.costs);

      for (EntState state : snapshot.getStates(true, false)) {
         if (!state.isUsed()) {
            EntSideState ess = state.getCurrentSideState();
            if (ess != null) {
               Eff e = ess.getCalculatedEffect();
               List<TacticCostType> validTypes = TacticCostType.getValidTypes(e);
               boolean found = false;

               for (int i = 0; i < validTypes.size(); i++) {
                  TacticCostType v = validTypes.get(i);
                  int amt = v.pippy && e.hasValue() ? e.getValue() : 1;

                  for (int i1 = 0; i1 < amt; i1++) {
                     found |= cpy.remove(v);
                  }
               }

               if (found) {
                  result.add(state);
               }

               if (cpy.isEmpty()) {
                  return result;
               }
            }
         }
      }

      return result;
   }

   private List<TacticCostType> getFulfilledCosts(Snapshot snapshot) {
      if (snapshot == null) {
         return new ArrayList<>();
      } else {
         List<EntState> heroes = this.getHeroesWhoWantToBeUsedForTactic(snapshot);
         ArrayList<TacticCostType> cpy = new ArrayList<>(this.costs);
         ArrayList<TacticCostType> fulfilled = new ArrayList<>();

         for (EntState state : heroes) {
            Eff e = state.getCurrentSideState().getCalculatedEffect();
            List<TacticCostType> validTypes = TacticCostType.getValidTypes(e);

            for (int i = 0; i < validTypes.size(); i++) {
               TacticCostType v = validTypes.get(i);
               int amt = e.hasValue() && v.pippy ? e.getValue() : 1;
               if (e.hasKeyword(Keyword.tactical)) {
                  amt *= 2;
               }

               for (int i1 = 0; i1 < amt; i1++) {
                  if (cpy.remove(v)) {
                     fulfilled.add(v);
                  }
               }
            }
         }

         return fulfilled;
      }
   }

   public String describe() {
      if (this.costs.size() == 0) {
         return null;
      } else {
         int store = 0;
         List<String> parts = new ArrayList<>();

         for (int i = 0; i < this.costs.size(); i++) {
            TacticCostType costType = this.costs.get(i);
            if (i < this.costs.size() - 1 && this.costs.get(i + 1) == costType) {
               store++;
            } else {
               int thisAmt = 1 + store;
               store = 0;
               parts.add(costType.describe(thisAmt));
            }
         }

         return Tann.commaList(parts);
      }
   }

   public Map<TacticCostType, Integer> getCostAmtsUnwise() {
      Map<TacticCostType, Integer> result = new HashMap<>();

      for (TacticCostType cost : this.costs) {
         if (result.get(cost) == null) {
            result.put(cost, 0);
         }

         result.put(cost, result.get(cost) + 1);
      }

      return result;
   }
}

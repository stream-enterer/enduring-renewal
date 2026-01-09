package com.tann.dice.gameplay.effect.targetable.ability.tactic;

import com.tann.dice.gameplay.content.ent.type.HeroType;
import com.tann.dice.gameplay.content.ent.type.lib.HeroTypeLib;
import com.tann.dice.gameplay.content.item.Item;
import com.tann.dice.gameplay.content.item.ItemLib;
import com.tann.dice.gameplay.effect.eff.EffBill;
import com.tann.dice.gameplay.effect.eff.keyword.Keyword;
import com.tann.dice.gameplay.effect.targetable.ability.Ability;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TacticUtils {
   public static List<Tactic> makeAll(boolean includeWeird) {
      List<Tactic> result = new ArrayList<>();

      for (HeroType heroType : HeroTypeLib.getMasterCopy()) {
         Tactic t = heroType.getTactic();
         if (t != null) {
            result.add(t);
         }
      }

      for (Item item : ItemLib.getMasterCopy()) {
         Ability a = item.getAbility();
         if (a instanceof Tactic) {
            result.add((Tactic)a);
         }
      }

      if (includeWeird) {
         result.addAll(makeWeird());
      }

      return result;
   }

   public static List<Tactic> makeWeird() {
      String PREF = "DT";
      return Arrays.asList(
         new Tactic("DTzzz", new TacticCost(TacticCostType.basicSword, 4), new EffBill().damage(6).bEff()),
         new Tactic("DTReshape", new TacticCost(TacticCostType.pips4), new EffBill().shield(4).keywords(Keyword.cleave).bEff()),
         new Tactic("DTDestroy", new TacticCost(TacticCostType.wild, 20), new EffBill().damage(20).bEff()),
         new Tactic("DTStar", new TacticCost(TacticCostType.blank, TacticCostType.blank, TacticCostType.blank), new EffBill().damage(5).bEff()),
         new Tactic("DTthrow", new TacticCost(TacticCostType.basicSword, TacticCostType.basicSword), new EffBill().damage(2).keywords(Keyword.ranged).bEff()),
         new Tactic("DTRecycle", new TacticCost(TacticCostType.wild, TacticCostType.wild, TacticCostType.wild), new EffBill().damage(1).bEff()),
         new Tactic("DTFeint", new TacticCost(TacticCostType.basicHeal, TacticCostType.basicShield), new EffBill().damage(2).bEff()),
         new Tactic("DTcomb", new TacticCost(TacticCostType.basicHeal, TacticCostType.basicShield), new EffBill().healAndShield(1).group().bEff()),
         new Tactic(
            "DToiwennn",
            new TacticCost(TacticCostType.basicShield, TacticCostType.basicShield, TacticCostType.basicShield, TacticCostType.basicShield),
            new EffBill().recharge().bEff()
         )
      );
   }
}

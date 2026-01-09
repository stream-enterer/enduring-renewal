package com.tann.dice.test;

import com.tann.dice.gameplay.content.ent.die.side.EntSide;
import com.tann.dice.gameplay.content.ent.type.HeroType;
import com.tann.dice.gameplay.content.ent.type.lib.HeroTypeLib;
import com.tann.dice.gameplay.effect.eff.Eff;
import com.tann.dice.gameplay.effect.eff.EffType;
import com.tann.dice.gameplay.effect.eff.VisualEffectType;
import com.tann.dice.gameplay.effect.targetable.ability.tactic.Tactic;
import com.tann.dice.gameplay.effect.targetable.ability.tactic.TacticCostType;
import com.tann.dice.gameplay.effect.targetable.ability.tactic.TacticUtils;
import com.tann.dice.test.util.Test;
import com.tann.dice.util.Tann;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class TestAbilities {
   @Test
   public static void testTacticAnim() {
      List<Tactic> bad = new ArrayList<>();

      for (Tactic tactic : TacticUtils.makeAll(false)) {
         Eff e = tactic.getBaseEffect();
         if (e.getType() != EffType.Or && !e.isFriendly() && (e.getVisual() == null || e.getVisual() == VisualEffectType.None)) {
            bad.add(tactic);
         }
      }

      Tann.assertBads(bad);
   }

   @Test
   public static void ensureUsableTactics() {
      List<HeroType> bads = new ArrayList<>();

      for (HeroType type : HeroTypeLib.getMasterCopy()) {
         if (!type.getName().equalsIgnoreCase("reflection")) {
            Tactic a = type.getTactic();
            if (a != null) {
               Map<TacticCostType, Integer> map = a.debugGetTacticCost().getCostAmtsUnwise();
               List<TacticCostType> types = new ArrayList<>(map.keySet());

               for (EntSide side : type.sides) {
                  types.removeAll(TacticCostType.getValidTypes(side.getBaseEffect()));
               }

               if (!types.isEmpty()) {
                  bads.add(type);
               }
            }
         }
      }

      Tann.assertBads(bads);
   }

   @Test
   public static void testAbilityFirstLetters() {
      List<Character> firstLetters = new ArrayList<>();
      List<Tactic> ts = TacticUtils.makeAll(false);
      Collections.sort(ts, makeAlph());

      for (Tactic t : ts) {
         firstLetters.add(t.getTitle().toLowerCase().charAt(0));
      }

      int size = firstLetters.size();
      Tann.uniquify(firstLetters);
      Tann.assertTrue("Abilities with same name: " + ts, size == firstLetters.size());
   }

   @Test
   public static void testAbilityCosts() {
      List<String> hashes = new ArrayList<>();
      List<Tactic> ts = TacticUtils.makeAll(false);
      Collections.sort(ts, makeAlph());

      for (Tactic t : ts) {
         hashes.add(t.debugGetTacticCost().describe());
      }

      List<String> cpy = new ArrayList<>(hashes);
      Tann.uniquify(cpy);
      Tann.assertTrue("Abilities with same name: " + cpy + ":" + hashes, cpy.size() == hashes.size());
   }

   private static Comparator<? super Tactic> makeAlph() {
      return new Comparator<Tactic>() {
         public int compare(Tactic o1, Tactic o2) {
            return o1.getTitle().compareTo(o2.getTitle());
         }
      };
   }
}

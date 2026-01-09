package com.tann.dice.test;

import com.tann.dice.gameplay.content.ent.die.side.EntSide;
import com.tann.dice.gameplay.content.ent.type.HeroCol;
import com.tann.dice.gameplay.content.ent.type.HeroType;
import com.tann.dice.gameplay.content.ent.type.lib.HeroTypeLib;
import com.tann.dice.gameplay.content.ent.type.lib.HeroTypeUtils;
import com.tann.dice.gameplay.content.item.Item;
import com.tann.dice.gameplay.content.item.ItemLib;
import com.tann.dice.gameplay.effect.eff.EffType;
import com.tann.dice.gameplay.effect.eff.keyword.Keyword;
import com.tann.dice.gameplay.trigger.personal.Personal;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.AffectSides;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.condition.AffectSideCondition;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.condition.SpecificSidesCondition;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.AddKeyword;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.AffectSideEffect;
import com.tann.dice.test.util.Skip;
import com.tann.dice.test.util.Test;
import com.tann.dice.util.Tann;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TestBannedCombos {
   @Test
   public static void duplicateCantrip() {
      checkKeywordCollision(Keyword.duplicate, Keyword.cantrip);
   }

   @Test
   @Skip
   public static void growthRampage() {
      checkKeywordCollision(Keyword.rampage, Keyword.growth);
   }

   @Test
   public static void cleaveKill() {
      checkEffKeywordCollision(EffType.Kill, Keyword.cleave);
   }

   @Test
   public static void quadGrowth() {
      checkKeywordCollision(Keyword.growth, Keyword.quadUse);
   }

   @Test
   @Skip
   public static void resurrectRescue() {
      checkEffKeywordCollision(EffType.Resurrect, Keyword.rescue);
   }

   @Test
   public static void growthRerollCantrip() {
      checkEffKeywordCollision(EffType.Reroll, Keyword.cantrip, Keyword.growth);
   }

   @Test
   public static void rechargeCleave() {
      checkEffKeywordCollision(EffType.Recharge, Keyword.cleave);
      checkEffKeywordCollision(EffType.Recharge, Keyword.descend);
      checkEffKeywordCollision(EffType.Recharge, Keyword.doubleUse);
      checkEffKeywordCollision(EffType.Recharge, Keyword.quadUse);
   }

   private static void checkKeywordCollision(Keyword a, Keyword b) {
      List<Integer> result = new ArrayList<>();
      result.addAll(checkSingleItemCollision(a, b));
      result.addAll(checkSingleItemCollision(b, a));
      Tann.assertTrue("Should be no collisions between " + a + " and " + b + ": " + result, result.size() == 0);
   }

   private static void checkEffKeywordCollision(EffType et, Keyword k) {
      List<Integer> result = new ArrayList<>();
      result.addAll(checkSingleItemCollision(et, k));
      if (result.size() > 0) {
         System.out.println(result);
      }

      Tann.assertTrue("Should be no collisions between " + et + " and " + k, result.size() == 0);
   }

   private static void checkEffKeywordCollision(EffType et, Keyword a, Keyword b) {
      List<Integer> result = new ArrayList<>();
      result.addAll(checkSingleItemCollision(et, a, b));
      result.addAll(checkSingleItemCollision(et, b, a));
      if (result.size() > 0) {
         System.out.println(result);
      }

      Tann.assertTrue("Should be no collisions between " + et + " and " + a + " and " + b, result.size() == 0);
   }

   private static List<Integer> checkSingleItemCollision(Keyword heroKeyword, Keyword itemKeyword) {
      List<Integer> fails = new ArrayList<>();
      Set<Integer> hero = getHeroIndices(heroKeyword);
      Set<Integer> item = getItemIndices(itemKeyword);

      for (int i : hero) {
         if (item.contains(i)) {
            fails.add(i);
         }
      }

      return fails;
   }

   private static List<Integer> checkSingleItemCollision(EffType heroEffect, Keyword itemKeyword) {
      List<Integer> fails = new ArrayList<>();
      Set<Integer> hero = getHeroIndices(heroEffect);
      Set<Integer> item = getItemIndices(itemKeyword);

      for (int i : hero) {
         if (item.contains(i)) {
            fails.add(i);
         }
      }

      return fails;
   }

   private static List<Integer> checkSingleItemCollision(EffType heroEffect, Keyword heroKeyword, Keyword itemKeyword) {
      List<Integer> fails = new ArrayList<>();
      Set<Integer> heroA = getHeroIndices(heroEffect, heroKeyword);
      Set<Integer> item = getItemIndices(itemKeyword);

      for (int i : heroA) {
         if (item.contains(i)) {
            fails.add(i);
         }
      }

      return fails;
   }

   private static Set<Integer> getItemIndices(Keyword k) {
      Set<Integer> result = new HashSet<>();

      for (Item e : ItemLib.getMasterCopy()) {
         if (e.getTier() < 10 && e.getTier() > 0 && !e.getName().contains("otion")) {
            for (Personal pt : e.getPersonals()) {
               if (pt instanceof AffectSides) {
                  AffectSides tas = (AffectSides)pt;

                  for (AffectSideEffect ase : tas.getEffects()) {
                     if (ase instanceof AddKeyword) {
                        AddKeyword ak = (AddKeyword)ase;
                        if (ak.getKeywordList().contains(k)) {
                           for (AffectSideCondition asc : tas.getConditions()) {
                              if (asc instanceof SpecificSidesCondition) {
                                 SpecificSidesCondition ssc = (SpecificSidesCondition)asc;
                                 result.addAll(Tann.intArrayToList(ssc.specificSidesType.sideIndices));
                              }
                           }
                        }
                     }
                  }
               }
            }
         }
      }

      return result;
   }

   private static Set<Integer> getHeroIndices(Keyword k) {
      Set<Integer> result = new HashSet<>();

      for (HeroType ht : HeroTypeLib.getMasterCopy()) {
         if (ht.heroCol != HeroCol.green) {
            for (int sideIndex = 0; sideIndex < 6; sideIndex++) {
               EntSide es = ht.sides[sideIndex];
               if (es.getBaseEffect().hasKeyword(k)) {
                  result.add(sideIndex);
               }
            }
         }
      }

      return result;
   }

   private static Set<Integer> getHeroIndices(EffType et) {
      Set<Integer> result = new HashSet<>();

      for (HeroType ht : HeroTypeLib.getMasterCopy()) {
         for (int sideIndex = 0; sideIndex < 6; sideIndex++) {
            EntSide es = ht.sides[sideIndex];
            if (es.getBaseEffect().getType() == et) {
               result.add(sideIndex);
            }
         }
      }

      return result;
   }

   private static Set<Integer> getHeroIndices(EffType et, Keyword k) {
      Set<Integer> result = new HashSet<>();
      List<HeroType> l = HeroTypeLib.getMasterCopy();
      List<HeroType> ignored = HeroTypeUtils.heroList("sphere", "statue", "glitch");
      l.removeAll(ignored);

      for (HeroType ht : l) {
         for (int sideIndex = 0; sideIndex < 6; sideIndex++) {
            EntSide es = ht.sides[sideIndex];
            if (es.getBaseEffect().hasKeyword(k) && es.getBaseEffect().getType() == et) {
               result.add(sideIndex);
            }
         }
      }

      return result;
   }
}

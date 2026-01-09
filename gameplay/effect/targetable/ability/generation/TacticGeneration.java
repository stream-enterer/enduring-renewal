package com.tann.dice.gameplay.effect.targetable.ability.generation;

import com.tann.dice.gameplay.content.ent.die.side.EntSide;
import com.tann.dice.gameplay.content.ent.die.side.blob.ESB;
import com.tann.dice.gameplay.content.ent.type.EntType;
import com.tann.dice.gameplay.content.ent.type.lib.HeroTypeUtils;
import com.tann.dice.gameplay.effect.eff.Eff;
import com.tann.dice.gameplay.effect.eff.EffUtils;
import com.tann.dice.gameplay.effect.eff.keyword.Keyword;
import com.tann.dice.gameplay.effect.targetable.ability.spell.SpellUtils;
import com.tann.dice.gameplay.effect.targetable.ability.tactic.Tactic;
import com.tann.dice.gameplay.effect.targetable.ability.tactic.TacticCost;
import com.tann.dice.gameplay.effect.targetable.ability.tactic.TacticCostType;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class TacticGeneration {
   public static final String GENTAC_PREF = "t";

   public static Eff getEffectWithStrengthForTactic(Random r, int tier, float strength) {
      float closeness = 0.15F;
      int attempts = 50;
      Keyword[] vals = Keyword.values();
      EntType ex = HeroTypeUtils.defaultHero(tier);

      for (int i = 0; i < 50; i++) {
         Eff e = EffUtils.random(r);
         if (SpellGeneration.okForAbilityMaybe(e)) {
            int NUM_KATT = 5;

            for (int kAtt = 0; kAtt < 5; kAtt++) {
               Keyword k = vals[r.nextInt(vals.length)];
               if (SpellUtils.allowAddingKeyword(k) && !k.reallySpellOnly()) {
                  Eff with = e.copy();
                  with.addKeyword(k);
                  float withStr = EntSide.getValueFromEffect(with, tier, ex, true);
                  boolean bad = Float.isNaN(withStr);
                  bad |= EntSide.getValueFromEffect(e, tier, ex, true) == withStr;
                  if (!bad) {
                     e = with;
                     if (r.nextFloat() > 0.1F) {
                        break;
                     }
                  }
               }
            }

            boolean valid = false;

            for (int j = 0; j < 2; j++) {
               float pw = EntSide.getValueFromEffect(e, tier, ex, true);
               valid = Math.abs(1.0F - pw / strength) < closeness;
               if (valid || !e.hasValue()) {
                  break;
               }

               float ratio = strength / pw;
               e.setValue(Math.max(1, Math.round(e.getValue() * ratio * (r.nextFloat() + 0.5F))));
            }

            if (valid) {
               return e;
            }
         }
      }

      return null;
   }

   public static Tactic makeTacticSafeMaybeNull(String heroName, int tier, Random r, EntSide[] sides) {
      int attempts = 50;

      for (int i = 0; i < 50; i++) {
         try {
            Tactic t = makeTacticForGreenGenerate(heroName, tier, r, sides);
            if (t != null) {
               return t;
            }
         } catch (Exception var7) {
         }
      }

      return null;
   }

   private static Tactic makeTacticForGreenGenerate(String heroName, int tier, Random r, EntSide[] sides) {
      TacticCost tc = getRandomCost(r, sides, tier);
      if (tc == null) {
         return null;
      } else {
         float pw = getPowerOfCostInOnePipSides(tc, tier) * HeroTypeUtils.getEffectTierFor(tier) * 0.8F;
         if (pw < 0.0F) {
            return null;
         } else {
            Eff e = getEffectWithStrengthForTactic(r, tier, pw);
            return e == null ? null : new Tactic("t" + heroName, tc, e);
         }
      }
   }

   public static float getPowerOfCostInOnePipSides(TacticCost cost, int tier) {
      float total = 0.0F;
      Map<TacticCostType, Integer> costAmts = cost.getCostAmtsUnwise();

      for (TacticCostType costType : costAmts.keySet()) {
         total += getPowerOfMiniCost(costType, costAmts.get(costType), tier);
      }

      return total;
   }

   private static float getPowerOfMiniCost(TacticCostType costType, int amt, int tier) {
      switch (costType) {
         case basicSword:
            return multi(amt, 1.0F, tier);
         case basicShield:
            return multi(amt, 0.7F, tier);
         case basicHeal:
            return multi(amt, 0.7F, tier);
         case basicMana:
            return multi(amt, 1.3F, tier);
         case wild:
            return multi(amt, 0.5F, tier);
         case blank:
            return 0.8F * amt;
         case pips1:
            return 1.0F * amt;
         case pips2:
            return 1.15F * amt;
         case pips3:
            return 1.3F * amt;
         case pips4:
            return 1.5F * amt;
         case keyword:
            return 1.1F * amt;
         case twoKeywords:
            return 1.35F * amt;
         case fourKeywords:
            return 2.7F * amt;
         default:
            return Float.NaN;
      }
   }

   private static float multi(int amt, float factor, int tier) {
      float extraFactor = 2.7F / HeroTypeUtils.getEffectTierFor(tier);
      return 0.6F + amt * factor * 0.2F * extraFactor;
   }

   private static TacticCost getRandomCost(Random r, EntSide[] sides, int tier) {
      List<TacticCostType> chosen = new ArrayList<>();
      float tpw = 0.0F;
      float eft = HeroTypeUtils.getEffectTierFor(tier);
      int typesAdded = 0;
      boolean hasPippy = false;
      boolean hasSpecificPipsOrKeyword = false;
      boolean hasWild = false;

      for (EntSide side : sides) {
         if (side != ESB.blank || !(r.nextFloat() > 0.1F)) {
            Eff be = side.getBaseEffect();
            List<TacticCostType> valids = TacticCostType.getValidTypes(be);
            if (!valids.isEmpty()) {
               TacticCostType test = valids.get(r.nextInt(valids.size()));
               if (!chosen.contains(test)) {
                  int amt = test.pippy ? Math.round((float)r.nextInt((int)(eft * r.nextFloat() * 2.0F) + 1)) : 1;
                  amt = Math.max(1, amt);
                  float pw = getPowerOfMiniCost(test, amt, tier);
                  if (!(tpw + pw > HeroTypeUtils.getEffectTierFor(tier) * 2.5F)) {
                     boolean nk = test.name().contains("ips") || test.name().contains("eyword");
                     if ((!hasPippy || !nk)
                        && (!test.pippy || !hasSpecificPipsOrKeyword)
                        && (!hasWild || !test.pippy)
                        && (!hasPippy || test != TacticCostType.wild)) {
                        hasWild |= test == TacticCostType.wild;
                        hasPippy |= test.pippy;
                        hasSpecificPipsOrKeyword |= nk;
                        tpw += pw;

                        for (int i = 0; i < amt; i++) {
                           chosen.add(test);
                        }

                        if (r.nextFloat() * ++typesAdded > 0.6F) {
                           break;
                        }
                     }
                  }
               }
            }
         }
      }

      return chosen.size() == 0 ? null : new TacticCost(chosen);
   }
}

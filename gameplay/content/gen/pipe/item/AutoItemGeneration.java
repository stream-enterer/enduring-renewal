package com.tann.dice.gameplay.content.gen.pipe.item;

import com.tann.dice.gameplay.content.ent.die.side.EntSide;
import com.tann.dice.gameplay.content.ent.die.side.EntSidesLib;
import com.tann.dice.gameplay.content.ent.die.side.blob.ESB;
import com.tann.dice.gameplay.content.ent.type.HeroType;
import com.tann.dice.gameplay.content.ent.type.lib.HeroTypeUtils;
import com.tann.dice.gameplay.content.item.ItBill;
import com.tann.dice.gameplay.content.item.Item;
import com.tann.dice.gameplay.effect.eff.keyword.KUtils;
import com.tann.dice.gameplay.effect.eff.keyword.Keyword;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.AffectSides;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.condition.SpecificSidesType;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.AddKeyword;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.ReplaceWith;
import com.tann.dice.util.Tann;
import com.tann.dice.util.tp.TP;
import java.util.Random;

public class AutoItemGeneration {
   private static final int SPIRAL_IN_ITER = 4;

   private static TP<Integer, AffectSides> makeAs(Random r) {
      switch (r.nextInt(3)) {
         case 0:
            return balancedGeneratedKeyword(r);
         case 1:
         default:
            return balancedGeneratedReplaceSides(r, false);
         case 2:
            return balancedGeneratedReplaceSides(r, true);
      }
   }

   private static TP<Integer, AffectSides> balancedGeneratedReplaceSides(Random r, boolean addKeyword) {
      SpecificSidesType sst = getNiceSidesType(r);
      float guessedHeroTier = 2.0F;
      EntSide es = EntSidesLib.random(r);
      if (addKeyword) {
         float preVal = AutoItemTier.guessPowerReplace((int)guessedHeroTier, es, sst);
         Keyword k = Tann.random(Keyword.values(), r);
         EntSide added = es.withKeyword(k);
         float postVal = AutoItemTier.guessPowerReplace((int)guessedHeroTier, added, sst);
         if (preVal == postVal) {
            return null;
         }

         es = added;
      }

      float guessed = AutoItemTier.guessPowerReplace(Math.round(guessedHeroTier), es, sst);
      if (Float.isNaN(guessed)) {
         return null;
      } else {
         guessedHeroTier = guessed / 4.0F + 1.0F;

         for (int i = 0; i < 4; i++) {
            int rounded = Math.round(guessedHeroTier);
            float discrep = guessedHeroTier - rounded;
            float var11 = AutoItemTier.guessPowerReplace(rounded, es, sst) + discrep * 1.2F;
            float var12 = Math.max(-1.0F, var11);
            guessed = Math.min(999.0F, var12);
            guessedHeroTier = (guessedHeroTier + guessed / 4.0F + 1.0F) / 2.0F;
         }

         return new TP<>(Math.round(guessed), new AffectSides(sst, new ReplaceWith(es)));
      }
   }

   private static TP<Integer, AffectSides> balancedGeneratedKeyword(Random r) {
      SpecificSidesType sst = getNiceSidesType(r);
      Keyword k = Tann.random(Keyword.values(), r);
      if (KUtils.isNonLinearWhenConvertingToAllHeroes(k)) {
         return null;
      } else {
         EntSide damage = ESB.dmgCleave.val(2);
         float guessed = 1.0F;
         float guessedHeroTier = 2.0F;

         for (int i = 0; i < 4; i++) {
            HeroType standIn = HeroTypeUtils.defaultHero(Math.round(guessedHeroTier));
            float preVal = damage.getApproxTotalEffectTier(standIn);
            damage = damage.withKeyword(k);
            float postVal = damage.getApproxTotalEffectTier(standIn);
            float delta = postVal - preVal;
            float sstFactor = getFactorKeywordAdd(sst);
            float flatMultiplier = 2.0F;
            guessed = Math.round(delta * sstFactor * flatMultiplier);
            guessedHeroTier = (guessedHeroTier + guessed / 4.0F + 1.0F) / 2.0F;
         }

         return new TP<>(Math.round(guessed), new AffectSides(sst, new AddKeyword(k)));
      }
   }

   private static float getFactorKeywordAdd(SpecificSidesType sst) {
      float result = 0.0F;

      for (int side : sst.sideIndices) {
         switch (side) {
            case 0:
            case 1:
               result += 0.7F;
               break;
            case 2:
               result++;
               break;
            case 3:
               result += 0.4F;
               break;
            case 4:
               result += 0.85F;
               break;
            case 5:
               result += 0.35F;
         }
      }

      return result;
   }

   public static SpecificSidesType getNiceSidesType(Random r) {
      return SpecificSidesType.getNiceSidesType(r);
   }

   public static Item make(String name, Random r) {
      TP<Integer, AffectSides> as = null;

      while (as == null || as.a <= 0) {
         as = makeAs(r);
      }

      return new ItBill(as.a, name, PipeItemGenerated.genItemPath(name)).prs(as.b).bItem();
   }
}

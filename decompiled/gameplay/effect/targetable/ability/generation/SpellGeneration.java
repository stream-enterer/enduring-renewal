package com.tann.dice.gameplay.effect.targetable.ability.generation;

import com.tann.dice.gameplay.content.ent.die.side.EntSide;
import com.tann.dice.gameplay.content.ent.die.side.blob.ESB;
import com.tann.dice.gameplay.content.ent.type.HeroCol;
import com.tann.dice.gameplay.content.ent.type.lib.HeroTypeUtils;
import com.tann.dice.gameplay.content.gen.pipe.entity.hero.generate.PipeHeroGenerated;
import com.tann.dice.gameplay.effect.eff.Eff;
import com.tann.dice.gameplay.effect.eff.EffType;
import com.tann.dice.gameplay.effect.eff.TargetingType;
import com.tann.dice.gameplay.effect.eff.conditionalBonus.conditionalRequirement.ConditionalRequirement;
import com.tann.dice.gameplay.effect.eff.keyword.Keyword;
import com.tann.dice.gameplay.effect.targetable.ability.spell.Spell;
import com.tann.dice.gameplay.effect.targetable.ability.spell.SpellUtils;
import com.tann.dice.statics.ImageUtils;
import com.tann.dice.util.WhiskerRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class SpellGeneration {
   public static Spell generate(int tier, long seed, HeroCol col) {
      WhiskerRandom r = new WhiskerRandom(seed);
      float tierManaVal = EffType.Mana.getEffectTier(tier, 1.0F, true, null);
      int cost = r.nextInt(r.nextInt(6) + 1) + 1;
      float SCL_FAC = 1.05F;
      float scaleCalc = (float)(Math.pow(1.05F, cost) - 0.049999952F);
      float calculatedEffect = tierManaVal * cost * scaleCalc;
      Eff e = getSpellEffect(calculatedEffect, r, tier, col);
      return new Spell(
         e, "s" + col.shortName() + "" + tier + "." + PipeHeroGenerated.getGeneratedString(seed), ImageUtils.loadExt("ability/spell/special/generated"), cost
      );
   }

   public static Eff getSpellEffect(float val, Random r, int tier, HeroCol col) {
      HeroCol sideCol = col;
      if (col == HeroCol.blue) {
         sideCol = r.nextBoolean() ? HeroCol.orange : HeroCol.yellow;
      }

      if (sideCol == HeroCol.red) {
         sideCol = r.nextBoolean() ? HeroCol.grey : HeroCol.red;
      }

      List<EntSide> sides = new ArrayList<>(HeroTypeUtils.getSidesWithColour(sideCol, true, false));
      Collections.shuffle(sides, r);
      Eff best = ESB.blank.getBaseEffect();
      float bestDiff = 5000.0F;

      for (int sideIndex = 0; sideIndex < sides.size(); sideIndex++) {
         EntSide es = sides.get(sideIndex);
         Eff esBase = es.getBaseEffect();
         if (okForAbilityMaybe(esBase)) {
            for (int v = 1; v < tier * 2 + 2; v++) {
               EntSide es2 = es.withValue(v);
               float pw = es2.getEffectTier(HeroTypeUtils.defaultHero(tier));
               switch (es2.getBaseEffect().getType()) {
                  case Resurrect:
                     pw *= 1.4F;
                  default:
                     float diff = Math.abs(val - pw);
                     if (diff / val < 0.04F) {
                        return es2.getBaseEffect();
                     }

                     if (diff < bestDiff) {
                        bestDiff = diff;
                        best = es2.getBaseEffect();
                     }
               }
            }
         }
      }

      return best;
   }

   public static boolean okForAbilityMaybe(Eff esBase) {
      for (ConditionalRequirement restriction : esBase.getRestrictions()) {
         if (restriction.toString().endsWith("Me")) {
            return false;
         }
      }

      EffType type = esBase.getType();
      if (type == EffType.Mana || type == EffType.Recharge || type == EffType.RedirectIncoming || type == EffType.Blank) {
         return false;
      } else if (esBase.getTargetingType() == TargetingType.Self) {
         return false;
      } else {
         for (Keyword w : esBase.getKeywords()) {
            if (!SpellUtils.allowAddingKeyword(w)) {
               return false;
            }
         }

         return true;
      }
   }
}

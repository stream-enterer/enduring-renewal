package com.tann.dice.gameplay.content.gen.pipe.entity.hero.generate;

import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.tann.dice.gameplay.content.ent.die.side.EntSide;
import com.tann.dice.gameplay.content.ent.die.side.blob.ESB;
import com.tann.dice.gameplay.content.ent.type.HeroCol;
import com.tann.dice.gameplay.content.ent.type.HeroType;
import com.tann.dice.gameplay.content.ent.type.bill.HTBill;
import com.tann.dice.gameplay.content.ent.type.lib.HeroTypeUtils;
import com.tann.dice.gameplay.content.gen.pipe.entity.hero.PipeHero;
import com.tann.dice.gameplay.content.gen.pipe.regex.PipeRegexNamed;
import com.tann.dice.gameplay.effect.Trait;
import com.tann.dice.gameplay.effect.eff.Eff;
import com.tann.dice.gameplay.effect.eff.EffType;
import com.tann.dice.gameplay.effect.eff.keyword.KUtils;
import com.tann.dice.gameplay.effect.eff.keyword.Keyword;
import com.tann.dice.gameplay.effect.targetable.ability.Ability;
import com.tann.dice.gameplay.effect.targetable.ability.generation.SpellGeneration;
import com.tann.dice.gameplay.effect.targetable.ability.generation.TacticGeneration;
import com.tann.dice.gameplay.effect.targetable.ability.spell.Spell;
import com.tann.dice.gameplay.effect.targetable.ability.spell.SpellLib;
import com.tann.dice.gameplay.effect.targetable.ability.tactic.Tactic;
import com.tann.dice.gameplay.modifier.generation.GenUtils;
import com.tann.dice.gameplay.save.settings.option.OptionLib;
import com.tann.dice.gameplay.save.settings.option.OptionUtils;
import com.tann.dice.gameplay.trigger.Collision;
import com.tann.dice.gameplay.trigger.personal.Generated;
import com.tann.dice.gameplay.trigger.personal.Personal;
import com.tann.dice.gameplay.trigger.personal.spell.learn.LearnTactic;
import com.tann.dice.statics.ImageUtils;
import com.tann.dice.util.Tann;
import com.tann.dice.util.TannLog;
import com.tann.dice.util.WhiskerRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class PipeHeroGenerated extends PipeRegexNamed<HeroType> {
   public static final int MAX_GENERATE_TIER = 999;
   private static final int MAX_SEED = (int)Math.pow(16.0, 3.0);
   private static final SingleAttempt REG_GEN = new SingleAttempt() {
      @Override
      public EntSide getRandomSide(Random r, HTBill htBill, List<EntSide> options, float targetStrength, HeroType example, int sideIndex) {
         return PipeHeroGenerated.getRandomSide(r, htBill, options, targetStrength, example, sideIndex);
      }
   };
   public static String RS = "blip";
   public static String BS = "blep";

   public PipeHeroGenerated() {
      super(HEROCOL, UP_TO_THREE_DIGITS_TIER, prnS("\\."), UP_TO_FIFTEEN_HEX);
   }

   protected HeroType internalMake(String[] groups) {
      String colS = groups[0];
      String tierS = groups[1];
      String seedS = groups[2];
      HeroCol col = HeroCol.byName(colS);
      if (col == null) {
         return null;
      } else if (!Tann.isInt(tierS)) {
         return null;
      } else {
         int tier = Integer.parseInt(tierS);
         if (tier >= 0 && tier <= 999) {
            long seed = GenUtils.hex(seedS);
            if (seed < 0L) {
               return null;
            } else {
               List<EntSide> options = HeroTypeUtils.getSidesWithColour(col, true, false);
               return multiAttempt(col, tier, seed, options, col.shortName() + tierS + "." + seedS, 1.0F, REG_GEN, texture(col, tier));
            }
         } else {
            return null;
         }
      }
   }

   public static HeroType multiAttempt(HeroCol col, int tier, long seed, List<EntSide> sides, String name, float cutoff, SingleAttempt sa, AtlasRegion texture) {
      return generate(col, tier, seed, sides, name, cutoff, sa, texture);
   }

   public static EntSide getRandomSide(Random r, HTBill htBill, List<EntSide> options, float targetStrength, HeroType example, int sideIndex) {
      if (sideIndex > 2 && r.nextInt(3) != 0) {
         return htBill.getSides()[r.nextInt(sideIndex)];
      } else {
         int attempts = 18;
         float SPECTRUM_WIDEN_MAX = 0.3F;
         float base = HeroTypeUtils.getEffectTierFor(example.getTier());
         float lowerThreshold = base * 0.35F;
         float upperThreshold = base * 1.15F;
         EntSide best = null;
         float bestDiff = 99999.0F;
         float bestVal = 99999.0F;

         for (int i = 0; i < attempts; i++) {
            EntSide newSide = options.get(r.nextInt(options.size())).copy();
            affectValue(newSide, example, targetStrength, r);
            float val = newSide.getApproxTotalEffectTier(example);
            float mult = 1.0F + (float)i / attempts * 0.3F;
            float diff = Math.abs(base - val);
            if (diff < bestDiff) {
               best = newSide;
               bestDiff = diff;
               bestVal = val;
            }

            if (val < 0.0F || bestVal > lowerThreshold * (1.0F / mult) && bestVal < upperThreshold * mult) {
               break;
            }
         }

         return best;
      }
   }

   static void affectValue(EntSide newSide, HeroType example, float targetStrength, Random r) {
      float MAX_MULTIPLIER = 1.7F;
      Eff e = newSide.getBaseEffect();
      if (e.hasValue()) {
         e.setValue(1);

         for (int i = 0; i < 3; i++) {
            float val = Math.abs(newSide.getApproxTotalEffectTier(example));
            float ratio = val / targetStrength;
            int newValue = (int)Math.round(Math.pow(r.nextFloat(), 2.0) / ratio * 1.7F * e.getValue());
            if (e.hasKeyword(Keyword.pain) && newValue > example.hp + 2) {
               newValue /= 2;
            }

            e.setValue(Math.max(1, Math.min(999, newValue)));
            if (newValue * ratio < 0.28F && e.getType() != EffType.Reroll) {
               e.setValue(1);
            }
         }
      }
   }

   public static boolean shouldAddGenerate() {
      return OptionLib.GENERATED_HEROES.c() && Tann.chance(OptionUtils.genChance());
   }

   public static HeroType generate(HeroCol col, int tier) {
      return generate(col, tier, (int)(Math.random() * MAX_SEED));
   }

   public static String getGeneratedString(long seed) {
      return GenUtils.hex(seed);
   }

   public static HeroType generate(HeroCol col, int tier, long seed) {
      if (tier >= 0 && col.isBasic()) {
         try {
            List<EntSide> options = HeroTypeUtils.getSidesWithColour(col, true, false);
            String left = col.shortName() + tier;
            String right = getGeneratedString(seed);
            String name = left + "." + right;
            HeroType result = multiAttempt(col, tier, seed, options, name, 1.0F, REG_GEN, texture(col, tier));
            if (result != null) {
               return result;
            }

            TannLog.error("Failed to generate: " + col + tier + ":" + seed);
         } catch (Exception var9) {
            var9.printStackTrace();
            TannLog.error(var9.getClass().getSimpleName() + "pfnw");
         }

         return PipeHero.getMissingno();
      } else {
         return PipeHero.getMissingno();
      }
   }

   private static AtlasRegion texture(HeroCol col, int tier) {
      int graphicalTier = Math.max(1, Math.min(3, tier));
      String s = "portrait/hero/special/generated/" + col.shortName() + graphicalTier;

      for (int i = 0; i < ImageUtils.genImages.size(); i++) {
         AtlasRegion ar = ImageUtils.genImages.get(i);
         if (ar.name.startsWith(s)) {
            return ar;
         }
      }

      return null;
   }

   private static HeroType generate(
      HeroCol col, int tier, long seed, List<EntSide> options, String name, float STRENGTH_CUTOFF, SingleAttempt singleAttempt, AtlasRegion texture
   ) {
      if (tier > 999) {
         return PipeHero.getMissingno();
      } else {
         Random r = new WhiskerRandom(seed);
         HTBill builder = new HTBill(col, tier);
         builder.name(name);
         builder.hp((int)((r.nextFloat() * 0.6F + 0.75F) * HeroTypeUtils.getHpFor(tier)));
         builder.sides(ESB.blank, ESB.blank, ESB.blank, ESB.blank, ESB.blank, ESB.blank);
         builder.trait(new Generated(), false);
         HeroType example = builder.bEntType();
         float targetStrength = HeroTypeUtils.getEffectTierFor(tier);
         HTBill htBill = null;
         int ATTEMPTS = 20;
         float maxPowerDelta = 0.3F;
         float alwaysSub = 0.015000001F;
         Trait t = null;
         boolean hasTactic = false;

         for (int i = 0; i < 20 && htBill == null; i++) {
            float strengthCut = STRENGTH_CUTOFF - i / 20.0F * 0.3F - 0.015000001F;
            if (t != null) {
               builder.removeTrait(t);
               t = null;
            }

            hasTactic = false;
            if (example.heroCol == HeroCol.green) {
               if (r.nextFloat() < 0.8) {
                  t = addRandomGreenTrait(example.level, r);
                  if (t != null) {
                     builder.trait(t);
                  }
               }

               if (t == null) {
                  t = new Trait(new LearnTactic(new Tactic("placeholder", null, (Eff)null)));
                  hasTactic = true;
                  builder.trait(t);
               }
            }

            htBill = singleAttempt(r, builder, options, targetStrength, strengthCut, example, singleAttempt);
         }

         if (htBill == null) {
            return PipeHero.getMissingno();
         } else {
            if (hasTactic) {
               htBill.removeTrait(t);

               for (int p = 0; p < 5; p++) {
                  Tactic again = TacticGeneration.makeTacticSafeMaybeNull(name, example.level, r, builder.getSides());
                  if (again != null) {
                     htBill.trait(new LearnTactic(again));
                     break;
                  }
               }
            }

            final HeroType result = builder.bEntType();
            List<EntSide> sides = Arrays.asList(result.sides);
            Collections.sort(sides, new Comparator<EntSide>() {
               public int compare(EntSide o1, EntSide o2) {
                  return (int)Math.signum(o2.getApproxTotalEffectTier(result) - o1.getApproxTotalEffectTier(result));
               }
            });
            EntSide[] arrSides = sides.toArray(new EntSide[0]);
            symmetricate(arrSides);
            builder.sides(arrSides);
            result = builder.bEntType();
            if (HeroTypeUtils.isSpelly(result.heroCol)) {
               Spell s = null;
               if (tier >= 0 && tier <= 2 && r.nextInt(2) == 0) {
                  s = getRandomDesignedHeroSpell(result.heroCol, tier, r);
               }

               if (s == null) {
                  s = SpellGeneration.generate(tier, seed, col);
               }

               if (s == null) {
                  s = SpellLib.MISSINGNO;
               }

               builder.spell(s);
            }

            if (texture != null) {
               builder.arOverride(texture);
            }

            return builder.resetOffsets().bEntType();
         }
      }
   }

   private static Trait addRandomGreenTrait(int traitTier, Random r) {
      if (traitTier > 0 && traitTier <= 3) {
         List<HeroType> options = HeroTypeUtils.getFilteredTypes(HeroCol.green, Math.max(1, Math.min(3, traitTier)), true);
         HeroType tmp = Tann.randomElement(options, r);
         if (tmp == null) {
            return null;
         } else if (tmp.getName().equalsIgnoreCase("mimic")) {
            return null;
         } else if (Collision.collides(tmp.getCollisionBits(), Collision.MODIFIER)) {
            return null;
         } else {
            List<Trait> traits = new ArrayList<>(tmp.traits);

            for (int i = traits.size() - 1; i >= 0; i--) {
               if (traits.get(i).personal.metaOnly()) {
                  traits.remove(i);
               }
            }

            return traits.size() == 1 ? traits.get(0) : null;
         }
      } else {
         return null;
      }
   }

   private static HTBill singleAttempt(
      Random r, HTBill builder, List<EntSide> options, float targetStrength, float STRENGTH_CUTOFF, HeroType example, SingleAttempt singleAttempt
   ) {
      builder.sides(ESB.blank, ESB.blank, ESB.blank, ESB.blank, ESB.blank, ESB.blank);
      float min = targetStrength * STRENGTH_CUTOFF;
      float max = targetStrength * (2.0F - STRENGTH_CUTOFF);
      int miniAttempts = 10;

      for (int sideIndex = 0; sideIndex < 6; sideIndex++) {
         for (int attemptIndex = 0; attemptIndex < 10; attemptIndex++) {
            EntSide newSide = singleAttempt.getRandomSide(r, builder, options, targetStrength, example, sideIndex);
            builder.setSide(sideIndex, newSide);
            HeroType built = builder.bEntType();
            float pw = built.getTotalEffectTier();
            if (Float.isNaN(pw)) {
               TannLog.error("nan " + built.heroCol);
            }

            if (attemptIndex == 9) {
               if (pw > max) {
                  return null;
               }

               if (pw < min && sideIndex == 5) {
                  return null;
               }
            }

            if (!(pw > max) && pw > min && finalChecks(built)) {
               return builder;
            }
         }
      }

      return null;
   }

   public static boolean finalChecks(HeroType ht) {
      return colourCheck(ht);
   }

   private static boolean colourCheck(HeroType ht) {
      switch (ht.heroCol) {
         case grey:
            return (has(ht, EffType.Shield) || has(ht, EffType.Buff))
               && (has(ht, EffType.Damage) || has(ht, EffType.Mana) || has(ht, Keyword.manaGain) || has(ht, Keyword.repel) || has(ht, EffType.Recharge));
         case yellow:
         case orange:
            return has(ht, EffType.Damage);
         case red:
            return (has(ht, EffType.Heal) || has(ht, EffType.HealAndShield) || has(ht, Keyword.selfHeal) || has(ht, EffType.Resurrect) || has(ht, EffType.Buff))
               && (has(ht, EffType.Mana) || has(ht, Keyword.manaGain));
         case blue:
            return has(ht, EffType.Mana) || has(ht, Keyword.manaGain);
         default:
            return true;
      }
   }

   public static boolean has(HeroType ht, EffType et) {
      EntSide[] sides = ht.getNiceSides();

      for (int i = 0; i < sides.length; i++) {
         Eff e = sides[i].getBaseEffect();
         if (e.getType() == et) {
            return true;
         }
      }

      return false;
   }

   public static boolean hasOrSimilarKeyword(HeroType ht, EffType et) {
      EntSide[] sides = ht.getNiceSides();

      for (int i = 0; i < sides.length; i++) {
         Eff e = sides[i].getBaseEffect();
         if (e.getType() == et) {
            return true;
         }

         if (KUtils.hasEquivalentKeyword(et, e)) {
            return true;
         }
      }

      return false;
   }

   private static boolean has(HeroType ht, Keyword k) {
      EntSide[] sides = ht.getNiceSides();

      for (int i = 0; i < sides.length; i++) {
         Eff e = sides[i].getBaseEffect();
         if (e.hasKeyword(k)) {
            return true;
         }
      }

      return false;
   }

   private static void symmetricate(EntSide[] arrSides) {
      int numBlanks = 0;

      for (EntSide es : arrSides) {
         if (es.getBaseEffect().getType() == EffType.Blank) {
            numBlanks++;
         }
      }

      if (numBlanks == 3) {
         Tann.swap(arrSides, 2, 4);
      }

      swapCheck(arrSides, 1, 2, 3);
      swapCheck(arrSides, 1, 3, 2);
      swapCheck(arrSides, 0, 2, 3);
      swapCheck(arrSides, 4, 2, 3, 3, 5);
   }

   private static void swapCheck(EntSide[] arrSides, int from, int to, int same) {
      if (arrSides[from].same(arrSides[same]) && !arrSides[from].same(arrSides[to])) {
         Tann.swap(arrSides, from, to);
      }
   }

   private static void swapCheck(EntSide[] arrSides, int from, int to, int same1, int same2, int diff1) {
      if (arrSides[from].same(arrSides[same1])
         && arrSides[from].same(arrSides[same2])
         && !arrSides[from].same(arrSides[to])
         && !arrSides[from].same(arrSides[diff1])) {
         Tann.swap(arrSides, from, to);
      }
   }

   private static Spell getRandomDesignedHeroSpell(HeroCol heroCol, int tier, Random r) {
      List<HeroType> options = HeroTypeUtils.getFilteredTypes(heroCol, Math.min(3, tier), true);
      HeroType ht = Tann.randomElement(options, r);
      if (ht == null) {
         return null;
      } else if (ht.traits.size() == 0) {
         System.out.println("huh" + ht.getName(false));
         return null;
      } else {
         for (Trait trait : ht.traits) {
            Personal pt = trait.personal;
            Ability a = pt.getAbility();
            if (a instanceof Spell) {
               return (Spell)a;
            }
         }

         return null;
      }
   }

   public HeroType example() {
      return generate(Tann.random(HeroCol.basics()), Tann.randomInt((int)Tann.random(10.0F)));
   }
}

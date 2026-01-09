package com.tann.dice.gameplay.content.gen.pipe.entity.hero.generate;

import com.tann.dice.gameplay.content.ent.die.side.EntSide;
import com.tann.dice.gameplay.content.ent.type.HeroCol;
import com.tann.dice.gameplay.content.ent.type.HeroType;
import com.tann.dice.gameplay.content.ent.type.bill.HTBill;
import com.tann.dice.gameplay.content.ent.type.lib.HeroTypeLib;
import com.tann.dice.gameplay.content.ent.type.lib.HeroTypeUtils;
import com.tann.dice.gameplay.content.gen.pipe.regex.PipeRegexNamed;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.PRNPart;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.pos.PRNMid;
import com.tann.dice.util.Tann;
import com.tann.dice.util.TannLog;
import com.tann.dice.util.bsRandom.Checker;
import com.tann.dice.util.bsRandom.RandomCheck;
import com.tann.dice.util.bsRandom.Supplier;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class PipeHeroAdjust extends PipeRegexNamed<HeroType> {
   public static final PRNPart SEP = new PRNMid("adj");

   public PipeHeroAdjust() {
      super(HERO, SEP, SINGLE_DIGIT_TIER);
   }

   protected HeroType internalMake(String[] groups) {
      String heroName = groups[0];
      String tier = groups[1];
      return this.make(heroName, tier);
   }

   private HeroType make(String heroName, String tierSt) {
      if (bad(heroName, tierSt)) {
         return null;
      } else {
         HeroType ht = HeroTypeLib.byName(heroName);
         if (ht.isMissingno()) {
            return null;
         } else if (!Tann.isInt(tierSt)) {
            return null;
         } else {
            int tier = Integer.parseInt(tierSt);
            return makeHero(ht, tier);
         }
      }
   }

   public static HeroType makeHero(HeroCol col, int tier) {
      return makeHero(HeroTypeUtils.getRandom(col, Tann.randomExcept(new Integer[]{1, 2, 3}, tier)), tier);
   }

   public static HeroType makeHeroAlternate(final HeroCol col, final int tier) {
      HeroType ht = RandomCheck.checkedRandom(new Supplier<HeroType>() {
         public HeroType supply() {
            return PipeHeroAdjust.makeHero(col, tier);
         }
      }, new Checker<HeroType>() {
         public boolean check(HeroType heroType) {
            return heroType != null;
         }
      }, HeroTypeUtils.byName("arjghkjehrg"));
      if (ht.isMissingno()) {
         TannLog.log("uhoh");
      }

      HeroType ht2 = HeroTypeLib.byName(ht + ".n." + reVowel(ht.getName(false), tier));
      if (ht2.isMissingno()) {
         TannLog.log("uhoh2");
      }

      return ht2;
   }

   private static String reVowel(String name, int tier) {
      return name.split("\\.")[0].replaceFirst("[aeiouy]", tier + "");
   }

   public static HeroType makeHero(HeroType src, int tier) {
      String name = src.getName(false);
      if (name.contains(".")) {
         return null;
      } else if (!name.equalsIgnoreCase("statue") && !name.equalsIgnoreCase("Twin")) {
         if (src.getTier() == tier) {
            return src;
         } else {
            int seed = Math.abs(tier + src.getName(false).hashCode() % 900);
            HeroType result = PipeHeroGenerated.multiAttempt(
               src.heroCol, tier, seed, Arrays.asList(src.sides), makeName(src, tier), 1.0F, adjustedHeroSA(src), src.portrait
            );
            if (result.isMissingno()) {
               TannLog.error("Failed to make PHWT " + src.getName(false) + ":" + tier);
               return null;
            } else {
               return result;
            }
         }
      } else {
         return null;
      }
   }

   private static String makeName(HeroType ht, int tier) {
      return ht.getName(false) + SEP + tier;
   }

   public static SingleAttempt adjustedHeroSA(final HeroType base) {
      return new SingleAttempt() {
         @Override
         public EntSide getRandomSide(Random r, HTBill builder, List<EntSide> options, float targetStrength, HeroType example, int sideIndex) {
            return PipeHeroGenerated.getRandomSide(r, builder, Arrays.asList(base.sides), targetStrength, example, sideIndex);
         }
      };
   }

   public HeroType example() {
      return makeHero(Tann.random(HeroCol.basics()), Tann.randomInt(10));
   }

   @Override
   public boolean isComplexAPI() {
      return true;
   }
}

package com.tann.dice.gameplay.modifier.modBal;

import com.tann.dice.gameplay.content.ent.type.HeroType;
import com.tann.dice.gameplay.content.ent.type.MonsterType;
import com.tann.dice.gameplay.content.ent.type.lib.HeroTypeLib;
import com.tann.dice.gameplay.content.ent.type.lib.MonsterTypeLib;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.condition.SpecificSidesType;
import com.tann.dice.util.Tann;
import java.util.List;

public class ModTierUtils {
   private static final float DEAD_HERO_VAL = -8.0F;
   private static final float ALL_BLANK_SIDES_VAL = -5.3333335F;
   private static final float ALL_DEATH_SIDES_VAL = -4.266667F;
   private static final float ALL_PAIN_SIDES_VAL = -2.1333334F;
   private static final float WITHOUT_HERO_VAL = -7.0F;
   private static final float FULL_DAMAGED_VAL = -2.24F;
   private static final float ONE_HP_VAL = -2.6880002F;
   private static final float PLUS_ONE_HERO_HP = 0.42F;
   private static final float BONUS_ALL_SIDES = 3.0F;
   private static final float DOUBLE_SIDES = 5.3999996F;
   private static final float DOUBLE_MONSTER_HP_VAL = -20.0F;
   private static final float SET_MONSTER_HP_ONE_VAL = 35.0F;
   private static final float MONSTER_IMMUNE_TURN_1 = -10.0F;
   private static final float MONSTER_IMMUNE_TURN_2 = -5.4F;
   private static final float MONSTER_SHIELD_1_EACH_TURN = -4.0F;
   private static final float START_WITH_ONE_MANA = 1.5F;

   public static float doubleSides(float mult) {
      return 5.3999996F * mult;
   }

   public static float doubleSidesAllHeroes(float mult) {
      return 5.3999996F * mult * 5.0F;
   }

   public static float heroBonusAllSides(float add) {
      return 3.0F * add;
   }

   public static float randomItem(int tier) {
      return TierUtils.itemModTier(tier) * 0.9F;
   }

   public static float deadHero(float mult) {
      return -8.0F * mult;
   }

   public static float missingHero(float mult) {
      return -7.0F * mult;
   }

   public static float deathKeyword(float mult) {
      return -4.266667F * mult;
   }

   public static float painKeyword(float mult) {
      return -2.1333334F * mult;
   }

   public static float setSingleHeroOneHp(float mult) {
      return -2.6880002F * mult;
   }

   public static float blanked(float mult) {
      return -5.3333335F * mult;
   }

   public static float blankedStasis(float mult) {
      return blanked(mult * 1.25F);
   }

   public static float blankedStuck(float mult) {
      return blanked(mult * 1.45F);
   }

   public static float blankedSticky(float mult) {
      return blanked(mult * 1.45F);
   }

   public static float fullyPoisoned() {
      return deadHero(0.8F);
   }

   public static float startDamaged(float ratio) {
      return -2.24F * ratio;
   }

   public static float startWithMana(float manaStart) {
      return 1.5F * manaStart;
   }

   public static float bonusAllHeroHp(float flatBonus) {
      return 0.42F * flatBonus * 5.0F;
   }

   public static float extraMonsterHP(float ratio) {
      return ratio > 0.0F ? -20.0F * ratio : 35.0F * -ratio;
   }

   public static float monsterShieldEachTurn(float shield) {
      return -4.0F * shield;
   }

   public static float monsterImmuneTurnOne(float mult) {
      return -10.0F * mult;
   }

   public static float monsterImmuneTurnTwo(float mult) {
      return -5.4F * mult;
   }

   public static float keywordToSides(SpecificSidesType sst, float keywordVal) {
      return keywordVal * getSSTFactor(sst, keywordVal);
   }

   private static float getSSTFactor(SpecificSidesType sst, float keywordVal) {
      float result = sst.getFactor();
      float pow = 1.12F;
      if (keywordVal > 0.0F) {
         result = (float)Math.pow(result, 0.89285713F);
      } else {
         result = (float)Math.pow(result, 1.12F);
      }

      return result;
   }

   public static boolean validForTier(float tier) {
      return validForTier(tier, 0.1F);
   }

   public static boolean validForTier(float tier, float thresholdFactor) {
      return Math.abs((tier - Math.round(tier)) / tier) < thresholdFactor;
   }

   public static float getDamagedRatio(int damaged, int per) {
      float total = 0.0F;
      List<HeroType> heroes = HeroTypeLib.getMasterCopy();

      for (HeroType type : heroes) {
         float ratio = (float)(damaged * (type.hp / per)) / type.hp;
         total += ratio;
      }

      return total / heroes.size();
   }

   public static float getBonusMonsterHpRatio(int amt, int per) {
      float total = 0.0F;

      for (MonsterType type : MonsterTypeLib.getMasterCopy()) {
         float ratio = (float)(amt * (type.hp / per)) / type.hp;
         total += ratio * MonFreq.getRelativeFrequency(type);
      }

      return total;
   }

   public static float getBonusMonsterHpFlat(float flat) {
      return flat > 0.0F
         ? (float)(Math.signum(flat) * Math.pow(Math.abs(flat), 0.92) * -3.6)
         : (float)(Math.pow(Math.abs(flat), 0.8F) * Math.signum(flat) * -6.4F);
   }

   public static float calcBonusMonsterHpFlat(int flat) {
      float total = 0.0F;

      for (MonsterType type : MonsterTypeLib.getMasterCopy()) {
         int oldHp = type.hp;
         int newHp = Math.max(1, type.hp + flat);
         float ratio = (float)(newHp - oldHp) / oldHp;
         total += ratio * MonFreq.getRelativeFrequency(type);
      }

      return total;
   }

   public static float getBonusMonsterHpLetterRatio(int amt, char[] chars) {
      float total = 0.0F;

      for (MonsterType type : MonsterTypeLib.getMasterCopy()) {
         int count = Tann.countCharsInString(chars, type.getName().toLowerCase()) * amt;
         float ratio = (float)count / type.hp;
         total += ratio * MonFreq.getRelativeFrequency(type);
      }

      return total;
   }

   public static float startPoisoned(float poisonAmt) {
      int maxPoison = 8;
      return Tann.niceTerp(poisonAmt, 8.0F, 1.0F, 0.75F) * fullyPoisoned();
   }

   public static float monsterPlus(int bonus) {
      return Tann.niceTerp(bonus, 3.0F, -17.0F, 0.55F);
   }
}

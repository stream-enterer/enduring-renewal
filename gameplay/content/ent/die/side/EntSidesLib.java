package com.tann.dice.gameplay.content.ent.die.side;

import com.tann.dice.gameplay.content.ent.EntSize;
import com.tann.dice.gameplay.content.ent.die.side.blob.ESB;
import com.tann.dice.gameplay.content.ent.die.side.blob.EntSidesBlobBig;
import com.tann.dice.gameplay.content.ent.die.side.blob.EntSidesBlobHuge;
import com.tann.dice.gameplay.content.ent.die.side.blob.EntSidesBlobSmall;
import com.tann.dice.gameplay.content.ent.type.lib.HeroTypeUtils;
import com.tann.dice.gameplay.effect.eff.EffType;
import com.tann.dice.gameplay.effect.eff.keyword.KUtils;
import com.tann.dice.gameplay.effect.eff.keyword.Keyword;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.choosable.ChoosableType;
import com.tann.dice.util.Tann;
import com.tann.dice.util.TannLog;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class EntSidesLib {
   private static List<EntSide> valueOne;
   private static final List<Object> STATIC_SIDES_REG = ESB.makeAllSidesReg();
   private static final List<Object> STATIC_SIDES_BIG = EntSidesBlobBig.makeAll();
   private static final List<Object> STATIC_SIDES_SMALL = EntSidesBlobSmall.makeAll();
   private static final List<Object> STATIC_SIDES_HUGE = EntSidesBlobHuge.makeAll();

   public static EntSide getSide(EffType type, boolean basicOnly) {
      switch (type) {
         case Damage:
            return basicOnly ? ESB.dmg.val(1) : ESB.genericSword;
         case Heal:
            return basicOnly ? ESB.heal.val(1) : ESB.genericHeal;
         case Shield:
            return basicOnly ? ESB.shield.val(1) : ESB.genericShield;
         case Mana:
            return basicOnly ? ESB.mana.val(1) : ESB.genericMana;
         case Summon:
            return ESB.genericSummon;
         case Blank:
            return ESB.blank;
         case Recharge:
            if (!basicOnly) {
               return ESB.recharge;
            }
         default:
            throw new RuntimeException("No side for " + type + "/" + basicOnly);
      }
   }

   public static EntSide getBlank(ChoosableType cht) {
      if (cht == null) {
         return ESB.blank;
      } else {
         switch (cht) {
            case Item:
               return ESB.blankItem;
            case Modifier:
               return ESB.blankCurse;
            default:
               return ESB.blank;
         }
      }
   }

   public static void clearStatics() {
      valueOne = null;
   }

   public static EntSide withValue(Object side, int val) {
      if (side instanceof EntSide) {
         return (EntSide)side;
      } else if (side instanceof EnSiBi) {
         return ((EnSiBi)side).val(val);
      } else {
         TannLog.error("errside: " + side);
         return ESB.blankBug;
      }
   }

   public static List<EntSide> getAllSidesWithValue() {
      if (valueOne == null) {
         List<EntSide> sides = new ArrayList<>();

         for (Object side : STATIC_SIDES_REG) {
            sides.add(withValue(side, 1));
         }

         valueOne = sides;
      }

      return valueOne;
   }

   public static List<EntSide> getBasicSides() {
      List<EntSide> result = new ArrayList<>();

      for (EntSide es : getAllSidesWithValue()) {
         if (es.getBaseEffect().isBasic()) {
            result.add(es);
         }
      }

      return result;
   }

   public static List<EntSide> exampleKeywordSides(Keyword keyword) {
      List<EntSide> result = new ArrayList<>();

      for (EntSide es : Arrays.asList(ESB.dmg.val(1), ESB.heal.val(1), ESB.mana.val(1), ESB.dmgAll.val(1), ESB.blank, ESB.stun, ESB.undying)) {
         if (KUtils.allowAddingKeyword(keyword, es.getBaseEffect())) {
            result.add(es);
         }
      }

      return result;
   }

   public static EntSide random(Random r, boolean crazy) {
      return random(r, crazy, EntSize.reg);
   }

   private static EntSide random(Random r, boolean crazy, EntSize size) {
      return size == EntSize.reg && !crazy
         ? HeroTypeUtils.random(r).sides[r.nextInt(6)]
         : withValue(Tann.randomElement(getSizedSides(size), r), r.nextInt(10) - 2);
   }

   public static List<Object> getSizedSides(EntSize size) {
      switch (size) {
         case small:
         default:
            return STATIC_SIDES_SMALL;
         case reg:
            return STATIC_SIDES_REG;
         case big:
            return STATIC_SIDES_BIG;
         case huge:
            return STATIC_SIDES_HUGE;
      }
   }

   public static EntSide random(Random r) {
      return random(r, EntSize.reg);
   }

   public static EntSide random(Random r, EntSize size) {
      return random(r, false, size);
   }
}

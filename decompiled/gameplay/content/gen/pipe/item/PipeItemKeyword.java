package com.tann.dice.gameplay.content.gen.pipe.item;

import com.tann.dice.gameplay.content.gen.pipe.mod.keyword.PipeModKeywordSide;
import com.tann.dice.gameplay.content.gen.pipe.regex.PipeRegexNamed;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.PRNPart;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.pos.PRNPref;
import com.tann.dice.gameplay.content.item.ItBill;
import com.tann.dice.gameplay.content.item.Item;
import com.tann.dice.gameplay.content.item.ItemLib;
import com.tann.dice.gameplay.effect.eff.keyword.KUtils;
import com.tann.dice.gameplay.effect.eff.keyword.Keyword;
import com.tann.dice.gameplay.modifier.modBal.TierUtils;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.choosable.ChoosableType;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.AffectSides;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.condition.SpecificSidesType;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.AddKeyword;
import com.tann.dice.util.Tann;
import com.tann.dice.util.bsRandom.Checker;
import com.tann.dice.util.bsRandom.RandomCheck;
import com.tann.dice.util.bsRandom.Supplier;
import com.tann.dice.util.ui.TextWriter;
import java.util.Arrays;
import java.util.List;

public class PipeItemKeyword extends PipeRegexNamed<Item> {
   static final PRNPart PREF = new PRNPref("k");
   public static final List<Keyword> banned = Arrays.asList(
      Keyword.copycat,
      Keyword.cruel,
      Keyword.dispel,
      Keyword.dogma,
      Keyword.duplicate,
      Keyword.echo,
      Keyword.ego,
      Keyword.enduring,
      Keyword.engage,
      Keyword.fierce,
      Keyword.growth,
      Keyword.inspired,
      Keyword.repel,
      Keyword.rescue,
      Keyword.resilient,
      Keyword.sixth,
      Keyword.stasis,
      Keyword.steel,
      Keyword.underdog,
      Keyword.vigil,
      Keyword.zeroed,
      Keyword.duel,
      Keyword.ranged,
      Keyword.eliminate,
      Keyword.chain,
      Keyword.fierce,
      Keyword.eliminate
   );

   public PipeItemKeyword() {
      super(PREF, KEYWORD);
   }

   protected Item internalMake(String[] groups) {
      Keyword k = Keyword.byName(groups[0]);
      return k != null ? makeItem(k) : null;
   }

   public static boolean isBanned(Keyword k) {
      return banned.contains(k);
   }

   private static Item makeItem(Keyword k) {
      if (k.abilityOnly()) {
         return null;
      } else {
         float tier = calcTier(k);
         ItBill ib = new ItBill(Math.round(tier), PREF + k.name().toLowerCase(), "special/keyword/" + TextWriter.getNameForColour(k.getColour()));
         ib.prs(new AffectSides(new AddKeyword(k)));
         return ib.bItem();
      }
   }

   private static float calcTierFromHardcoded(Keyword k) {
      switch (k) {
         case ranged:
            return 5.0F;
         case echo:
            return 15.0F;
         case enduring:
            return 8.0F;
         case groooooowth:
            return 14.0F;
         case stasis:
            return 5.0F;
         case steel:
            return 16.0F;
         case selfShield:
            return 11.0F;
         case selfHeal:
            return 9.0F;
         case rescue:
            return 14.0F;
         case rampage:
            return 16.0F;
         case cruel:
            return 8.0F;
         case duel:
            return 9.0F;
         case engage:
            return 11.0F;
         case poison:
            return 11.0F;
         default:
            return -69.0F;
      }
   }

   private static float calcTier(Keyword k) {
      float f = calcTierFromHardcoded(k);
      if (f != -69.0F) {
         return f;
      } else {
         f = calcTierFromBlessing(k);
         return f != -69.0F ? f : calcTierFromEff(k);
      }
   }

   private static float calcTierFromEff(Keyword k) {
      float base = PipeModKeywordSide.guessBaseVal(k);
      return base == 0.0F ? -69.0F : base * 1.4F;
   }

   private static float calcTierFromBlessing(Keyword k) {
      float tier = KUtils.getModTierAllHero(k) / 5.0F;
      if (tier == 0.0F) {
         return -69.0F;
      } else {
         tier = TierUtils.fromModTier(ChoosableType.Item, tier);
         tier = Math.max(0.0F, tier);
         tier *= 1.2F;
         if (isBanned(k)) {
            tier = -69.0F;
         }

         return tier;
      }
   }

   public Item example() {
      return makeItem(RandomCheck.checkedRandom(new Supplier<Keyword>() {
         public Keyword supply() {
            return Tann.random(Keyword.values());
         }
      }, new Checker<Keyword>() {
         public boolean check(Keyword keyword) {
            return !keyword.abilityOnly();
         }
      }, Keyword.pain));
   }

   public static Item makeRandomForEvent(SpecificSidesType sst) {
      Keyword k = RandomCheck.checkedRandom(Arrays.asList(Keyword.values()), new Checker<Keyword>() {
         public boolean check(Keyword keyword) {
            return !keyword.abilityOnly();
         }
      }, Keyword.enduring);
      return ItemLib.byName(sst.getShortName() + ".k." + k);
   }

   @Override
   public float getRarity(boolean wild) {
      return 0.3F;
   }

   @Override
   public boolean canGenerate(boolean wild) {
      return true;
   }

   protected Item generateInternal(boolean wild) {
      int attempts = 5;

      for (int at = 0; at < attempts; at++) {
         Item i = makeItem(Tann.random(Keyword.values()));
         if (i != null && i.getTier() != 0) {
            return i;
         }
      }

      return null;
   }

   @Override
   public boolean showHigher() {
      return true;
   }
}

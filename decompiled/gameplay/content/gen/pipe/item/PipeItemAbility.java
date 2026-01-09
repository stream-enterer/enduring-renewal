package com.tann.dice.gameplay.content.gen.pipe.item;

import com.tann.dice.gameplay.content.gen.pipe.regex.PipeRegexNamed;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.PRNPart;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.pos.PRNPref;
import com.tann.dice.gameplay.content.item.ItBill;
import com.tann.dice.gameplay.content.item.Item;
import com.tann.dice.gameplay.effect.targetable.ability.Ability;
import com.tann.dice.gameplay.effect.targetable.ability.AbilityUtils;
import com.tann.dice.gameplay.trigger.personal.spell.learn.LearnAbility;

public class PipeItemAbility extends PipeRegexNamed<Item> {
   public static final PRNPart PREF = new PRNPref("learn");

   public PipeItemAbility() {
      super(PREF, ABILITY);
   }

   protected Item internalMake(String[] groups) {
      String abTag = groups[0];
      Ability a = AbilityUtils.byName(abTag);
      return a == null ? null : this.makeInternal(a, abTag);
   }

   private Item makeInternal(Ability ab, String abTag) {
      if (ab == null) {
         return null;
      } else {
         float tier = AbilityUtils.likeFromHeroTier(ab);
         int approxTier;
         if (Float.isNaN(tier)) {
            approxTier = 0;
         } else {
            approxTier = Math.round(AbilityUtils.heroTierFactorToItemTier(tier));
         }

         return new ItBill(approxTier, PREF + abTag, ab.getImage()).prs(LearnAbility.make(ab)).bItem();
      }
   }

   @Override
   public boolean canGenerate(boolean wild) {
      return true;
   }

   protected Item generateInternal(boolean wild) {
      if (!wild) {
      }

      Ability a = AbilityUtils.random();
      Item i = this.makeInternal(a, a.getTitle());
      return i.getTier() == 0 ? null : i;
   }

   public Item example() {
      return this.generateInternal(false);
   }

   @Override
   public boolean showHigher() {
      return true;
   }
}

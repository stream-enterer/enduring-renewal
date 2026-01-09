package com.tann.dice.gameplay.content.gen.pipe.item;

import com.tann.dice.gameplay.content.ent.die.side.EnSiBi;
import com.tann.dice.gameplay.content.ent.die.side.EntSide;
import com.tann.dice.gameplay.content.gen.pipe.regex.PipeRegexNamed;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.PRNPart;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.pos.PRNPref;
import com.tann.dice.gameplay.content.item.ItBill;
import com.tann.dice.gameplay.content.item.Item;
import com.tann.dice.gameplay.modifier.Modifier;
import com.tann.dice.gameplay.modifier.ModifierLib;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.AffectSides;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.condition.SpecificSidesType;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.ReplaceWith;

public class PipeItemEnchant extends PipeRegexNamed<Item> {
   private static final PRNPart PREF = new PRNPref("enchant");

   public PipeItemEnchant() {
      super(PREF, MODIFIER);
   }

   protected Item internalMake(String[] groups) {
      return this.makeShortened(ModifierLib.byName(groups[0]));
   }

   protected Item makeShortened(Modifier src) {
      if (src.isMissingno()) {
         return null;
      } else {
         int tier = Math.round(src.getTier() * 0.3F);
         EntSide es = new EnSiBi().enchant(src);
         return new ItBill(tier, PREF + src.getName(false), "special/enchant").prs(new AffectSides(SpecificSidesType.Middle, new ReplaceWith(es))).bItem();
      }
   }

   @Override
   public boolean isComplexAPI() {
      return true;
   }

   public Item example() {
      return this.makeShortened(ModifierLib.byName("extra reroll"));
   }
}

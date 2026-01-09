package com.tann.dice.gameplay.content.gen.pipe.item.sideReally;

import com.tann.dice.gameplay.content.ent.die.side.EnSiBi;
import com.tann.dice.gameplay.content.ent.die.side.EntSide;
import com.tann.dice.gameplay.content.gen.pipe.regex.PipeRegexNamed;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.PRNPart;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.pos.PRNPref;
import com.tann.dice.gameplay.content.item.ItBill;
import com.tann.dice.gameplay.content.item.Item;
import com.tann.dice.gameplay.effect.targetable.ability.Ability;
import com.tann.dice.gameplay.effect.targetable.ability.AbilityUtils;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.AffectSides;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.condition.SpecificSidesType;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.ReplaceWith;
import com.tann.dice.util.Tann;

public class PipeItemCast extends PipeRegexNamed<Item> {
   private static final PRNPart PREF = new PRNPref("cast");

   public PipeItemCast() {
      super(PREF, ABILITY);
   }

   protected Item internalMake(String[] groups) {
      return this.makeShortened(groups[0]);
   }

   protected Item makeShortened(String abilityName) {
      Ability src = AbilityUtils.byName(abilityName);
      return src == null ? null : make(-69, PREF + abilityName, "cast", new EnSiBi().cast(src));
   }

   protected static Item make(int tier, String name, String specialTx, EntSide side) {
      return new ItBill(tier, name, "special/" + specialTx).prs(new AffectSides(SpecificSidesType.Middle, new ReplaceWith(side))).bItem();
   }

   @Override
   public boolean isComplexAPI() {
      return true;
   }

   public Item example() {
      return Tann.half() ? this.makeShortened("slice") : this.makeShortened("sthief.abilitydata.mage");
   }
}

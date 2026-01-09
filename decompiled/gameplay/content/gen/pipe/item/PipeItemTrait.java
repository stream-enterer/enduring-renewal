package com.tann.dice.gameplay.content.gen.pipe.item;

import com.tann.dice.gameplay.content.ent.type.EntType;
import com.tann.dice.gameplay.content.ent.type.lib.EntTypeUtils;
import com.tann.dice.gameplay.content.ent.type.lib.MonsterTypeLib;
import com.tann.dice.gameplay.content.gen.pipe.regex.PipeRegexNamed;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.PRNPart;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.pos.PRNPref;
import com.tann.dice.gameplay.content.item.ItBill;
import com.tann.dice.gameplay.content.item.Item;
import com.tann.dice.gameplay.effect.Trait;
import com.tann.dice.gameplay.trigger.global.chance.MonsterChance;
import com.tann.dice.gameplay.trigger.personal.Personal;
import com.tann.dice.gameplay.trigger.personal.linked.TriggerPersonalToGlobal;
import com.tann.dice.gameplay.trigger.personal.stats.CalcOnly;
import java.util.ArrayList;
import java.util.List;

public class PipeItemTrait extends PipeRegexNamed<Item> {
   static final PRNPart PREF = new PRNPref("t");

   public PipeItemTrait() {
      super(PREF, ENTITY);
   }

   public Item example() {
      return this.make(MonsterTypeLib.randomWithRarity());
   }

   protected Item internalMake(String[] groups) {
      String mName = groups[0];
      return bad(mName) ? null : this.make(EntTypeUtils.byName(mName));
   }

   private Item make(EntType src) {
      if (src.isMissingno()) {
         return null;
      } else if (src.traits.size() == 0) {
         return null;
      } else {
         ItBill ib = new ItBill(-69, PREF + src.getName(), "special/trait");
         int found = 0;

         for (Personal p : getFromTraits(src)) {
            ib.prs(p);
            found++;
         }

         return found == 0 ? null : ib.bItem();
      }
   }

   public static List<Trait> getValidTraits(EntType e) {
      List<Trait> result = new ArrayList<>();

      for (Trait trait : e.traits) {
         Personal p = trait.personal;
         if (!p.skipTraitPanel() && !(p instanceof TriggerPersonalToGlobal) && !(p instanceof MonsterChance) && !(p instanceof CalcOnly)) {
            result.add(trait);
         }
      }

      return result;
   }

   public static List<Personal> getFromTraits(EntType e) {
      List<Personal> result = new ArrayList<>();

      for (Trait trait : getValidTraits(e)) {
         result.add(trait.personal);
      }

      return result;
   }

   @Override
   public boolean showHigher() {
      return true;
   }
}

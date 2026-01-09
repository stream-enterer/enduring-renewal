package com.tann.dice.gameplay.content.gen.pipe.mod.pool;

import com.tann.dice.gameplay.content.gen.pipe.regex.PipeRegexNamed;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.PRNPart;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.pos.PRNPref;
import com.tann.dice.gameplay.content.item.Item;
import com.tann.dice.gameplay.content.item.ItemLib;
import com.tann.dice.gameplay.modifier.Modifier;
import com.tann.dice.gameplay.trigger.global.pool.item.GlobalClearPoolItem;
import com.tann.dice.gameplay.trigger.global.pool.item.GlobalExtraItemPool;
import com.tann.dice.util.Tann;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PipeModItemPool extends PipeRegexNamed<Modifier> {
   static final PRNPart PREF = new PRNPref("itempool");

   public PipeModItemPool() {
      super(PREF, ITEM_MULTI);
   }

   protected Modifier internalMake(String[] groups) {
      String itemStrings = groups[0];
      if (bad(itemStrings)) {
         return null;
      } else {
         String[] sep = itemStrings.split("\\+", -1);
         if (bad(sep)) {
            return null;
         } else {
            List<Item> types = new ArrayList<>();

            for (int i = 0; i < sep.length; i++) {
               Item ii = ItemLib.byName(sep[i]);
               if (ii.isMissingno()) {
                  return null;
               }

               types.add(ii);
            }

            return this.create(types);
         }
      }
   }

   private Modifier create(List<Item> types) {
      List<String> heroNames = new ArrayList<>();

      for (int i = 0; i < types.size(); i++) {
         Item ht = types.get(i);
         if (ht.isMissingno()) {
            return null;
         }

         heroNames.add(ht.getName());
      }

      String name = PREF + Tann.commaList(heroNames, "+", "+");
      return new Modifier(name, new GlobalClearPoolItem(), new GlobalExtraItemPool(types));
   }

   public Modifier example() {
      return this.create(Arrays.asList(ItemLib.byName("leather vest"), ItemLib.byName("x2.leather vest"), ItemLib.byName("x3.leather vest")));
   }
}

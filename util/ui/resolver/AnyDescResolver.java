package com.tann.dice.util.ui.resolver;

import com.badlogic.gdx.graphics.Color;
import com.tann.dice.gameplay.content.ent.die.side.EntSide;
import com.tann.dice.gameplay.content.ent.type.EntType;
import com.tann.dice.gameplay.content.ent.type.lib.EntTypeUtils;
import com.tann.dice.gameplay.content.item.Item;
import com.tann.dice.gameplay.content.item.ItemLib;
import com.tann.dice.gameplay.effect.Trait;
import com.tann.dice.gameplay.effect.eff.keyword.Keyword;
import com.tann.dice.gameplay.modifier.Modifier;
import com.tann.dice.gameplay.modifier.ModifierLib;
import com.tann.dice.util.Colours;
import com.tann.dice.util.ui.TextWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class AnyDescResolver extends Resolver<Object> {
   public AnyDescResolver() {
      super(new Comparator<Object>() {
         @Override
         public int compare(Object o1, Object o2) {
            return o2.getClass().getSimpleName().compareTo(o1.getClass().getSimpleName());
         }
      });
   }

   @Override
   protected Color getCol() {
      return Colours.grey;
   }

   @Override
   protected String getTypeName() {
      return "desc";
   }

   @Override
   protected String getOtherOverrideDesc() {
      return "search for any by description";
   }

   @Override
   protected Object byName(String text) {
      return null;
   }

   @Override
   protected List<Object> search(String text) {
      if (text == null) {
         return null;
      } else {
         text = text.toLowerCase();
         List<Object> results = new ArrayList<>();

         for (EntType entType : EntTypeUtils.getAll()) {
            boolean good = false;

            for (EntSide side : entType.sides) {
               if (this.transform(side.getBaseEffect().describe(true)).contains(text)) {
                  good = true;
               }
            }

            for (Trait trait : entType.traits) {
               if (trait.visible && trait.personal != null && this.transform(trait.personal.describeForTriggerPanel()).contains(text)) {
                  good = true;
               }
            }

            if (good) {
               results.add(entType);
            }
         }

         for (Item item : ItemLib.getMasterCopy()) {
            if (this.transform(item.getDescription()).contains(text)) {
               results.add(item);
            }
         }

         for (Modifier m : ModifierLib.getAll()) {
            if (this.transform(m.getFullDescription()).contains(text)) {
               results.add(m);
            }
         }

         for (Keyword k : Keyword.values()) {
            if (this.transform(k.getRules()).contains(text)) {
               results.add(k);
            }
         }

         return results;
      }
   }

   private String transform(String src) {
      return src == null ? "" : TextWriter.stripTags(src.toLowerCase());
   }

   @Override
   public void resolve(Object o) {
   }
}

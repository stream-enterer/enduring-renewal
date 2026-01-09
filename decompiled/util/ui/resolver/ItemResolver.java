package com.tann.dice.util.ui.resolver;

import com.badlogic.gdx.graphics.Color;
import com.tann.dice.gameplay.content.gen.pipe.item.PipeItem;
import com.tann.dice.gameplay.content.item.Item;
import com.tann.dice.gameplay.content.item.ItemLib;
import com.tann.dice.util.Colours;
import java.util.Comparator;
import java.util.List;

public abstract class ItemResolver extends Resolver<Item> {
   public ItemResolver() {
      super(new Comparator<Item>() {
         public int compare(Item o1, Item o2) {
            return o1.getName(false).compareTo(o2.getName(false));
         }
      });
   }

   protected Item byName(String text) {
      Item i = ItemLib.byName(text);
      return !i.isMissingno() ? i : null;
   }

   protected Item byCache(String text) {
      return PipeItem.byCache(text);
   }

   @Override
   protected List<Item> search(String text) {
      return ItemLib.search(text);
   }

   @Override
   protected String getTypeName() {
      return "an item";
   }

   @Override
   protected Color getCol() {
      return Colours.grey;
   }
}

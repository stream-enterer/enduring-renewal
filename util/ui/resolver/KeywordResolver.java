package com.tann.dice.util.ui.resolver;

import com.badlogic.gdx.graphics.Color;
import com.tann.dice.gameplay.effect.eff.keyword.Keyword;
import com.tann.dice.util.Colours;
import java.util.Comparator;
import java.util.List;

public abstract class KeywordResolver extends Resolver<Keyword> {
   public KeywordResolver() {
      super(new Comparator<Keyword>() {
         public int compare(Keyword o1, Keyword o2) {
            return o1.getName().compareTo(o2.getName());
         }
      });
   }

   protected Keyword byName(String text) {
      return Keyword.byName(text);
   }

   @Override
   protected List<Keyword> search(String text) {
      return Keyword.search(text);
   }

   @Override
   protected String getTypeName() {
      return "a keyword";
   }

   @Override
   protected Color getCol() {
      return Colours.green;
   }
}

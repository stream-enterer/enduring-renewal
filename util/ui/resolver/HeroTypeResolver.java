package com.tann.dice.util.ui.resolver;

import com.badlogic.gdx.graphics.Color;
import com.tann.dice.gameplay.content.ent.type.HeroType;
import com.tann.dice.gameplay.content.ent.type.lib.HeroTypeLib;
import com.tann.dice.gameplay.content.gen.pipe.entity.hero.PipeHero;
import com.tann.dice.util.Colours;
import java.util.Comparator;
import java.util.List;

public abstract class HeroTypeResolver extends Resolver<HeroType> {
   public HeroTypeResolver() {
      super(new Comparator<HeroType>() {
         public int compare(HeroType o1, HeroType o2) {
            return o1.getName(false).compareTo(o2.getName(false));
         }
      });
   }

   protected HeroType byName(String text) {
      HeroType ht = HeroTypeLib.byName(text);
      return !ht.isMissingno() ? ht : null;
   }

   protected HeroType byCache(String text) {
      return PipeHero.byCache(text);
   }

   @Override
   protected List<HeroType> search(String text) {
      return HeroTypeLib.search(text);
   }

   @Override
   protected String getTypeName() {
      return "a hero";
   }

   @Override
   protected Color getCol() {
      return Colours.yellow;
   }
}

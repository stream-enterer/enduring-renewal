package com.tann.dice.util.ui.resolver;

import com.badlogic.gdx.graphics.Color;
import com.tann.dice.gameplay.content.ent.type.HeroType;
import com.tann.dice.gameplay.content.ent.type.MonsterType;
import com.tann.dice.gameplay.content.item.Item;
import com.tann.dice.gameplay.effect.eff.keyword.Keyword;
import com.tann.dice.gameplay.modifier.Modifier;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Tann;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public abstract class MetaResolver extends Resolver<Object> {
   final Resolver[] resolvers;

   public MetaResolver(Resolver... resolvers) {
      super(
         new Comparator<Object>() {
            @Override
            public int compare(Object o1, Object o2) {
               return o1.getClass() != o2.getClass()
                  ? o1.getClass().getSimpleName().compareTo(o2.getClass().getSimpleName())
                  : o1.toString().compareTo(o2.toString());
            }
         }
      );
      this.resolvers = resolvers;
   }

   public MetaResolver() {
      this(new ModifierResolver() {
         public void resolve(Modifier modifier) {
         }
      }, new KeywordResolver() {
         public void resolve(Keyword keyword) {
         }
      }, new ItemResolver() {
         public void resolve(Item item) {
         }
      }, new MonsterTypeResolver() {
         public void resolve(MonsterType type) {
         }
      }, new HeroTypeResolver() {
         public void resolve(HeroType heroType) {
         }
      });
   }

   @Override
   protected Color getCol() {
      return Colours.grey;
   }

   @Override
   protected String getTypeName() {
      return "any";
   }

   @Override
   protected Object byName(String text) {
      for (int i = 0; i < this.resolvers.length; i++) {
         Resolver resolver = this.resolvers[i];
         Object o = resolver.byCache(text);
         if (!Tann.isMissingnoObject(o)) {
            return o;
         }
      }

      for (int ix = 0; ix < this.resolvers.length; ix++) {
         Resolver resolver = this.resolvers[ix];
         Object o = resolver.byName(text);
         if (!Tann.isMissingnoObject(o)) {
            return o;
         }
      }

      return null;
   }

   @Override
   protected List<Object> search(String text) {
      List<Object> result = new ArrayList<>();

      for (Resolver resolver : this.resolvers) {
         result.addAll(resolver.search(text));
      }

      return result;
   }
}

package com.tann.dice.util.ui.resolver;

import com.badlogic.gdx.graphics.Color;
import com.tann.dice.gameplay.content.ent.type.MonsterType;
import com.tann.dice.gameplay.content.ent.type.lib.MonsterTypeLib;
import com.tann.dice.gameplay.content.gen.pipe.entity.monster.PipeMonster;
import com.tann.dice.util.Colours;
import java.util.Comparator;
import java.util.List;

public abstract class MonsterTypeResolver extends Resolver<MonsterType> {
   public MonsterTypeResolver() {
      super(new Comparator<MonsterType>() {
         public int compare(MonsterType o1, MonsterType o2) {
            return o1.getName().compareTo(o2.getName());
         }
      });
   }

   protected MonsterType byName(String text) {
      MonsterType ht = MonsterTypeLib.byName(text);
      return !ht.isMissingno() ? ht : null;
   }

   @Override
   protected List<MonsterType> search(String text) {
      return MonsterTypeLib.search(text);
   }

   @Override
   protected String getTypeName() {
      return "a monster";
   }

   @Override
   protected Color getCol() {
      return Colours.purple;
   }

   protected MonsterType byCache(String text) {
      return PipeMonster.byCache(text);
   }
}

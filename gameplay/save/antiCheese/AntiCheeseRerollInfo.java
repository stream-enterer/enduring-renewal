package com.tann.dice.gameplay.save.antiCheese;

import com.tann.dice.gameplay.content.ent.group.PartyLayoutType;
import com.tann.dice.gameplay.content.ent.type.HeroType;
import com.tann.dice.gameplay.modifier.Modifier;
import java.util.ArrayList;
import java.util.List;

public class AntiCheeseRerollInfo {
   private final List<HeroType> oldHeroes;
   private final List<Modifier> oldOptions;
   private final PartyLayoutType layout;

   public List<HeroType> getOldHeroes() {
      return this.oldHeroes;
   }

   public List<Modifier> getOldOptions() {
      return this.oldOptions;
   }

   public PartyLayoutType getLayout() {
      return this.layout;
   }

   public static AntiCheeseRerollInfo makeBlank() {
      return new AntiCheeseRerollInfo(new ArrayList<>(), new ArrayList<>(), null);
   }

   public AntiCheeseRerollInfo(List<HeroType> oldHeroes, List<Modifier> oldOptions, PartyLayoutType layout) {
      this.oldHeroes = oldHeroes;
      this.oldOptions = oldOptions;
      this.layout = layout;
   }
}

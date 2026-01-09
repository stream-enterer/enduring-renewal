package com.tann.dice.gameplay.modifier.generation;

import com.tann.dice.gameplay.trigger.global.Global;
import com.tann.dice.gameplay.trigger.global.chance.GlobalRarity;
import com.tann.dice.gameplay.trigger.global.chance.Rarity;
import java.util.Arrays;
import java.util.List;

public abstract class MMS implements ModMaker {
   final Rarity rarity;

   public MMS(Rarity rarity) {
      this.rarity = rarity;
   }

   public MMS() {
      this(null);
   }

   @Override
   public final List<Global> make(int i) {
      return this.rarity == null ? Arrays.asList(this.ms(i)) : Arrays.asList(this.ms(i), GlobalRarity.fromRarity(this.rarity));
   }

   public abstract Global ms(int var1);
}

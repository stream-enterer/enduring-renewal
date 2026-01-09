package com.tann.dice.gameplay.content.ent.type.lib;

import com.tann.dice.gameplay.content.ent.type.HeroType;
import com.tann.dice.gameplay.content.ent.type.blob.heroblobs.HeroTypeBlob;
import com.tann.dice.gameplay.content.gen.pipe.Pipe;
import com.tann.dice.gameplay.content.gen.pipe.entity.hero.PipeHero;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;

public class HeroTypeLib {
   private static List<HeroType> ALL_HEROES;

   @Nonnull
   public static HeroType byName(String name) {
      return HeroTypeUtils.byName(name);
   }

   @Nonnull
   public static HeroType safeByName(String name) {
      Pipe.setupChecks();
      HeroType ht = HeroTypeUtils.byName(name);
      Pipe.disableChecks();
      return ht;
   }

   public static HeroType[] byNames(String... names) {
      HeroType[] result = new HeroType[names.length];

      for (int i = 0; i < names.length; i++) {
         result[i] = byName(names[i]);
      }

      return result;
   }

   public static List<HeroType> getMasterCopy() {
      return new ArrayList<>(ALL_HEROES);
   }

   public static void init() {
      ALL_HEROES = HeroTypeBlob.makeDesigned();
      PipeHero.init(ALL_HEROES);
   }

   public static List<HeroType> search(String text) {
      text = text.toLowerCase();
      List<HeroType> result = new ArrayList<>();

      for (HeroType ht : getMasterCopy()) {
         if (ht.getName(false).toLowerCase().contains(text)) {
            result.add(ht);
         }
      }

      return result;
   }

   public static HeroType getMissingno() {
      return byName("glitch");
   }
}

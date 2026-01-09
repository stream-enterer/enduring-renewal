package com.tann.dice.gameplay.content.gen.pipe.mod.pool;

import com.tann.dice.gameplay.content.ent.type.HeroType;
import com.tann.dice.gameplay.content.ent.type.lib.HeroTypeLib;
import com.tann.dice.gameplay.content.gen.pipe.regex.PipeRegexNamed;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.PRNPart;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.pos.PRNPref;
import com.tann.dice.gameplay.modifier.Modifier;
import com.tann.dice.gameplay.trigger.global.pool.hero.GlobalClearPoolHero;
import com.tann.dice.gameplay.trigger.global.pool.hero.GlobalExtraLevelupOptions;
import com.tann.dice.util.Tann;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PipeModHeroPool extends PipeRegexNamed<Modifier> {
   static final PRNPart PREF = new PRNPref("heropool");

   public PipeModHeroPool() {
      super(PREF, HERO_MULTI);
   }

   protected Modifier internalMake(String[] groups) {
      String heroStrings = groups[0];
      if (bad(heroStrings)) {
         return null;
      } else {
         String[] sep = heroStrings.split("\\+", -1);
         if (bad(sep)) {
            return null;
         } else {
            List<HeroType> types = new ArrayList<>();

            for (int i = 0; i < sep.length; i++) {
               HeroType ht = HeroTypeLib.byName(sep[i]);
               if (ht.isMissingno()) {
                  return null;
               }

               types.add(ht);
            }

            return this.create(types);
         }
      }
   }

   private Modifier create(List<HeroType> types) {
      List<String> heroNames = new ArrayList<>();

      for (int i = 0; i < types.size(); i++) {
         HeroType ht = types.get(i);
         if (ht.isMissingno()) {
            return null;
         }

         heroNames.add(ht.getName());
      }

      String name = PREF + Tann.commaList(heroNames, "+", "+");
      return new Modifier(name, new GlobalClearPoolHero(), new GlobalExtraLevelupOptions(types));
   }

   public Modifier example() {
      return this.create(Arrays.asList(HeroTypeLib.byName("rogue.col.r.n.Rouge"), HeroTypeLib.byName("gambler")));
   }
}

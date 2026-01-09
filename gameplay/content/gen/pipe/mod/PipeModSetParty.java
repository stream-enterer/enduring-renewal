package com.tann.dice.gameplay.content.gen.pipe.mod;

import com.tann.dice.gameplay.content.ent.type.HeroType;
import com.tann.dice.gameplay.content.ent.type.lib.HeroTypeLib;
import com.tann.dice.gameplay.content.ent.type.lib.HeroTypeUtils;
import com.tann.dice.gameplay.content.gen.pipe.regex.PipeRegexNamed;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.PRNPart;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.pos.PRNPref;
import com.tann.dice.gameplay.modifier.Modifier;
import com.tann.dice.gameplay.trigger.global.Global;
import com.tann.dice.gameplay.trigger.global.GlobalAddHero;
import com.tann.dice.gameplay.trigger.global.changeHero.GlobalChangeHeroAll;
import com.tann.dice.gameplay.trigger.global.changeHero.effects.KillHero;
import com.tann.dice.util.Tann;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PipeModSetParty extends PipeRegexNamed<Modifier> {
   static final PRNPart PREF = new PRNPref("party");

   public PipeModSetParty() {
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
      float tier = 0.0F;
      List<String> heroNames = new ArrayList<>();

      for (int i = 0; i < types.size(); i++) {
         heroNames.add(types.get(i).getName());
      }

      String name = PREF + Tann.commaList(heroNames, "+", "+");
      List<Global> globals = new ArrayList<>();
      globals.add(new GlobalChangeHeroAll(new KillHero()));

      for (String heroName : heroNames) {
         globals.add(new GlobalAddHero(HeroTypeLib.byName(heroName)));
      }

      return new Modifier(tier, name, globals);
   }

   public Modifier example() {
      return this.create(Arrays.asList(HeroTypeUtils.random(), HeroTypeUtils.random()));
   }

   @Override
   public boolean showHigher() {
      return true;
   }
}

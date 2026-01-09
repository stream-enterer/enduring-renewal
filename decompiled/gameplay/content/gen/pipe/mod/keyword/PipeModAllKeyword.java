package com.tann.dice.gameplay.content.gen.pipe.mod.keyword;

import com.tann.dice.gameplay.content.gen.pipe.regex.PipeRegexNamed;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.PRNPart;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.pos.PRNPref;
import com.tann.dice.gameplay.effect.eff.keyword.KUtils;
import com.tann.dice.gameplay.effect.eff.keyword.Keyword;
import com.tann.dice.gameplay.modifier.Modifier;
import com.tann.dice.gameplay.trigger.global.chance.Rarity;
import com.tann.dice.gameplay.trigger.global.linked.all.GlobalAllEntities;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.AffectSides;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.AddKeyword;
import com.tann.dice.util.Tann;

public class PipeModAllKeyword extends PipeRegexNamed<Modifier> {
   private final boolean hero;

   public PipeModAllKeyword(boolean hero) {
      super(getPref(hero), KEYWORD);
      this.hero = hero;
   }

   private static PRNPart getPref(boolean hero) {
      return new PRNPref(hero ? "hero" : "monster");
   }

   protected Modifier internalMake(String[] groups) {
      Keyword k = Keyword.byName(groups[0]);
      return this.make(k);
   }

   private Modifier make(Keyword k) {
      if (k != null && !k.abilityOnly()) {
         float tier = this.hero ? KUtils.getModTierAllHero(k) : KUtils.getModTierAllMonster(k);
         if (tier == 0.0F) {
            tier = -0.069F;
         }

         return new Modifier(tier, getPref(this.hero) + k.getName(), new GlobalAllEntities(this.hero, new AffectSides(new AddKeyword(k))));
      } else {
         return null;
      }
   }

   public Modifier example() {
      return this.make(Tann.random(Keyword.values()));
   }

   @Override
   public boolean canGenerate(boolean wild) {
      return !wild;
   }

   protected Modifier generateInternal(boolean wild) {
      Keyword[] vals = Keyword.values();
      int attempts = 20;

      for (int i = 0; i < 20; i++) {
         Keyword k = Tann.random(vals);
         if (KUtils.getModTierAll(k, this.hero) != 0.0F) {
            Rarity r = KUtils.getRarity(k);
            if (r == null || Tann.chance(r.getValue())) {
               return this.make(k);
            }
         }
      }

      return null;
   }

   @Override
   public float getRarity(boolean wild) {
      return 0.35F;
   }

   @Override
   public boolean showHigher() {
      return true;
   }

   @Override
   public boolean isComplexAPI() {
      return !this.hero;
   }
}

package com.tann.dice.gameplay.content.gen.pipe.mod;

import com.tann.dice.gameplay.content.ent.type.HeroCol;
import com.tann.dice.gameplay.content.ent.type.HeroType;
import com.tann.dice.gameplay.content.ent.type.lib.HeroTypeUtils;
import com.tann.dice.gameplay.content.gen.pipe.entity.hero.generate.PipeHeroGenerated;
import com.tann.dice.gameplay.content.gen.pipe.regex.PipeRegexNamed;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.PRNPart;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.pos.PRNPref;
import com.tann.dice.gameplay.modifier.Modifier;
import com.tann.dice.gameplay.modifier.modBal.TierUtils;
import com.tann.dice.gameplay.trigger.global.GlobalAddHero;
import com.tann.dice.util.Tann;

public class PipeModAddHero extends PipeRegexNamed<Modifier> {
   private static final PRNPart PREF = new PRNPref("add");

   public PipeModAddHero() {
      super(PREF, HERO);
   }

   protected Modifier internalMake(String[] groups) {
      HeroType ht = HeroTypeUtils.byName(groups[0]);
      return ht.isMissingno() ? null : make(ht);
   }

   public static Modifier make(HeroType input) {
      String name = PREF + input.getName(false);
      float tier = TierUtils.extraHeroModTier(input.getTier());
      return new Modifier(tier, name, new GlobalAddHero(input));
   }

   public Modifier example() {
      return make(HeroTypeUtils.random());
   }

   @Override
   public boolean canGenerate(boolean wild) {
      return !wild;
   }

   protected Modifier generateInternal(boolean wild) {
      return wild ? make(PipeHeroGenerated.generate(Tann.random(HeroCol.basics()), Tann.randomInt(5))) : this.example();
   }

   @Override
   public float getRarity(boolean wild) {
      return !wild ? 0.3F : super.getRarity(wild);
   }

   @Override
   public boolean showHigher() {
      return true;
   }
}

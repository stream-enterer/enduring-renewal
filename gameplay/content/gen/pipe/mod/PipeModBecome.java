package com.tann.dice.gameplay.content.gen.pipe.mod;

import com.tann.dice.gameplay.content.ent.type.HeroType;
import com.tann.dice.gameplay.content.ent.type.lib.HeroTypeLib;
import com.tann.dice.gameplay.content.ent.type.lib.HeroTypeUtils;
import com.tann.dice.gameplay.content.gen.pipe.regex.PipeRegexNamed;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.PRNPart;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.pos.PRNPref;
import com.tann.dice.gameplay.modifier.Modifier;
import com.tann.dice.gameplay.trigger.global.changeHero.GlobalChangeHeroAll;
import com.tann.dice.gameplay.trigger.global.changeHero.effects.SetHero;

public class PipeModBecome extends PipeRegexNamed<Modifier> {
   private static PRNPart PREF = new PRNPref("b");

   public PipeModBecome() {
      super(PREF, HERO);
   }

   public Modifier example() {
      return this.make(HeroTypeUtils.random());
   }

   protected Modifier internalMake(String[] groups) {
      return this.make(HeroTypeLib.byName(groups[0]));
   }

   private Modifier make(HeroType ht) {
      return ht.isMissingno() ? null : new Modifier(PREF + ht.getName(), new GlobalChangeHeroAll(new SetHero(ht)));
   }

   @Override
   public boolean isComplexAPI() {
      return true;
   }
}

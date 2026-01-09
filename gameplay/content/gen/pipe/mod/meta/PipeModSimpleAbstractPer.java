package com.tann.dice.gameplay.content.gen.pipe.mod.meta;

import com.tann.dice.gameplay.content.gen.pipe.regex.PipeRegexNamed;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.PRNPart;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.pos.PRNPref;
import com.tann.dice.gameplay.modifier.Modifier;
import com.tann.dice.gameplay.modifier.ModifierLib;

public abstract class PipeModSimpleAbstractPer extends PipeRegexNamed<Modifier> {
   final PRNPart PREF;

   protected PipeModSimpleAbstractPer(String pref) {
      this(new PRNPref(pref));
   }

   public PipeModSimpleAbstractPer(PRNPref PREF) {
      super(PREF, MODIFIER);
      this.PREF = PREF;
   }

   protected Modifier internalMake(String[] groups) {
      String modString = groups[0];
      return bad(modString) ? null : this.make(ModifierLib.byName(modString));
   }

   public Modifier example() {
      return this.make(ModifierLib.random());
   }

   protected String nameFor(Modifier src) {
      return this.PREF + src.getName();
   }

   protected abstract Modifier make(Modifier var1);
}

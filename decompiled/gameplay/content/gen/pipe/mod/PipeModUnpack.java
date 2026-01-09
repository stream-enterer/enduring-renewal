package com.tann.dice.gameplay.content.gen.pipe.mod;

import com.tann.dice.gameplay.content.gen.pipe.regex.PipeRegexNamed;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.PRNPart;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.pos.PRNPref;
import com.tann.dice.gameplay.modifier.Modifier;
import com.tann.dice.gameplay.modifier.ModifierLib;
import com.tann.dice.gameplay.trigger.Trigger;
import com.tann.dice.gameplay.trigger.global.Global;
import com.tann.dice.gameplay.trigger.global.linked.GlobalLinked;
import com.tann.dice.gameplay.trigger.personal.Personal;

public class PipeModUnpack extends PipeRegexNamed<Modifier> {
   private static final PRNPart PREF = new PRNPref("unpack");

   public PipeModUnpack() {
      super(PREF, MODIFIER);
   }

   public Modifier example() {
      return this.make(ModifierLib.random());
   }

   protected Modifier internalMake(String[] groups) {
      return this.make(ModifierLib.byName(groups[0]));
   }

   private Modifier make(Modifier modifier) {
      Global g = modifier.getSingleGlobalOrNull();
      if (g instanceof GlobalLinked) {
         GlobalLinked gl = (GlobalLinked)g;
         Trigger t = gl.linkDebug();
         if (t instanceof Personal) {
            return null;
         }

         if (t instanceof Global) {
            Global inside = (Global)t;
            return new Modifier(PREF + modifier.toString(), inside);
         }
      }

      return null;
   }

   @Override
   public boolean isComplexAPI() {
      return true;
   }
}

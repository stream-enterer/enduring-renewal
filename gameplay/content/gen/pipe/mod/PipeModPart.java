package com.tann.dice.gameplay.content.gen.pipe.mod;

import com.tann.dice.gameplay.content.gen.pipe.regex.PipeRegexNamed;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.PRNPart;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.pos.PRNMid;
import com.tann.dice.gameplay.modifier.Modifier;
import com.tann.dice.gameplay.modifier.ModifierLib;
import com.tann.dice.gameplay.trigger.global.Global;
import com.tann.dice.util.bsRandom.Checker;
import com.tann.dice.util.bsRandom.RandomCheck;
import java.util.ArrayList;
import java.util.List;

public class PipeModPart extends PipeRegexNamed<Modifier> {
   private static final PRNPart sep = new PRNMid("part");

   public PipeModPart() {
      super(MODIFIER, sep, PipeRegexNamed.DIGIT);
   }

   protected Modifier internalMake(String[] groups) {
      String mod1 = groups[0];
      String partNum = groups[1];
      return this.make(ModifierLib.byName(mod1), Integer.parseInt(partNum));
   }

   private Modifier make(Modifier a, int index) {
      if (a != null && !a.isMissingno()) {
         List<Global> globals = new ArrayList<>(a.getGlobals());

         for (int i = globals.size() - 1; i >= 0; i--) {
            if (globals.get(i).metaOnly()) {
               globals.remove(i);
            }
         }

         if (globals.size() >= 2 && globals.size() > index) {
            Global only = globals.get(index);
            String name = a.getName() + sep + index;
            return new Modifier(name, only);
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   public Modifier example() {
      Modifier m = RandomCheck.checkedRandom(ModifierLib.makeSupplier(), new Checker<Modifier>() {
         public boolean check(Modifier m) {
            return m.getSingleGlobalOrNull() == null;
         }
      }, ModifierLib.byName("crust"));
      return this.make(m, (int)(Math.random() * 2.0));
   }

   @Override
   public boolean isComplexAPI() {
      return true;
   }
}

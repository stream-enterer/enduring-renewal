package com.tann.dice.gameplay.content.gen.pipe.mod.meta;

import com.tann.dice.gameplay.content.gen.pipe.regex.PipeRegexNamed;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.PRNS;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.pos.PRNMid;
import com.tann.dice.gameplay.modifier.Modifier;
import com.tann.dice.gameplay.modifier.ModifierLib;
import com.tann.dice.gameplay.trigger.Trigger;
import com.tann.dice.gameplay.trigger.global.Global;
import com.tann.dice.gameplay.trigger.global.linked.GlobalLinked;
import com.tann.dice.gameplay.trigger.global.linked.all.GlobalAllEntities;
import com.tann.dice.gameplay.trigger.personal.Personal;

public class PipeModSplice extends PipeRegexNamed<Modifier> {
   private static PRNS SEP = new PRNMid("splice");

   public PipeModSplice() {
      super(MODIFIER, SEP, MODIFIER);
   }

   public Modifier example() {
      return make(ModifierLib.random(), ModifierLib.random());
   }

   protected Modifier internalMake(String[] groups) {
      String s1 = groups[0];
      String s2 = groups[1];
      Modifier mod1;
      Modifier mod2;
      if (s1.length() < s2.length()) {
         if ((mod1 = ModifierLib.byName(s1)).isMissingno() || (mod2 = ModifierLib.byName(s2)).isMissingno()) {
            return null;
         }
      } else if ((mod2 = ModifierLib.byName(s2)).isMissingno() || (mod1 = ModifierLib.byName(s1)).isMissingno()) {
         return null;
      }

      return make(mod1, mod2);
   }

   private static Modifier make(Modifier outer, Modifier inner) {
      return inner.isMissingno() ? null : make(outer, inner.getSingleGlobalOrNull(), inner.getName());
   }

   public static Modifier make(Modifier outer, Trigger inner, String innerName) {
      try {
         Global spliced = splice(outer, inner);
         if (spliced == null) {
            return null;
         } else {
            String realName = outer.getName() + SEP + innerName;
            return new Modifier(0.0F, realName, spliced);
         }
      } catch (Exception var5) {
         return null;
      }
   }

   private static Global splice(Modifier outer, Trigger inner) {
      if (!outer.isMissingno() && inner != null) {
         Global og = outer.getSingleGlobalOrNull();
         if (og != null && og instanceof GlobalLinked) {
            GlobalLinked ogl = (GlobalLinked)og;
            if (inner instanceof Global) {
               Global ig = (Global)inner;
               Global spliced = ogl.splice(ig);
               if (spliced != null) {
                  return spliced;
               }

               if (inner instanceof GlobalLinked) {
                  Trigger t = ((GlobalLinked)inner).linkDebug();
                  if (t instanceof Personal) {
                     spliced = ogl.splice((Personal)t);
                     if (spliced != null) {
                        return spliced;
                     }
                  }
               }
            } else if (inner instanceof Personal) {
               Personal p = (Personal)inner;
               Global splicedx = ogl.splice(p);
               if (splicedx != null) {
                  return splicedx;
               }

               splicedx = ogl.splice(new GlobalAllEntities(true, p));
               if (splicedx != null) {
                  return splicedx;
               }
            }

            return null;
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   @Override
   public boolean isComplexAPI() {
      return true;
   }
}

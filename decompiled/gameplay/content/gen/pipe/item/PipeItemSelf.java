package com.tann.dice.gameplay.content.gen.pipe.item;

import com.tann.dice.gameplay.content.gen.pipe.regex.PipeRegexNamed;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.PRNPart;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.pos.PRNPref;
import com.tann.dice.gameplay.content.item.ItBill;
import com.tann.dice.gameplay.content.item.Item;
import com.tann.dice.gameplay.effect.eff.Eff;
import com.tann.dice.gameplay.effect.eff.EffBill;
import com.tann.dice.gameplay.effect.eff.TargetingType;
import com.tann.dice.gameplay.modifier.Modifier;
import com.tann.dice.gameplay.modifier.ModifierLib;
import com.tann.dice.gameplay.trigger.global.Global;
import com.tann.dice.gameplay.trigger.global.eff.GlobalEndTurnEff;
import com.tann.dice.gameplay.trigger.global.eff.GlobalStartTurnEff;
import com.tann.dice.gameplay.trigger.global.linked.GlobalPositional;
import com.tann.dice.gameplay.trigger.global.linked.all.GlobalAllEntities;
import com.tann.dice.gameplay.trigger.global.scaffolding.turnRequirement.GlobalTurnRequirement;
import com.tann.dice.gameplay.trigger.personal.Personal;
import com.tann.dice.gameplay.trigger.personal.eff.EndOfTurnEff;
import com.tann.dice.gameplay.trigger.personal.eff.StartOfTurnSelf;
import com.tann.dice.gameplay.trigger.personal.linked.PersonalTurnRequirement;

public class PipeItemSelf extends PipeRegexNamed<Item> {
   static final PRNPart PREF = new PRNPref("self");

   public PipeItemSelf() {
      super(PREF, MODIFIER);
   }

   protected Item internalMake(String[] groups) {
      String modName = groups[0];
      return this.make(ModifierLib.byName(modName));
   }

   private Item make(Modifier src) {
      if (src.isMissingno()) {
         return null;
      } else {
         Global g = src.getSingleGlobalOrNull();
         if (g == null) {
            return null;
         } else {
            Personal p = transformToPersonal(g);
            return p == null ? null : new ItBill(-69, PREF + src.getName(), "special/self").prs(p).bItem();
         }
      }
   }

   public static Personal transformToPersonal(Global g) {
      if (g instanceof GlobalAllEntities) {
         return ((GlobalAllEntities)g).personal;
      } else if (g instanceof GlobalPositional) {
         return ((GlobalPositional)g).personal;
      } else {
         if (g instanceof GlobalTurnRequirement) {
            GlobalTurnRequirement gtr = (GlobalTurnRequirement)g;
            Global gl = gtr.debugLinked();
            if (gl != null) {
               Personal p = transformToPersonal(gl);
               if (p != null) {
                  return new PersonalTurnRequirement(gtr.getRequirement(), p);
               }
            }
         } else if (g instanceof GlobalStartTurnEff) {
            GlobalStartTurnEff gste = (GlobalStartTurnEff)g;
            Eff e = gste.getSingleEffOrNull();
            if (e == null) {
               return null;
            }

            if (e.getTargetingType() == TargetingType.Group) {
               return new StartOfTurnSelf(new EffBill(e).targetType(TargetingType.Self).bEff());
            }
         } else if (g instanceof GlobalEndTurnEff) {
            GlobalEndTurnEff gstex = (GlobalEndTurnEff)g;
            Eff ex = gstex.getSingleEffOrNull();
            if (ex == null) {
               return null;
            }

            if (ex.getTargetingType() == TargetingType.Group) {
               return new EndOfTurnEff(new EffBill(ex).targetType(TargetingType.Self).bEff());
            }
         }

         return null;
      }
   }

   public Item example() {
      return this.make(ModifierLib.random());
   }

   @Override
   public boolean showHigher() {
      return true;
   }

   @Override
   public boolean isComplexAPI() {
      return true;
   }
}

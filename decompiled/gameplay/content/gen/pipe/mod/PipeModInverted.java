package com.tann.dice.gameplay.content.gen.pipe.mod;

import com.tann.dice.gameplay.content.gen.pipe.regex.PipeRegexNamed;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.PRNPart;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.pos.PRNPref;
import com.tann.dice.gameplay.modifier.Modifier;
import com.tann.dice.gameplay.modifier.ModifierLib;
import com.tann.dice.gameplay.trigger.global.Global;
import com.tann.dice.gameplay.trigger.global.linked.all.GlobalAllEntities;

public class PipeModInverted extends PipeRegexNamed<Modifier> {
   static final PRNPart PREF = new PRNPref("inv");

   public PipeModInverted() {
      super(PREF, MODIFIER);
   }

   public Modifier example() {
      return this.make(ModifierLib.random());
   }

   protected Modifier internalMake(String[] groups) {
      return this.make(ModifierLib.byName(groups[0]));
   }

   private Modifier make(Modifier src) {
      if (src.isMissingno()) {
         return null;
      } else {
         Global g = src.getSingleGlobalOrNull();
         if (g instanceof GlobalAllEntities) {
            GlobalAllEntities gae = (GlobalAllEntities)g;
            return gae.getPlayer() == null ? null : new Modifier(-src.getTier(), PREF + src.getName(), new GlobalAllEntities(!gae.getPlayer(), gae.personal));
         } else {
            return null;
         }
      }
   }

   @Override
   public boolean isComplexAPI() {
      return true;
   }
}

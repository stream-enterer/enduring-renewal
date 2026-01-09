package com.tann.dice.gameplay.content.gen.pipe.mod;

import com.tann.dice.gameplay.battleTest.Zone;
import com.tann.dice.gameplay.content.gen.pipe.regex.PipeRegexNamed;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.PRNPart;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.pos.PRNPref;
import com.tann.dice.gameplay.modifier.Modifier;
import com.tann.dice.gameplay.trigger.global.level.GlobalZoneOverride;
import com.tann.dice.util.Tann;

public class PipeModZone extends PipeRegexNamed<Modifier> {
   static final PRNPart PREF = new PRNPref("zone");

   public PipeModZone() {
      super(PREF, PipeRegexNamed.ZONE);
   }

   public Modifier example() {
      return this.make(Tann.random(Zone.values()));
   }

   protected Modifier internalMake(String[] groups) {
      Zone z = Tann.caseInsensitiveValueOf(groups[0], Zone.values());
      return z == null ? null : this.make(z);
   }

   private Modifier make(Zone z) {
      String name = PREF.toString() + z;
      return new Modifier(name, new GlobalZoneOverride(z));
   }

   @Override
   public boolean isComplexAPI() {
      return true;
   }
}

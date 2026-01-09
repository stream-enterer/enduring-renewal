package com.tann.dice.gameplay.content.gen.pipe.entity.monster;

import com.tann.dice.gameplay.content.ent.EntSize;
import com.tann.dice.gameplay.content.ent.die.side.blob.EntSidesBlobSmall;
import com.tann.dice.gameplay.content.ent.type.MonsterType;
import com.tann.dice.gameplay.content.ent.type.bill.MTBill;
import com.tann.dice.gameplay.content.gen.pipe.regex.PipeRegexNamed;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.PRNPart;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.pos.PRNPref;
import com.tann.dice.gameplay.effect.Trait;
import com.tann.dice.gameplay.modifier.Modifier;
import com.tann.dice.gameplay.modifier.ModifierLib;
import com.tann.dice.gameplay.trigger.personal.specificMonster.Vase;
import com.tann.dice.statics.sound.Sounds;

public class PipeMonsterVase extends PipeRegexNamed<MonsterType> {
   private static final PRNPart PREF = new PRNPref("Vase");

   public PipeMonsterVase() {
      super(PREF, MODIFIER);
   }

   protected MonsterType internalMake(String[] groups) {
      String tag = groups[0];
      return this.make(ModifierLib.byName(tag));
   }

   private MonsterType make(Modifier toGain) {
      if (toGain.isMissingno()) {
         return null;
      } else {
         String name = PREF + toGain.getName();
         return new MTBill(EntSize.small)
            .name(name)
            .hp(3)
            .death(Sounds.deathExplosion)
            .max(1)
            .texture("special/vase")
            .sides(
               EntSidesBlobSmall.blank,
               EntSidesBlobSmall.blank,
               EntSidesBlobSmall.blank,
               EntSidesBlobSmall.blank,
               EntSidesBlobSmall.blank,
               EntSidesBlobSmall.blank
            )
            .trait(new Trait(new Vase(toGain), true))
            .bEntType();
      }
   }

   public MonsterType example() {
      return this.make(ModifierLib.random());
   }

   @Override
   public boolean isComplexAPI() {
      return true;
   }
}

package com.tann.dice.gameplay.content.gen.pipe.entity.monster;

import com.tann.dice.gameplay.content.ent.EntSize;
import com.tann.dice.gameplay.content.ent.die.side.blob.EntSidesBlobSmall;
import com.tann.dice.gameplay.content.ent.type.MonsterType;
import com.tann.dice.gameplay.content.ent.type.bill.MTBill;
import com.tann.dice.gameplay.content.gen.pipe.entity.hero.PipeHeroAbility;
import com.tann.dice.gameplay.content.gen.pipe.regex.PipeRegexNamed;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.PRNPart;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.pos.PRNPref;
import com.tann.dice.gameplay.effect.Trait;
import com.tann.dice.gameplay.effect.targetable.ability.Ability;
import com.tann.dice.gameplay.effect.targetable.ability.AbilityUtils;
import com.tann.dice.gameplay.trigger.personal.specificMonster.Orb;
import com.tann.dice.statics.sound.Sounds;

public class PipeMonsterOrb extends PipeRegexNamed<MonsterType> {
   private static final PRNPart PREF = new PRNPref("orb");

   public PipeMonsterOrb() {
      super(PREF, ABILITY);
   }

   protected MonsterType internalMake(String[] groups) {
      String tag = groups[0];
      return this.make(AbilityUtils.byName(tag), tag);
   }

   private MonsterType make(Ability castOnDeath, String abTag) {
      if (castOnDeath != null && PipeHeroAbility.okForAbilityUntargetedSecondMaybe(castOnDeath.getBaseEffect())) {
         String name = PREF + abTag;
         return new MTBill(EntSize.small)
            .name(name)
            .hp(3)
            .death(Sounds.deathExplosion)
            .max(1)
            .texture("special/orb")
            .sides(
               EntSidesBlobSmall.summonHexia.val(1),
               EntSidesBlobSmall.summonBones.val(1),
               EntSidesBlobSmall.summonBones.val(1),
               EntSidesBlobSmall.summonBones.val(1)
            )
            .trait(new Trait(new Orb(castOnDeath), true))
            .bEntType();
      } else {
         return null;
      }
   }

   public MonsterType example() {
      Ability a = AbilityUtils.byName("slice");
      return a == null ? null : this.make(AbilityUtils.random(), a.getTitle());
   }

   @Override
   public boolean isComplexAPI() {
      return true;
   }
}

package com.tann.dice.gameplay.content.gen.pipe.mod;

import com.tann.dice.gameplay.content.ent.type.MonsterType;
import com.tann.dice.gameplay.content.ent.type.lib.MonsterTypeLib;
import com.tann.dice.gameplay.content.gen.pipe.item.PipeItemTrait;
import com.tann.dice.gameplay.content.gen.pipe.regex.PipeRegexNamed;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.PRNPart;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.pos.PRNSuff;
import com.tann.dice.gameplay.effect.Trait;
import com.tann.dice.gameplay.modifier.Modifier;
import com.tann.dice.gameplay.trigger.global.linked.all.GlobalMonsters;
import com.tann.dice.util.Tann;
import java.util.List;

public class PipeModSpirit extends PipeRegexNamed<Modifier> {
   static final PRNPart SUFF = new PRNSuff("Spirit");
   final String[] VALID_WEIRD_MONS = new String[]{"ghost", "slimer", "bones", "basalt", "boar", "quartz", "cyclops", "ogre"};

   public PipeModSpirit() {
      super(MONSTER, SUFF);
   }

   public Modifier example() {
      return make(MonsterTypeLib.randomWithRarity());
   }

   protected Modifier internalMake(String[] groups) {
      String mon = groups[0];
      return bad(mon) ? null : make(MonsterTypeLib.byName(mon));
   }

   public static Modifier make(MonsterType src) {
      if (!src.isMissingno() && src.traits.size() != 0) {
         List<Trait> traitps = PipeItemTrait.getValidTraits(src);
         if (traitps.size() != 1) {
            return null;
         } else {
            Trait t = traitps.get(0);
            float dmgR = src.getAvgEffectTier(true) / src.getAvgEffectTier(false);
            float hpR = src.getEffectiveHp() / src.hp;
            float tier = ((hpR - 1.0F) * 1.4F + (dmgR - 1.0F)) * -25.0F;
            if (tier < 0.0F) {
               tier /= 2.0F;
            }

            return new Modifier(tier, src.getName() + SUFF, new GlobalMonsters(t.personal));
         }
      } else {
         return null;
      }
   }

   @Override
   public boolean canGenerate(boolean wild) {
      return !wild;
   }

   @Override
   public float getRarity(boolean wild) {
      return 0.07F;
   }

   protected Modifier generateInternal(boolean wild) {
      return make(MonsterTypeLib.byName(Tann.random(this.VALID_WEIRD_MONS)));
   }

   @Override
   public boolean isComplexAPI() {
      return true;
   }
}

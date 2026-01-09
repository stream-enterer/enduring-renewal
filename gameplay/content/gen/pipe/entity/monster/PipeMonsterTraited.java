package com.tann.dice.gameplay.content.gen.pipe.entity.monster;

import com.tann.dice.gameplay.content.ent.type.MonsterType;
import com.tann.dice.gameplay.content.ent.type.bill.MTBill;
import com.tann.dice.gameplay.content.ent.type.lib.EntTypeUtils;
import com.tann.dice.gameplay.content.ent.type.lib.MonsterTypeLib;
import com.tann.dice.gameplay.content.gen.pipe.item.PipeItemTrait;
import com.tann.dice.gameplay.content.gen.pipe.regex.PipeRegexNamed;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.PRNPart;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.pos.PRNMid;
import com.tann.dice.gameplay.effect.Trait;
import com.tann.dice.gameplay.trigger.global.chance.MonsterChance;
import com.tann.dice.gameplay.trigger.personal.Personal;
import com.tann.dice.gameplay.trigger.personal.util.CalcStats;
import java.util.List;

public class PipeMonsterTraited extends PipeRegexNamed<MonsterType> {
   private static final PRNPart MID = new PRNMid("t");

   public PipeMonsterTraited() {
      super(MONSTER, MID, MONSTER);
   }

   public MonsterType example() {
      return this.make(MonsterTypeLib.randomWithRarity(), MonsterTypeLib.randomWithRarity());
   }

   protected MonsterType internalMake(String[] groups) {
      String mName = groups[0];
      String tMName = groups[1];
      return bad(mName, tMName) ? null : this.make(MonsterTypeLib.byName(mName), MonsterTypeLib.byName(tMName));
   }

   private MonsterType make(MonsterType src, MonsterType traitSrc) {
      if (!src.isMissingno() && !traitSrc.isMissingno()) {
         List<Trait> traits = PipeItemTrait.getValidTraits(traitSrc);
         if (traits.size() == 0) {
            return null;
         } else {
            MTBill mtb = EntTypeUtils.copy(src);

            for (int i = 0; i < traits.size(); i++) {
               Trait t = traits.get(i);
               Personal p = t.personal;
               if (!(p instanceof MonsterChance)) {
                  if (t.calcStats != null) {
                     float hp = t.calcStats.getHp();
                     float pw = t.calcStats.getDamage();
                     mtb.trait(p, new CalcStats(hp, pw), t.visible);
                  } else {
                     mtb.trait(p, null, t.visible);
                  }
               }
            }

            MonsterType result = mtb.bEntType();
            String realName = result.getName(false) + MID + traitSrc.getName(false);
            mtb.name(realName);
            return mtb.bEntType();
         }
      } else {
         return null;
      }
   }

   @Override
   public float getRarity(boolean wild) {
      return 0.3F;
   }

   @Override
   public boolean canGenerate(boolean wild) {
      return true;
   }

   protected MonsterType generateInternal(boolean wild) {
      MonsterType a = MonsterTypeLib.randomWithRarity();
      MonsterType b = MonsterTypeLib.randomWithRarity();
      if (a == b) {
         return null;
      } else {
         MonsterType combined = this.make(MonsterTypeLib.randomWithRarity(), MonsterTypeLib.randomWithRarity());
         if (combined == null) {
            return null;
         } else {
            return combined.getAvgEffectTier(true) == a.getAvgEffectTier(true) && combined.getEffectiveHp() == a.getEffectiveHp() ? null : combined;
         }
      }
   }

   @Override
   public boolean isComplexAPI() {
      return true;
   }
}

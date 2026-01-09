package com.tann.dice.gameplay.content.gen.pipe.entity.monster;

import com.tann.dice.gameplay.content.ent.type.MonsterType;
import com.tann.dice.gameplay.content.ent.type.bill.MTBill;
import com.tann.dice.gameplay.content.ent.type.lib.EntTypeUtils;
import com.tann.dice.gameplay.content.ent.type.lib.MonsterTypeLib;
import com.tann.dice.gameplay.content.gen.pipe.regex.PipeRegexNamed;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.PRNPart;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.pos.PRNMid;
import com.tann.dice.gameplay.trigger.personal.stats.CopyOtherPowerEstimate;

public class PipeMonsterBalanced extends PipeRegexNamed<MonsterType> {
   private static final PRNPart MID = new PRNMid("bal");

   public PipeMonsterBalanced() {
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

   private MonsterType make(MonsterType src, MonsterType balSrc) {
      if (!src.isMissingno() && !balSrc.isMissingno()) {
         MTBill mtb = EntTypeUtils.copy(src);
         float hpDelta = src.getEffectiveHp() - balSrc.getEffectiveHp();
         float pwDelta = src.getAvgEffectTier() - balSrc.getAvgEffectTier();
         if (hpDelta == 0.0F && pwDelta == 0.0F) {
            return null;
         } else {
            mtb.trait(new CopyOtherPowerEstimate(balSrc));
            MonsterType result = mtb.bEntType();
            String realName = result.getName(false) + MID + balSrc.getName(false);
            mtb.name(realName);
            return mtb.bEntType();
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

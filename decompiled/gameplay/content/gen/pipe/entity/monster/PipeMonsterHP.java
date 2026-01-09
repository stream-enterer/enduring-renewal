package com.tann.dice.gameplay.content.gen.pipe.entity.monster;

import com.tann.dice.gameplay.content.ent.type.MonsterType;
import com.tann.dice.gameplay.content.ent.type.bill.MTBill;
import com.tann.dice.gameplay.content.ent.type.lib.EntTypeUtils;
import com.tann.dice.gameplay.content.ent.type.lib.MonsterTypeLib;
import com.tann.dice.gameplay.content.gen.pipe.regex.PipeRegexNamed;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.PRNPart;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.pos.PRNMid;
import com.tann.dice.util.Tann;

public class PipeMonsterHP extends PipeRegexNamed<MonsterType> {
   static final PRNPart sep = new PRNMid("hp");

   public PipeMonsterHP() {
      super(MONSTER, sep, UP_TO_THREE_DIGITS);
   }

   protected MonsterType internalMake(String[] groups) {
      String hts = groups[0];
      String hp = groups[1];
      return !Tann.isInt(hp) ? null : make(MonsterTypeLib.byName(hts), Integer.parseInt(hp));
   }

   private static MonsterType make(MonsterType src, int hp) {
      if (src.isMissingno()) {
         return null;
      } else if (hp <= 0 || hp > 999) {
         return null;
      } else if (src.hp == hp) {
         return src;
      } else {
         MTBill cpy = EntTypeUtils.copy(src);
         cpy.hp(hp);
         cpy.name("" + src + sep + hp);
         return cpy.bEntType();
      }
   }

   public MonsterType example() {
      return make(MonsterTypeLib.randomWithRarity(), Tann.randomInt(1, 22));
   }
}

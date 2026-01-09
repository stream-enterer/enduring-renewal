package com.tann.dice.gameplay.content.gen.pipe.entity.hero;

import com.tann.dice.gameplay.content.ent.type.HeroType;
import com.tann.dice.gameplay.content.ent.type.bill.HTBill;
import com.tann.dice.gameplay.content.ent.type.lib.HeroTypeLib;
import com.tann.dice.gameplay.content.ent.type.lib.HeroTypeUtils;
import com.tann.dice.gameplay.content.gen.pipe.regex.PipeRegexNamed;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.PRNPart;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.pos.PRNMid;
import com.tann.dice.util.Tann;

public class PipeHeroHP extends PipeRegexNamed<HeroType> {
   static final PRNPart sep = new PRNMid("hp");

   public PipeHeroHP() {
      super(HERO, sep, UP_TO_THREE_DIGITS);
   }

   protected HeroType internalMake(String[] groups) {
      String hts = groups[0];
      String hp = groups[1];
      return !Tann.isInt(hp) ? null : make(HeroTypeLib.byName(hts), Integer.parseInt(hp));
   }

   private static HeroType make(HeroType src, int hp) {
      if (src.isMissingno()) {
         return null;
      } else if (hp <= 0 || hp > 999) {
         return null;
      } else if (src.hp == hp) {
         return src;
      } else {
         HTBill cpy = HeroTypeUtils.copy(src);
         cpy.hp(hp);
         cpy.name("" + src + sep + hp);
         return cpy.bEntType();
      }
   }

   public HeroType example() {
      return make(HeroTypeUtils.random(), Tann.randomInt(1, 22));
   }
}

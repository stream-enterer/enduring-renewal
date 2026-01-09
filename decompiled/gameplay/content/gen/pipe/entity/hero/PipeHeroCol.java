package com.tann.dice.gameplay.content.gen.pipe.entity.hero;

import com.tann.dice.gameplay.content.ent.type.HeroCol;
import com.tann.dice.gameplay.content.ent.type.HeroType;
import com.tann.dice.gameplay.content.ent.type.bill.HTBill;
import com.tann.dice.gameplay.content.ent.type.lib.HeroTypeLib;
import com.tann.dice.gameplay.content.ent.type.lib.HeroTypeUtils;
import com.tann.dice.gameplay.content.gen.pipe.regex.PipeRegexNamed;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.PRNPart;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.pos.PRNMid;
import com.tann.dice.util.Tann;

public class PipeHeroCol extends PipeRegexNamed<HeroType> {
   public static final PRNPart SEP = new PRNMid("col");

   public PipeHeroCol() {
      super(HERO, SEP, HEROCOL);
   }

   protected HeroType internalMake(String[] groups) {
      String heroName = groups[0];
      String heroCol = groups[1];
      return this.make(heroName, heroCol);
   }

   private HeroType make(String heroName, String heroCol) {
      if (bad(heroName, heroCol)) {
         return null;
      } else {
         HeroType ht = HeroTypeLib.byName(heroName);
         if (ht.isMissingno()) {
            return null;
         } else {
            HeroCol col = HeroCol.byName(heroCol);
            return col == null ? null : this.make(ht, col);
         }
      }
   }

   private HeroType make(HeroType src, HeroCol col) {
      if (src.heroCol == col) {
         return null;
      } else {
         HTBill bill = HeroTypeUtils.copy(src);
         bill.col(col);
         String realName = src.getName(false) + SEP + col.shortName();
         bill.name(realName);
         return bill.bEntType();
      }
   }

   public HeroType example() {
      return this.make(HeroTypeUtils.random(), Tann.random(HeroCol.values()));
   }
}

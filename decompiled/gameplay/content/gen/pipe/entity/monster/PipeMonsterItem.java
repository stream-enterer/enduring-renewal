package com.tann.dice.gameplay.content.gen.pipe.entity.monster;

import com.tann.dice.gameplay.content.ent.type.MonsterType;
import com.tann.dice.gameplay.content.ent.type.lib.HeroTypeUtils;
import com.tann.dice.gameplay.content.ent.type.lib.MonsterTypeLib;
import com.tann.dice.gameplay.content.gen.pipe.regex.PipeRegexNamed;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.PRNPart;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.pos.PRNMid;
import com.tann.dice.gameplay.content.item.Item;
import com.tann.dice.gameplay.content.item.ItemLib;
import com.tann.dice.gameplay.trigger.personal.item.AsIfHasItem;

public class PipeMonsterItem extends PipeRegexNamed<MonsterType> {
   public static final PRNPart SEP = new PRNMid("i");
   final boolean lazy;

   public PipeMonsterItem() {
      this(false);
   }

   public PipeMonsterItem(boolean lazy) {
      super(getMonsterPart(lazy), SEP, ITEM);
      this.lazy = lazy;
   }

   private static PRNPart getMonsterPart(boolean lazy) {
      return lazy ? PipeRegexNamed.MONSTER_LAZY : PipeRegexNamed.MONSTER;
   }

   protected MonsterType internalMake(String[] groups) {
      String full = groups[0] + SEP + groups[1];
      String target = ".i.";
      int li = full.length();

      String monsterName;
      String itemName;
      MonsterType h;
      Item i;
      do {
         li = full.lastIndexOf(".i.", li - 1);
         if (li == -1) {
            return null;
         }

         monsterName = full.substring(0, li);
         itemName = full.substring(li + ".i.".length());
      } while (
         monsterName.length() < itemName.length()
            ? (h = MonsterTypeLib.byName(monsterName)).isMissingno() || (i = ItemLib.byName(itemName)).isMissingno()
            : (i = ItemLib.byName(itemName)).isMissingno() || (h = MonsterTypeLib.byName(monsterName)).isMissingno()
      );

      return this.make(h, i);
   }

   private MonsterType make(MonsterType ht, Item i) {
      if (!ht.isMissingno() && !i.isMissingno()) {
         AsIfHasItem aiha = new AsIfHasItem(i);
         String realMonsterName = ht.getName() + SEP + i.getName();
         return HeroTypeUtils.withPassive(ht, realMonsterName, aiha, null);
      } else {
         return null;
      }
   }

   @Override
   public boolean isHiddenAPI() {
      return this.lazy;
   }

   public MonsterType example() {
      return this.make(MonsterTypeLib.random(), ItemLib.random());
   }

   @Override
   public boolean showHigher() {
      return true;
   }
}

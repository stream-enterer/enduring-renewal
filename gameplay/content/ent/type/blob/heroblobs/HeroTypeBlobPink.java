package com.tann.dice.gameplay.content.ent.type.blob.heroblobs;

import com.tann.dice.gameplay.content.ent.die.side.EntSide;
import com.tann.dice.gameplay.content.ent.type.HeroType;
import com.tann.dice.gameplay.content.ent.type.lib.HeroTypeLib;
import com.tann.dice.gameplay.content.ent.type.lib.HeroTypeUtils;
import com.tann.dice.gameplay.content.gen.pipe.entity.hero.PipeHero;
import com.tann.dice.gameplay.content.item.Item;
import com.tann.dice.gameplay.content.item.ItemLib;
import com.tann.dice.gameplay.effect.eff.Eff;
import com.tann.dice.gameplay.effect.eff.keyword.Keyword;
import com.tann.dice.util.Tann;
import com.tann.dice.util.TannLog;
import java.util.Arrays;
import java.util.List;

public class HeroTypeBlobPink {
   public static HeroType levelupExample(HeroType from) {
      int newTier = from.level + 1;
      int newHp = Math.round(from.hp * HeroTypeUtils.getHpFor(newTier) / HeroTypeUtils.getHpFor(from.level));
      long bannedBits = 0L;
      int attempts = 20;
      int iMin = 1 + from.level * 2;
      int iMax = 4 + from.level * 2;

      for (int i = 0; i < attempts; i++) {
         List<Item> list = ItemLib.randomWithExactQuality(1, Tann.randomInt(iMin, iMax), bannedBits);
         if (!list.isEmpty()) {
            Item nextItem = list.get(0);
            if (!nextItem.getName().contains("otion")) {
               String name = from.getName(true, false).replace(".", "") + nextItem.getName().charAt(0);
               HeroType built = HeroTypeLib.byName(from.getName() + ".tier." + newTier + ".hp." + newHp + ".i." + nextItem.getName() + ".n." + name);
               if (!built.isMissingno() && !hasBadKeyword(built) && alwaysDifferent(nextItem, from, null)) {
                  return built;
               }
            }
         }
      }

      TannLog.error("error with levelling up hero " + from);
      return PipeHero.getMissingno();
   }

   private static boolean hasBadKeyword(HeroType built) {
      for (EntSide side : built.sides) {
         if (hasBadKeyword(side.getBaseEffect())) {
            return true;
         }
      }

      return false;
   }

   private static boolean hasBadKeyword(Eff e) {
      boolean friendly = e.isFriendly();

      for (Keyword keyword : e.getKeywords()) {
         switch (keyword.getAllowType()) {
            case KIND_TARG_PIPS:
               if (!friendly) {
                  return false;
               }
               break;
            case UNKIND_TARG_PIPS:
            case UNKIND_TARG:
            case ENEMY_TARG:
               if (friendly) {
                  return false;
               }
         }
      }

      return false;
   }

   private static boolean alwaysDifferent(Item i, HeroType src, int[] finalHash) {
      int[] myHash = null;
      HeroType test = HeroTypeLib.byName(src.getName() + ".i." + i.getName());
      myHash = test.makeEnt().getSideHashes();
      if (finalHash == null) {
         finalHash = myHash;
         if (Arrays.equals(myHash, src.makeEnt().getSideHashes())) {
            return false;
         }
      } else if (Arrays.equals(myHash, finalHash)) {
         return false;
      }

      String find = ".i.";
      String srcName = src.getName();
      return !srcName.contains(find) ? true : alwaysDifferent(i, HeroTypeLib.byName(srcName.substring(0, srcName.lastIndexOf(find))), finalHash);
   }
}

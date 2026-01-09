package com.tann.dice.gameplay.content.ent.type.lib;

import com.tann.dice.gameplay.content.ent.die.side.EntSide;
import com.tann.dice.gameplay.content.ent.type.EntType;
import com.tann.dice.gameplay.content.ent.type.HeroType;
import com.tann.dice.gameplay.content.ent.type.MonsterType;
import com.tann.dice.gameplay.content.ent.type.bill.ETBill;
import com.tann.dice.gameplay.content.ent.type.bill.HTBill;
import com.tann.dice.gameplay.content.ent.type.bill.MTBill;
import com.tann.dice.gameplay.effect.Trait;
import com.tann.dice.gameplay.effect.eff.Eff;
import java.util.ArrayList;
import java.util.List;

public class EntTypeUtils {
   public static List<EntType> getAll() {
      List<EntType> result = new ArrayList<>();
      result.addAll(MonsterTypeLib.getAllValidMonsters());
      result.addAll(HeroTypeLib.getMasterCopy());
      return result;
   }

   public static EntType byName(String en) {
      HeroType ht = HeroTypeLib.byName(en);
      return (EntType)(!ht.isMissingno() ? ht : MonsterTypeLib.byName(en));
   }

   public static EntType random() {
      return (EntType)(Math.random() > 0.5 ? MonsterTypeLib.randomWithRarity() : HeroTypeUtils.random());
   }

   public static List<Eff> getAllEffs() {
      List<Eff> result = new ArrayList<>();

      for (EntType entType : getAll()) {
         for (EntSide side : entType.sides) {
            result.add(side.getBaseEffect());
         }
      }

      return result;
   }

   public static MTBill copy(MonsterType src) {
      MTBill mtb = new MTBill(src.size);
      mtb.death(src.deathSound);
      finishInit(mtb, src);
      return mtb;
   }

   public static HTBill copy(HeroType src) {
      return HeroTypeUtils.copy(src);
   }

   public static ETBill copyE(EntType src) {
      if (src instanceof MonsterType) {
         return copy((MonsterType)src);
      } else {
         return src instanceof HeroType ? HeroTypeUtils.copy((HeroType)src) : null;
      }
   }

   public static void finishInit(ETBill htb, EntType src) {
      htb.name(src.getName(false));
      EntSide[] sides = new EntSide[6];

      for (int i = 0; i < sides.length; i++) {
         sides[i] = src.sides[i].copy();
      }

      htb.sidesRaw(sides);
      htb.hp(src.hp);
      htb.arOverride(src.portrait);
      htb.offsetOverride(src.offsets);

      for (Trait t : src.traits) {
         htb.trait(t);
      }
   }

   public static boolean anyMissingno(List<? extends EntType> monsterList) {
      for (int i = 0; i < monsterList.size(); i++) {
         if (monsterList.get(i).isMissingno()) {
            return true;
         }
      }

      return false;
   }
}

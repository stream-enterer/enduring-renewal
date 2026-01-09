package com.tann.dice.gameplay.progress.stats.stat.pickRate;

import com.tann.dice.gameplay.content.ent.type.HeroType;
import com.tann.dice.gameplay.content.ent.type.lib.HeroTypeLib;
import com.tann.dice.gameplay.content.item.Item;
import com.tann.dice.gameplay.content.item.ItemLib;
import com.tann.dice.gameplay.modifier.Modifier;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.choosable.Choosable;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.choosable.special.LevelupHeroChoosable;
import com.tann.dice.gameplay.progress.stats.stat.Stat;
import com.tann.dice.gameplay.progress.stats.stat.StatLib;
import java.util.ArrayList;
import java.util.List;

public class PickStat extends Stat {
   public static final int REJECT_VAL = 65536;
   public static final int PICK_VAL = 1;

   public PickStat(Choosable c) {
      super(nameFor(c));
   }

   public static String nameFor(Choosable o) {
      if (o instanceof LevelupHeroChoosable) {
         return nameFor(((LevelupHeroChoosable)o).getHeroType());
      } else if (o instanceof HeroType) {
         return nameFor((HeroType)o);
      } else if (o instanceof Item) {
         return nameFor((Item)o);
      } else if (o instanceof Modifier) {
         return nameFor((Modifier)o);
      } else if (o == null) {
         return "no pick for null";
      } else {
         throw new RuntimeException("No pick for " + o.getClass().getSimpleName());
      }
   }

   public static String nameFor(HeroType type) {
      return type.getName(false) + "-hp";
   }

   public static String nameFor(Modifier type) {
      return type.getName();
   }

   public static String nameFor(Item type) {
      return type.getName(false) + "-ip";
   }

   public static int val(Stat s, boolean reject) {
      return BitStat.val(s.getValue(), reject);
   }

   public static int getRandomValue(int rngMax) {
      return (int)(Math.random() * rngMax) + (int)(Math.random() * rngMax) * 65536;
   }

   public int getValue(boolean reject) {
      return val(this, reject);
   }

   public static List<Stat> make(StatLib.StatSource statSource) {
      if (statSource == StatLib.StatSource.Dungeon) {
         return new ArrayList<>();
      } else {
         List<Stat> result = new ArrayList<>();

         for (HeroType ht : HeroTypeLib.getMasterCopy()) {
            if (ht.level != 1) {
               result.add(new PickStat(ht));
            }
         }

         for (Item e : ItemLib.getMasterCopy()) {
            result.add(new PickStat(e));
         }

         return result;
      }
   }

   @Override
   public boolean isBoring() {
      return true;
   }
}

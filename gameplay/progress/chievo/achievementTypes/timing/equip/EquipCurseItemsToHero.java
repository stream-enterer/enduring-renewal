package com.tann.dice.gameplay.progress.chievo.achievementTypes.timing.equip;

import com.tann.dice.gameplay.content.ent.Hero;
import com.tann.dice.gameplay.content.ent.group.Party;
import com.tann.dice.gameplay.content.item.Item;
import com.tann.dice.gameplay.content.item.ItemLib;
import com.tann.dice.gameplay.progress.chievo.unlock.Unlockable;
import java.util.Arrays;
import java.util.List;

public class EquipCurseItemsToHero extends EquipAchievement {
   final int numCurseItems;

   public EquipCurseItemsToHero(String name, int numCurseItems, Unlockable... unlockables) {
      super(name, DESCRIBE(numCurseItems), unlockables);
      this.numCurseItems = numCurseItems;
      this.diff(20.0F);
   }

   private static String DESCRIBE(int numCurseItems) {
      return "Equip " + numCurseItems + " curse items to a single hero";
   }

   public static List<EquipAchievement> make() {
      return Arrays.asList(new EquipCurseItemsToHero("Encumbered", 2, ItemLib.byName("Soul Link"), ItemLib.byName("martyr")));
   }

   @Override
   public boolean onEquip(Party party) {
      for (Hero h : party.getHeroes()) {
         int curseItems = 0;

         for (Item i : h.getItems()) {
            if (i.getTier() < 0) {
               curseItems++;
            }
         }

         if (curseItems >= this.numCurseItems) {
            return true;
         }
      }

      return false;
   }
}

package com.tann.dice.gameplay.progress.chievo.achievementTypes.timing.equip;

import com.tann.dice.gameplay.content.ent.Hero;
import com.tann.dice.gameplay.content.ent.group.Party;
import com.tann.dice.gameplay.content.item.Item;
import com.tann.dice.gameplay.content.item.ItemLib;
import com.tann.dice.gameplay.progress.chievo.unlock.Unlockable;
import java.util.Arrays;
import java.util.List;

public class EquipComboAchievement extends EquipAchievement {
   final Item a;
   final Item b;

   public EquipComboAchievement(String name, Item a, Item b, Unlockable... unlockables) {
      super(name, "Equip '" + a.getName(false) + "' and '" + b.getName(false) + "' to the same hero", unlockables);
      this.a = a;
      this.b = b;
   }

   @Override
   public boolean onEquip(Party party) {
      for (Hero h : party.getHeroes()) {
         if (h.hasItem(this.a) && h.hasItem(this.b)) {
            return true;
         }
      }

      return false;
   }

   public static List<EquipAchievement> makeAll() {
      return Arrays.asList(new EquipComboAchievement("Bedtime", ItemLib.byName("Pillow"), ItemLib.byName("Duvet")));
   }
}

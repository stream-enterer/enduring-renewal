package com.tann.dice.gameplay.progress.chievo.achievementTypes.timing.equip;

import com.tann.dice.gameplay.content.ent.group.Party;
import com.tann.dice.gameplay.progress.chievo.Achievement;
import com.tann.dice.gameplay.progress.chievo.unlock.Unlockable;
import com.tann.dice.util.Tann;
import java.util.ArrayList;
import java.util.List;

public abstract class EquipAchievement extends Achievement {
   public EquipAchievement(String name, String description, Unlockable... unlockables) {
      super(name, description, unlockables);
   }

   public abstract boolean onEquip(Party var1);

   public static List<EquipAchievement> makeAll() {
      List<EquipAchievement> all = new ArrayList<>();
      all.addAll(EquipNumItemsToHero.makeAll());
      all.addAll(EquipComboAchievement.makeAll());
      all.addAll(EquipSpecificHeroItemAchievement.makeAll());
      all.addAll(EquipCurseItemsToHero.make());
      return all;
   }

   public static void testMissingno() {
      for (EquipAchievement ea : makeAll()) {
         if (ea instanceof EquipComboAchievement) {
            EquipComboAchievement eca = (EquipComboAchievement)ea;
            Tann.assertTrue(ea.toString(), !eca.a.isMissingno() && !eca.b.isMissingno());
         }

         if (ea instanceof EquipSpecificHeroItemAchievement) {
            EquipSpecificHeroItemAchievement eha = (EquipSpecificHeroItemAchievement)ea;
            Tann.assertTrue(ea.toString(), !eha.item.isMissingno() && !eha.heroType.isMissingno());
         }
      }
   }
}

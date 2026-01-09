package com.tann.dice.gameplay.progress.chievo.achievementTypes.timing.equip;

import com.tann.dice.gameplay.content.ent.Hero;
import com.tann.dice.gameplay.content.ent.group.Party;
import com.tann.dice.gameplay.content.ent.type.HeroType;
import com.tann.dice.gameplay.content.ent.type.lib.HeroTypeUtils;
import com.tann.dice.gameplay.progress.chievo.unlock.Unlockable;
import java.util.Arrays;
import java.util.List;

public class EquipNumItemsToHero extends EquipAchievement {
   final int numItems;
   final HeroType heroType;

   public EquipNumItemsToHero(int numItems, HeroType heroType, Unlockable... unlockables) {
      this(heroType.getName(false) + " " + numItems + " items", numItems, heroType, unlockables);
   }

   public EquipNumItemsToHero(String name, int numItems, HeroType heroType, Unlockable... unlockables) {
      super(name, "Equip " + numItems + " items to " + heroType.getName(true), unlockables);
      this.numItems = numItems;
      this.heroType = heroType;
   }

   @Override
   public boolean onEquip(Party party) {
      Hero ht = party.getByType(this.heroType);
      return ht == null ? false : ht.getItems().size() >= this.numItems;
   }

   public static List<EquipAchievement> makeAll() {
      return Arrays.asList(
         new EquipNumItemsToHero("Hoard", 3, HeroTypeUtils.byName("hoarder")),
         new EquipNumItemsToHero("Collection", 3, HeroTypeUtils.byName("collector")),
         new EquipNumItemsToHero("Museum", 3, HeroTypeUtils.byName("curator"))
      );
   }
}

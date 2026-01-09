package com.tann.dice.gameplay.progress.chievo.achievementTypes.timing.equip;

import com.tann.dice.gameplay.content.ent.Hero;
import com.tann.dice.gameplay.content.ent.group.Party;
import com.tann.dice.gameplay.content.ent.type.HeroType;
import com.tann.dice.gameplay.content.ent.type.lib.HeroTypeUtils;
import com.tann.dice.gameplay.content.item.Item;
import com.tann.dice.gameplay.content.item.ItemLib;
import com.tann.dice.gameplay.progress.chievo.unlock.Unlockable;
import java.util.Arrays;
import java.util.List;

public class EquipSpecificHeroItemAchievement extends EquipAchievement {
   final HeroType heroType;
   final Item item;

   public EquipSpecificHeroItemAchievement(String name, HeroType heroType, Item item, Unlockable... unlockables) {
      super(name, "Equip '" + item.getName(false) + "' to " + heroType.getName(true), unlockables);
      this.heroType = heroType;
      this.item = item;
      this.diff(20.0F);
   }

   @Override
   public boolean onEquip(Party party) {
      Hero h = party.getByType(this.heroType);
      return h != null && h.hasItem(this.item);
   }

   public static List<EquipAchievement> makeAll() {
      return Arrays.asList(
         new EquipSpecificHeroItemAchievement("7 damage!", HeroTypeUtils.byName("gambler"), ItemLib.byName("Ace of Spades")),
         new EquipSpecificHeroItemAchievement("My hat!", HeroTypeUtils.byName("Jester"), ItemLib.byName("Jester Cap"))
      );
   }
}

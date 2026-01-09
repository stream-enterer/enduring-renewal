package com.tann.dice.gameplay.trigger.global.chance;

import com.badlogic.gdx.graphics.Color;
import com.tann.dice.gameplay.save.settings.option.OptionLib;
import com.tann.dice.gameplay.trigger.personal.linked.TriggerPersonalToGlobal;

public class MonsterChance extends TriggerPersonalToGlobal {
   final Rarity rarity;

   public MonsterChance(Rarity rarity) {
      super(GlobalRarity.fromRarity(rarity));
      this.rarity = rarity;
   }

   @Override
   public boolean showInEntPanelInternal() {
      return false;
   }

   @Override
   public boolean showInDiePanel() {
      return OptionLib.SHOW_RARITY.c();
   }

   @Override
   public String getImageName() {
      return "rarity/star";
   }

   @Override
   public Color getImageCol() {
      return this.rarity.col;
   }

   @Override
   public boolean metaOnly() {
      return true;
   }
}

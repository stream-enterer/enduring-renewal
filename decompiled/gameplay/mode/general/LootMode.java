package com.tann.dice.gameplay.mode.general;

import com.badlogic.gdx.graphics.Color;
import com.tann.dice.gameplay.context.config.ContextConfig;
import com.tann.dice.gameplay.context.config.difficultyConfig.LootConfig;
import com.tann.dice.gameplay.mode.Mode;
import com.tann.dice.gameplay.mode.meta.folder.FolderType;
import com.tann.dice.gameplay.trigger.Collision;
import com.tann.dice.gameplay.trigger.personal.item.ItemSlots;
import com.tann.dice.util.Colours;
import java.util.List;

public class LootMode extends Mode {
   public LootMode() {
      super("Loot");
   }

   @Override
   public Color getColour() {
      return Colours.orange;
   }

   @Override
   public String[] getDescriptionLines() {
      return new String[]{"no levelups", "loot after each fight", new ItemSlots(1).describeForSelfBuff(), "this mode is [purple]difficult[cu]"};
   }

   @Override
   protected List<ContextConfig> makeAllConfigs() {
      return LootConfig.make();
   }

   @Override
   public String getSaveKey() {
      return "loot";
   }

   @Override
   public long getBannedCollisionBits() {
      return Collision.ITEM_REWARD | Collision.LEVELUP_REWARD;
   }

   @Override
   public FolderType getFolderType() {
      return FolderType.cool;
   }
}

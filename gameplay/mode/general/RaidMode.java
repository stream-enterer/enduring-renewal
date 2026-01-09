package com.tann.dice.gameplay.mode.general;

import com.badlogic.gdx.graphics.Color;
import com.tann.dice.gameplay.context.config.ContextConfig;
import com.tann.dice.gameplay.context.config.difficultyConfig.RaidConfig;
import com.tann.dice.gameplay.mode.Mode;
import com.tann.dice.gameplay.mode.meta.folder.FolderType;
import com.tann.dice.gameplay.trigger.Collision;
import com.tann.dice.util.Colours;
import java.util.List;

public class RaidMode extends Mode {
   public RaidMode() {
      super("Raid");
   }

   @Override
   public Color getColour() {
      return Colours.orange;
   }

   @Override
   public String[] getDescriptionLines() {
      return new String[]{"[yellow]10 heroes[cu]", "[purple]double monsters", "no items, only level-ups", "try changing [light]ui size to -1[cu] in settings"};
   }

   @Override
   protected List<ContextConfig> makeAllConfigs() {
      return RaidConfig.make();
   }

   @Override
   public String getSaveKey() {
      return "mob";
   }

   @Override
   public long getBannedCollisionBits() {
      return Collision.ITEM | Collision.ITEM_REWARD | Collision.HERO_POSITION;
   }

   @Override
   public boolean disablePartyLayout() {
      return true;
   }

   @Override
   public FolderType getFolderType() {
      return FolderType.cool;
   }
}

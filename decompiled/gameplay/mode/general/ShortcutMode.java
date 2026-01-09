package com.tann.dice.gameplay.mode.general;

import com.badlogic.gdx.graphics.Color;
import com.tann.dice.gameplay.context.config.ContextConfig;
import com.tann.dice.gameplay.context.config.difficultyConfig.ShortcutConfig;
import com.tann.dice.gameplay.mode.Mode;
import com.tann.dice.gameplay.mode.meta.folder.FolderType;
import com.tann.dice.gameplay.trigger.Collision;
import com.tann.dice.util.Colours;
import java.util.List;

public class ShortcutMode extends Mode {
   public ShortcutMode() {
      super("Shortcut");
   }

   @Override
   public String[] getDescriptionLines() {
      return new String[]{"skip the first 8 fights"};
   }

   @Override
   protected List<ContextConfig> makeAllConfigs() {
      return ShortcutConfig.make();
   }

   @Override
   public Color getColour() {
      return Colours.yellow;
   }

   @Override
   public String getSaveKey() {
      return "shortcut";
   }

   @Override
   public long getBannedCollisionBits() {
      return Collision.SPECIFIC_LEVEL_WIDE;
   }

   @Override
   public FolderType getFolderType() {
      return FolderType.cool;
   }
}

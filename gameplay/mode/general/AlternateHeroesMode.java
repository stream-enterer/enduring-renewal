package com.tann.dice.gameplay.mode.general;

import com.badlogic.gdx.graphics.Color;
import com.tann.dice.gameplay.context.config.ContextConfig;
import com.tann.dice.gameplay.context.config.difficultyConfig.AlternateHeroesConfig;
import com.tann.dice.gameplay.mode.Mode;
import com.tann.dice.gameplay.mode.meta.folder.FolderType;
import com.tann.dice.util.Colours;
import java.util.List;

public class AlternateHeroesMode extends Mode {
   public AlternateHeroesMode() {
      super("Alternate");
   }

   @Override
   public Color getColour() {
      return Colours.red;
   }

   @Override
   public String[] getDescriptionLines() {
      return new String[]{"all heroes are levelled up/down versions of existing heroes"};
   }

   @Override
   protected List<ContextConfig> makeAllConfigs() {
      return AlternateHeroesConfig.make();
   }

   @Override
   public String getSaveKey() {
      return "alternate";
   }

   @Override
   public FolderType getFolderType() {
      return FolderType.cool;
   }
}

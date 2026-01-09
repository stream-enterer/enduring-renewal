package com.tann.dice.gameplay.mode.general;

import com.badlogic.gdx.graphics.Color;
import com.tann.dice.gameplay.context.config.ContextConfig;
import com.tann.dice.gameplay.context.config.difficultyConfig.GenerateHeroesConfig;
import com.tann.dice.gameplay.mode.Mode;
import com.tann.dice.gameplay.mode.meta.folder.FolderType;
import com.tann.dice.util.Colours;
import java.util.List;

public class GenerateHeroesMode extends Mode {
   public GenerateHeroesMode() {
      super("Generate");
   }

   @Override
   public Color getColour() {
      return Colours.red;
   }

   @Override
   public String[] getDescriptionLines() {
      return new String[]{"all heroes randomly-generated"};
   }

   @Override
   protected List<ContextConfig> makeAllConfigs() {
      return GenerateHeroesConfig.make();
   }

   @Override
   public String getSaveKey() {
      return "generate";
   }

   @Override
   public FolderType getFolderType() {
      return FolderType.cool;
   }
}

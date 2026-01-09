package com.tann.dice.gameplay.mode.general;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.context.DungeonContext;
import com.tann.dice.gameplay.context.config.ContextConfig;
import com.tann.dice.gameplay.context.config.difficultyConfig.ClassicConfig;
import com.tann.dice.gameplay.mode.Mode;
import com.tann.dice.util.Colours;
import java.util.List;

public class ClassicMode extends Mode {
   public ClassicMode() {
      super("Classic");
   }

   @Override
   public String[] getDescriptionLines() {
      return new String[]{"full 20-fight dungeon"};
   }

   @Override
   protected List<ContextConfig> makeAllConfigs() {
      return ClassicConfig.make();
   }

   @Override
   public Color getColour() {
      return Colours.green;
   }

   @Override
   public String getSaveKey() {
      return "classic";
   }

   @Override
   public List<Actor> getEndInfo(DungeonContext context, int previousFurthestReached, boolean victory) {
      return super.getEndInfo(context, previousFurthestReached, victory);
   }

   @Override
   public boolean displayPopup() {
      return false;
   }
}

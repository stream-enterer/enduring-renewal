package com.tann.dice.gameplay.mode.debuggy;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.context.config.ContextConfig;
import com.tann.dice.gameplay.mode.Mode;
import com.tann.dice.gameplay.mode.meta.folder.FolderType;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Pixl;
import java.util.ArrayList;
import java.util.List;

public class EmptyMode extends Mode {
   public EmptyMode() {
      super("Empty");
   }

   @Override
   public Color getColour() {
      return Colours.grey;
   }

   @Override
   public Actor makeStartGameCard(List<ContextConfig> all) {
      return new Pixl().pix();
   }

   @Override
   public String[] getDescriptionLines() {
      return new String[]{"empty"};
   }

   @Override
   protected List<ContextConfig> makeAllConfigs() {
      return new ArrayList<>();
   }

   @Override
   public String getSaveKey() {
      return null;
   }

   @Override
   public FolderType getFolderType() {
      return FolderType.crappy;
   }
}

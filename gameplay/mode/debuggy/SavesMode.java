package com.tann.dice.gameplay.mode.debuggy;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.context.config.ContextConfig;
import com.tann.dice.gameplay.mode.Mode;
import com.tann.dice.gameplay.mode.meta.folder.FolderType;
import com.tann.dice.gameplay.save.SaveState;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Pixl;
import com.tann.dice.util.Tann;
import java.util.ArrayList;
import java.util.List;

public class SavesMode extends Mode {
   public SavesMode() {
      super("Saves");
   }

   @Override
   public String[] getDescriptionLines() {
      return new String[]{"all in-progress games"};
   }

   @Override
   public Actor makeStartGameCard(List<ContextConfig> all) {
      Pixl p = new Pixl(5);

      for (Mode m : Mode.getAllSaveBearingModes()) {
         Actor loadButton = SaveState.getLoadButton(m.getSaveKey());
         if (loadButton != null) {
            p.actor(new Pixl(2, 3).border(m.getColour()).text(m.getTextButtonName()).row().actor(loadButton).pix(), com.tann.dice.Main.width * 0.8F);
         }
      }

      return Tann.makeScrollpaneIfNecessary(p.pix());
   }

   @Override
   public Color getColour() {
      return Colours.grey;
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

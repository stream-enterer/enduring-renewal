package com.tann.dice.gameplay.mode.meta.folder;

import com.tann.dice.gameplay.mode.Mode;
import com.tann.dice.util.Colours;
import java.util.List;

public class RootFolder extends FolderMode {
   public RootFolder() {
      super("/", Colours.z_white);
   }

   @Override
   public List<Mode> getContainedModes() {
      return Mode.getAllModesOmni();
   }

   @Override
   public String[] getDescriptionLines() {
      return new String[]{"all modes"};
   }

   @Override
   public Mode getParent() {
      return null;
   }
}

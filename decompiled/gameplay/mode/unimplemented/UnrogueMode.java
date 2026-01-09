package com.tann.dice.gameplay.mode.unimplemented;

import com.tann.dice.gameplay.context.config.ContextConfig;
import com.tann.dice.gameplay.mode.Mode;
import com.tann.dice.gameplay.mode.meta.folder.FolderType;
import java.util.List;

public class UnrogueMode extends Mode {
   public UnrogueMode() {
      super("Unrogue");
   }

   @Override
   public String[] getDescriptionLines() {
      return new String[]{"When you lose, gain a random t0 item and restart the fight"};
   }

   @Override
   protected List<ContextConfig> makeAllConfigs() {
      return Mode.CLASSIC.getConfigs();
   }

   @Override
   public String getSaveKey() {
      return "unrogue";
   }

   @Override
   public FolderType getFolderType() {
      return FolderType.unfinished;
   }

   @Override
   public boolean isPlayable() {
      return false;
   }
}

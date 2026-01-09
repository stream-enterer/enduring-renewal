package com.tann.dice.gameplay.mode.cursey;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.context.config.ContextConfig;
import com.tann.dice.gameplay.context.config.cursed.BlursedConfig;
import com.tann.dice.gameplay.mode.Mode;
import com.tann.dice.gameplay.mode.meta.folder.FolderType;
import com.tann.dice.util.Colours;
import com.tann.dice.util.ui.TextWriter;
import java.util.Arrays;
import java.util.List;

public class BlursedMode extends Mode {
   public BlursedMode() {
      super("[green]Bl[purple]ursed");
   }

   @Override
   public final String getTextButtonName() {
      return this.name;
   }

   @Override
   public String[] getDescriptionLines() {
      return new String[]{"same as " + Mode.CURSE.getTextButtonName() + " mode", "but start with a blessing choice", "[grey]Warning: long"};
   }

   @Override
   protected List<ContextConfig> makeAllConfigs() {
      return Arrays.asList(new BlursedConfig());
   }

   @Override
   public Color getColour() {
      return Colours.green;
   }

   @Override
   public String getSaveKey() {
      return "curse-easy";
   }

   @Override
   public Actor makeWinsActor(ContextConfig config) {
      BlursedConfig cc = (BlursedConfig)this.getConfigs().get(0);
      int furthestReached = cc.getFurthestReached();
      return (Actor)(furthestReached <= 0 ? new Actor() : new TextWriter("[yellow]Highscore: " + furthestReached, 5000, Colours.purple, 3));
   }

   @Override
   public FolderType getFolderType() {
      return FolderType.cursed;
   }

   @Override
   public long getBannedCollisionBits() {
      return CurseMode.getCollisionBitStatic();
   }
}

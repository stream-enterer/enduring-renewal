package com.tann.dice.gameplay.mode.cursey;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.context.config.ContextConfig;
import com.tann.dice.gameplay.context.config.cursed.CurseUltraConfig;
import com.tann.dice.gameplay.mode.Mode;
import com.tann.dice.gameplay.mode.meta.folder.FolderType;
import com.tann.dice.util.Colours;
import com.tann.dice.util.ui.TextWriter;
import java.util.Arrays;
import java.util.List;

public class CurseModeUltra extends Mode {
   public CurseModeUltra() {
      super("Cursed-Ultra");
   }

   @Override
   public String[] getDescriptionLines() {
      return new String[]{
         "same as " + Mode.CURSE.getTextButtonName() + " mode", "but all [purple]curses are t2-3[cu]", "and you choose [green]blessings value 10[cu] each loop"
      };
   }

   @Override
   protected List<ContextConfig> makeAllConfigs() {
      return Arrays.asList(new CurseUltraConfig());
   }

   @Override
   public Color getColour() {
      return Colours.red;
   }

   @Override
   public String getSaveKey() {
      return "curse-ultra3";
   }

   @Override
   public Actor makeWinsActor(ContextConfig config) {
      CurseUltraConfig cc = (CurseUltraConfig)this.getConfigs().get(0);
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

package com.tann.dice.gameplay.mode.cursey;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.context.config.ContextConfig;
import com.tann.dice.gameplay.context.config.cursed.CurseHyperConfig;
import com.tann.dice.gameplay.mode.Mode;
import com.tann.dice.gameplay.mode.meta.folder.FolderType;
import com.tann.dice.util.Colours;
import com.tann.dice.util.ui.TextWriter;
import java.util.Arrays;
import java.util.List;

public class CurseModeHyper extends Mode {
   public CurseModeHyper() {
      super("Cursed-Hyper");
   }

   @Override
   public Color getColour() {
      return Colours.pink;
   }

   @Override
   public String[] getDescriptionLines() {
      return new String[]{
         "infinite play (until you lose!)",
         "gain a [purple]curse[cu] at the start",
         "gain a [purple]curse[cu] after each fight",
         "gain a [green]blessing[cu] after each boss"
      };
   }

   @Override
   protected List<ContextConfig> makeAllConfigs() {
      return Arrays.asList(new CurseHyperConfig());
   }

   @Override
   public String getSaveKey() {
      return "cursed-hyper3";
   }

   @Override
   public Actor makeWinsActor(ContextConfig config) {
      CurseHyperConfig cc = (CurseHyperConfig)this.getConfigs().get(0);
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

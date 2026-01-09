package com.tann.dice.gameplay.mode.general;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.context.DungeonContext;
import com.tann.dice.gameplay.context.config.ContextConfig;
import com.tann.dice.gameplay.context.config.misc.InstantConfig;
import com.tann.dice.gameplay.mode.Mode;
import com.tann.dice.gameplay.mode.meta.folder.FolderType;
import com.tann.dice.screens.titleScreen.GameStart;
import com.tann.dice.util.Colours;
import com.tann.dice.util.ui.standardButton.StandardButton;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InstantMode extends Mode {
   public InstantMode() {
      super("Instant");
   }

   @Override
   public String[] getDescriptionLines() {
      return new String[]{"play a single random fight", "with random heroes and items"};
   }

   @Override
   protected List<ContextConfig> makeAllConfigs() {
      return Arrays.asList(new InstantConfig(0), new InstantConfig(3), new InstantConfig(6), new InstantConfig(9));
   }

   @Override
   public Color getColour() {
      return Colours.grey;
   }

   @Override
   public String getSaveKey() {
      return "Instant";
   }

   @Override
   public List<Actor> getEndInfo(DungeonContext context, int previousFurthestReached, boolean victory) {
      return getInfo();
   }

   private static List<Actor> getInfo() {
      List<Actor> result = new ArrayList<>();
      return result;
   }

   @Override
   public List<Actor> getEndOptions(DungeonContext dungeonContext, boolean victory) {
      List<Actor> result = new ArrayList<>(super.getEndOptions(dungeonContext, victory));
      StandardButton another = new StandardButton("[orange]ANOTHER!");
      final InstantConfig ac = (InstantConfig)dungeonContext.getContextConfig();
      another.setRunnable(new Runnable() {
         @Override
         public void run() {
            GameStart.start(new InstantConfig(ac.handicap).makeContext());
         }
      });
      result.add(another);
      return result;
   }

   @Override
   public boolean skipStats() {
      return true;
   }

   @Override
   public FolderType getFolderType() {
      return FolderType.crappy;
   }

   @Override
   public boolean disablePartyLayout() {
      return true;
   }

   @Override
   public boolean skipShowBoss() {
      return true;
   }
}

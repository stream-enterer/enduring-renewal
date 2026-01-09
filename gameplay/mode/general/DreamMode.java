package com.tann.dice.gameplay.mode.general;

import com.badlogic.gdx.graphics.Color;
import com.tann.dice.gameplay.battleTest.Difficulty;
import com.tann.dice.gameplay.battleTest.Zone;
import com.tann.dice.gameplay.context.DungeonContext;
import com.tann.dice.gameplay.context.config.ContextConfig;
import com.tann.dice.gameplay.context.config.difficultyConfig.DifficultyConfig;
import com.tann.dice.gameplay.mode.Mode;
import com.tann.dice.gameplay.mode.meta.folder.FolderType;
import com.tann.dice.util.Colours;
import com.tann.dice.util.tp.TP;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DreamMode extends Mode {
   public DreamMode() {
      super("Dream");
   }

   @Override
   public Color getColour() {
      return Colours.blue;
   }

   @Override
   public String[] getDescriptionLines() {
      return new String[]{"All monsters can be encountered anywhere"};
   }

   @Override
   protected List<ContextConfig> makeAllConfigs() {
      return DreamMode.DreamConfig.make();
   }

   @Override
   public String getSaveKey() {
      return "dream";
   }

   @Override
   public boolean skipShowBoss() {
      return true;
   }

   @Override
   public FolderType getFolderType() {
      return FolderType.cool;
   }

   public static class DreamConfig extends DifficultyConfig {
      public DreamConfig(Difficulty difficulty) {
         super(Mode.DREAM, difficulty);
      }

      public static List<ContextConfig> make() {
         List<ContextConfig> cc = new ArrayList<>();

         for (Difficulty value : Difficulty.values()) {
            cc.add(new DreamMode.DreamConfig(value));
         }

         return cc;
      }

      @Override
      public List<TP<Zone, Integer>> getOverrideLevelTypes(DungeonContext context) {
         return Arrays.asList(new TP<>(Zone.All, 20));
      }

      @Override
      public int[] getBossLevels() {
         return new int[0];
      }
   }
}

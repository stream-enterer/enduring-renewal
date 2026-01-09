package com.tann.dice.gameplay.mode.general;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.context.DungeonContext;
import com.tann.dice.gameplay.context.config.ContextConfig;
import com.tann.dice.gameplay.context.config.difficultyConfig.DemoConfig;
import com.tann.dice.gameplay.mode.Mode;
import com.tann.dice.gameplay.mode.meta.folder.FolderType;
import com.tann.dice.gameplay.trigger.Collision;
import com.tann.dice.screens.titleScreen.ModesPanel;
import com.tann.dice.statics.sound.Sounds;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Pixl;
import com.tann.dice.util.Tann;
import com.tann.dice.util.ui.standardButton.StandardButton;
import java.util.ArrayList;
import java.util.List;

public class DemoMode extends Mode {
   public DemoMode() {
      super("Demo");
   }

   @Override
   public String[] getDescriptionLines() {
      return com.tann.dice.Main.demo
         ? new String[]{"first 12 fights only"}
         : new String[]{"first 12 fights only", "you own the full version", "this is just a mode"};
   }

   @Override
   protected List<ContextConfig> makeAllConfigs() {
      return DemoConfig.make();
   }

   @Override
   public Color getColour() {
      return Colours.grey;
   }

   @Override
   public String getSaveKey() {
      return "demo";
   }

   @Override
   public boolean displayPopup() {
      return false;
   }

   @Override
   public List<Actor> getEndOptions(DungeonContext dungeonContext, boolean victory) {
      List<Actor> result = new ArrayList<>(super.getEndOptions(dungeonContext, victory));
      if (com.tann.dice.Main.demo) {
         StandardButton a = new StandardButton("[green]£$¥ full game ¥$£");
         a.setRunnable(new Runnable() {
            @Override
            public void run() {
               Sounds.playSound(Sounds.pip);
               Actor fullGroup = new Pixl(2, 5).border(Colours.grey).actor(ModesPanel.makeFullVersionGroup()).pix();
               com.tann.dice.Main.getCurrentScreen().push(fullGroup, 0.7F);
               Tann.center(fullGroup);
            }
         });
         result.add(0, a);
      }

      return result;
   }

   @Override
   public FolderType getFolderType() {
      return FolderType.crappy;
   }

   @Override
   public long getBannedCollisionBits() {
      return Collision.SPECIFIC_LEVEL;
   }
}

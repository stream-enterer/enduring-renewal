package com.tann.dice.gameplay.mode.meta.folder;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.context.config.ContextConfig;
import com.tann.dice.gameplay.mode.Mode;
import com.tann.dice.gameplay.progress.chievo.AchLib;
import com.tann.dice.gameplay.progress.chievo.unlock.UnUtil;
import com.tann.dice.screens.titleScreen.TitleScreen;
import com.tann.dice.statics.sound.Sounds;
import com.tann.dice.util.Pixl;
import com.tann.dice.util.Tann;
import com.tann.dice.util.ui.standardButton.StandardButton;
import java.util.ArrayList;
import java.util.List;

public abstract class FolderMode extends Mode {
   final Color col;

   protected FolderMode(String name, Color col) {
      super(name);
      this.col = col;
   }

   @Override
   public Actor makeStartGameCard(List<ContextConfig> all) {
      Pixl p = new Pixl(4);
      List<Actor> acts = new ArrayList<>();

      for (final Mode m : this.getContainedModes()) {
         StandardButton a = m.makeModeSelectButton();
         final boolean locked = UnUtil.isLocked(m);
         a.setRunnable(new Runnable() {
            @Override
            public void run() {
               if (locked) {
                  AchLib.showUnlockFor(m);
               } else {
                  Sounds.playSound(Sounds.pip);
                  TitleScreen.showMode(m);
               }
            }
         });
         acts.add(a);
      }

      int guessMaxW = (int)(com.tann.dice.Main.width * 0.78F);
      Actor ex = new Pixl().listActor(guessMaxW, acts).pix();
      float factor = 1.1F;
      int add = 15;
      int maxW = (int)(ex.getWidth() * factor) + add;
      int maxH = (int)(Math.max(ex.getHeight(), acts.get(0).getHeight() * 2.0F) * factor) + add;
      p.actor(Tann.layoutMinArea(acts, 4, maxW, maxH));
      return p.pix();
   }

   @Override
   public final Color getColour() {
      return this.col;
   }

   public abstract List<Mode> getContainedModes();

   @Override
   protected List<ContextConfig> makeAllConfigs() {
      return new ArrayList<>();
   }

   @Override
   public String getSaveKey() {
      return null;
   }

   @Override
   public boolean skipUnlockNotify() {
      return true;
   }

   public boolean isLocked() {
      if (this.getFolderType() != FolderType.unfinished && this.getFolderType() != FolderType.debug) {
         for (Mode containedMode : this.getContainedModes()) {
            if (!UnUtil.isLocked(containedMode)) {
               return false;
            }
         }

         return true;
      } else {
         return true;
      }
   }

   @Override
   public boolean skipMetaInfoOnNameClick() {
      return true;
   }
}

package com.tann.dice.gameplay.save.settings.option;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.progress.chievo.unlock.Unlockable;
import com.tann.dice.statics.sound.Sounds;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Pixl;
import com.tann.dice.util.Tann;

public abstract class Option implements Unlockable {
   protected final String name;
   protected final String desc;

   protected Option(String name) {
      this(name, null);
   }

   protected Option(String name, String desc) {
      this.name = name;
      this.desc = desc;
   }

   public String getName() {
      return this.name;
   }

   @Override
   public final TextureRegion getAchievementIcon() {
      return null;
   }

   @Override
   public String getAchievementIconString() {
      return "[text]O";
   }

   @Override
   public Actor makeUnlockActor(boolean big) {
      return this.makeCogActor();
   }

   public abstract Actor makeCogActor();

   protected void manualSelectAction() {
   }

   public boolean isDebug() {
      return this.name.startsWith("db-");
   }

   public boolean isValid() {
      return true;
   }

   public boolean showExtraInfo() {
      return showExtraInfo(this.name, this.desc);
   }

   public static boolean showExtraInfo(String name, String extraText) {
      if (extraText == null) {
         return false;
      } else {
         Sounds.playSound(Sounds.pip);
         Actor a = new Pixl(4, 3).border(Colours.purple).text(name).row().text("[text]" + extraText, 110).pix();
         com.tann.dice.Main.getCurrentScreen().push(a);
         Tann.center(a);
         return true;
      }
   }

   public void reset() {
   }

   public Actor makeFullDescribedUnlockActor() {
      if (this.desc != null && !this.desc.isEmpty()) {
         Color c = Colours.withAlpha(Colours.blue, 0.11F).cpy();
         return new Pixl(3).flatBorder(c).actor(this.makeUnlockActor(true)).row().text(this.desc, 80).pix();
      } else {
         return this.makeUnlockActor(true);
      }
   }

   @Override
   public String toString() {
      return this.name;
   }
}

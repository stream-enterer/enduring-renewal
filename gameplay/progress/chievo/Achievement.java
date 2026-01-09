package com.tann.dice.gameplay.progress.chievo;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.mode.Mode;
import com.tann.dice.gameplay.progress.chievo.unlock.UnUtil;
import com.tann.dice.gameplay.progress.chievo.unlock.Unlockable;
import com.tann.dice.statics.ImageUtils;
import com.tann.dice.util.Colours;
import com.tann.dice.util.ImageActor;
import com.tann.dice.util.Pixl;
import com.tann.dice.util.lang.Words;
import com.tann.dice.util.ui.TextWriter;

public abstract class Achievement {
   public static final String UNLOCK_CHIEV_NAME = "achievement";
   public static final String SECRET_CHIEV_NAME = "secret";
   public static final Color CHALLENGE_COL = Colours.yellow;
   public static final Color SECRET_COL = Colours.purple;
   public static final int TEXTURE_SIZE = 16;
   public static final float DEFAULT_DIFFICULTY = -1.0F;
   private final String name;
   private final String description;
   private Unlockable[] unlockables;
   TextureRegion image;
   String alternateImage;
   private boolean achieved;
   private float difficulty = -1.0F;

   public Achievement(String name, String description, Unlockable... unlockables) {
      this.name = name;
      this.description = description;
      TextureRegion maybe = ImageUtils.loadExtNull("icon/achievements/" + name.toLowerCase().replaceAll(" ", "-"));
      if (maybe != null) {
         this.image = maybe;
      }

      this.unlockables = unlockables;
      if (unlockables.length > 1) {
         this.alternateImage = unlockables[0].getAchievementIconString() + unlockables.length;
      } else if (unlockables.length == 1) {
         Unlockable u = unlockables[0];
         if (u.getAchievementIcon() != null) {
            this.image = u.getAchievementIcon();
         } else if (u.getAchievementIconString() != null) {
            this.alternateImage = u.getAchievementIconString();
         }
      }
   }

   public void setAchievedStateInternal(boolean newState) {
      if (this.achieved != newState) {
         this.achieved = newState;
      }
   }

   public boolean isAchieved() {
      return this.achieved;
   }

   @Override
   public String toString() {
      return this.getName();
   }

   public Actor getImage() {
      if (this.image != null) {
         return new ImageActor(this.image);
      } else {
         return (Actor)(this.alternateImage != null
            ? new TextWriter("[notranslate]" + this.alternateImage)
            : new ImageActor(ImageUtils.loadExt("icon/achievements/placeholder")));
      }
   }

   public String getName() {
      return this.name;
   }

   public String getDescription() {
      return this.description;
   }

   public Actor getUnlockActor() {
      if (this.unlockables.length == 0) {
         return null;
      } else {
         String text;
         if (this.unlockables.length == 1) {
            text = "[yellow]unlocks " + Words.singular(UnUtil.nameFor((Class<? extends Unlockable>)this.unlockables[0].getClass()));
         } else {
            String nm = UnUtil.nameFor((Class<? extends Unlockable>)this.unlockables[0].getClass());
            if (!this.allUnlockablesSame()) {
               nm = "thing";
            }

            text = "[yellow]unlocks " + Words.fullPlural(nm, this.unlockables.length);
         }

         Pixl p = new Pixl().text(text);
         if (this.isAchieved()) {
            Pixl inner = new Pixl(1);

            for (Unlockable u : this.unlockables) {
               inner.actor(u.makeUnlockActor(this.unlockables.length == 1), com.tann.dice.Main.width * 0.8F);
            }

            p.row(1).actor(inner.pix());
         }

         return p.pix();
      }
   }

   private boolean allUnlockablesSame() {
      if (this.unlockables.length == 0) {
         return true;
      } else {
         String name = UnUtil.nameFor(this.unlockables[0]);

         for (int i = 0; i < this.unlockables.length; i++) {
            if (!UnUtil.nameFor(this.unlockables[i]).equalsIgnoreCase(name)) {
               return false;
            }
         }

         return true;
      }
   }

   public Unlockable[] getUnlockables() {
      return this.unlockables;
   }

   public Achievement diff(float difficulty) {
      this.difficulty = difficulty;
      return this;
   }

   public float getDifficulty() {
      return this.difficulty;
   }

   public boolean isChallenge() {
      return this.getUnlockables().length > 0;
   }

   public boolean isCompletable() {
      return true;
   }

   public String getExplanelName() {
      if (!this.isCompletable()) {
         return "???";
      } else {
         return com.tann.dice.Main.self().translator.shouldTranslate() && this.getName().equals("Draw")
            ? "[notranslate]" + com.tann.dice.Main.t("Draw (% Achievement %)")
            : this.getName();
      }
   }

   public String getExplanelDescription() {
      return !this.isAchieved() && !this.isCompletable() ? "Another achievement must be completed first..." : this.getDescription();
   }

   public String getDebugClassName() {
      Class c = this.getClass();

      while (c.getSimpleName().isEmpty()) {
         c = c.getSuperclass();
      }

      return c.getSimpleName();
   }

   public boolean forSpecificMode(Mode mode) {
      return false;
   }
}

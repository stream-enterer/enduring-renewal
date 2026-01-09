package com.tann.dice.screens.dungeon.panels.popup;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.tann.dice.gameplay.progress.chievo.Achievement;
import com.tann.dice.gameplay.progress.chievo.AchievementCompletionListener;
import com.tann.dice.screens.dungeon.panels.book.page.ledgerPage.AchievementIconView;
import com.tann.dice.statics.sound.Sounds;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Pixl;
import com.tann.dice.util.listener.TannListener;
import com.tann.dice.util.ui.TextWriter;
import com.tann.dice.util.ui.action.PixAction;
import java.util.ArrayList;
import java.util.List;

public class PopupHolder extends Group implements AchievementCompletionListener {
   private static final float DOWN_TIME = 0.3F;
   private static final float DELAY = 5.0F;
   private static final float UP_TIME = 0.3F;
   private static final Interpolation terp = Interpolation.pow2Out;
   List<Actor> popups = new ArrayList<>();
   public static final String ALWAYS_ON_TOP = "alwaysontop";

   public PopupHolder() {
      this.setName("alwaysontop");
      this.setTransform(false);
   }

   public void addPopup(Actor popup) {
      this.toFront();
      this.addActor(popup);
      int y = 0;

      for (Actor a : this.popups) {
         y = (int)(y + (a.getHeight() - 1.0F));
      }

      popup.setY(y);
      popup.setX(this.getWidth() - popup.getWidth());
      this.popups.add(popup);
      y = (int)(y + popup.getHeight());
      this.clearActions();
      this.addAction(
         Actions.sequence(
            PixAction.moveTo((int)this.getX(), com.tann.dice.Main.height - y, 0.3F, terp),
            Actions.delay(5.0F),
            PixAction.moveTo((int)this.getX(), com.tann.dice.Main.height, 0.3F, terp),
            Actions.run(this.makeClearRunnable())
         )
      );
   }

   public void addText(String text) {
      Pixl p = new Pixl(3, 1).border(Colours.grey);
      TextWriter tw = new TextWriter(text);
      p.actor(tw).pix();
      this.addPopup(p.pix());
   }

   public void addAchievement(final Achievement achievement) {
      Group ap = AchievementPopup.make(achievement);
      ap.addListener(new TannListener() {
         @Override
         public boolean info(int button, float x, float y) {
            Sounds.playSound(Sounds.pip);
            Actor e = AchievementIconView.makeAchievementDetail(achievement);
            com.tann.dice.Main.getCurrentScreen().center(e);
            com.tann.dice.Main.getCurrentScreen().push(e, true, true, true, 0.4F);
            return false;
         }
      });
      this.addPopup(ap);
   }

   private Runnable makeClearRunnable() {
      return new Runnable() {
         @Override
         public void run() {
            PopupHolder.this.popups.clear();
            PopupHolder.this.clearChildren();
         }
      };
   }

   public void draw(Batch batch, float parentAlpha) {
      super.draw(batch, parentAlpha);
   }

   @Override
   public void onUnlock(Achievement a) {
      this.addAchievement(a);
   }
}

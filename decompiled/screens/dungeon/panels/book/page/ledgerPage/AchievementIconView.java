package com.tann.dice.screens.dungeon.panels.book.page.ledgerPage;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.tann.dice.gameplay.progress.chievo.Achievement;
import com.tann.dice.statics.sound.Sounds;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Draw;
import com.tann.dice.util.Pixl;
import com.tann.dice.util.Tann;
import com.tann.dice.util.listener.TannListener;

public class AchievementIconView extends Group {
   final Achievement achievement;
   private static final int border = 1;

   public AchievementIconView(final Achievement achievement) {
      this.achievement = achievement;
      this.setSize(18.0F, 18.0F);
      Actor a = achievement.getImage();
      this.addActor(a);
      Tann.center(a);
      this.setTransform(false);
      this.addListener(new TannListener() {
         @Override
         public boolean info(int button, float x, float y) {
            Actor e = AchievementIconView.makeAchievementDetail(achievement);
            Vector2 cursor = com.tann.dice.Main.getCursor();
            e.setPosition((int)cursor.x, (int)cursor.y);
            Sounds.playSound(Sounds.pip);
            com.tann.dice.Main.getCurrentScreen().push(e, true, true, true, 0.7F);
            Tann.center(e);
            return true;
         }
      });
   }

   public static Actor makeAchievementDetail(Achievement achievement) {
      Color border = achievement.isAchieved() ? Colours.green : Colours.grey;
      Pixl p = new Pixl(3, 3).border(border).text(achievement.getExplanelName()).row().text("[text]" + achievement.getExplanelDescription(), 105);
      Actor a = achievement.getUnlockActor();
      if (a != null && achievement.isCompletable()) {
         p.row().actor(a);
      }

      return p.pix();
   }

   public void draw(Batch batch, float parentAlpha) {
      Draw.fillActor(batch, this, Colours.dark, this.achievement.isAchieved() ? Colours.green : Colours.grey, 1);
      super.draw(batch, parentAlpha);
   }
}

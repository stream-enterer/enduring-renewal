package com.tann.dice.gameplay.phase.endPhase;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.tann.dice.statics.sound.Sounds;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Draw;
import com.tann.dice.util.ImageActor;
import com.tann.dice.util.Pixl;
import com.tann.dice.util.Tann;
import java.util.List;

public class RunEndPanel extends Group {
   final ImageActor image;
   final ImageActor template;
   final String[] sound;
   final List<Actor> rightButtons;
   final float delay;
   private Group left;
   private Group right;

   public RunEndPanel(TextureRegion mainImage, TextureRegion templateImage, String[] sound, List<Actor> leftStuff, List<Actor> rightButtons, float delay) {
      this.image = new ImageActor(mainImage);
      this.template = new ImageActor(templateImage);
      this.sound = sound;
      this.rightButtons = rightButtons;
      this.delay = delay;
      this.setSize(com.tann.dice.Main.width + 2, 60.0F);
      if (com.tann.dice.Main.isPortrait()) {
         int PORT_EXTRA_HEIGHT = 60;
         this.setWidth(100.0F);
         this.setHeight(this.image.getHeight() + PORT_EXTRA_HEIGHT * 2);
      }

      this.addActor(this.template);
      Tann.center(this.template);
      this.template.setVisible(false);
      this.template.setColor(Colours.dark);
      this.addActor(this.image);
      this.image.setColor(1.0F, 1.0F, 1.0F, 0.0F);
      Tann.center(this.image);
      Pixl l = new Pixl(2);

      for (Actor a : leftStuff) {
         l.actor(a).row();
      }

      this.left = l.pix();
      Pixl r = new Pixl(3);

      for (Actor a : rightButtons) {
         r.actor(a, 80.0F);
      }

      this.right = r.pix();
      this.addActor(this.left);
      this.addActor(this.right);
      Tann.center(this.left);
      Tann.center(this.right);
      if (com.tann.dice.Main.isPortrait()) {
         Pixl p = new Pixl(4, 5).actor(this.left).row().actor(mainImage).row().actor(this.right);
         Tann.become(this, p.pix());
      } else {
         this.left.setVisible(false);
         this.right.setVisible(false);
         this.left.setX((int)(this.getWidth() / 5.0F - this.left.getWidth() / 2.0F));
         this.right.setX((int)(this.getWidth() / 5.0F * 4.0F - this.right.getWidth() / 2.0F));
      }
   }

   public void draw(Batch batch, float parentAlpha) {
      int border = 1;
      Draw.fillActor(batch, this, Colours.dark, Colours.purple, border);
      super.draw(batch, parentAlpha);
   }

   public void slideIn(final boolean fromSave) {
      Tann.center(this);
      this.setX(com.tann.dice.Main.width);
      float durMul = fromSave ? 0.0F : 1.0F;
      this.addAction(
         Actions.sequence(
            Actions.delay(this.delay * durMul),
            Actions.moveTo(
               com.tann.dice.Main.isPortrait() ? (int)(com.tann.dice.Main.width / 2 - this.getWidth() / 2.0F) : -1.0F,
               this.getY(),
               0.5F * durMul,
               Interpolation.pow2Out
            ),
            Actions.parallel(Actions.targeting(this.image, Actions.fadeIn(1.5F * durMul)), Actions.run(new Runnable() {
               @Override
               public void run() {
                  RunEndPanel.this.template.setVisible(true);
                  if (!fromSave) {
                     Sounds.playSound(RunEndPanel.this.sound);
                  }
               }
            })),
            Actions.run(new Runnable() {
               @Override
               public void run() {
                  RunEndPanel.this.left.setVisible(true);
                  RunEndPanel.this.right.setVisible(true);
               }
            })
         )
      );
   }
}

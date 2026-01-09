package com.tann.dice.util;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.tann.dice.statics.Images;

public class LockOverlay extends Actor {
   final Group toLock;
   final boolean chains;
   private Color background = Colours.dark;

   public LockOverlay(Group toLock, boolean hasChains) {
      this.toLock = toLock;
      this.chains = hasChains;
      int gap = 1;
      this.setSize(toLock.getWidth() - 2.0F, toLock.getHeight() - 2.0F);
      this.setPosition(gap, gap);
      toLock.addActor(this);
   }

   public void setBackground(Color col) {
      this.background = col;
   }

   public void draw(Batch batch, float parentAlpha) {
      if (!this.chains) {
         batch.setColor(Colours.z_white);
         batch.draw(
            Images.padlock,
            (int)(this.getX() + this.getWidth() / 2.0F - Images.padlock.getRegionWidth() / 2),
            (int)(this.getY() + this.getHeight() / 2.0F - Images.padlock.getRegionHeight() / 2)
         );
      } else {
         Draw.fillActor(batch, this, this.background);
         batch.setColor(Colours.withAlpha(Colours.grey, this.getColor().a));
         batch.draw(
            Images.padlock,
            (int)(this.getX() + this.getWidth() / 2.0F - Images.padlock.getRegionWidth() / 2),
            (int)(this.getY() + this.getHeight() / 2.0F - Images.padlock.getRegionHeight() / 2)
         );
         Draw.drawLine(
            batch, (int)this.getX(), (int)this.getY(), (int)(this.getX() + this.getWidth() - 1.0F), (int)(this.getY() + this.getHeight() - 1.0F), 1.0F
         );
         Draw.drawLine(
            batch, (int)this.getX(), (int)(this.getY() + this.getHeight() - 1.0F), (int)(this.getX() + this.getWidth() - 1.0F), (int)this.getY(), 1.0F
         );
      }
   }
}

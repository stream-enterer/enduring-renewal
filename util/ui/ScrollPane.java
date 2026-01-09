package com.tann.dice.util.ui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane.ScrollPaneStyle;
import com.badlogic.gdx.utils.Null;
import com.tann.dice.util.FontWrapper;

public class ScrollPane extends com.badlogic.gdx.scenes.scene2d.ui.ScrollPane {
   public ScrollPane(@Null Actor actor) {
      super(actor);
   }

   public ScrollPane(@Null Actor actor, Skin skin) {
      super(actor, skin);
   }

   public ScrollPane(@Null Actor actor, Skin skin, String styleName) {
      super(actor, skin, styleName);
   }

   public ScrollPane(@Null Actor actor, ScrollPaneStyle style) {
      super(actor, style);
   }

   public void drawHD(float offsetX, float offsetY) {
      com.tann.dice.Main.self().backgroundBatch.flush();
      if (this.clipBegin(
         com.tann.dice.Main.scale * (offsetX + this.getX()),
         com.tann.dice.Main.scale * (offsetY + this.getY()),
         com.tann.dice.Main.scale * this.getWidth(),
         com.tann.dice.Main.scale * this.getHeight()
      )) {
         if (TextBox.drawHDChildren(null, this, offsetX, offsetY, false)) {
            com.tann.dice.Main.self().backgroundBatch.flush();
         }

         this.clipEnd();
      }
   }

   public void draw(Batch batch, float parentAlpha) {
      TextBox.SkipDraw++;
      super.draw(batch, parentAlpha);
      TextBox.SkipDraw--;
      if (TextBox.SkipDraw == 0 && FontWrapper.getFont().isHDFont()) {
         com.tann.dice.Main.self().stop2d(true);
         if (this.clipBegin(
            com.tann.dice.Main.scale * this.getX(),
            com.tann.dice.Main.scale * this.getY(),
            com.tann.dice.Main.scale * this.getWidth(),
            com.tann.dice.Main.scale * this.getHeight()
         )) {
            TextBox.preDraw(batch);
            TextBox.drawHDChildren(batch, this, 0.0F, 0.0F, false);
            TextBox.postDraw();
            this.clipEnd();
            com.tann.dice.Main.self().start2d(true);
         }
      }
   }
}

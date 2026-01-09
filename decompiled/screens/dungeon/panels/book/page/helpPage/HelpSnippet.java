package com.tann.dice.screens.dungeon.panels.book.page.helpPage;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Draw;
import com.tann.dice.util.ui.TextWriter;

public class HelpSnippet extends Group {
   static int WIDTH;
   TextWriter tw;
   int border = 0;
   int gap = 0;
   boolean hasBorder;

   static void setWIDTH(int width) {
      WIDTH = width;
   }

   public HelpSnippet(String content) {
      this.setTransform(false);
      this.tw = new TextWriter("[notranslate]" + getPrefix() + com.tann.dice.Main.t(content), WIDTH - (this.border + this.gap) * 2);
      this.init();
   }

   public HelpSnippet(String content, TextureRegion img) {
      this.setTransform(false);
      this.tw = new TextWriter("[notranslate]" + getPrefix() + com.tann.dice.Main.t(content), img, WIDTH - (this.border + this.gap) * 2);
      this.init();
   }

   private void init() {
      this.addActor(this.tw);
      this.tw.setPosition(this.border + this.gap, this.border + this.gap);
      this.setSize(this.tw.getWidth(), this.tw.getHeight() + this.border * 1 + this.gap * 2);
   }

   public void setBorder(boolean hasBorder) {
      this.hasBorder = hasBorder;
   }

   public void draw(Batch batch, float parentAlpha) {
      if (this.hasBorder) {
         Draw.fillActor(batch, this, Colours.grey);
         batch.setColor(Colours.dark);
         Draw.fillRectangle(batch, this.getX() + this.border, this.getY() + this.border, this.getWidth() - this.border * 2, this.getHeight() - this.border * 1);
      }

      super.draw(batch, parentAlpha);
   }

   private static String getPrefix() {
      return "[text]- ";
   }
}

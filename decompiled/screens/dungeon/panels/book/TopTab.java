package com.tann.dice.screens.dungeon.panels.book;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Draw;
import com.tann.dice.util.Tann;
import com.tann.dice.util.ui.TextWriter;

public class TopTab extends Group {
   private static final Color FOCUSED_FILL = Colours.purple;
   private static final Color FOCUSED_BG = Colours.shiftedTowards(Colours.purple, Colours.dark, 0.7F).cpy();
   private static final Color TEXT_HIGHLIGHT_COL = Colours.light;
   final Object identifier;
   TextWriter tw;
   final String name;
   boolean focused = false;

   public TopTab(Object identifier, String title) {
      this(identifier, title, (int)new TextWriter(title).getWidth());
   }

   public TopTab(Object identifier, String title, int width) {
      this.setTransform(false);
      this.identifier = identifier;
      this.name = title;
      this.tw = new TextWriter(this.name);
      this.addActor(this.tw);
      this.setSize(width, this.tw.getHeight());
   }

   public String getTabName() {
      return this.name;
   }

   protected void sizeChanged() {
      if (this.tw != null) {
         Tann.center(this.tw);
      }
   }

   public void draw(Batch batch, float parentAlpha) {
      if (this.focused) {
         Draw.fillActor(batch, this, FOCUSED_BG, FOCUSED_FILL, 1);
      } else {
         int gap = 1;
         batch.setColor(Colours.dark);
         Draw.fillRectangle(batch, this.getX() + gap, this.getY() + gap, this.getWidth() - gap * 2, this.getHeight() - gap * 2);
      }

      super.draw(batch, parentAlpha);
   }

   public void focusedPage(Object chk) {
      this.setFocused(this.identifier == chk);
   }

   public void setFocused(boolean b) {
      this.focused = b;
      this.tw.setOverrideColour(null);
      if (this.focused) {
         this.tw.setOverrideColour(TEXT_HIGHLIGHT_COL);
      }
   }

   public Object getIdentifier() {
      return this.identifier;
   }
}

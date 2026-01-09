package com.tann.dice.screens.dungeon.panels.book.page.helpPage;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.tann.dice.screens.dungeon.panels.book.page.BookPage;
import com.tann.dice.util.Pixl;
import com.tann.dice.util.Tann;
import com.tann.dice.util.TannStageUtils;
import com.tann.dice.util.ui.ScrollPane;

public class ContentPanel extends ScrollPane {
   final int maxHeight;
   final int maxWidth;
   boolean leftAligned;

   public ContentPanel(int width, int height, BookPage bookPage) {
      super(new Actor(), Tann.makeScrollpaneStyle(true));
      this.leftAligned = bookPage instanceof HelpPage;
      this.setTransform(false);
      this.maxHeight = height;
      this.maxWidth = width;
      this.setScrollbarsOnTop(true);
      this.setTransform(false);
      this.setContent(null);
      this.setupFadeScrollBars(0.25F, 0.65F);
      this.setOverscroll(false, false);
   }

   private Group makeGroup(Actor snippets) {
      Group g = this.makeGroupBasic(snippets);
      int gap = (int)((this.getWidth() - g.getWidth()) / 2.0F);
      if (gap > 0) {
         Pixl p = new Pixl().row(BookPage.getPadding());
         if (this.leftAligned) {
            int lag = 2;
            p.gap(lag).actor(g).gap(gap * 2 - lag);
         } else {
            p.gap(gap).actor(g).gap(gap);
         }

         return p.row(BookPage.getPadding()).pix();
      } else {
         return g;
      }
   }

   private Group makeGroupBasic(Actor snippets) {
      Pixl p = new Pixl(0);
      p.actor(snippets).row();
      Group g = p.pix(8);
      g.setTransform(true);
      return g;
   }

   public void setContent(Actor a) {
      TannStageUtils.ensureSafeForScrollpane(a);
      this.clearChildren();
      this.setWidth(this.maxWidth);
      Group g = this.makeGroup(a);
      if (g.getHeight() > this.maxHeight - BookPage.getPadding()) {
         int topGap = 1;
         a = new Pixl().row(topGap).actor(a).row(2).pix();
      }

      g = this.makeGroup(a);
      this.setHeight(Math.min((float)this.maxHeight, g.getHeight()));
      this.setActor(g);
      if (this.leftAligned) {
         g.setX(-50.0F);
      }
   }
}

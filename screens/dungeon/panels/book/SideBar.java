package com.tann.dice.screens.dungeon.panels.book;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.tann.dice.statics.Images;
import com.tann.dice.util.Colours;
import com.tann.dice.util.ImageActor;
import com.tann.dice.util.Rectactor;
import com.tann.dice.util.ui.TextEfficientGroup;
import java.util.ArrayList;
import java.util.List;

public class SideBar extends TextEfficientGroup {
   private List<TopTab> items = new ArrayList<>();
   public static final int DEFAULT_HEIGHT = 18;
   public static final int LANDSCAPE_GAP_PER_SIDEBAR = 7;
   boolean drawBorder;

   public SideBar() {
      this.setTransform(false);
      this.setTouchable(Touchable.childrenOnly);
   }

   public static int getSideWidth() {
      return com.tann.dice.Main.self().translator.longMenuItems() ? 62 : 45;
   }

   public void addItem(TopTab item) {
      this.items.add(item);
   }

   public List<TopTab> getItems() {
      return this.items;
   }

   @Override
   public void draw(Batch batch, float parentAlpha) {
      if (this.drawBorder) {
      }

      super.draw(batch, parentAlpha);
   }

   public void highlightAssoc(Object identifier) {
      for (TopTab item : this.getItems()) {
         item.focusedPage(identifier);
      }
   }

   public void highlightItem(TopTab sbi) {
      for (TopTab item : this.getItems()) {
         item.setFocused(sbi == item);
      }
   }

   public String getName() {
      return super.getName();
   }

   public void layoutPortrait(int width, int height) {
      int widthPerButton = this.gwpbh(this.items.size(), width);
      int currentY = 0;
      int currentX = 0;
      int rows = 1;
      if (widthPerButton < 40) {
         widthPerButton = this.gwpbh((int)Math.ceil(this.items.size() / 2.0F), width);
         currentY += height - 1;
         rows = 2;
      }

      List<TopTab> tabs = new ArrayList<>();
      tabs.addAll(this.items);

      for (int i = 0; i < this.items.size(); i++) {
         TopTab it = this.items.get(i);
         it.setSize(widthPerButton, height);
         this.addActor(it);
         if (currentX + widthPerButton > width) {
            currentX = 0;
            currentY -= height - 1;
         }

         it.setPosition(currentX, currentY);
         currentX += widthPerButton - 1;
      }

      int perRow = (int)Math.ceil((float)this.items.size() / rows);
      int remainder = perRow * rows - this.items.size();

      for (int i = 0; i < remainder; i++) {
         TopTab it = new TopTab("abc", "[grey]x");
         tabs.add(it);
         it.setSize(widthPerButton, height);
         this.addActor(it);
         it.setPosition(currentX, currentY);
         currentX += widthPerButton - 1;
      }

      int extraPixels = width - widthPerButton * perRow + (perRow - 1);
      if (extraPixels > 0) {
         for (int i = perRow - 1; i < tabs.size(); i += perRow) {
            TopTab tt = tabs.get(i);
            tt.setWidth(tt.getWidth() + extraPixels);
         }
      }

      this.setSize(width, height * rows - (rows - 1) * 1);
   }

   private int gwpbh(int size, int width) {
      return Math.min((width + size - 1) / size, 999);
   }

   public void layoutLandscape(int height) {
      height -= 7;
      int maxh = 5000;
      int heightPerButton = Math.min((height + this.items.size() - 1) / this.items.size(), 5000);
      int extraHeight = height - heightPerButton * this.items.size() + (this.items.size() - 1);
      int currentY = 0;
      float extraFactor = 0.0F;
      if (this.items.size() == 3) {
         if (com.tann.dice.Main.self().translator.longMenuItems()) {
            Rectactor ra1 = new Rectactor(getSideWidth(), 1, Colours.transparent, Colours.purple);
            this.addActor(ra1);
            ra1.setPosition(0.0F, -6.0F);
            Rectactor ra2 = new Rectactor(getSideWidth(), 1, Colours.transparent, Colours.purple);
            this.addActor(ra2);
            ra2.setPosition(0.0F, -8.0F);
         } else {
            ImageActor ia = new ImageActor(Images.noChecker);
            ia.setColor(Colours.withAlpha(Colours.purple, 0.2F).cpy());
            this.addActor(ia);
            ia.setPosition(1.0F, -ia.getHeight());
         }
      }

      for (int i = this.items.size() - 1; i >= 0; i--) {
         TopTab it = this.items.get(i);
         it.setSize(getSideWidth(), heightPerButton);
         float naf = (float)(this.items.size() - i) / this.items.size() * extraHeight;
         if ((int)naf != (int)extraFactor) {
            it.setHeight(it.getHeight() + 1.0F);
         }

         extraFactor = naf;
         this.addActor(it);
         it.setY(currentY);
         currentY = (int)(currentY + (it.getHeight() - 1.0F));
      }

      int hh = heightPerButton * this.items.size() - (this.items.size() - 1);
      hh = currentY + 1;
      this.setSize(getSideWidth(), hh);
   }

   public void setDrawBorder(boolean b) {
      this.drawBorder = b;
   }
}

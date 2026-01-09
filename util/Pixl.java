package com.tann.dice.util;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.tann.dice.util.ui.TextEfficientGroup;
import com.tann.dice.util.ui.TextWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Pixl {
   final Group g;
   int forceWidth = -1;
   List<Pixl.Row> rows = new ArrayList<>();
   Pixl.Row currentRow;
   final int padding;
   final int baseGap;
   Color border_inner;
   Color border_outer;
   int border_size = -1;
   boolean pixed = false;

   public Pixl() {
      this(0);
   }

   public Pixl(int baseGap) {
      this(baseGap, 0);
   }

   public Pixl(int baseGap, int padding) {
      this.g = new TextEfficientGroup();
      this.baseGap = baseGap;
      this.padding = padding;
      this.currentRow = new Pixl.Row(0);
      if (padding != 0) {
         this.row(padding);
      }
   }

   public Pixl row() {
      this.row(this.baseGap);
      return this;
   }

   public Pixl forceWidth(int width) {
      this.forceWidth = width;
      return this;
   }

   public Pixl row(int gap) {
      if (this.currentRow.isTotallyBlank()) {
         this.currentRow.aboveRowGap = gap;
         return this;
      } else {
         this.currentRow.finish();
         this.rows.add(this.currentRow);
         this.currentRow = new Pixl.Row(gap);
         return this;
      }
   }

   public Pixl gap(int gap) {
      this.currentRow.addGap(gap);
      return this;
   }

   public Pixl border(Color border) {
      if (border == null) {
         throw new RuntimeException();
      } else {
         return this.border(Colours.dark, border, 1);
      }
   }

   public Pixl fill(Color fill) {
      if (fill == null) {
         throw new RuntimeException();
      } else {
         return this.border(fill, null, 0);
      }
   }

   public Pixl border(Color inner, Color border, int size) {
      this.border_inner = inner;
      this.border_outer = border;
      this.border_size = size;
      return this;
   }

   public Pixl flatBorder(Color c) {
      return this.border(c, Colours.dark, 0);
   }

   public Pixl actor(TextureRegion tr) {
      if (tr == null) {
         return this;
      } else {
         this.currentRow.addActor(new ImageActor(tr));
         return this;
      }
   }

   public Pixl actor(Pixl p) {
      if (p == null) {
         return this;
      } else {
         this.currentRow.addActor(p.pix());
         return this;
      }
   }

   public Pixl actor(Actor a) {
      if (a == null) {
         return this;
      } else {
         this.currentRow.addActor(a);
         return this;
      }
   }

   public Pixl actor(Actor a, float width) {
      return a == null ? this : this.actor(a, (int)width, this.baseGap);
   }

   public Pixl actor(Actor a, int width, int rowGap) {
      if (a == null) {
         return this;
      } else {
         if (!this.canHandle(a, width) && !this.currentRow.isTotallyBlank()) {
            this.row(rowGap);
         }

         this.currentRow.addActor(a);
         return this;
      }
   }

   public Group pix() {
      return this.pix(1);
   }

   public Group pix(int align) {
      if (this.pixed) {
         TannLog.error("eep, already pixed");
         return makeErrorActor("pix");
      } else {
         if (this.padding != 0) {
            this.row(this.padding);
         }

         if (!this.currentRow.isTotallyBlank()) {
            this.row();
         }

         this.currentRow.finish();
         this.rows.add(this.currentRow);
         this.pixed = true;
         int maxWidth = 0;
         int totalHeight = 0;

         for (int i = 0; i < this.rows.size(); i++) {
            Pixl.Row r = this.rows.get(i);
            totalHeight += r.getHeight();
            if (i == this.rows.size() - 1) {
               totalHeight -= r.aboveRowGap;
            }

            maxWidth = Math.max(r.getWidth(), maxWidth);
         }

         if (this.forceWidth != -1) {
            maxWidth = this.forceWidth;
         }

         this.g.setSize(maxWidth, totalHeight);
         int currentY = (int)this.g.getHeight();

         for (int i = 0; i < this.rows.size(); i++) {
            Pixl.Row r = this.rows.get(i);
            currentY -= r.aboveRowGap;
            int usedWidth = 0;

            for (int elementIndex = 0; elementIndex < r.elementList.size(); elementIndex++) {
               Pixl.Element e = r.elementList.get(elementIndex);
               if (e.a != null) {
                  usedWidth += (int)e.a.getWidth();
               } else {
                  usedWidth += e.gap;
               }
            }

            int currentX;
            if ((align & 8) != 0) {
               currentX = 0;
            } else if ((align & 16) != 0) {
               currentX = (int)(this.g.getWidth() - usedWidth);
            } else {
               currentX = (int)((this.g.getWidth() - usedWidth) / 2.0F);
            }

            int rowHeight = r.getHeight();
            int rowHeightExcludingAboveGap = rowHeight - r.aboveRowGap;

            for (int elementIndexx = 0; elementIndexx < r.elementList.size(); elementIndexx++) {
               Pixl.Element e = r.elementList.get(elementIndexx);
               if (e.a != null) {
                  int y;
                  if ((align & 2) != 0) {
                     y = (int)(currentY - e.a.getHeight());
                  } else if ((align & 4) != 0) {
                     y = currentY - rowHeightExcludingAboveGap;
                  } else {
                     y = (int)(currentY - rowHeightExcludingAboveGap / 2.0F - e.a.getHeight() / 2.0F);
                  }

                  this.g.addActor(e.a);
                  e.a.setPosition(currentX, y);
                  currentX = (int)(currentX + e.a.getWidth());
               } else {
                  currentX += e.gap;
               }
            }

            currentY -= rowHeight;
            currentY += r.aboveRowGap;
         }

         if (this.border_inner != null || this.border_outer != null) {
            Actor a = new Rectactor((int)this.g.getWidth(), (int)this.g.getHeight(), this.border_size, this.border_outer, this.border_inner);
            this.g.addActor(a);
            a.toBack();
         }

         return this.g;
      }
   }

   public static Group makeErrorActor(String tag) {
      return new Pixl(3, 3).border(Colours.pink).text("[b]error oops[n]" + tag).pix();
   }

   public Pixl text(String s) {
      return this.text(s, this.forceWidth == -1 ? 9999 : this.forceWidth - this.baseGap * 2);
   }

   public Pixl text(int i) {
      return this.text(i + "");
   }

   public Pixl text(String s, int forceWidth) {
      return forceWidth == -1 ? this.actor(new TextWriter(s)) : this.actor(new TextWriter(s, forceWidth - this.baseGap * 2));
   }

   public Pixl tannText(String s) {
      return this.forceWidth == -1
         ? this.actor(TextWriter.withTannFontOverride(s))
         : this.actor(TextWriter.withTannFontOverride(s, this.forceWidth - this.baseGap * 2));
   }

   public Pixl image(TextureRegion image) {
      return this.actor(new ImageActor(image));
   }

   public Pixl image(TextureRegion image, int max) {
      return this.actor(new ImageActor(image), max);
   }

   public Pixl image(TextureRegion image, boolean flipped) {
      ImageActor ia = new ImageActor(image);
      if (flipped) {
         ia.setXFlipped(true);
      }

      return this.actor(ia);
   }

   public Pixl image(TextureRegion image, Color tint) {
      Actor a = new ImageActor(image);
      a.setColor(tint);
      return this.actor(a);
   }

   public Pixl listActor(List<Actor> actors, int gap) {
      return this.listActor(actors, gap, 9999);
   }

   public Pixl listActor(List<Actor> actors, int gap, int maxWidth) {
      return this.listActor(actors, gap, maxWidth, 2);
   }

   public Pixl listActor(List<Actor> actors, int gap, int maxWidth, int align) {
      Pixl p = new Pixl(gap);

      for (int i = 0; i < actors.size(); i++) {
         Actor a = actors.get(i);
         if (a != null) {
            p.actor(a, maxWidth);
         }
      }

      return this.actor(p.pix(align));
   }

   public Pixl listActor(int maxWidth, Actor... actors) {
      return this.listActor(maxWidth, Arrays.asList(actors));
   }

   public Pixl listActor(int maxWidth, int align, Actor... actors) {
      return this.listActor(Arrays.asList(actors), this.baseGap, maxWidth, align);
   }

   public Pixl listActor(int maxWidth, List<Actor> actors) {
      return this.listActor(actors, this.baseGap, maxWidth);
   }

   public void cancelRowGap() {
      this.currentRow.removeTrailingGaps();
      this.row(0);
   }

   public void multiText(List<String> newDescLines, int maxWidth) {
      for (int i = 0; i < newDescLines.size(); i++) {
         String s = newDescLines.get(i);
         this.text(s, maxWidth);
         if (i < newDescLines.size() - 1) {
            this.row();
         }
      }
   }

   public Pixl rowedActors(List<? extends Actor> items) {
      for (int i = 0; i < items.size(); i++) {
         Actor a = items.get(i);
         if (a != null) {
            this.actor(a);
            if (i < items.size() - 1) {
               this.row();
            }
         }
      }

      return this;
   }

   public Pixl actorRowIf(boolean shouldAdd, Actor a) {
      return !shouldAdd ? this : this.actor(a).row();
   }

   public Pixl rowActorIf(boolean shouldAdd, Actor a) {
      return !shouldAdd ? this : this.row().actor(a);
   }

   private int getCurrentSize() {
      int w = 0;

      for (Pixl.Element e : this.currentRow.elementList) {
         w += e.getWidth();
      }

      return w;
   }

   public boolean canHandle(Actor a, int width) {
      return this.getCurrentSize() + this.baseGap + a.getWidth() <= width;
   }

   static class Element {
      int gap;
      Actor a;

      public Element(Actor a) {
         this.a = a;
      }

      public Element(int gap) {
         this.gap = gap;
      }

      public int getWidth() {
         return this.a == null ? this.gap : (int)this.a.getWidth();
      }
   }

   class Row {
      List<Pixl.Element> elementList = new ArrayList<>();
      int aboveRowGap;

      public Row(int aboveRowGap) {
         this.aboveRowGap = aboveRowGap;
         if (Pixl.this.padding != 0) {
            this.addGap(Pixl.this.padding);
         }
      }

      void addActor(Actor a) {
         if (a == null) {
            throw new NullPointerException("adding a null actor");
         } else {
            if (this.elementList.size() > 0 && this.elementList.get(this.elementList.size() - 1).a != null) {
               this.addGap(Pixl.this.baseGap);
            }

            this.elementList.add(new Pixl.Element(a));
         }
      }

      void addGap(int gap) {
         this.elementList.add(new Pixl.Element(gap));
      }

      public int getWidth() {
         int total = 0;

         for (int elementIndex = 0; elementIndex < this.elementList.size(); elementIndex++) {
            Pixl.Element e = this.elementList.get(elementIndex);
            total += e.getWidth();
         }

         return total;
      }

      public int getHeight() {
         int maxActorHeight = 0;

         for (int elementIndex = 0; elementIndex < this.elementList.size(); elementIndex++) {
            Pixl.Element e = this.elementList.get(elementIndex);
            maxActorHeight = (int)Math.max((float)maxActorHeight, e.a == null ? 0.0F : e.a.getHeight());
         }

         return maxActorHeight + this.aboveRowGap;
      }

      public void removeTrailingGaps() {
         for (int i = this.elementList.size() - 1; i >= 0; i--) {
            Pixl.Element e = this.elementList.get(i);
            if (e.a != null) {
               return;
            }

            this.elementList.remove(e);
         }
      }

      public void finish() {
         if (Pixl.this.padding != 0) {
            Pixl.this.gap(Pixl.this.padding);
         }
      }

      public boolean isTotallyBlank() {
         return this.elementList.isEmpty() && this.aboveRowGap == 0;
      }
   }
}

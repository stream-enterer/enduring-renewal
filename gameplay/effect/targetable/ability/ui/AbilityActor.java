package com.tann.dice.gameplay.effect.targetable.ability.ui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.tann.dice.gameplay.effect.targetable.ability.Ability;
import com.tann.dice.gameplay.fightLog.Snapshot;
import com.tann.dice.screens.dungeon.DungeonScreen;
import com.tann.dice.statics.Images;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Draw;
import com.tann.dice.util.ImageActor;
import com.tann.dice.util.Pixl;
import com.tann.dice.util.Tann;
import com.tann.dice.util.TannFont;
import com.tann.dice.util.ui.Button;
import com.tann.dice.util.ui.TextWriter;
import java.util.List;

public class AbilityActor extends Group {
   public static final int HEIGHT = 29;
   public static final int MAX_WIDTH = 48;
   static final int PIC_SIZE = 10;
   static final int BORDER_GAP = 0;
   static final int INNER_GAP = 2;
   static final int OUTER_GAP = 2;
   static final int TEXT_GAP = 0;
   static final int PIC_EXTRA_PIXELS = 0;
   final Ability ability;
   Snapshot presentSnapshot;
   boolean dead;
   boolean showText;

   public static boolean canDisplay(Ability ability, int width) {
      int textWidth = TannFont.font.getWidth(com.tann.dice.Main.t(ability.getTitle()));
      int textAvailable = width - 14;
      return textWidth <= textAvailable;
   }

   public AbilityActor(Ability ability, int width, Snapshot present) {
      this.setTransform(false);
      this.setPresentSnapshot(present);
      this.ability = ability;
      this.reset(width);
      this.setTransform(false);
   }

   public void reset(int width) {
      this.clearChildren();
      this.setSize(width, 29.0F);
      ImageActor pic = new ImageActor(this.ability.getImage());
      String text = this.ability.getTitle();
      int textWidth = TannFont.font.getWidth(com.tann.dice.Main.t(text));
      int textAvailable = (int)(this.getWidth() - 14.0F);
      Pixl p = new Pixl(0);
      if (this.ability.useImage()) {
         p.actor(pic);
         if (this.showText) {
            p.gap((textAvailable - textWidth) / 3);
            p.text(TextWriter.getTag(this.ability.getIdCol()) + text);
         }
      } else {
         String tdt = text;
         if (!this.showText) {
            tdt = Tann.makeEllipses(text, 5);
         }

         p.text(TextWriter.getTag(this.ability.getIdCol()) + tdt);
      }

      Group g = p.pix();
      this.addActor(g);
      int TOP_PART_HEIGHT = 14;
      g.setPosition((int)(this.getWidth() / 2.0F - g.getWidth() / 2.0F), this.getHeight() - g.getHeight() / 2.0F - 7.0F);
      int outerGap = 2;
      int totalWidth = (int)(this.getWidth() - outerGap * 2);
      List<Actor> costActors = this.ability.getCostActors(this.presentSnapshot, totalWidth);
      textAvailable = Tann.totalWidth(costActors);
      int totalGap = totalWidth - 4 - textAvailable;
      int perGap = Math.round(Math.min(5.0F, totalGap / (costActors.size() + 2.0F)));
      Pixl costPix = new Pixl(perGap);

      for (int i = 0; i < costActors.size(); i++) {
         Actor a = costActors.get(i);
         costPix.actor(a);
      }

      Actor ca = costPix.pix();
      this.addActor(ca);
      Tann.center(ca);
      int costSize = 16;
      ca.setY((int)((costSize - ca.getHeight()) / 2.0F));
   }

   public void setPresentSnapshot(Snapshot presentSnapshot) {
      this.presentSnapshot = presentSnapshot;
   }

   public void draw(Batch batch, float parentAlpha) {
      batch.setColor(Colours.dark);
      Draw.fillRectangle(batch, this.getX() + 1.0F, this.getY() + 1.0F, this.getWidth() - 2.0F, this.getHeight() - 2.0F);
      boolean faded = this.isDead();
      if (!faded) {
         Actor parent = this.getParent();
         if (parent != null && parent instanceof AbilityHolder) {
            AbilityHolder sh = (AbilityHolder)parent;
            if (sh.tuckState == AbilityHolder.TuckState.Tucked) {
               faded = true;
            }
         }
      }

      batch.setColor(this.ability.getCol());
      if (DungeonScreen.get() != null && DungeonScreen.get().targetingManager.getSelectedTargetable() == this.ability) {
         batch.setColor(Colours.light);
      }

      Button.ninePatch.draw(batch, this.getX(), this.getY(), this.getWidth(), this.getHeight());
      Draw.fillRectangle(batch, this.getX(), this.getY() + this.getHeight() - 2.0F - 2.0F - 10.0F - 0.0F, this.getWidth(), 1.0F);
      super.draw(batch, parentAlpha);
      if (faded) {
         batch.setColor(Colours.withAlpha(Colours.dark, 0.45F));
         Button.ninePatch.draw(batch, this.getX(), this.getY(), this.getWidth(), this.getHeight());
         Draw.fillRectangle(batch, this.getX() + 1.0F, this.getY() + 1.0F, this.getWidth() - 2.0F, this.getHeight() - 2.0F);
      }

      if (this.isDead()) {
         batch.setColor(Colours.red);
         TextureRegion sk = Images.skull;
         batch.draw(
            sk, this.getX() + (int)(this.getWidth() / 2.0F - sk.getRegionWidth() / 2), this.getY() + (int)(this.getHeight() / 2.0F - sk.getRegionHeight() / 2)
         );
      }
   }

   protected boolean isDead() {
      return this.dead;
   }

   public void setDead(Boolean b) {
      this.dead = b;
   }

   public void setShowText(boolean showText) {
      this.showText = showText;
   }
}

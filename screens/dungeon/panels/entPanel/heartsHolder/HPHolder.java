package com.tann.dice.screens.dungeon.panels.entPanel.heartsHolder;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.tann.dice.gameplay.content.ent.Ent;
import com.tann.dice.gameplay.content.ent.EntSize;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.fightLog.FightLog;
import com.tann.dice.gameplay.save.settings.option.OptionUtils;
import com.tann.dice.gameplay.trigger.personal.Personal;
import com.tann.dice.gameplay.trigger.personal.specialPips.SpecialHp;
import com.tann.dice.screens.dungeon.DungeonScreen;
import com.tann.dice.statics.Images;
import com.tann.dice.util.Colours;
import com.tann.dice.util.TannFont;

public class HPHolder extends Group {
   final Ent ent;
   final HPHolder.PipSize pipSize;
   final PipData hd = new PipData();
   EntState visualState;
   EntState futureState;

   public HPHolder(Ent e) {
      EntState visualState = e.getState(FightLog.Temporality.Visual);
      this.pipSize = HPHolder.PipSize.calculate(e.getSize(), visualState.getMaxHp());
      this.setTransform(false);
      this.ent = e;
      int actualColumns = Math.min(this.pipSize.getCol(e.getSize()), visualState.getMaxHp());
      int actualRows = (visualState.getMaxHp() - 1) / this.pipSize.getCol(e.getSize()) + 1;
      this.setWidth(actualColumns * (this.pipSize.width + this.pipSize.gap) - this.pipSize.gap);
      this.setHeight(actualRows * (this.pipSize.height + this.pipSize.gap) - this.pipSize.gap);
      this.newState(e.getState(FightLog.Temporality.Future), FightLog.Temporality.Future);
      this.newState(e.getState(FightLog.Temporality.Visual), FightLog.Temporality.Visual);
   }

   private void setupStates() {
      if (this.futureState != null
         && this.visualState != null
         && (
            !(com.tann.dice.Main.getCurrentScreen() instanceof DungeonScreen)
               || this.futureState.getSnapshot() == null
               || this.visualState.getSnapshot() == null
               || this.visualState.getSnapshot().getTurn() == this.futureState.getSnapshot().getTurn()
         )) {
         this.hd.reset();
         int purples = this.visualState.getMaxHp() - Math.max(0, this.visualState.getHp());
         int yellows = Math.min(this.visualState.getHp(), this.futureState.getBlockableDamageTaken() - this.visualState.getBlockableDamageTaken());
         int greens = Math.min(this.visualState.getHp() - yellows, this.futureState.getPoisonDamageTaken(true) - this.visualState.getPoisonDamageTaken(true));
         int reds = this.visualState.getHp() - yellows - greens;
         this.hd.set(Math.max(0, reds), Math.max(0, greens), Math.max(0, yellows), Math.max(0, purples));
         int minHp = this.visualState.getMinTriggerPipHp();

         for (Personal t : this.visualState.getActivePersonals()) {
            if (t instanceof SpecialHp) {
               SpecialHp tr = (SpecialHp)t;

               for (int i : tr.getPips(this.visualState.getMaxHp())) {
                  if (i < minHp && i < this.visualState.getMaxHp()) {
                     this.hd.addSpecialPip(i, tr.getPipTannple(true));
                  }
               }
            }
         }
      }
   }

   public void draw(Batch batch, float parentAlpha) {
      this.drawFull(batch, (int)this.getX(), (int)(this.getY() + this.getHeight()), this.hd);
      this.drawOverkill(batch);
      super.draw(batch, parentAlpha);
   }

   private void drawFull(Batch batch, int x, int y, PipData hd) {
      int heartGap = this.pipSize.gap;
      int columns = this.pipSize.getCol(this.ent.getSize());
      int heartHeight = this.pipSize.height;
      int heartWidth = this.pipSize.width;

      for (int i = 0; i < hd.max; i++) {
         Color col = hd.getCol(i);
         if (i % columns == 0 && i != 0) {
            y -= heartHeight + heartGap;
            x = (int)this.getX();
         }

         TextureRegion tr = hd.getImage(i, this.pipSize);
         batch.setColor(col);
         batch.draw(tr, x, y - tr.getRegionHeight());
         x += heartWidth + heartGap;
      }
   }

   private void drawOverkill(Batch batch) {
      if (this.visualState != null && this.futureState != null && !this.visualState.isDead()) {
         int overkill = Math.max(0, -this.futureState.getHp());
         int poison = this.futureState.getPoisonDamageTaken(true);
         int poisonOverkill = Math.min(poison, overkill);
         int regularOverkill = overkill - poisonOverkill;
         if (regularOverkill > 0) {
            this.drawOverkill(batch, regularOverkill, false, 0);
         }

         if (poisonOverkill > 0) {
            this.drawOverkill(batch, poisonOverkill, true, regularOverkill > 0 ? -(TannFont.font.getHeight() + 1) : 0);
         }
      }
   }

   private void drawOverkill(Batch batch, int amount, boolean poison, int bonusY) {
      if (poison) {
         batch.setColor(OptionUtils.poisonCol());
      } else {
         batch.setColor(Colours.yellow);
      }

      String overkillText = "+" + amount;
      int textWidth = TannFont.font.getWidth(overkillText);
      int textStart = (int)(this.ent.isPlayer() ? this.getX() - 1.0F - textWidth : this.getX() + this.getWidth() + 1.0F);
      TannFont.font.drawString(batch, overkillText, textStart, this.getY() + this.getHeight() - TannFont.font.getHeight() + bonusY, false);
   }

   public boolean smallPips() {
      return this.pipSize != HPHolder.PipSize.normal;
   }

   public void addDamageFlibs(EntState oldState, EntState newState) {
      if (this.pipSize != HPHolder.PipSize.pixel) {
         if (!DungeonScreen.get().isLoading()) {
            int amtPoison = newState.getPoisonDamageTaken() - oldState.getPoisonDamageTaken();
            int amount = -(newState.getHp() - oldState.getHp());
            if (amount <= 100) {
               for (int i = 0; i < amount; i++) {
                  int heartIndex = oldState.getHp() - i - 1;
                  PipticleSwipe sa = new PipticleSwipe(i < amtPoison);
                  this.setHearticlePosition(sa, heartIndex);
                  this.addActor(sa);
               }
            }
         }
      }
   }

   public void addHeartFlibs(EntState oldState, EntState newState) {
      if (this.pipSize != HPHolder.PipSize.pixel) {
         if (DungeonScreen.get() == null || !DungeonScreen.get().isLoading()) {
            int hp = oldState.getHp();
            int amount = newState.getHp() - oldState.getHp();
            if (amount <= 100) {
               for (int i = 0; i < amount; i++) {
                  int heartIndex = hp + i;
                  PipticleHeart hh = new PipticleHeart(this.pipSize);
                  this.setHearticlePosition(hh, heartIndex);
                  this.addActor(hh);
               }
            }
         }
      }
   }

   private void setHearticlePosition(Pipticle h, int heartIndex) {
      if (heartIndex < 0) {
         h.setPosition(-500.0F, -500.0F);
      } else {
         int heartGap = this.pipSize.gap;
         int columns = this.pipSize.getCol(this.ent.getSize());
         int heartHeight = this.pipSize.height;
         int heartWidth = this.pipSize.width;
         int colIndex = heartIndex % columns;
         int x = colIndex * heartWidth + heartGap * (colIndex - 1);
         int y = (int)(this.getHeight() - heartHeight - heartIndex / columns * (heartHeight + heartGap)) - 1;
         h.setPosition(x, y);
      }
   }

   public void newState(EntState state, FightLog.Temporality temporality) {
      switch (temporality) {
         case Visual:
            this.visualState = state;
            this.setupStates();
            break;
         case Future:
            this.futureState = state;
            this.setupStates();
      }
   }

   public static enum PipSize {
      normal(Images.hp, 3, 3, 1, 5, 5, 5, 10, 1, 3, 4, 5),
      little(Images.hp_small, 2, 2, 1, 10, 10, 10, 10, 2, 4, 6, 7),
      pixel(Images.hp_tallPixel, 1, 2, 0, 25, 25, 25, 40, 3, 5, 7, 40);

      final TextureRegion img;
      final int width;
      final int height;
      final int gap;
      final int colSmall;
      final int colReg;
      final int colBig;
      final int colHuge;
      final int rowSmall;
      final int rowReg;
      final int rowBig;
      final int rowHuge;

      private PipSize(
         TextureRegion img,
         int width,
         int height,
         int gap,
         int colSmall,
         int colReg,
         int colBig,
         int colHuge,
         int rowSmall,
         int rowReg,
         int rowBig,
         int rowHuge
      ) {
         this.img = img;
         this.width = width;
         this.height = height;
         this.gap = gap;
         this.colSmall = colSmall;
         this.colReg = colReg;
         this.colBig = colBig;
         this.colHuge = colHuge;
         this.rowSmall = rowSmall;
         this.rowReg = rowReg;
         this.rowBig = rowBig;
         this.rowHuge = rowHuge;
      }

      private int getMax(EntSize size) {
         return this.getCol(size) * this.getRow(size);
      }

      private int getRow(EntSize size) {
         switch (size) {
            case small:
               return this.rowSmall;
            case reg:
               return this.rowReg;
            case big:
               return this.rowBig;
            case huge:
               return this.rowHuge;
            default:
               throw new RuntimeException();
         }
      }

      private int getCol(EntSize size) {
         switch (size) {
            case small:
               return this.colSmall;
            case reg:
               return this.colReg;
            case big:
               return this.colBig;
            case huge:
               return this.colHuge;
            default:
               throw new RuntimeException();
         }
      }

      public static HPHolder.PipSize calculate(EntSize size, int maxHp) {
         for (HPHolder.PipSize value : values()) {
            if (maxHp <= value.getMax(size)) {
               return value;
            }
         }

         return pixel;
      }
   }
}

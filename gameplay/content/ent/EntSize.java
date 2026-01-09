package com.tann.dice.gameplay.content.ent;

import com.tann.dice.gameplay.content.ent.die.side.EntSide;
import com.tann.dice.gameplay.content.ent.die.side.blob.ESB;
import com.tann.dice.gameplay.content.ent.die.side.blob.EntSidesBlobBig;
import com.tann.dice.gameplay.content.ent.die.side.blob.EntSidesBlobHuge;
import com.tann.dice.gameplay.content.ent.die.side.blob.EntSidesBlobSmall;
import com.tann.dice.gameplay.content.ent.type.EntType;
import com.tann.dice.gameplay.content.ent.type.lib.MonsterTypeLib;

public enum EntSize {
   small(12, 16, 3),
   reg(16, 24, 4),
   big(22, 30, 5),
   huge(28, 64, 11);

   private final int pixels;
   private final int panelHeight;
   private final int reinforceSize;
   private Ent ex;

   private EntSize(int pixels, int panelHeight, int reinforceSize) {
      this.pixels = pixels;
      this.panelHeight = panelHeight;
      this.reinforceSize = reinforceSize;
   }

   public static EntSize getFromPx(int px) {
      for (EntSize s : values()) {
         if (s.pixels == px) {
            return s;
         }
      }

      return null;
   }

   public int getNumBlood() {
      return this.panelHeight * 2;
   }

   public EntSide getBlank() {
      switch (this) {
         case small:
            return EntSidesBlobSmall.blank;
         case reg:
            return ESB.blank;
         case big:
            return EntSidesBlobBig.blank;
         case huge:
            return EntSidesBlobHuge.blank;
         default:
            throw new RuntimeException("Unable to find blank for " + this);
      }
   }

   public EntSide getExerted() {
      switch (this) {
         case small:
            return EntSidesBlobSmall.blankExerted;
         case reg:
            return ESB.blankExerted;
         case big:
            return EntSidesBlobBig.blankExerted;
         case huge:
            return EntSidesBlobHuge.blankExerted;
         default:
            throw new RuntimeException("Unable to find blank for " + this);
      }
   }

   public int getReinforceSize() {
      return this.panelHeight;
   }

   public int getPixels() {
      return this.pixels;
   }

   public int getPipWidth() {
      return this == huge ? 3 : 2;
   }

   public Ent getExampleEntity() {
      if (this.ex == null) {
         this.ex = this.makeEt().makeEnt();
      }

      return this.ex;
   }

   private EntType makeEt() {
      switch (this) {
         case small:
         default:
            return MonsterTypeLib.byName("archer");
         case reg:
            return MonsterTypeLib.byName("goblin");
         case big:
            return MonsterTypeLib.byName("slate");
         case huge:
            return MonsterTypeLib.byName("dragon");
      }
   }
}

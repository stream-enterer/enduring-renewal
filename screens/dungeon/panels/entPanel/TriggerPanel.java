package com.tann.dice.screens.dungeon.panels.entPanel;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.content.ent.Ent;
import com.tann.dice.gameplay.content.ent.EntSize;
import com.tann.dice.gameplay.fightLog.FightLog;
import com.tann.dice.gameplay.save.settings.option.OptionLib;
import com.tann.dice.gameplay.trigger.personal.Personal;
import com.tann.dice.statics.Images;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Draw;
import java.util.List;

public class TriggerPanel extends Actor {
   Ent ent;
   private static final int GAP = 1;
   private static final int BUFFSIZE = 5;
   private static final int WIDTH = 5;
   int itemsPerColumn;
   final boolean horizontal;

   public TriggerPanel(Ent ent, boolean horizontal) {
      this.ent = ent;
      this.horizontal = horizontal;
      if (ent.getSize() == EntSize.huge) {
         this.itemsPerColumn = 4;
      } else if (horizontal) {
         this.itemsPerColumn = 1;
      } else {
         switch (ent.getSize()) {
            case small:
               this.itemsPerColumn = 1;
               break;
            case reg:
               this.itemsPerColumn = 2;
               break;
            case big:
               this.itemsPerColumn = 3;
         }
      }

      this.setSize(5.0F, this.itemsPerColumn * 5 + (this.itemsPerColumn - 1) * 1);
   }

   public void draw(Batch batch, float parentAlpha) {
      super.draw(batch, parentAlpha);
      batch.setColor(Colours.z_white);
      List<Personal> visualTriggerList = this.ent.getState(FightLog.Temporality.Visual).getActivePersonals();
      List<Personal> allTriggers = this.ent.getEntPanel().getAllDescribableTriggers();
      int drawIndex = 0;

      for (int i = 0; i < allTriggers.size(); i++) {
         Personal t = allTriggers.get(i);
         if (t.showInEntPanel() && !t.skipNetAndIcon()) {
            int yCo = drawIndex % this.itemsPerColumn;
            int xCo = drawIndex / this.itemsPerColumn;
            if (!this.ent.isPlayer() && (!this.horizontal || this.ent.getSize() == EntSize.small)) {
               xCo = -xCo;
            }

            Boolean incoming = Personal.treatAsIncoming(t, visualTriggerList);
            boolean drawAsIncoming = incoming == null || incoming;
            if (!drawAsIncoming || t.showAsIncoming()) {
               int x = (int)(this.getX() + xCo * 6);
               int y = (int)(this.getY() + this.getHeight() - 6 * (yCo + 1) + 1.0F);
               batch.setColor(Colours.z_white);
               TextureRegion pImage = t.getImage();
               if (pImage == null) {
                  pImage = Images.triggerBug;
               }

               if (drawAsIncoming) {
                  int sinc = OptionLib.STATIC_INCOMING_DEBUFF.c();
                  int sz = 5;
                  switch (sinc) {
                     case 0:
                     default:
                        batch.setColor(Colours.withAlpha(Colours.z_white, 0.35F));
                        batch.draw(pImage, x, y);
                        break;
                     case 1:
                        batch.setColor(Colours.z_white);
                        batch.draw(pImage, x, y);
                        batch.setColor(Colours.yellow);
                        Draw.draw(batch, Images.plus, (float)(x + 3), (float)(y + 3));
                        break;
                     case 2:
                        float min = 0.2F;
                        float max = 0.99F;
                        batch.setColor(Colours.withAlpha(Colours.z_white, min + (max - min) * com.tann.dice.Main.pulsateFactor()));
                        batch.draw(pImage, x, y);
                        com.tann.dice.Main.requestRendering();
                  }
               } else {
                  batch.draw(pImage, x, y);
               }

               drawIndex++;
            }
         }
      }
   }
}

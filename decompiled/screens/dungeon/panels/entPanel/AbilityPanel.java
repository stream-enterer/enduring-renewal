package com.tann.dice.screens.dungeon.panels.entPanel;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.effect.targetable.ability.Ability;
import com.tann.dice.screens.dungeon.panels.ExplanelReposition;
import com.tann.dice.screens.dungeon.panels.Explanel.Explanel;
import com.tann.dice.statics.Images;
import com.tann.dice.statics.sound.Sounds;
import com.tann.dice.util.Colours;
import com.tann.dice.util.TannFont;
import com.tann.dice.util.listener.TannListener;

public class AbilityPanel extends Actor {
   final Ability ability;

   public AbilityPanel(Ability ability) {
      float size = Images.itemBorder.getRegionWidth();
      this.setSize(size, size);
      this.ability = ability;
   }

   public void addStandardListener() {
      this.addListener(new TannListener() {
         @Override
         public boolean info(int button, float x, float y) {
            Actor top = com.tann.dice.Main.getCurrentScreen().getTopPushedActor();
            if (top instanceof Explanel) {
               Explanel e = (Explanel)top;
               if (e.isShowing(AbilityPanel.this.ability)) {
                  com.tann.dice.Main.getCurrentScreen().popSingleLight();
                  Sounds.playSound(Sounds.pop);
                  return true;
               }
            }

            com.tann.dice.Main.getCurrentScreen().pop(Explanel.class);
            Explanel exp = new Explanel(AbilityPanel.this.ability, true);
            Actor a = com.tann.dice.Main.getCurrentScreen().getTopPushedActor();
            if (a instanceof ExplanelReposition) {
               ((ExplanelReposition)a).repositionExplanel(exp);
            } else if (com.tann.dice.Main.getCurrentScreen() != null) {
               com.tann.dice.Main.getCurrentScreen().repositionExplanel(exp);
            }

            com.tann.dice.Main.getCurrentScreen().push(exp, false, true, true, 0.0F);
            Sounds.playSound(Sounds.pip);
            return true;
         }
      });
   }

   public void draw(Batch batch, float parentAlpha) {
      super.draw(batch, parentAlpha);
      batch.setColor(this.ability.getIdCol());
      batch.draw(Images.itemAbilityBorder, this.getX(), this.getY());
      TextureRegion image = this.ability.getImage();
      if (this.ability.useImage()) {
         int imageSize = image.getRegionHeight();
         batch.setColor(Colours.z_white);
         batch.draw(image, this.getX() + this.getWidth() / 2.0F - imageSize / 2, this.getY() + this.getHeight() / 2.0F - imageSize / 2);
      } else {
         TannFont f = TannFont.font;
         f.drawString(
            batch, this.ability.getTitle().charAt(0) + "", (int)(this.getX() + this.getWidth() / 2.0F), (int)(this.getY() + this.getHeight() / 2.0F), 1
         );
      }

      super.draw(batch, parentAlpha);
   }
}

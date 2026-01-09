package com.tann.dice.screens.dungeon.panels.book.views;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.tann.dice.gameplay.content.ent.type.HeroType;
import com.tann.dice.gameplay.progress.chievo.unlock.UnUtil;
import com.tann.dice.screens.dungeon.panels.Explanel.EntPanelInventory;
import com.tann.dice.statics.Images;
import com.tann.dice.statics.sound.Sounds;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Draw;
import com.tann.dice.util.ImageActor;
import com.tann.dice.util.Rectactor;
import com.tann.dice.util.Tann;
import com.tann.dice.util.listener.TannListener;

public class HeroLedgerView extends EntityLedgerView {
   final HeroType h;

   public HeroLedgerView(HeroType h, boolean visible) {
      this.setTransform(false);
      this.h = h;
      this.setSize(26.0F, 26.0F);
      TextureRegion tr = h.portrait;
      if (UnUtil.isLocked(h)) {
         tr = Images.padlock;
      }

      Actor ia = new ImageActor(tr);
      this.addActor(ia);
      Tann.center(ia);
      if (!visible && !UnUtil.isLocked(h)) {
         addUnencountered(this);
      }
   }

   public static void addUnencountered(Group g) {
      Color c = Colours.withAlpha(Colours.dark, 0.6F).cpy();
      int ex = 0;
      Rectactor ra = new Rectactor((int)g.getWidth() + ex * 2, (int)g.getHeight() + ex * 2, c, c);
      ra.setTouchable(Touchable.disabled);
      g.addActor(ra);
      ra.setPosition(-ex, -ex);
   }

   public void draw(Batch batch, float parentAlpha) {
      Draw.fillActor(batch, this, Colours.dark, this.h.heroCol.col, 1);
      super.draw(batch, parentAlpha);
   }

   @Override
   public EntityLedgerView basicListener() {
      this.addListener(new TannListener() {
         @Override
         public boolean info(int button, float x, float y) {
            EntPanelInventory dp = new EntPanelInventory(HeroLedgerView.this.h.makeEnt());
            Sounds.playSound(Sounds.pip);
            com.tann.dice.Main.getCurrentScreen().push(dp, 0.1F);
            Tann.center(dp);
            return true;
         }
      });
      return this;
   }
}

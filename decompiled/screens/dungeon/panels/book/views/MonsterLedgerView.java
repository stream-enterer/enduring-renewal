package com.tann.dice.screens.dungeon.panels.book.views;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.tann.dice.gameplay.content.ent.type.MonsterType;
import com.tann.dice.gameplay.progress.chievo.unlock.UnUtil;
import com.tann.dice.screens.dungeon.panels.Explanel.EntPanelInventory;
import com.tann.dice.statics.Images;
import com.tann.dice.statics.sound.Sounds;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Draw;
import com.tann.dice.util.ImageActor;
import com.tann.dice.util.Tann;
import com.tann.dice.util.listener.TannListener;

public class MonsterLedgerView extends EntityLedgerView {
   private final MonsterType type;

   public MonsterLedgerView(MonsterType type, boolean hidden) {
      this.type = type;
      this.setTransform(false);
      switch (type.size) {
         case small:
            this.setSize(26.0F, 20.0F);
            break;
         case reg:
            this.setSize(30.0F, 28.0F);
            break;
         case big:
            this.setSize(35.0F, 35.0F);
            break;
         case huge:
            this.setSize(70.0F, 70.0F);
      }

      TextureRegion tr = type.portrait;
      if (UnUtil.isLocked(type)) {
         tr = Images.padlock;
      }

      ImageActor ia = new ImageActor(tr);
      ia.setXFlipped(true);
      this.addActor(ia);
      Tann.center(ia);
      this.setTransform(false);
      if (hidden && !UnUtil.isLocked(type)) {
         HeroLedgerView.addUnencountered(this);
      }
   }

   public void draw(Batch batch, float parentAlpha) {
      Draw.fillActor(batch, this, Colours.dark, Colours.purple, 1);
      super.draw(batch, parentAlpha);
   }

   @Override
   public EntityLedgerView basicListener() {
      this.addListener(new TannListener() {
         @Override
         public boolean info(int button, float x, float y) {
            EntPanelInventory dp = new EntPanelInventory(MonsterLedgerView.this.type.makeEnt());
            Sounds.playSound(Sounds.pip);
            com.tann.dice.Main.getCurrentScreen().push(dp, 0.0F);
            Tann.center(dp);
            return true;
         }
      });
      return this;
   }
}

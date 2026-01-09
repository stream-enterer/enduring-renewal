package com.tann.dice.gameplay.mode.chooseParty;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.tann.dice.gameplay.content.ent.type.HeroCol;
import com.tann.dice.gameplay.content.ent.type.HeroType;
import com.tann.dice.gameplay.content.ent.type.lib.HeroTypeLib;
import com.tann.dice.gameplay.content.ent.type.lib.HeroTypeUtils;
import com.tann.dice.gameplay.progress.chievo.unlock.UnUtil;
import com.tann.dice.screens.dungeon.panels.Explanel.EntPanelInventory;
import com.tann.dice.screens.dungeon.panels.book.views.HeroLedgerView;
import com.tann.dice.statics.sound.Sounds;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Draw;
import com.tann.dice.util.Pixl;
import com.tann.dice.util.Tann;
import com.tann.dice.util.bsRandom.Checker;
import com.tann.dice.util.bsRandom.RandomCheck;
import com.tann.dice.util.listener.TannListener;

public class HeroSelector extends Group {
   HeroType type;
   public final int portraitWidth = 25;
   public final int portraitHeight = 28;
   Runnable toggleRun;

   public HeroSelector() {
      this.setSize(25.0F, 28.0F);
      this.type = HeroTypeUtils.byName("Fighter");
      this.addListener(new TannListener() {
         @Override
         public boolean action(int button, int pointer, float x, float y) {
            Sounds.playSound(Sounds.pip);
            HeroSelector.this.showOptions();
            return true;
         }

         @Override
         public boolean info(int button, float x, float y) {
            Sounds.playSound(Sounds.pip);
            HeroSelector.this.showDiePanel(HeroSelector.this.type);
            return true;
         }
      });
   }

   public void setToRandomHeroType() {
      this.type = RandomCheck.checkedRandom(HeroTypeUtils.getTierHeroes(1), new Checker<HeroType>() {
         public boolean check(HeroType heroType) {
            return !UnUtil.isLocked(heroType);
         }
      }, HeroTypeUtils.byName("Defender"));
   }

   public void draw(Batch batch, float parentAlpha) {
      Draw.fillRectangle(batch, this.getX(), this.getY(), 25.0F, 28.0F, Colours.dark, this.type.heroCol.col, 1);
      int heroX = (int)(this.getX() + 12.0F - this.type.portrait.getRegionWidth() / 2);
      int heroY = (int)(this.getY() + 14.0F - this.type.portrait.getRegionHeight() / 2);
      batch.setColor(Colours.z_white);
      batch.draw(this.type.portrait, heroX, heroY);
      super.draw(batch, parentAlpha);
   }

   public void setTo(HeroType type) {
      this.setInternal(type);
      this.toggleRun.run();
   }

   public void setInternal(HeroType newType) {
      if (newType == null || newType.isMissingno() || newType.getTier() != 1) {
         newType = HeroTypeLib.byName("thief");
      }

      this.type = newType;
   }

   public void setToggleRun(Runnable runnable) {
      this.toggleRun = runnable;
   }

   public HeroType getType() {
      return this.type;
   }

   private void showDiePanel(HeroType ht) {
      EntPanelInventory dp = new EntPanelInventory(ht.makeEnt());
      com.tann.dice.Main.getCurrentScreen().push(dp, 0.7F);
      Tann.center(dp);
   }

   private void showOptions() {
      int gap = 2;
      int w = (int)(com.tann.dice.Main.width * 0.97F);
      Pixl p = new Pixl(2);

      for (HeroCol hc : HeroCol.basics()) {
         for (final HeroType ht : HeroTypeUtils.getFilteredTypes(hc, 1, true)) {
            final boolean locked = UnUtil.isLocked(ht);
            if (!locked) {
               Actor indiv = new HeroLedgerView(ht, true);
               indiv.addListener(new TannListener() {
                  @Override
                  public boolean action(int button, int pointer, float x, float y) {
                     if (locked) {
                        Sounds.playSound(Sounds.error);
                        return true;
                     } else {
                        Sounds.playSound(Sounds.confirm);
                        HeroSelector.this.setTo(ht);
                        com.tann.dice.Main.getCurrentScreen().popSingleMedium();
                        return true;
                     }
                  }

                  @Override
                  public boolean info(int button, float x, float y) {
                     if (locked) {
                        Sounds.playSound(Sounds.error);
                        return true;
                     } else {
                        Sounds.playSound(Sounds.pip);
                        HeroSelector.this.showDiePanel(ht);
                        return true;
                     }
                  }
               });
               p.actor(indiv, w);
            }
         }

         p.row();
      }

      Actor a = p.pix();
      com.tann.dice.Main.getCurrentScreen().push(a, 0.7F);
      Tann.center(a);
   }
}

package com.tann.dice.screens.dungeon.panels.Explanel;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.tann.dice.gameplay.content.ent.Ent;
import com.tann.dice.gameplay.content.ent.Hero;
import com.tann.dice.gameplay.content.ent.die.side.EntSide;
import com.tann.dice.gameplay.content.item.Item;
import com.tann.dice.gameplay.effect.Trait;
import com.tann.dice.gameplay.fightLog.FightLog;
import com.tann.dice.gameplay.phase.Phase;
import com.tann.dice.gameplay.phase.PhaseManager;
import com.tann.dice.gameplay.trigger.personal.Personal;
import com.tann.dice.screens.dungeon.DungeonScreen;
import com.tann.dice.screens.dungeon.panels.DieSidePanel;
import com.tann.dice.screens.dungeon.panels.ExplanelReposition;
import com.tann.dice.screens.dungeon.panels.entPanel.AbilityPanel;
import com.tann.dice.screens.dungeon.panels.entPanel.ItemHeroPanel;
import com.tann.dice.statics.Images;
import com.tann.dice.statics.sound.Sounds;
import com.tann.dice.util.Tann;
import com.tann.dice.util.listener.TannListener;
import com.tann.dice.util.ui.Flasher;

public class NetPanel extends Group {
   Ent de;
   int size;
   DieSidePanel[] dieSidePanels = new DieSidePanel[6];

   public NetPanel(Ent de, boolean onlySides) {
      this.setTransform(false);
      this.size = de.getSize().getPixels();
      this.setSize((this.size - 1) * 4 + 1, (this.size - 1) * 3 + 1);
      this.de = de;

      for (int i = 0; i < 6; i++) {
         int x = -5;
         int y = -5;
         switch (i) {
            case 0:
               x = 1;
               y = 2;
               break;
            case 1:
               x = 1;
               y = 0;
               break;
            case 2:
               x = 0;
               y = 1;
               break;
            case 3:
               x = 2;
               y = 1;
               break;
            case 4:
               x = 1;
               y = 1;
               break;
            case 5:
               x = 3;
               y = 1;
         }

         this.place(this.dieSidePanels[i] = this.setup(de.getSides()[i]), x, y);
      }

      if (!onlySides) {
         if (de instanceof Hero) {
            Hero h = (Hero)de;
            int slots = h.getNumberItemSlots();

            for (int i = 0; i < slots; i++) {
               Item e = h.getItems(i);
               if (h.getState(FightLog.Temporality.Present).ignoreItem(e)) {
                  e = null;
               }

               int x = i == 2 ? 2 : 0;
               int y = i == 1 ? 0 : 2;
               if (i == 3) {
                  x = 3;
               }

               this.place(new ItemHeroPanel(e, h, i), x, y);
            }

            int traitIndex = 0;

            for (Trait t : h.getHeroType().traits) {
               if (showTraitInNet(t)) {
                  this.addTraitPanel(t, traitIndex++);
               }
            }
         }
      }
   }

   public static boolean showTraitInNet(Trait t) {
      return t.visible && !t.personal.skipTraitPanel() && !t.personal.skipNetAndIcon();
   }

   private void addTraitPanel(Trait t, int index) {
      if (index <= 1) {
         if (t.personal.getAbility() != null) {
            AbilityPanel abilityPanel = new AbilityPanel(t.personal.getAbility());
            abilityPanel.addStandardListener();
            this.place(abilityPanel, 2 + index, 0);
         } else {
            Actor tp = this.makeTraitActor(this.de, t);
            this.place(tp, 2 + index, 0);
         }
      }
   }

   private Actor makeTraitActor(final Ent ent, final Trait trait) {
      Actor a = trait.personal.getTraitActor();
      Group g = Tann.makeGroup(a);
      g.setSize(Images.itemBorder.getRegionWidth(), Images.itemBorder.getRegionHeight());
      Tann.center(a);
      g.addListener(new TannListener() {
         @Override
         public boolean info(int button, float x, float y) {
            Actor ax = com.tann.dice.Main.getCurrentScreen().getTopPushedActor();
            Personal pers = trait.personal;
            if (ax instanceof Explanel) {
               com.tann.dice.Main.getCurrentScreen().popSingleLight();
               Explanel old = (Explanel)ax;
               if (old.isShowing(pers)) {
                  return true;
               }
            }

            Explanel e = new Explanel(pers, ent, false, ent.getDiePanel().getWidth());
            NetPanel.this.showInfo(e);
            return true;
         }
      });
      return g;
   }

   private void showInfo(Explanel exp) {
      com.tann.dice.Main.getCurrentScreen().push(exp, false, true, true, 0.0F);
      Actor a = com.tann.dice.Main.getCurrentScreen().getTopPushedActor();
      a = com.tann.dice.Main.getCurrentScreen().getTopPushedActor();
      if (a instanceof ExplanelReposition) {
         ((ExplanelReposition)a).repositionExplanel(exp);
      } else {
         Vector2 pos = Tann.getAbsoluteCoordinates(this);
         exp.setPosition(exp.getNiceX(), pos.y - 4.0F - exp.getHeight());
         exp.setY(Math.max((float)exp.getExtraBelowExtent(), exp.getY()));
      }

      Sounds.playSound(Sounds.pip);
   }

   void place(Actor a, int x, int y) {
      this.addActor(a);
      int w = (int)a.getWidth();
      int h = (int)a.getHeight();
      a.setPosition(x * (this.size - 1) + (this.size - w) / 2, y * (this.size - 1) + (this.size - h) / 2);
   }

   private DieSidePanel setup(final EntSide s) {
      DieSidePanel dsp = new DieSidePanel(s, this.de);
      dsp.addListener(new TannListener() {
         @Override
         public boolean info(int button, float x, float y) {
            if (DungeonScreen.get() != null && DungeonScreen.get().partyManagementPanel.isDragging()) {
               return false;
            } else {
               Explanel top = com.tann.dice.Main.getCurrentScreen().getTopExplanel();
               if (top != null && top.isShowing(s)) {
                  com.tann.dice.Main.getCurrentScreen().popSingleLight();
                  Sounds.playSound(Sounds.pop);
                  return true;
               } else {
                  com.tann.dice.Main.getCurrentScreen().pop(Explanel.class);
                  Explanel exp = new Explanel(s, NetPanel.this.de);
                  NetPanel.this.showInfo(exp);
                  return true;
               }
            }
         }
      });
      return dsp;
   }

   public void flashSide(int index) {
      this.dieSidePanels[index]
         .addActor(new Flasher(this.dieSidePanels[index], DieSidePanel.EQUIP_BONUS_FLASH_COLOUR, 0.4F, DieSidePanel.EQUIP_BONUS_FLASH_INTERPOLATION));
   }

   public void draw(Batch batch, float parentAlpha) {
      super.draw(batch, parentAlpha);
      Phase phase = PhaseManager.get().getPhase();
      if (phase != null && phase.highlightDice() && DungeonScreen.checkActive(this.de)) {
         for (DieSidePanel dsp : this.dieSidePanels) {
            if (dsp.side == this.de.getDie().getCurrentSide()) {
               dsp.drawHighlight(batch);
            }
         }
      }
   }
}

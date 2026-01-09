package com.tann.dice.screens.shaderFx;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.tann.dice.screens.dungeon.panels.entPanel.EntPanelCombat;
import com.tann.dice.util.FontWrapper;
import com.tann.dice.util.Tann;

public enum DeathType {
   DelayedAlphaArrow,
   Acid,
   Alpha,
   Burn,
   Cut,
   CutDiagonal,
   Wipe,
   Singularity,
   Ellipse,
   Crush,
   BloodSplatterEdge,
   Flee;

   public float activate(Actor a) {
      boolean player = true;
      EntPanelCombat ep = null;
      Vector2 pos = Tann.getAbsoluteCoordinates(a);
      if (a instanceof EntPanelCombat) {
         ep = (EntPanelCombat)a;
         player = ep.ent.isPlayer();
      }

      FXContainer fxContainer;
      switch (this) {
         case Acid:
            fxContainer = new FXAcid(a);
            break;
         case DelayedAlphaArrow:
            fxContainer = new FXAlpha(a, 0.25F);
            break;
         case Burn:
            fxContainer = new FXBurn(a);
            break;
         case Cut:
            fxContainer = new FXCut(a, player ? -1 : 1, 0.2F, 0.35F, 20.0F, 0.5F, (float)(0.5 + (Math.random() * 2.0 - 1.0) * 0.4F));
            break;
         case CutDiagonal:
            fxContainer = new FXCut(a, player ? -1 : 1, 0.2F, 0.35F, 20.0F, 1.0F, 0.0F);
            break;
         case Wipe:
            fxContainer = new FXWipe(a, new Vector2(player ? -1.0F : 1.0F, 0.0F));
            break;
         case Crush:
            fxContainer = new FXWipe(a, new Vector2(0.0F, -1.0F));
            break;
         case Singularity:
            fxContainer = new FXSingularity(a);
            break;
         case Ellipse:
            fxContainer = new FXEllipse(a);
            break;
         case BloodSplatterEdge:
            fxContainer = new FXAlpha(a);
            int numBlood = 5;
            if (ep != null) {
               numBlood = ep.ent.getSize().getNumBlood();
            }

            if (com.tann.dice.Main.getCurrentScreen() == null) {
               return -1.0F;
            }

            int mult = player ? 1 : -1;
            Tann.addBlood(
               com.tann.dice.Main.getCurrentScreen(),
               numBlood,
               pos.x + a.getWidth() / 2.0F + a.getWidth() / 2.0F * mult,
               2.4F,
               pos.y + a.getHeight() / 2.0F,
               a.getHeight() / 4.0F,
               mult * 8,
               12.0F,
               0.0F,
               8.0F
            );
            break;
         case Flee:
            fxContainer = new FXFlee(a, player);
            break;
         case Alpha:
         default:
            fxContainer = new FXAlpha(a);
      }

      if (ep != null && !FontWrapper.getFont().isTannFont()) {
         ep.title.setVisible(false);
         ep.title.addAction(Actions.sequence(Actions.delay(fxContainer.getDuration()), Actions.visible(true)));
      }

      fxContainer.replace();
      return fxContainer.getDuration();
   }
}

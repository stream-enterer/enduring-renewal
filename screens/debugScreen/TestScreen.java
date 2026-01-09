package com.tann.dice.screens.debugScreen;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Array.ArrayIterator;
import com.tann.dice.gameplay.content.ent.Hero;
import com.tann.dice.gameplay.content.ent.type.lib.HeroTypeLib;
import com.tann.dice.screens.Screen;
import com.tann.dice.screens.dungeon.panels.Explanel.EntPanelInventory;
import com.tann.dice.statics.Images;
import com.tann.dice.util.Tann;

public class TestScreen extends Screen {
   public TestScreen() {
      final Actor a = new Image(Images.wreath);
      this.addActor(a);
      a.setPosition(com.tann.dice.Main.width / 2, com.tann.dice.Main.height / 2);
      a.addListener(new InputListener() {
         public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
            float dist = 50.0F;
            float newX = a.getX() + Tann.random() * dist * 2.0F - dist;
            float newY = a.getY() + Tann.random() * dist * 2.0F - dist;
            a.addAction(Actions.moveTo(newX, newY, 0.4F, Interpolation.pow2Out));
            a.addAction(Actions.moveTo(newX, newY, 0.4F, Interpolation.pow2Out));
            return super.touchDown(event, x, y, pointer, button);
         }
      });
      this.addListener(new InputListener() {
         public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
            if (button == 0) {
               EntPanelInventory panel = new EntPanelInventory(new Hero(Tann.random(HeroTypeLib.getMasterCopy())));
               TestScreen.this.addActor(panel);
               panel.setPosition((int)(x - panel.getWidth() / 2.0F), (int)(y - panel.getHeight() / 2.0F));
            } else {
               ArrayIterator var8 = TestScreen.this.getChildren().iterator();

               while (var8.hasNext()) {
                  Actor a = (Actor)var8.next();
                  a.addAction(Actions.moveBy(Tann.random(-20.0F, 20.0F), Tann.random(-20.0F, 20.0F), 0.2F));
               }
            }

            return super.touchDown(event, x, y, pointer, button);
         }
      });
   }

   @Override
   public void preDraw(Batch batch) {
   }

   @Override
   public void postDraw(Batch batch) {
   }

   @Override
   public void preTick(float delta) {
   }

   @Override
   public void postTick(float delta) {
   }

   @Override
   public void keyPress(int keycode) {
   }

   @Override
   public Screen copy() {
      return new TestScreen();
   }
}

package com.tann.dice.gameplay.effect.targetable.ability.ui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.tann.dice.gameplay.effect.targetable.ability.spell.ManaActor;
import com.tann.dice.gameplay.fightLog.Snapshot;
import com.tann.dice.screens.dungeon.DungeonScreen;
import com.tann.dice.statics.Images;
import com.tann.dice.util.Chrono;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Tann;
import com.tann.dice.util.lang.Words;
import com.tann.dice.util.listener.TannListener;
import com.tann.dice.util.ui.TextWriter;
import java.util.ArrayList;
import java.util.List;

public class ManaHolder extends Group {
   private static final String NUMBERED = "numbered";
   private static final int gap = 2;
   private static final int border = 3;
   private static final int MAX_CHARGE_ACTORS = 5;
   List<Actor> allActors = new ArrayList<>();
   Actor inputGrabber = new Actor();

   public ManaHolder(Snapshot visual) {
      this.setTransform(false);
      this.update(0, visual.getMaxMana());
      this.setTouchable(Touchable.childrenOnly);
   }

   public void update(final int newMana, final int maxMana) {
      for (Actor allActor : this.allActors) {
         if ("numbered".equals(allActor.getName())) {
            allActor.remove();
         }
      }

      if (newMana == 0 && this.allActors.size() == 0) {
         this.setSize(0.0F, 0.0F);
      } else {
         List<Actor> newActors = new ArrayList<>();
         int newChargeActors = newMana > 5 ? 1 : newMana;

         for (int i = 0; i < newChargeActors && i < this.allActors.size(); i++) {
            Actor a = this.allActors.get(i);
            if (a instanceof ManaActor) {
               newActors.add(a);
            }
         }

         this.allActors.removeAll(newActors);
         if (com.tann.dice.Main.isPortrait() && newMana > 1 && this.getY() > 34.800003F && com.tann.dice.Main.width < 188 + Math.min(5, newMana) * 8) {
            for (int ix = 0; ix < newActors.size(); ix++) {
               newActors.get(ix).remove();
            }

            newActors.clear();
            Actor a = Tann.imageWithText(Images.mana, "" + newMana, Colours.dark);
            a.setName("numbered");
            newActors.add(a);
         } else {
            for (int ix = newActors.size(); ix < newChargeActors; ix++) {
               newActors.add(new ManaActor());
            }

            for (int ix = 0; ix < newActors.size(); ix++) {
               Actor a = newActors.get(ix);
               if (a instanceof ManaActor) {
                  ((ManaActor)a).setStruck(ix >= maxMana);
               }
            }

            if (newMana > 5) {
               TextWriter tw = null;

               for (int ixx = 0; ixx < this.allActors.size(); ixx++) {
                  Actor a = this.allActors.get(ixx);
                  if (a instanceof TextWriter) {
                     tw = (TextWriter)a;
                     this.allActors.remove(a);
                     break;
                  }
               }

               String text = "[blue]x" + newMana;
               if (newMana > maxMana) {
                  text = text + " (" + maxMana + ")[h][blank]";
               }

               if (tw == null) {
                  tw = new TextWriter(text);
               } else {
                  tw.setText(text);
               }

               newActors.add(tw);
            }
         }

         for (Actor a : this.allActors) {
            if (a instanceof TextWriter) {
               a.remove();
            } else {
               a.addAction(
                  Actions.parallel(
                     Actions.moveTo(-a.getWidth() / 2.0F, a.getY(), 0.3F, Chrono.i), Actions.sequence(Actions.fadeOut(0.3F), Actions.removeActor())
                  )
               );
            }
         }

         int totalWidth = 0;

         for (Actor ax : newActors) {
            totalWidth = (int)(totalWidth + ax.getWidth());
         }

         totalWidth += 2 * (newActors.size() - 1);
         totalWidth += 6;
         int targetHeight = 16;
         this.setHeight(targetHeight);
         float spd = DungeonScreen.get().isLoading() ? 0.0F : 0.3F;
         this.addAction(Actions.sizeTo(totalWidth, targetHeight, spd, Chrono.i));
         this.addAction(Actions.alpha(Math.signum((float)newMana), spd));

         for (Actor ax : newActors) {
            if (!ax.hasParent()) {
               this.addActor(ax);
               ax.setPosition((int)(-ax.getWidth() / 2.0F), (int)(targetHeight / 2.0F - ax.getHeight() / 2.0F));
            }
         }

         int currentX = (int)(-totalWidth / 2.0F + 3.0F);

         for (int ixxx = 0; ixxx < newActors.size(); ixxx++) {
            Actor axx = newActors.get(ixxx);
            axx.addAction(Actions.moveTo(currentX, (int)(targetHeight / 2.0F - axx.getHeight() / 2.0F), spd, Chrono.i));
            currentX = (int)(currentX + (2.0F + axx.getWidth()));
         }

         this.allActors = newActors;
         this.inputGrabber.clearListeners();
         if (newMana > 0) {
            this.addActor(this.inputGrabber);
            this.inputGrabber.toFront();
            this.inputGrabber.setSize(totalWidth, targetHeight);
            this.inputGrabber.setX(-totalWidth / 2);
            this.inputGrabber.addListener(new TannListener() {
               @Override
               public boolean info(int button, float x, float y) {
                  AbilityHolder.showInfo("[blue]" + newMana + "/" + maxMana + " " + Words.manaString() + " stored", Colours.blue);
                  return true;
               }
            });
         } else {
            this.removeActor(this.inputGrabber);
         }
      }
   }

   public void draw(Batch batch, float parentAlpha) {
      if (this.getWidth() != 0.0F) {
         batch.setColor(this.getColor());
         Images.manaPatch.draw(batch, (int)(this.getX() - this.getWidth() / 2.0F), this.getY(), this.getWidth(), this.getHeight());
         super.draw(batch, parentAlpha);
      }
   }
}

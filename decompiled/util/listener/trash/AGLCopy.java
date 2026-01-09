package com.tann.dice.util.listener.trash;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;

public class AGLCopy implements EventListener {
   static final Vector2 tmpCoords = new Vector2();
   static final Vector2 tmpCoords2 = new Vector2();
   private final GDCopy detector;
   InputEvent event;
   Actor actor;
   Actor touchDownTarget;

   public AGLCopy() {
      this(20.0F, 0.4F, 1.1F, 2.1474836E9F);
   }

   public AGLCopy(float halfTapSquareSize, float tapCountInterval, float longPressDuration, float maxFlingDelay) {
      this.detector = new GDCopy(halfTapSquareSize, tapCountInterval, longPressDuration, maxFlingDelay, new GDCopy.GestureAdapter() {
         @Override
         public boolean tap(float stageX, float stageY, int count, int button) {
            AGLCopy.this.actor.stageToLocalCoordinates(AGLCopy.tmpCoords.set(stageX, stageY));
            AGLCopy.this.tap(AGLCopy.this.event, AGLCopy.tmpCoords.x, AGLCopy.tmpCoords.y, count, button);
            return true;
         }

         @Override
         public boolean longPress(float stageX, float stageY) {
            AGLCopy.this.actor.stageToLocalCoordinates(AGLCopy.tmpCoords.set(stageX, stageY));
            return AGLCopy.this.longPress(AGLCopy.this.actor, AGLCopy.tmpCoords.x, AGLCopy.tmpCoords.y);
         }
      });
   }

   public boolean handle(Event e) {
      if (!(e instanceof InputEvent)) {
         return false;
      } else {
         InputEvent event = (InputEvent)e;
         switch (event.getType()) {
            case touchDown:
               this.actor = event.getListenerActor();
               this.touchDownTarget = event.getTarget();
               this.detector.touchDown(event.getStageX(), event.getStageY(), event.getPointer(), event.getButton());
               this.actor.stageToLocalCoordinates(tmpCoords.set(event.getStageX(), event.getStageY()));
               this.touchDown(event, tmpCoords.x, tmpCoords.y, event.getPointer(), event.getButton());
               if (event.getTouchFocus()) {
                  event.getStage().addTouchFocus(this, event.getListenerActor(), event.getTarget(), event.getPointer(), event.getButton());
               }

               return true;
            case touchUp:
               this.event = event;
               this.actor = event.getListenerActor();
               this.detector.touchUp(event.getStageX(), event.getStageY(), event.getPointer(), event.getButton());
               this.actor.stageToLocalCoordinates(tmpCoords.set(event.getStageX(), event.getStageY()));
               this.touchUp(event, tmpCoords.x, tmpCoords.y, event.getPointer(), event.getButton());
               return true;
            case touchDragged:
               this.event = event;
               this.actor = event.getListenerActor();
               this.detector.touchDragged(event.getStageX(), event.getStageY(), event.getPointer());
               return true;
            default:
               return false;
         }
      }
   }

   public void touchDown(InputEvent event, float x, float y, int pointer, int button) {
   }

   public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
   }

   public void tap(InputEvent event, float x, float y, int count, int button) {
   }

   public boolean longPress(Actor actor, float x, float y) {
      return false;
   }
}

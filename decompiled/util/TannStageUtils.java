package com.tann.dice.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.SnapshotArray;
import com.badlogic.gdx.utils.Array.ArrayIterator;
import com.tann.dice.statics.Images;
import com.tann.dice.util.ui.ScrollPane;
import com.tann.dice.util.ui.TextWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class TannStageUtils {
   public static void clearActorsOfType(Group g, Class<? extends Actor> actorClass) {
      SnapshotArray<Actor> children = g.getChildren();

      for (int i = children.size - 1; i >= 0; i--) {
         Actor a = (Actor)children.get(i);
         if (actorClass.isInstance(a)) {
            a.remove();
         }
      }
   }

   public static Actor getRandomActor() {
      return getActorWithClass(Actor.class, com.tann.dice.Main.getCurrentScreen());
   }

   public static <T extends Actor> T getActorWithClass(Class<T> c, Group src) {
      List<T> rs = getActorsWithClass(c, src);
      return rs.size() > 0 ? rs.get(0) : null;
   }

   public static List<Actor> getActorsWithName(String name, Group src) {
      List<Actor> result = new ArrayList<>();

      for (Actor ac : getAllActors(src)) {
         if (name.equalsIgnoreCase(ac.getName())) {
            result.add(ac);
         }
      }

      return result;
   }

   public static <T extends Actor> List<T> getActorsWithClass(Class<T> c, Group src) {
      List<T> result = new ArrayList<>();

      for (Actor ac : getAllActors(src)) {
         if (c.isInstance(ac)) {
            result.add((T)ac);
         }
      }

      return result;
   }

   public static List<Actor> getAllActors(Group src) {
      List<Actor> result = new ArrayList<>();
      result.add(src);
      ArrayIterator var2 = src.getChildren().iterator();

      while (var2.hasNext()) {
         Actor child = (Actor)var2.next();
         if (child instanceof Group) {
            result.addAll(getAllActors((Group)child));
         } else {
            result.add(child);
         }
      }

      return result;
   }

   public static boolean hasActor(Group cp, Class textMarqueeClass) {
      ArrayIterator var2 = cp.getChildren().iterator();

      while (var2.hasNext()) {
         Actor child = (Actor)var2.next();
         if (textMarqueeClass == child.getClass()) {
            return true;
         }

         if (child instanceof Group && hasActor((Group)child, textMarqueeClass)) {
            return true;
         }
      }

      return false;
   }

   public static void putBehindAlwaysOnTop(Actor a) {
      Group g = a.getParent();
      if (g != null) {
         ArrayIterator var2 = g.getChildren().iterator();

         while (var2.hasNext()) {
            Actor child = (Actor)var2.next();
            if ("alwaysontop".equalsIgnoreCase(child.getName())) {
               a.setZIndex(child.getZIndex());
               return;
            }
         }
      }
   }

   public static void drawDebug(Stage stage) {
      Batch b = stage.getBatch();
      b.begin();
      drawDebug(b, stage.getRoot(), 0, 0);
      b.end();
   }

   private static void drawDebug(Batch b, Group g, int x, int y) {
      drawDebugSingle(b, g, x, y);
      ArrayIterator var4 = g.getChildren().iterator();

      while (var4.hasNext()) {
         Actor a = (Actor)var4.next();
         if (a instanceof Group) {
            drawDebug(b, (Group)a, x + (int)g.getX(), y + (int)g.getY());
         } else {
            drawDebugSingle(b, a, x + (int)g.getX(), y + (int)g.getY());
         }
      }
   }

   private static void drawDebugSingle(Batch b, Actor a, int x, int y) {
      boolean extraMode = Gdx.input.isKeyPressed(129);
      if (!extraMode || a instanceof Group && ((Group)a).isTransform()) {
         int hash = a.getClass().getSimpleName().hashCode();
         Color c = Tann.getHashColour(hash);
         c.a = 0.3F;
         b.setColor(c);
         Draw.fillRectangle(b, (int)(x + a.getX()), (int)(y + a.getY()), a.getWidth(), a.getHeight());
         if (extraMode) {
            TextureRegion tr = Images.skull;
            Draw.draw(b, tr, x + a.getX(1) - tr.getRegionWidth() / 2, y + a.getY(1) - tr.getRegionHeight() / 2);
         }
      }
   }

   public static void sortActorsBySizeBiggestFirst(List<Actor> musicianPanels) {
      Collections.sort(musicianPanels, new Comparator<Actor>() {
         public int compare(Actor o1, Actor o2) {
            return (int)(o2.getWidth() * o2.getHeight() - o1.getWidth() * o1.getHeight());
         }
      });
   }

   public static void sortActorsBySizeHeightPerWidth(List<Actor> musicianPanels) {
      Collections.sort(musicianPanels, new Comparator<Actor>() {
         public int compare(Actor o1, Actor o2) {
            float o1r = o1.getHeight() / o1.getWidth();
            float o2r = o2.getHeight() / o2.getWidth();
            return Float.compare(o1r, o2r);
         }
      });
   }

   public static void sortActorsBySizeForJukebox(List<Actor> musicianPanels, int contentWidth) {
      sortActorsBySizeForJukeboxTailored(musicianPanels, contentWidth);
   }

   private static void sortActorsBySizeForJukeboxTailored(List<Actor> musicianPanels, final int contentWidth) {
      Collections.sort(musicianPanels, new Comparator<Actor>() {
         public int compare(Actor o1, Actor o2) {
            int tinyThresh = 30;
            int r = Boolean.compare(o2.getHeight() < tinyThresh, o1.getHeight() < tinyThresh);
            if (r != 0) {
               return r;
            } else {
               float hd = -20.0F;
               float o1r = (o1.getHeight() + hd) / o1.getWidth();
               float o2r = (o2.getHeight() + hd) / o2.getWidth();
               float lim = contentWidth / 2 - 2;
               if (o1.getWidth() > lim) {
                  o1r -= 5000.0F;
               }

               if (o2.getWidth() > lim) {
                  o2r -= 5000.0F;
               }

               return Float.compare(o1r, o2r);
            }
         }
      });
   }

   public static void sortActorsBySizeTallestFirst(List<Actor> musicianPanels) {
      Collections.sort(musicianPanels, new Comparator<Actor>() {
         public int compare(Actor o1, Actor o2) {
            return (int)(o2.getHeight() - o1.getHeight());
         }
      });
   }

   public static void removeCopyButton(Group g) {
      for (Actor actor : getActorsWithName("copy", g)) {
         g.removeActor(actor);
      }
   }

   public static Actor noListener(Actor actor) {
      actor.clearListeners();
      return actor;
   }

   public static boolean isMouseHeld() {
      return Gdx.input.isTouched();
   }

   public static void ensureSafeForScrollpane(Actor a) {
      if (a instanceof Group) {
         ArrayIterator var1 = ((Group)a).getChildren().iterator();

         while (var1.hasNext()) {
            Actor child = (Actor)var1.next();
            ensureSafeForScrollpane(child);
         }
      }

      if (a instanceof Render3D) {
         ((Render3D)a).makeSafeForScrollpane();
      }
   }

   private static void actorFrontOfScreenSomehow(Actor a) {
      if (com.tann.dice.Main.self().control.usesMouse()) {
         if (a instanceof Group) {
            ArrayIterator var1 = ((Group)a).getChildren().iterator();

            while (var1.hasNext()) {
               Actor child = (Actor)var1.next();
               actorFrontOfScreenSomehow(child);
            }
         }

         if (a instanceof ScrollPane) {
            com.tann.dice.Main.stage.setScrollFocus(a);
         }
      }
   }

   public static void actorJustPushed(Actor a) {
      actorFrontOfScreenSomehow(a);
   }

   public static void actorSurfacedFromOtherPopping(Actor a) {
      actorFrontOfScreenSomehow(a);
   }

   public static Group errorActor(String ctx) {
      return new TextWriter("err: " + ctx);
   }

   public static boolean active(Actor a) {
      return a != null && a.hasParent();
   }

   public static void ensureCR1(Actor a, float time) {
      a.addAction(Actions.delay(time));
   }
}

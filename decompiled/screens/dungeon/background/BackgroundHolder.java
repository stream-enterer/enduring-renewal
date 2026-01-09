package com.tann.dice.screens.dungeon.background;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.tann.dice.gameplay.battleTest.Zone;
import com.tann.dice.gameplay.save.settings.option.OptionUtils;
import com.tann.dice.screens.debugScreen.TransSection;
import com.tann.dice.screens.dungeon.DungeonScreen;
import com.tann.dice.statics.ImageUtils;
import com.tann.dice.statics.Images;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Draw;
import com.tann.dice.util.Tann;
import com.tann.dice.util.tp.TP;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class BackgroundHolder extends Group {
   public static Interpolation MOVE_TERP = Interpolation.smooth;
   List<Actor> backgroundActors = new ArrayList<>();
   private static int SCR_W = Gdx.graphics.getWidth();
   boolean skipProgress;
   static TextureRegion override = ImageUtils.loadExtBig("dungeon/tiling/missingno");
   int index = 0;
   Map<Integer, Vector2> indexPositions = new HashMap<>();
   int currentX;
   int setupIndex = 0;

   public BackgroundHolder() {
      this.setTransform(false);
   }

   public static int getHFull() {
      if (com.tann.dice.Main.isPortrait()) {
         Actor a = DungeonScreen.get().enemy;
         return (int)((a.getY() + a.getHeight()) * com.tann.dice.Main.scale);
      } else {
         return Gdx.graphics.getHeight();
      }
   }

   public void populate(List<TP<Zone, Integer>> levelTypes) {
      this.skipProgress = ((Zone)levelTypes.get(0).a).validMonsters.size() == 0;
      SCR_W = Gdx.graphics.getWidth();
      this.indexPositions.clear();
      Zone first = (Zone)levelTypes.get(0).a;
      Zone last = (Zone)levelTypes.get(levelTypes.size() - 1).a;
      this.backFill(first);

      for (int i = 0; i < levelTypes.size(); i++) {
         Zone t = (Zone)levelTypes.get(i).a;
         int numLevels = (Integer)levelTypes.get(i).b;
         this.populate(t, numLevels);
         if (i < levelTypes.size() - 1) {
            this.addTransition(t, (Zone)levelTypes.get(i + 1).a);
         }
      }

      this.addSectionImage(getImage(last, -1));
   }

   private static TextureRegion getImage(Zone type, int offset) {
      DungeonScreen ds = DungeonScreen.get();
      if (ds != null) {
         int seed = ds.getDungeonContext().getSeed();
         seed += type.index;
         Random r = Tann.makeStdRandom(seed);
         List<TextureRegion> bs = type.background;
         if (bs.isEmpty()) {
            return Images.qmark;
         } else if (bs.size() != 1 && offset != -1) {
            int chosenForOne = r.nextInt(3);
            if (chosenForOne == offset) {
               return bs.get(1);
            } else {
               if (bs.size() > 2) {
                  boolean higher = offset == 2 || offset == 1 && chosenForOne == 2;
                  if (higher ^ r.nextBoolean()) {
                     return bs.get(2);
                  }
               }

               return bs.get(0);
            }
         } else {
            return bs.get(0);
         }
      } else {
         return override;
      }
   }

   private void backFill(Zone type) {
      int backFillX = SCR_W;

      while (backFillX > 0) {
         TextureRegion tr = getImage(type, -1);
         backFillX = (int)(backFillX - getWidth(tr));
         Image image = new Image(tr);
         this.backgroundActors.add(image);
         image.setSize(getWidth(tr), getHeight(tr));
         image.setPosition(backFillX, this.getImageY());
         this.addActor(image);
      }

      this.currentX = SCR_W;
   }

   private int getImageY() {
      return 0;
   }

   private static float getWidth(TextureRegion tr) {
      int regionHeight = tr.getRegionHeight();
      float scaledHeight = getHFull();
      float scale = scaledHeight / regionHeight;
      float scaledWidth = tr.getRegionWidth() * scale;
      return (float)Math.ceil(scaledWidth);
   }

   private static float getHeight(TextureRegion tr) {
      return getHFull();
   }

   private void populate(Zone type, int numLevels) {
      int counter = 0;

      for (int i = 0; i < numLevels - 1; i++) {
         this.addSectionImage(getImage(type, counter++));
      }
   }

   private void addTransition(Zone prev, Zone next) {
      TextureRegion tr = prev.getTransition(next);
      if (tr != null) {
         this.addSectionImage(tr);
      } else {
         TransSection ts = new TransSection(getImage(prev, 0), getImage(next, 0));
         ts.setSize(getWidth(getImage(prev, 0)), getHeight(getImage(next, 0)));
         this.addSection(ts);
      }
   }

   public Vector2 progress() {
      this.index++;
      this.index = this.index % this.indexPositions.size();
      Vector2 previousPos;
      if (this.index == 0) {
         previousPos = new Vector2(0.0F, 0.0F);
      } else {
         previousPos = this.indexPositions.get(this.index - 1);
      }

      Vector2 nextPos = this.indexPositions.get(this.index);
      if (!this.skipProgress) {
         this.addAction(Actions.moveTo(nextPos.x, nextPos.y, GET_MOVE_SPD(), MOVE_TERP));
      }

      return previousPos.cpy().sub(nextPos);
   }

   public static float GET_MOVE_SPD() {
      return 2.0F * OptionUtils.backgroundMoveMult();
   }

   public void setStartIndex(int currentLevelNumber) {
      this.index = currentLevelNumber % this.indexPositions.size();
      if (this.index >= 0) {
         Vector2 pos = this.indexPositions.get(this.index);
         this.setPosition(pos.x, pos.y);
      }
   }

   public void act(float delta) {
      super.act(delta);

      for (int index = 0; index < this.backgroundActors.size(); index++) {
         Actor i = this.backgroundActors.get(index);
         float imageX = this.getX() + i.getX();
         i.setVisible(imageX + i.getWidth() >= 0.0F && imageX <= SCR_W);
      }
   }

   public void draw(Batch batch, float parentAlpha) {
      com.tann.dice.Main.self().stop2d(true);
      SpriteBatch batch1 = com.tann.dice.Main.self().startBackground();
      float posX = this.getX();
      float posY = this.getY();
      int intX = (int)(this.getX() / com.tann.dice.Main.scale) * com.tann.dice.Main.scale;
      int intY = (int)(this.getY() / com.tann.dice.Main.scale) * com.tann.dice.Main.scale;
      this.setPosition(intX, intY);
      super.draw(batch1, parentAlpha);
      PortBoard.drawTopIfNecessary(batch1);
      this.setPosition(posX, posY);
      batch1.setColor(Colours.dark);
      Draw.fillRectangle(batch1, com.tann.dice.Main.width * com.tann.dice.Main.scale, 0.0F, 10.0F, Gdx.graphics.getHeight());
      DungeonScreen.drawVersionPixel(batch1);
      com.tann.dice.Main.self().stopBackground();
      com.tann.dice.Main.self().start2d(true);
   }

   private void addSectionImage(TextureRegion tr) {
      Actor image = new Image(tr);
      image.setSize(getWidth(tr), getHeight(tr));
      this.addSection(image);
   }

   private void addSection(Actor image) {
      this.addActor(image);
      this.backgroundActors.add(image);
      image.setPosition(this.currentX, this.getImageY());
      this.indexPositions.put(this.setupIndex, new Vector2((int)(-this.currentX + SCR_W - image.getWidth()), 0.0F));
      this.setupIndex++;
      this.currentX = (int)(this.currentX + image.getWidth());
   }
}

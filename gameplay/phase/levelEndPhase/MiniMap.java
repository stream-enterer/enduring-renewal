package com.tann.dice.gameplay.phase.levelEndPhase;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.utils.ScissorStack;
import com.tann.dice.gameplay.battleTest.Zone;
import com.tann.dice.gameplay.context.DungeonContext;
import com.tann.dice.statics.ImageUtils;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Draw;
import com.tann.dice.util.Tann;
import com.tann.dice.util.tp.TP;

public class MiniMap extends Group {
   static final float edgeGap = 1.0F;
   private static final int MAX_SHOW = 8;
   final int regionWidth = 56;
   final int levelsPerZone = 4;
   final int singleWidth = 14;
   int firstIndex;
   int lastIndex;
   DungeonContext dungeonContext;

   public MiniMap(DungeonContext dungeonContext) {
      this.setTransform(false);
      this.dungeonContext = dungeonContext;
      int show = Math.min(8, dungeonContext.getTotalLength());
      this.setSize(14 * show + 2.0F, 30.0F);
      int levelNumber = dungeonContext.getCurrentMod20LevelNumber();
      this.firstIndex = Math.max(0, levelNumber + 1 - show / 2);
      this.lastIndex = this.firstIndex + show;
      int tooFar = dungeonContext.getContextConfig().getTotalDifferentLevels() - this.lastIndex;
      if (tooFar < 0) {
         this.firstIndex += tooFar;
         this.lastIndex += tooFar;
      }

      int zoneIndex = 0;
      Group container = Tann.makeGroup();

      for (TP<Zone, Integer> tannp : dungeonContext.getLevelTypes()) {
         MiniMap.BGDraw bgDraw = null;
         int baseX = zoneIndex * 56;
         int shiftAcross = Math.max(-levelNumber, -12);
         int extraX = shiftAcross * 14;

         for (int d4 = 0; d4 < tannp.b / 4; d4++) {
            bgDraw = new MiniMap.BGDraw(tannp.a.minimap);
            int realX = baseX + extraX * 0 + tannp.a.minimap.getRegionWidth() * d4;
            bgDraw.setPosition(realX, 1.0F);
            container.addActor(bgDraw);
            bgDraw.toBack();
         }

         for (int i = 0; i < tannp.b; i++) {
            int levelIndex = zoneIndex * 4 + i + 1;
            boolean boss = dungeonContext.getContextConfig().isBoss(levelIndex);
            MiniMap.Node.NodeState nodeState;
            if (levelIndex < levelNumber) {
               nodeState = MiniMap.Node.NodeState.Complete;
            } else if (levelIndex == levelNumber) {
               nodeState = MiniMap.Node.NodeState.Current;
            } else {
               nodeState = MiniMap.Node.NodeState.Neutral;
            }

            MiniMap.Node n = new MiniMap.Node(nodeState, boss);
            n.setPosition(baseX + i * 14 + 7, (int)(this.getHeight() / 2.0F - n.getHeight() / 2.0F));
            container.addActor(n);
         }

         zoneIndex++;
      }

      this.addActor(container);
      container.setPosition(1.0F, 1.0F);
      container.setPosition(1 - Math.min(Math.max(0, levelNumber - 4), 12) * 14, 1.0F);
   }

   public void draw(Batch batch, float parentAlpha) {
      Draw.fillActor(batch, this, Colours.dark, Colours.purple, 1);
      Vector2 pos = Tann.getAbsoluteCoordinates(this);
      int gap = 1;
      Rectangle s = new Rectangle(pos.x + gap, pos.y + gap, this.getWidth() - gap * 2, this.getHeight() - gap * 2);
      batch.flush();
      boolean added = ScissorStack.pushScissors(s);
      if (added) {
         super.draw(batch, parentAlpha);
         batch.flush();
         ScissorStack.popScissors();
      }
   }

   static class BGDraw extends Actor {
      final TextureRegion bg;

      BGDraw(TextureRegion bg) {
         this.bg = bg;
      }

      public void draw(Batch batch, float parentAlpha) {
         if (this.bg != null) {
            super.draw(batch, parentAlpha);
            batch.setColor(Colours.z_white);
            batch.draw(this.bg, this.getX(), this.getY());
         }
      }
   }

   static class Node extends Actor {
      final boolean boss;
      final MiniMap.Node.NodeState nodeState;
      final TextureRegion textureRegion;

      public Node(MiniMap.Node.NodeState nodeState, boolean boss) {
         this.nodeState = nodeState;
         this.boss = boss;
         this.textureRegion = ImageUtils.loadExt("ui/minimap/icon/" + (boss ? "boss" : "level") + "-" + nodeState.toString().toLowerCase());
      }

      public void draw(Batch batch, float parentAlpha) {
         batch.setColor(Colours.z_white);
         batch.draw(
            this.textureRegion,
            (int)(this.getX() - this.textureRegion.getRegionWidth() / 2.0F),
            (int)(this.getY() - this.textureRegion.getRegionHeight() / 2.0F)
         );
      }

      public static enum NodeState {
         Neutral,
         Current,
         Complete;
      }
   }
}

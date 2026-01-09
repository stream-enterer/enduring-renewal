package com.tann.dice.screens.graph;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.tann.dice.gameplay.content.ent.EntSize;
import com.tann.dice.gameplay.content.ent.die.side.EntSide;
import com.tann.dice.gameplay.content.ent.die.side.EntSidesLib;
import com.tann.dice.gameplay.content.ent.die.side.blob.ESB;
import com.tann.dice.gameplay.content.ent.type.EntType;
import com.tann.dice.gameplay.content.ent.type.lib.HeroTypeUtils;
import com.tann.dice.gameplay.content.ent.type.lib.MonsterTypeLib;
import com.tann.dice.gameplay.effect.eff.Eff;
import com.tann.dice.gameplay.effect.eff.EffType;
import com.tann.dice.screens.dungeon.panels.Explanel.Explanel;
import com.tann.dice.util.Colours;
import com.tann.dice.util.DebugUtilsUseful;
import com.tann.dice.util.Draw;
import com.tann.dice.util.ImageActor;
import com.tann.dice.util.LineActor;
import com.tann.dice.util.Pixl;
import com.tann.dice.util.Rectactor;
import com.tann.dice.util.Tann;
import com.tann.dice.util.listener.TannListener;
import com.tann.dice.util.ui.ScrollPane;
import com.tann.dice.util.ui.TextWriter;
import com.tann.dice.util.ui.standardButton.StandardButton;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class GraphUtils {
   public static Actor makeWidth(int width, GraphUpdate graphUpdate) {
      return make(new ArrayList<>(Arrays.asList(ESB.dmg.val(1))), (width - 40) / 30, true, false, graphUpdate);
   }

   public static Actor make(final List<EntSide> active, final int maxPips, final boolean hero, boolean startPressed, final GraphUpdate graphUpdate) {
      Pixl p = new Pixl(2);
      p.actor(makeGraph(active, maxPips, hero)).row(27);
      if (active.size() > 0) {
         for (final EntSide es : active) {
            ImageActor ia = new ImageActor(es.getTexture());
            ia.addListener(new TannListener() {
               @Override
               public boolean action(int button, int pointer, float x, float y) {
                  int index = GraphUtils.indexOf(active, es);
                  if (index != -1) {
                     active.remove(index);
                     graphUpdate.newGraph(GraphUtils.make(active, maxPips, hero, false, graphUpdate));
                  }

                  return true;
               }

               @Override
               public boolean info(int button, float x, float y) {
                  Actor a = new Explanel(es, null);
                  com.tann.dice.Main.getCurrentScreen().push(a);
                  Tann.center(a);
                  return true;
               }
            });
            Actor cont = new Pixl().border(hashCol(es)).actor(ia).pix();
            p.actor(cont);
         }
      }

      StandardButton plus = new StandardButton("+");
      p.actor(plus);
      Runnable tl = new Runnable() {
         @Override
         public void run() {
            Pixl popup = new Pixl(2).border(Colours.dark, Colours.grey, 1);
            List<EntSide> list = EntSidesLib.getAllSidesWithValue();

            for (int i = list.size() - 1; i >= 0; i--) {
               EntSide es = list.get(i);
               if (es.isGeneric()) {
                  list.remove(i);
               }
            }

            Collections.sort(list, new Comparator<EntSide>() {
               public int compare(EntSide o1, EntSide o2) {
                  Eff e1 = o1.getBaseEffect();
                  Eff e2 = o2.getBaseEffect();
                  if (e1.isBasic() != e2.isBasic()) {
                     return e1.isBasic() ? -1 : 1;
                  } else if (o1.size != o2.size) {
                     if (o1.size == EntSize.small) {
                        return 1;
                     } else {
                        return o2.size == EntSize.small ? -1 : o1.size.getPixels() - o2.size.getPixels();
                     }
                  } else {
                     EffType t1 = e1.getType();
                     EffType t2 = e2.getType();
                     if (t2 != t1) {
                        return t1.ordinal() - t2.ordinal();
                     } else {
                        int count1 = DebugUtilsUseful.getUses(o1, true);
                        int count2 = DebugUtilsUseful.getUses(o2, true);
                        return count1 != count2 ? count2 - count1 : 0;
                     }
                  }
               }
            });

            for (final EntSide es : list) {
               Actor a = es.makeBasicSideActor(GraphUtils.indexOf(active, es) == -1 ? 0 : 3, false, null);
               a.addListener(new TannListener() {
                  @Override
                  public boolean action(int button, int pointer, float x, float y) {
                     int index = GraphUtils.indexOf(active, es);
                     if (index == -1) {
                        active.add(es);
                     } else {
                        active.remove(index);
                     }

                     com.tann.dice.Main.getCurrentScreen().popAllLight();
                     com.tann.dice.Main.getCurrentScreen().popSingleMedium();
                     graphUpdate.newGraph(GraphUtils.make(active, maxPips, hero, true, graphUpdate));
                     return true;
                  }

                  @Override
                  public boolean info(int button, float x, float y) {
                     Actor ax = new Explanel(es, null);
                     com.tann.dice.Main.getCurrentScreen().push(ax);
                     Tann.center(ax);
                     return true;
                  }
               });
               popup.actor(a, com.tann.dice.Main.width * 0.7F);
            }

            Actor a = popup.pix();
            final ScrollPane sp = Tann.makeScrollpane(a);
            sp.setSize(a.getWidth() + 6.0F, com.tann.dice.Main.height * 0.8F);
            sp.addListener(new TannListener() {
               @Override
               public boolean action(int button, int pointer, float x, float y) {
                  com.tann.dice.Main.getCurrentScreen().pop(sp);
                  return true;
               }
            });
            com.tann.dice.Main.getCurrentScreen().push(sp);
            Tann.center(sp);
         }
      };
      plus.setRunnable(tl);
      if (startPressed) {
         tl.run();
      }

      StandardButton heroToggle = new StandardButton(hero ? "hero" : "monster");
      heroToggle.setRunnable(new Runnable() {
         @Override
         public void run() {
            graphUpdate.newGraph(GraphUtils.make(active, maxPips, !hero, false, graphUpdate));
         }
      });
      p.row().actor(heroToggle);
      Actor a = p.pix();
      a.setName("graph");
      return a;
   }

   private static int indexOf(List<EntSide> list, EntSide es) {
      for (int i = 0; i < list.size(); i++) {
         if (list.get(i).same(es)) {
            return i;
         }
      }

      return -1;
   }

   private static Actor makeGraph(List<EntSide> active, int maxPips, boolean hero) {
      int gridSize = 30;
      int pipWidth = 30;
      int valHeight = 30;
      int maxVal = 0;

      for (EntSide es : active) {
         maxVal = (int)Math.max((double)maxVal, Math.ceil(es.withValue(maxPips).getEffectTier(HeroTypeUtils.byName("veteran"))));
      }

      maxVal = Math.min(maxVal, 30);
      Actor leftBar = makeLeftBar(maxVal, 30, Colours.orange);
      Actor bottomBar = makeBottomBar(maxPips, 30);
      Group g = new Group() {
         public void draw(Batch batch, float parentAlpha) {
            Draw.fillActor(batch, this, Colours.dark, Colours.grey, 1);
            super.draw(batch, parentAlpha);
         }
      };
      g.setSize(bottomBar.getWidth(), leftBar.getHeight());
      g.addActor(leftBar);
      leftBar.setPosition(-leftBar.getWidth(), 0.0F);
      g.addActor(bottomBar);
      bottomBar.setPosition(0.0F, -bottomBar.getHeight());
      addGridLines(g, 30, maxPips, 30, maxVal);

      for (EntSide es : active) {
         addSideToGraph(g, es, maxPips, 30, 30, hero);
      }

      String t = com.tann.dice.Main.t("Calc'd Value").replace(" ", "[n]");
      g = new Pixl(0).text("[notranslate][orange]" + t).gap(12).actor(g).pix();
      TextWriter pips = new TextWriter("Pips");
      g.addActor(pips);
      pips.setPosition((int)(g.getWidth() / 2.0F - pips.getWidth() / 2.0F), -pips.getHeight() - 15.0F);
      return g;
   }

   private static void addGridLines(Group g, int pipWidth, int maxPips, int valHeight, int maxVal) {
      Color col = Colours.withAlpha(Colours.light, 0.06F).cpy();

      for (int i = 0; i < maxVal; i++) {
         Rectactor ra = new Rectactor((int)g.getWidth(), 1, col);
         g.addActor(ra);
         ra.setY(i * valHeight);
      }

      for (int i = 0; i < maxPips; i++) {
         Rectactor ra = new Rectactor(1, (int)g.getHeight(), col);
         g.addActor(ra);
         ra.setX(i * pipWidth);
      }
   }

   public static Color hashCol(EntSide es) {
      AtlasRegion ar = (AtlasRegion)es.getTexture();
      return Tann.getHashColour(ar.name.hashCode());
   }

   private static void addSideToGraph(Group graph, EntSide es, int maxPips, int pipWidth, int valHeight, boolean hero) {
      EntType[] types;
      if (hero) {
         types = new EntType[]{HeroTypeUtils.byName("thief"), HeroTypeUtils.byName("guardian"), HeroTypeUtils.byName("veteran")};
      } else {
         types = new EntType[]{MonsterTypeLib.byName("bones")};
      }

      boolean cantrippy = es.withValue(1).getExtraFlatEffectTier(types[0]) > 0.0F;

      for (int lv = 0; lv < types.length; lv++) {
         float prevPower = 0.0F;
         Color col = hashCol(es);
         col.a = 1.0F - lv * 0.3F;

         for (int pipNum = 0; pipNum <= maxPips; pipNum++) {
            EntSide test = es.withValue(pipNum);
            float power = test.getEffectTier(types[lv]);
            if (pipNum > 0) {
               Actor line = new LineActor((pipNum - 1) * pipWidth, prevPower * valHeight, pipNum * pipWidth, power * valHeight);
               line.setColor(col);
               graph.addActor(line);
            }

            prevPower = power;
         }

         if (cantrippy) {
            float prevCantrip = 0.0F;
            Color cantripCol = col.cpy().lerp(Colours.pink, 0.6F);

            for (int pipNum = 0; pipNum <= maxPips; pipNum++) {
               EntSide test = es.withValue(pipNum);
               float power = test.getExtraFlatEffectTier(types[lv]);
               if (pipNum > 0) {
                  Actor line = new LineActor((pipNum - 1) * pipWidth, prevCantrip * valHeight, pipNum * pipWidth, power * valHeight);
                  line.setColor(cantripCol);
                  graph.addActor(line);
               }

               prevCantrip = power;
            }
         }
      }
   }

   private static Actor makeBottomBar(int maxPips, int pipDist) {
      Pixl bottomBar = new Pixl();

      for (int i = 0; i <= maxPips; i++) {
         Actor rect = new Rectactor(1, 4, Colours.light);
         Group g = Tann.makeGroup(rect);
         TextWriter tw = new TextWriter(i + "");
         g.addActor(tw);
         tw.setPosition((int)(-tw.getWidth() / 2.0F), -tw.getHeight() - 2.0F);
         bottomBar.actor(g);
         if (i < maxPips) {
            bottomBar.gap(pipDist - (int)g.getWidth());
         }
      }

      return bottomBar.pix();
   }

   private static Actor makeLeftBar(int maxVal, int valHeight, Color col) {
      Pixl leftBar = new Pixl();

      for (int i = maxVal; i >= 0; i--) {
         Actor rect = new Rectactor(4, 1, col);
         Group g = Tann.makeGroup(rect);
         TextWriter tw = new TextWriter(TextWriter.getTag(col) + i);
         g.addActor(tw);
         tw.setPosition(-tw.getWidth() - 2.0F, (int)(-tw.getHeight() / 2.0F));
         leftBar.actor(g);
         if (i > 0) {
            leftBar.row((int)(valHeight - g.getHeight()));
         }
      }

      return leftBar.pix(16);
   }
}

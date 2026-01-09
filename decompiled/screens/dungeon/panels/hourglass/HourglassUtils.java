package com.tann.dice.screens.dungeon.panels.hourglass;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.fightLog.Snapshot;
import com.tann.dice.gameplay.trigger.Trigger;
import com.tann.dice.gameplay.trigger.global.Global;
import com.tann.dice.gameplay.trigger.personal.Personal;
import com.tann.dice.screens.dungeon.DungeonScreen;
import com.tann.dice.screens.dungeon.DungeonUtils;
import com.tann.dice.statics.Images;
import com.tann.dice.statics.sound.Sounds;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Draw;
import com.tann.dice.util.ImageActor;
import com.tann.dice.util.Pixl;
import com.tann.dice.util.Tann;
import com.tann.dice.util.listener.TannListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HourglassUtils {
   public static List<HourglassElement> getHourglassItems(Snapshot present) {
      List<HourglassElement> result = new ArrayList<>();

      for (Global g : present.getGlobals()) {
         HourglassElement items = getHourglassItems(g);
         if (items != null) {
            result.add(items);
         }
      }

      List<EntState> sts = present.getStates(true, false);

      for (int stsi = 0; stsi < sts.size(); stsi++) {
         EntState es = sts.get(stsi);
         List<Personal> ps = es.getActivePersonals();

         for (int i = 0; i < ps.size(); i++) {
            HourglassElement he = getHourglassItems(ps.get(i), es.getEnt().getName(true) + ": ");
            if (he != null) {
               result.add(he);
            }
         }
      }

      return result;
   }

   public static HourglassElement getHourglassItems(Trigger t) {
      return getHourglassItems(t, null);
   }

   public static HourglassElement getHourglassItems(Trigger t, String pref) {
      HourglassElement hge = t.hourglassUtil();
      if (hge == null) {
         return null;
      } else {
         if (pref != null) {
            hge.message = pref + hge.message;
         }

         return hge;
      }
   }

   public static boolean hourglassShouldBeHighlit(Snapshot present, List<HourglassElement> items) {
      for (HourglassElement item : items) {
         if (item.hourglassShouldBeHighlit(present)) {
            return true;
         }
      }

      return false;
   }

   public static Actor makeHourglassButton(final Snapshot present) {
      List<HourglassElement> hourglassItems = getHourglassItems(present);
      int turn = present.getTurn();
      final Map<Integer, List<HourglassElement>> levelToGlasses = new HashMap<>();
      boolean anyActive = false;

      for (HourglassElement hourglassItem : hourglassItems) {
         List<Integer> ints = hourglassItem.getTurns(turn);
         if (ints != null) {
            anyActive = true;

            for (Integer integer : ints) {
               if (integer != 0) {
                  if (levelToGlasses.get(integer) == null) {
                     levelToGlasses.put(integer, new ArrayList<>());
                  }

                  levelToGlasses.get(integer).add(hourglassItem);
               }
            }
         }
      }

      if (!hourglassItems.isEmpty() && anyActive) {
         final boolean highlit = hourglassShouldBeHighlit(present, hourglassItems);
         ImageActor butt = new ImageActor(DungeonUtils.getBaseImage()) {
            @Override
            public void draw(Batch batch, float parentAlpha) {
               super.draw(batch, parentAlpha);
               TextureRegion tr = highlit ? Images.hourglassActive : Images.hourglass;
               Draw.draw(
                  batch,
                  tr,
                  (float)((int)(this.getX() + this.getWidth() / 2.0F - tr.getRegionWidth() / 2)),
                  (float)((int)(this.getY() + this.getHeight() / 2.0F - tr.getRegionHeight() / 2))
               );
            }
         };
         butt.addListener(new TannListener() {
            @Override
            public boolean action(int button, int pointer, float x, float y) {
               Sounds.playSound(Sounds.pip);
               int width = 90;
               Pixl p = new Pixl(2, 3).border(Colours.grey);
               p.text("[text]Current turn: [yellow]" + present.getTurn());

               for (Integer level : levelToGlasses.keySet()) {
                  List<HourglassElement> allForLevel = levelToGlasses.get(level);
                  Map<HourglassTime, List<HourglassElement>> timeElementMapForTurn = new HashMap<>();

                  for (HourglassTime time : HourglassTime.values()) {
                     List<HourglassElement> elemsForTime = new ArrayList<>();
                     timeElementMapForTurn.put(time, elemsForTime);

                     for (HourglassElement hourglassElement : allForLevel) {
                        if (hourglassElement.hourglassTime == time) {
                           elemsForTime.add(hourglassElement);
                        }
                     }
                  }

                  for (HourglassTime time : HourglassTime.values()) {
                     List<HourglassElement> elems = timeElementMapForTurn.get(time);
                     if (!elems.isEmpty()) {
                        Pixl levelInner = new Pixl(2, 2).border(Colours.grey);
                        String endText;
                        switch (time) {
                           case START:
                              endText = "[grey] (start)[cu]";
                              break;
                           case END:
                              endText = "[grey] (end)[cu]";
                              break;
                           default:
                              endText = "";
                        }

                        levelInner.text("[text]Turn [yellow]" + level + "[cu]" + endText);

                        for (HourglassElement elem : elems) {
                           levelInner.row().text(elem.getRealMessage(), 90);
                        }

                        p.row().actor(levelInner.pix(8));
                     }
                  }
               }

               Actor a = p.pix(8);
               DungeonScreen.get().push(a, 0.7F);
               Tann.center(a);
               return true;
            }
         });
         return butt;
      } else {
         return null;
      }
   }
}

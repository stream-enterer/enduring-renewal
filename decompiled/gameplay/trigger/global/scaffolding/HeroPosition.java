package com.tann.dice.gameplay.trigger.global.scaffolding;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.trigger.Collision;
import com.tann.dice.statics.Images;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Draw;
import com.tann.dice.util.Pixl;
import com.tann.dice.util.TannLog;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum HeroPosition {
   TOP("Top hero", "top", 0),
   MIDDLE("Middle hero", "mid", 2),
   BOT("Bottom hero", "bot", -1),
   BOT_TWO("Bottom two heroes", "bot2", -2, -1),
   TOP_AND_BOTTOM("Top/bottom heroes", "topbot", 0, -1),
   TOP_TWO("Top two heroes", "top2", 0, 1),
   TOP_THREE("Top three heroes", "top3", 0, 1, 2),
   TOP_FOUR("Top four heroes", "top4", 0, 1, 2, 3),
   MIDDLE_THREE("Middle three heroes", "mid3", 1, 2, 3),
   BOTTOM_THREE("Bottom three heroes", "bot3", -3, -2, -1),
   EVERY_OTHER("Every other hero", "eo", 0, 2, -1);

   private final String desc;
   private final String shortDesc;
   private final int[] pos;

   public static List<HeroPosition> samples() {
      return Arrays.asList(TOP, MIDDLE, BOT_TWO, MIDDLE_THREE);
   }

   private HeroPosition(String desc, String shortDesc, int... pos) {
      this.shortDesc = shortDesc;
      this.desc = desc;
      this.pos = pos;
   }

   public static HeroPosition top(int i) {
      switch (i) {
         case 1:
            return TOP;
         case 2:
            return TOP_TWO;
         case 3:
            return TOP_THREE;
         case 4:
            return TOP_FOUR;
         default:
            throw new RuntimeException("not enough heroes");
      }
   }

   public String shortName() {
      return this.describe().replaceAll(" heroes", "").replaceAll(" hero", "");
   }

   public String veryShortName() {
      return this.shortDesc;
   }

   public String describe() {
      return this.desc;
   }

   public int[] getRawPosition() {
      return this.pos;
   }

   public <T> List<T> getFromPosition(List<T> t) {
      List<T> result = new ArrayList<>();

      for (int i : this.pos) {
         if (i >= 0) {
            if (t.size() > i) {
               result.add(t.get(i));
            }
         } else if (t.size() > Math.abs(i) - 1) {
            result.add(t.get(t.size() + i));
         }
      }

      return result;
   }

   public Actor makeActor() {
      Pixl p = new Pixl();
      if (this.pos[0] != 0) {
         this.elipses(p);
      }

      for (int index = 0; index < this.pos.length; index++) {
         if (index > 0) {
            int me = this.pos[index];
            int prev = this.pos[index - 1];
            if (Math.abs(me - prev) != 1 || Math.signum((float)me) < 0.0F != Math.signum((float)prev) < 0.0F) {
               this.elipses(p);
            }
         }

         p.image(Images.arrowLeft, Colours.light).row(1);
      }

      if (this.pos[this.pos.length - 1] != -1) {
         this.elipses(p);
      }

      p.row(0);
      return p.pix();
   }

   private void elipses(Pixl p) {
      for (int i = 0; i < 3; i++) {
         p.image(Draw.getSq(), Colours.light).row(1);
      }
   }

   public long getCollisionBit() {
      return Collision.HERO_POSITION;
   }

   public static HeroPosition byName(String name) {
      for (HeroPosition hp : values()) {
         if (hp.shortDesc.equalsIgnoreCase(name) || hp.name().equalsIgnoreCase(name)) {
            return hp;
         }
      }

      TannLog.error(name + " pos not found");
      return TOP;
   }
}

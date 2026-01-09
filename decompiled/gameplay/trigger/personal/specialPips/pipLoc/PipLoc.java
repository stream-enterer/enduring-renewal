package com.tann.dice.gameplay.trigger.personal.specialPips.pipLoc;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Rectactor;
import com.tann.dice.util.Tann;
import com.tann.dice.util.lang.Words;
import com.tann.dice.util.tp.TP;
import com.tann.dice.util.ui.HpGrid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PipLoc {
   public final PipLocType type;
   final int val;
   Map<Integer, int[]> cache = new HashMap<>();

   public PipLoc(PipLocType type, int val) {
      this.type = type;
      this.val = val;
   }

   public static PipLoc all() {
      return new PipLoc(PipLocType.All, -1);
   }

   public static PipLoc offsetEvery(int every, int offset) {
      return new PipLoc(PipLocType.EveryN, (offset << 16) + every);
   }

   public int[] getLocs(int maxHp) {
      if (this.cache.get(maxHp) == null) {
         this.cache.put(maxHp, this.calcLocs(maxHp));
      }

      return this.cache.get(maxHp);
   }

   private int[] calcLocs(int maxHp) {
      switch (this.type) {
         case Specific:
            if (maxHp < this.val) {
               return new int[0];
            }

            return new int[]{this.val};
         case EveryN:
            int offset = this.val >> 16;
            int per = this.val & (int)(Math.pow(2.0, 16.0) - 1.0);
            int total = (maxHp + per - 1 - offset) / per;
            int[] result = new int[total];

            for (int i = 0; i < result.length; i++) {
               result[i] = offset + i * per;
            }

            return result;
         case EveryFraction:
            int div = this.val;
            if (div <= 1) {
               return new int[0];
            }

            int[] result = new int[Math.min(maxHp, div - 1)];

            for (int i = 0; i < result.length; i++) {
               result[i] = (int)((float)maxHp / div * (i + 1));
            }

            return result;
         case LeftmostN:
            int[] result = new int[this.val];
            int i = 0;

            while (i < result.length) {
               result[i] = i++;
            }

            return result;
         case RightmostN:
            int[] result = new int[this.val];

            for (int i = 0; i < result.length; i++) {
               result[i] = maxHp - result.length + i;
            }

            return result;
         case All:
            int[] result = new int[maxHp];
            int i = 0;

            while (i < result.length) {
               result[i] = i++;
            }

            return result;
         default:
            throw new RuntimeException("not implemented: " + this.type);
      }
   }

   public int getNextPipLocation(int minTriggerHp, int maxHP) {
      int[] pipLocations = this.getLocs(maxHP);

      for (int index = pipLocations.length - 1; index >= 0; index--) {
         int pipLoc = pipLocations[index];
         if (pipLoc < minTriggerHp) {
            return pipLoc;
         }
      }

      return -1;
   }

   public boolean allowAddingToMaxHpEntity(int maxHp) {
      return this.getLocs(maxHp).length > 0;
   }

   public Actor makeTraitPanelActor(TP<TextureRegion, Color> pipTannple) {
      return this.makePanelActor(pipTannple, 3, true);
   }

   public Actor makePanelActor(TP<TextureRegion, Color> pipTannple) {
      return this.makePanelActor(pipTannple, 999, false);
   }

   public Actor makePanelActor(TP<TextureRegion, Color> pipTannple, int maxPips, boolean skipElipses) {
      boolean elipses = true;
      boolean elipsesEnd = true;
      Color dotdot = Colours.red;
      int max;
      switch (this.type) {
         case Specific:
            max = this.val + 1;
            break;
         case EveryN:
            max = 10;
            break;
         case EveryFraction:
            max = this.val * 2 - 1;
            elipses = false;
            break;
         case LeftmostN:
            max = this.val;
            elipsesEnd = true;
            break;
         case RightmostN:
            max = this.val;
            elipsesEnd = false;
            break;
         case All:
            max = 8;
            dotdot = pipTannple.b;
            elipses = true;
            break;
         default:
            throw new RuntimeException("Unimp pip type: " + this.type);
      }

      max = Math.min(max, maxPips);
      TextureRegion[] specials = new TextureRegion[max];
      Color[] specialCols = new Color[max];
      int[] locs = this.getLocs(max);

      for (int i = 0; i < locs.length; i++) {
         specials[locs[i]] = pipTannple.a;
         specialCols[locs[i]] = pipTannple.b;
      }

      List<Actor> starts = new ArrayList<>();
      List<Actor> ends = new ArrayList<>();
      List<Actor> toAdd = elipsesEnd ? ends : starts;
      if (elipses && !skipElipses) {
         for (int i = 0; i < 3; i++) {
            toAdd.add(new Rectactor(1, 1, dotdot));
         }
      }

      return HpGrid.make(max, 0, 0, max, specials, specialCols, starts, ends);
   }

   public boolean isActive(int dmgLoc, int maxHp) {
      return Tann.contains(this.getLocs(maxHp), dmgLoc);
   }

   public String describe() {
      switch (this.type) {
         case Specific:
            return "the " + Words.ordinal(this.val + 1) + " hp";
         case EveryN:
            int per = this.val & (int)(Math.pow(2.0, 16.0) - 1.0);
            return "every " + Words.ordinal(per) + " hp";
         case EveryFraction:
            if (this.val == 2) {
               return "the middle hp";
            }

            return this.val - 1 + " evenly-spaced hp";
         case LeftmostN:
            return this.mostN("inner", this.val);
         case RightmostN:
            return this.mostN("outer", this.val);
         case All:
            return "all hp";
         default:
            return "unknown type: " + this.type;
      }
   }

   private String mostN(String s, int val) {
      return val == 1 ? "the " + s + " hp" : "the " + s + " " + val + " hp";
   }

   public String hyphenTag() {
      switch (this.type) {
         case LeftmostN:
         case RightmostN:
            return this.val + "";
         default:
            return null;
      }
   }
}

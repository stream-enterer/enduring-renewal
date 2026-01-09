package com.tann.dice.gameplay.trigger;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.effect.eff.Eff;
import com.tann.dice.gameplay.effect.eff.keyword.Keyword;
import com.tann.dice.screens.dungeon.panels.hourglass.HourglassElement;
import com.tann.dice.statics.Images;
import com.tann.dice.util.ImageActor;
import com.tann.dice.util.Pixl;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public abstract class Trigger implements Cloneable {
   public static final String NOT_ALONE = "notalone";
   public static Comparator<Trigger> sorter = new Comparator<Trigger>() {
      public int compare(Trigger o1, Trigger o2) {
         return (int)Math.signum(o1.getPriority() - o2.getPriority());
      }
   };

   public static boolean notAlone(Actor a) {
      return "notalone".equalsIgnoreCase(a.getName());
   }

   public String describeForSelfBuff() {
      return "not implemented";
   }

   public final Actor makePanelActor(boolean big) {
      try {
         return this.makePanelActorI(big);
      } catch (Exception var3) {
         var3.printStackTrace();
         return new Pixl().text("[pink]" + var3.getClass().getSimpleName()).pix();
      }
   }

   public Actor makePanelActorI(boolean big) {
      return unknown();
   }

   public List<Keyword> getReferencedKeywords() {
      return new ArrayList<>();
   }

   public final long getCollisionBits() {
      return this.getCollisionBits(null);
   }

   public long getCollisionBits(Boolean player) {
      return 0L;
   }

   public boolean skipEquipImage() {
      return false;
   }

   public boolean skipTest() {
      return false;
   }

   public static Actor unknown() {
      ImageActor ia = new ImageActor(Images.panelUnknown);
      ia.setName("notalone");
      return ia;
   }

   public static String describeTriggers(List<? extends Trigger> triggers) {
      String result = "";

      for (int i = 0; i < triggers.size(); i++) {
         Trigger t = triggers.get(i);
         String desc = t.describeForSelfBuff();
         if (desc != null) {
            if (result.length() > 0) {
               result = result + "[n][nh]";
            }

            result = result + com.tann.dice.Main.tOnce(desc);
         }

         if (t.clearDescription()) {
            result = "";
         }
      }

      return result;
   }

   protected boolean clearDescription() {
      return false;
   }

   public boolean clearIcons() {
      return false;
   }

   public float getPriority() {
      return 0.0F;
   }

   public HourglassElement hourglassUtil() {
      return null;
   }

   public Eff getSingleEffOrNull() {
      return null;
   }

   public Keyword getStronglyAssociatedKeyword() {
      return null;
   }

   public boolean isOnPick() {
      return false;
   }

   public boolean allTurnsOnly() {
      return this.isOnPick();
   }

   public boolean allLevelsOnly() {
      return this.isOnPick();
   }

   public boolean metaOnly() {
      return false;
   }

   public boolean isMultiplable() {
      return false;
   }

   public boolean skipMultiplable() {
      return false;
   }

   public static boolean checkMultiplability(List<? extends Trigger> triggers, boolean liberal) {
      for (int i = 0; i < triggers.size(); i++) {
         Trigger t = triggers.get(i);
         if (!t.metaOnly() && !t.skipMultiplable()) {
            if (!liberal && !triggers.get(i).isMultiplable()) {
               return false;
            }

            if (liberal && triggers.get(i).isMultiplable()) {
               return true;
            }
         }
      }

      return !liberal;
   }

   public String hyphenTag() {
      return null;
   }
}

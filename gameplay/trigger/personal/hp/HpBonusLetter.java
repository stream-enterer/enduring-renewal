package com.tann.dice.gameplay.trigger.personal.hp;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.modifier.modBal.ModTierUtils;
import com.tann.dice.gameplay.save.settings.option.OptionLib;
import com.tann.dice.gameplay.trigger.Collision;
import com.tann.dice.gameplay.trigger.personal.Personal;
import com.tann.dice.statics.Images;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Pixl;
import com.tann.dice.util.Tann;
import com.tann.dice.util.lang.Words;
import java.util.ArrayList;
import java.util.List;

public class HpBonusLetter extends Personal {
   public static final char[] NUMBERS = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
   public static final char[] VOWELS = new char[]{'a', 'e', 'i', 'o', 'u', 'y'};
   public static final char[] CONSONANTS = new char[]{'b', 'c', 'd', 'f', 'g', 'h', 'j', 'k', 'l', 'm', 'n', 'p', 'q', 'r', 's', 't', 'v', 'w', 'x', 'y', 'z'};
   final char[] chars;
   final int bonus;

   public HpBonusLetter(int bonus, char... chars) {
      this.chars = chars;
      this.bonus = bonus;
   }

   @Override
   public String describeForSelfBuff() {
      String result = Tann.delta(this.bonus) + " hp for each ";
      if (this.chars == VOWELS) {
         result = result + "vowel";
      } else if (this.chars == CONSONANTS) {
         result = result + "consonant";
      } else if (this.chars == NUMBERS) {
         result = result + "number";
      } else {
         List<String> charsList = new ArrayList<>();

         for (char c : this.chars) {
            charsList.add("'" + c + "'");
         }

         result = result + Tann.commaList(charsList);
      }

      return result + " in my name";
   }

   @Override
   public int getBonusMaxHp(int maxHp, EntState ent) {
      int extra = 0;
      String nameToCheck = transformName(ent.getEnt().getName(false).toLowerCase());
      return extra + Tann.countCharsInString(this.chars, nameToCheck) * this.bonus;
   }

   public static String transformName(String in) {
      if (in.length() < 2) {
         return in;
      } else if (Tann.isInt("" + in.charAt(1))) {
         int firstPart = 4;
         return in.length() > firstPart ? in.substring(0, firstPart) + in.substring(firstPart, in.length()).replaceAll("\\..*", "") : in;
      } else {
         return in.replaceAll("\\..*", "");
      }
   }

   @Override
   public float getPriority() {
      return -10.0F;
   }

   @Override
   public Actor makePanelActorI(boolean big) {
      int gap = 1;
      Pixl p = new Pixl(0);
      p.text(Words.plusString(this.bonus)).gap(1);

      for (int i = 0; i < this.bonus; i++) {
         p.image(Images.hp, Colours.red).gap(1);
      }

      p.text("[text]/").gap(1);
      if (this.chars == CONSONANTS) {
         p.text("[text]consonant").gap(1);
      } else if (this.chars == NUMBERS) {
         p.text("[text]number").gap(1);
      } else {
         String s = "";

         for (char c : this.chars) {
            s = s + c;
         }

         p.text("[text]" + s);
      }

      Actor a = p.pix();
      if (OptionLib.MOD_CALC.c()) {
         float estVal = ModTierUtils.extraMonsterHP(ModTierUtils.getBonusMonsterHpLetterRatio(this.bonus, this.chars));
         a = new Pixl(2).actor(a).row().text(Tann.floatFormat(estVal)).pix();
      }

      return a;
   }

   @Override
   public long getCollisionBits(Boolean player) {
      return Collision.hpFor(player);
   }

   @Override
   public boolean allTurnsOnly() {
      return true;
   }
}

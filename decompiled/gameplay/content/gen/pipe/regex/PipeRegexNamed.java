package com.tann.dice.gameplay.content.gen.pipe.regex;

import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.PRNB64UPTO;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.PRNBracket;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.PRNCOL;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.PRNChoosable;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.PRNDelta;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.PRNDifficulty;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.PRNDigitSpecial;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.PRNDigits;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.PRNEntityOrItem;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.PRNEntityType;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.PRNHC;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.PRNHEXUPTO;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.PRNHSL;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.PRNHT;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.PRNHTA;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.PRNHTLS;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.PRNHTLazy;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.PRNHTMulti;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.PRNHeroPos;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.PRNI;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.PRNItemMulti;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.PRNKeyword;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.PRNLevel;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.PRNMT;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.PRNMTMulti;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.PRNMod;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.PRNPart;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.PRNPhaseString;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.PRNRect;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.PRNRichText;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.PRNRichTextMulti;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.PRNS;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.PRNSCapturedOneOf;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.PRNSideID;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.PRNSideMulti;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.PRNSidePos;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.PRNSideSingle;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.PRNTEX;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.PRNText;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.PRNTier;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.PRNZone;

public abstract class PipeRegexNamed<T> extends PipeRegex<T> {
   public static final PRNPart DIGIT = new PRNDigits(1);
   public static final PRNPart DIFFICULTY = new PRNDifficulty();
   public static final PRNPart SINGLE_DIGIT_TIER = new PRNTier(1);
   public static final PRNPart UP_TO_TWO_DIGITS_TIER = new PRNTier(2);
   public static final PRNPart UP_TO_THREE_DIGITS_TIER = new PRNTier(3);
   public static final PRNPart LEVEL = new PRNLevel();
   public static final PRNPart DIGIT_2_9 = new PRNDigitSpecial(2, 9);
   public static final PRNPart UP_TO_TWO_DIGITS = new PRNDigits(2);
   public static final PRNPart EXACTLY_TWO_DIGITS = new PRNDigits(2, true);
   public static final PRNPart RECT = new PRNRect();
   public static final PRNPart UP_TO_THREE_DIGITS = new PRNDigits(3);
   public static final PRNPart UP_TO_FOUR_DIGITS = new PRNDigits(4);
   public static final PRNPart SIDE_ID = new PRNSideID();
   public static final PRNPart HEROCOL = new PRNHC();
   public static final PRNPart ABILITY = new PRNHTA();
   public static final PRNPart ONHIT = new PRNHTLS();
   public static final PRNPart TRIGGERHP = new PRNHTLS();
   public static final PRNPart ENTITY = new PRNEntityType();
   public static final PRNPart ENTITY_OR_ITEM = new PRNEntityOrItem();
   public static final PRNPart MONSTER = new PRNMT();
   public static final PRNPart MONSTER_MULTI = new PRNMTMulti();
   public static final PRNPart HERO = new PRNHT();
   public static final PRNPart HERO_LAZY = new PRNHTLazy(false);
   public static final PRNPart MONSTER_LAZY = new PRNHTLazy(true);
   public static final PRNPart HERO_MULTI = new PRNHTMulti();
   public static final PRNPart BRACKET_LEFT = new PRNBracket(false);
   public static final PRNPart BRACKET_RIGHT = new PRNBracket(true);
   public static final PRNPart ITEM_MULTI = new PRNItemMulti();
   public static final PRNPart SIDE_SINGLE = new PRNSideSingle();
   public static final PRNPart SIDE_MULTI = new PRNSideMulti();
   public static final PRNPart DASH = new PRNS("-");
   public static final PRNPart DOT = new PRNS(".");
   public static final PRNPart COLON = new PRNS(":");
   public static final PRNPart ITEM = new PRNI();
   public static final PRNPart MODIFIER = new PRNMod();
   public static final PRNPart TEX = new PRNTEX();
   public static final PRNPart COLOUR = new PRNCOL();
   public static final PRNPart NAME = new PRNText();
   public static final PRNPart RICH_TEXT = new PRNRichText();
   public static final PRNPart RICH_TEXT_MULTI = new PRNRichTextMulti();
   public static final PRNPart UP_TO_THREE_B64 = new PRNB64UPTO(3);
   public static final PRNPart UP_TO_THREE_HEX = new PRNHEXUPTO(3);
   public static final PRNPart UP_TO_FIVE_HEX = new PRNHEXUPTO(5);
   public static final PRNPart UP_TO_FIFTEEN_HEX = new PRNHEXUPTO(15);
   public static final PRNPart HSL = new PRNHSL();
   public static final PRNPart TWO_DIGIT_DELTA = new PRNDelta(2);
   public static final PRNPart SIDE_POSITION = new PRNSidePos();
   public static final PRNPart KEYWORD = new PRNKeyword();
   public static final PRNPart HERO_POSITION = new PRNHeroPos();
   public static final PRNPart ZONE = new PRNZone();
   public static final PRNPart PHASE_STRING = new PRNPhaseString();
   public static final PRNPart CHOOSABLE = new PRNChoosable();
   final PRNPart[] parts;

   protected PipeRegexNamed(PRNPart... parts) {
      super(getRegex(parts));
      this.parts = parts;
   }

   private static String getRegex(PRNPart[] parts) {
      String regex = "";

      for (int i = 0; i < parts.length; i++) {
         regex = regex + parts[i].regex();
      }

      return regex;
   }

   public static PRNPart prnS(String str) {
      return new PRNS(str);
   }

   protected static PRNPart prnSCapturedOneOf(String... options) {
      return new PRNSCapturedOneOf(options);
   }

   @Override
   public String document() {
      String out = "[grey]";

      for (int i = 0; i < this.parts.length; i++) {
         PRNPart part = this.parts[i];
         out = out + part.getColDesc();
         if (i < this.parts.length - 1) {
            out = out + "[cu]";
         }
      }

      return out;
   }
}

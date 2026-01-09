package com.tann.dice.util;

public abstract class Separators {
   public static final String PARTY_HEROES = ",";
   public static final String CUSTOM_MOD_LIST = ",";
   public static final String PINS_LIST = ",";
   public static final String HERO_ITEMS = "~";
   public static final String CHOOSABLE_LIST = "@3";
   public static final String CHOOSABLE_LIST_REG = "@3";
   public static final String CHOOSABLE_PART = "~";
   public static final String CHOOSABLE_METALIST = "@4";
   public static final String NIGHTMARE_LIST = "@5";
   public static final String TEXTMOD_DOT = ".";
   public static final String TEXTMOD_DOT_REGEX = "\\.";
   public static final String TEXTMOD_ARG1 = ":";
   public static final String TEXTMOD_ARG2 = "-";
   public static final String TEXTMOD_ENTITY_LIST = "+";
   public static final String TEXTMOD_ENTITY_LIST_REGEX = "\\+";
   public static final String TEXTMOD_ITEM_COMBINE = "#";
   public static final String TEXTMOD_MOD_COMBINE = "&";
   public static final String PHASE_PART = ";";
   public static final String PHASE_META_LIST = "@1";
   public static final String PHASE_META_LIST_2 = "@2";
   public static final String PHASE_META_LIST_3 = "@6";
   public static final String PHASE_META_LIST_4 = "@7";
   public static final String CHOICE_TYPE = "#";
   public static final String MOD_LIST_START = "=";
   public static final String copyAchievements = "xxxxxxxxxxxxxxx";
   public static final String TEXTMOD_GREEDY_CAPTURE_GROUP = "(.+)";
   public static final String TEXTMOD_LAZY_CAPTURE_GROUP = "(.+?)";

   public static boolean bannedFromDocument(String s) {
      return s.contains("+") || s.contains(",") || s.contains(".") || s.contains("#") || s.contains("&") || s.contains(";") || s.contains("~");
   }
}

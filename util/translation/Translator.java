package com.tann.dice.util.translation;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.StringBuilder;
import com.badlogic.gdx.utils.JsonValue.JsonIterator;
import com.tann.dice.gameplay.save.settings.option.OptionLib;
import com.tann.dice.util.ui.TextWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Translator {
   private final boolean translationDebug = false;
   private int disableLevel = 0;
   public static final String ALLOWED_ACCENTS = "éàèùçâêîôûëïüñáíóú¡¿ìòœãõ";
   private final String languageCode;
   private final LinkedHashMap<String, String> translationCache = new LinkedHashMap<String, String>() {
      @Override
      protected boolean removeEldestEntry(Entry<String, String> eldest) {
         return this.size() > 2000;
      }
   };
   private final Map<String, String> literalTranslations = new HashMap<>();
   private final List<MatchTranslation> matchTranslations = new ArrayList<>();
   private final FileHandle output = Gdx.files.local("translation_missing.txt");
   private final Set<String> translationMissing = new HashSet<>();
   private final Matcher initialMatcher;
   private final Matcher finalMatcher;
   private final Matcher initialWhitespaceMatcher;
   private final Matcher finalWhitespaceMatcher;
   private final Matcher tagMatcher;
   private final Matcher wordMatcher;
   private final Matcher whitespaceMatcher;
   private final Matcher contextMatcher;
   private final String[] newlineTypes = new String[]{"n", "nh", "n2"};

   public Translator(String languageCode) {
      this.initialWhitespaceMatcher = Pattern.compile("^\\s+").matcher("");
      this.finalWhitespaceMatcher = Pattern.compile("\\s+$").matcher("");
      this.initialMatcher = Pattern.compile("^(\\s|\\[[^\\]]+\\])+").matcher("");
      this.finalMatcher = Pattern.compile("(\\s|\\[[^\\]]+\\])+$").matcher("");
      this.tagMatcher = Pattern.compile("\\[[^\\]]+\\]").matcher("");
      this.wordMatcher = Pattern.compile("[a-zA-Z][a-zA-Z]").matcher("");
      this.whitespaceMatcher = Pattern.compile("/^\\s+$/").matcher("");
      this.contextMatcher = Pattern.compile("\\(([a-zA-Z0-9]+)\\.java").matcher("");
      FileHandle file = Gdx.files.internal("lang/" + languageCode + ".json");
      if (file.exists()) {
         JsonValue root = new JsonReader().parse(file.readString());
         JsonIterator iterator = root.iterator();

         while (iterator.hasNext()) {
            JsonValue value = iterator.next();
            String key = value.name();
            String translation = value.asString();
            if (key.contains("#{")) {
               this.matchTranslations.add(new MatchTranslation(key, translation));
            } else {
               this.literalTranslations.put(key, translation);
            }
         }

         Collections.sort(this.matchTranslations, new Comparator<MatchTranslation>() {
            public int compare(MatchTranslation o1, MatchTranslation o2) {
               return o1.size() - o2.size();
            }
         });
         this.languageCode = languageCode;
      } else {
         this.languageCode = "en";
      }
   }

   public String getLanguageCode() {
      return this.languageCode;
   }

   public void init() {
   }

   public String translate(String text) {
      return this.translate(text, null);
   }

   public boolean shouldTranslate() {
      return !this.languageCode.equals("en");
   }

   public boolean longMenuItems() {
      return this.languageCode.equals("ru") || this.languageCode.equals("it") || this.languageCode.equals("es") || this.languageCode.equals("pt");
   }

   public boolean longDifficultyNames() {
      return this.languageCode.equals("ru") || this.languageCode.equals("it");
   }

   public boolean longEntityNames() {
      return !this.languageCode.equals("en");
   }

   public static String getLanguageCodeFromOption() {
      if (OptionLib.LANGUAGE.c() == 0) {
         String lang = com.tann.dice.Main.self().control.getDeviceLanguage();
         return lang != null ? lang : "en";
      } else {
         return TextWriter.stripTags(OptionLib.LANGUAGE.cString());
      }
   }

   public void disable() {
      this.disableLevel++;
   }

   public void enable() {
      this.disableLevel--;
   }

   public String translate(String text, String context) {
      if (text == null) {
         return null;
      } else if (this.disableLevel > 0) {
         return text;
      } else if (!this.shouldTranslate()) {
         return text;
      } else if (text.contains("[notranslateall]")) {
         return text;
      } else {
         for (String n : this.newlineTypes) {
            if (text.contains("[" + n + "]")) {
               String[] pieces = text.split("\\[" + n + "\\]");
               StringBuilder sb = new StringBuilder();

               for (String p : pieces) {
                  String t = this.translate(p, context);
                  if (!sb.isEmpty()) {
                     sb.append("[" + n + "]");
                  }

                  sb.append(t);
               }

               return sb.toString();
            }
         }

         if (text.contains("[notranslate]")) {
            return text;
         } else if (text.startsWith("[text]")) {
            return "[text]" + this.translate(text.substring(6), context);
         } else if (text.endsWith("+")) {
            return this.translate(text.substring(0, text.length() - 1), context) + "+";
         } else {
            String stripped = TextWriter.stripTags(text);
            if (!this.wordMatcher.reset(stripped).find()) {
               return text;
            } else if (text.equals("")) {
               return text;
            } else if (this.whitespaceMatcher.reset(text).matches()) {
               return text;
            } else if (stripped.length() == 1) {
               return text;
            } else {
               String prefixWhitespace = "";
               String postfixWhitespace = "";
               this.initialWhitespaceMatcher.reset(text);
               if (this.initialWhitespaceMatcher.find()) {
                  prefixWhitespace = this.initialWhitespaceMatcher.group();
                  text = text.substring(this.initialWhitespaceMatcher.end());
               }

               this.finalWhitespaceMatcher.reset(text);
               if (this.finalWhitespaceMatcher.find()) {
                  postfixWhitespace = this.finalWhitespaceMatcher.group();
                  text = text.substring(0, this.finalWhitespaceMatcher.start());
               }

               if (prefixWhitespace.isEmpty() && postfixWhitespace.isEmpty()) {
                  String prefix = "";
                  String postfix = "";
                  String originalText = text;
                  this.initialMatcher.reset(text);
                  if (this.initialMatcher.find()) {
                     prefix = this.initialMatcher.group();
                     text = text.substring(this.initialMatcher.end());
                  }

                  this.finalMatcher.reset(text);
                  if (this.finalMatcher.find()) {
                     postfix = this.finalMatcher.group();
                     text = text.substring(0, this.finalMatcher.start());
                  }

                  if (this.tagMatcher.reset(text).find()) {
                     prefix = "";
                     postfix = "";
                     text = originalText;
                  }

                  int endI = text.length() - 1;
                  char a = text.charAt(0);
                  char b = text.charAt(endI);
                  if ((a != '(' || b != ')') && (a != '\'' || b != '\'') && (a != '"' || b != '"')) {
                     String translatedText = null;
                     if (this.literalTranslations.containsKey(text)) {
                        translatedText = this.literalTranslations.get(text);
                     } else if (this.translationCache.containsKey(text)) {
                        translatedText = this.translationCache.get(text);
                     } else {
                        for (int i = 0; i < this.matchTranslations.size(); i++) {
                           translatedText = this.matchTranslations.get(i).translate(text);
                           if (translatedText != null) {
                              break;
                           }
                        }

                        this.translationCache.put(text, translatedText);
                     }

                     if (translatedText == null) {
                        translatedText = text;
                     }

                     return prefix + translatedText + postfix;
                  } else {
                     return prefix + a + this.translate(text.substring(1, text.length() - 1), context) + b + postfix;
                  }
               } else {
                  return prefixWhitespace + this.translate(text, context) + postfixWhitespace;
               }
            }
         }
      }
   }
}

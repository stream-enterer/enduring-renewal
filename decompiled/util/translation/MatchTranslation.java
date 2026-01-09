package com.tann.dice.util.translation;

import com.badlogic.gdx.utils.StringBuilder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

class MatchTranslation {
   private final List<MatchTranslatorPiece> keyPieces = new ArrayList<>();
   private final List<MatchTranslatorPiece> translationPieces = new ArrayList<>();

   private static void separateIntoPieces(String source, List<MatchTranslatorPiece> into) {
      StringBuilder sb = new StringBuilder();
      boolean isHash = false;
      boolean isTag = false;

      for (int i = 0; i < source.length(); i++) {
         char c = source.charAt(i);
         if (isHash) {
            if (c == '{') {
               if (sb.length > 0) {
                  into.add(new MatchTranslatorPiece(MatchTranslatorPieceType.Text, sb.toString()));
               }

               sb.setLength(0);
               isTag = true;
               isHash = false;
               continue;
            }

            sb.append('#');
            isHash = false;
         }

         if (isTag) {
            if (c == '}') {
               into.add(new MatchTranslatorPiece(MatchTranslatorPieceType.Tag, sb.toString()));
               sb.setLength(0);
               isTag = false;
            } else {
               sb.append(c);
            }
         } else if (c == '#') {
            isHash = true;
         } else {
            sb.append(c);
         }
      }

      if (sb.length > 0) {
         into.add(new MatchTranslatorPiece(isTag ? MatchTranslatorPieceType.Tag : MatchTranslatorPieceType.Text, sb.toString()));
      }
   }

   MatchTranslation(String key, String translation) {
      separateIntoPieces(key, this.keyPieces);
      separateIntoPieces(translation, this.translationPieces);
   }

   int size() {
      return this.keyPieces.size();
   }

   String translate(String s) {
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.setLength(0);
      stringBuilder.append(s);
      Map<String, String> matches = new HashMap<>();
      boolean reverse = !this.keyPieces.isEmpty()
         && this.keyPieces.get(0).type == MatchTranslatorPieceType.Tag
         && this.keyPieces.get(this.keyPieces.size() - 1).type == MatchTranslatorPieceType.Text;
      int i = reverse ? this.keyPieces.size() - 1 : 0;

      while (i >= 0 && i < this.keyPieces.size()) {
         MatchTranslatorPiece p = this.keyPieces.get(i);
         if (p.type == MatchTranslatorPieceType.Text) {
            if (stringBuilder.length < p.content.length()) {
               return null;
            }

            if (reverse) {
               for (int j = p.content.length() - 1; j >= 0; j--) {
                  char inputChar = stringBuilder.charAt(stringBuilder.length() - 1);
                  char keyChar = p.content.charAt(j);
                  if (inputChar != keyChar) {
                     return null;
                  }

                  stringBuilder.deleteCharAt(stringBuilder.length() - 1);
               }
            } else {
               for (int j = 0; j < p.content.length(); j++) {
                  char inputChar = stringBuilder.charAt(0);
                  char keyChar = p.content.charAt(j);
                  if (inputChar != keyChar) {
                     return null;
                  }

                  stringBuilder.deleteCharAt(0);
               }
            }
         } else {
            Matcher matcher = reverse ? p.tagType.ender : p.tagType.starter;
            matcher.reset(stringBuilder);
            if (!matcher.find()) {
               return null;
            }

            String match = matcher.group();
            matches.put(p.content, match);
            if (reverse) {
               stringBuilder.delete(stringBuilder.length() - match.length(), stringBuilder.length());
            } else {
               stringBuilder.delete(0, match.length());
            }
         }

         if (reverse) {
            i--;
         } else {
            i++;
         }
      }

      if (stringBuilder.length > 0) {
         return null;
      } else {
         stringBuilder.setLength(0);

         for (int ix = 0; ix < this.translationPieces.size(); ix++) {
            MatchTranslatorPiece px = this.translationPieces.get(ix);
            if (px.type == MatchTranslatorPieceType.Text) {
               stringBuilder.append(px.content);
            } else {
               String text = matches.get(px.content);
               if ((px.content.startsWith("*sidetype") || px.content.startsWith("*keyword")) && text.contains("/")) {
                  String[] words = text.split("/");

                  for (int j = 0; j < words.length; j++) {
                     if (j > 0) {
                        stringBuilder.append("/");
                     }

                     stringBuilder.append(com.tann.dice.Main.self().translator.translate(words[j]));
                  }
               } else if (px.content.startsWith("*")) {
                  stringBuilder.append(com.tann.dice.Main.self().translator.translate(text));
               } else if (px.content.startsWith("ord")) {
                  text = matches.get("ord");
                  if (px.content.endsWith("f")) {
                     stringBuilder.append(com.tann.dice.Main.self().translator.translate(text + " (f)"));
                  } else if (px.content.endsWith("fpl")) {
                     stringBuilder.append(com.tann.dice.Main.self().translator.translate(text + " (fpl)"));
                  } else if (px.content.endsWith("pl")) {
                     stringBuilder.append(com.tann.dice.Main.self().translator.translate(text + " (pl)"));
                  } else if (px.content.endsWith("n")) {
                     stringBuilder.append(com.tann.dice.Main.self().translator.translate(text + " (n)"));
                  } else if (px.content.endsWith("num")) {
                     stringBuilder.append(com.tann.dice.Main.self().translator.translate(text + " (num)"));
                  } else {
                     stringBuilder.append(com.tann.dice.Main.self().translator.translate(text));
                  }
               } else {
                  stringBuilder.append(text);
               }
            }
         }

         return stringBuilder.toString();
      }
   }
}

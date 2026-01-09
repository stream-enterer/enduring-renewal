package com.tann.dice.util.translation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

enum TagType {
   Number("-?\\+?\\d+"),
   Ord("\\d+(?:st|nd|rd|th)|zeroth"),
   Word("[\\p{L}\\p{M}\\d]+"),
   Text(".+"),
   Keyword("(?:(?:(?:\\[\\w+\\])?[\\p{L}\\p{M}\\d]+\\[cu\\])+(?:\\[text\\])?\\/?)+"),
   SimpleKeyword("\\[\\w+\\][\\p{L}\\p{M}\\d \\+-]+\\[cu\\]"),
   Sidetype("(?:(?:\\w+|\\[\\w+\\][\\p{L}\\p{M}\\d' -]+\\[cu\\])\\/?)+"),
   Tag("\\[\\w+\\]");

   final String pattern;
   final Matcher starter;
   final Matcher ender;

   private TagType(String pattern) {
      this.pattern = pattern;
      this.starter = Pattern.compile("^" + this.pattern).matcher("");
      this.ender = Pattern.compile(this.pattern + "$").matcher("");
   }
}

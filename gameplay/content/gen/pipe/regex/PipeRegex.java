package com.tann.dice.gameplay.content.gen.pipe.regex;

import com.tann.dice.gameplay.content.gen.pipe.Pipe;
import com.tann.dice.util.Tann;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class PipeRegex<T> extends Pipe<T> {
   final Pattern pattern;
   final Matcher matcher;
   final int numGroups;

   protected PipeRegex(String patternString) {
      this.pattern = Pattern.compile(patternString, 2);
      this.matcher = this.pattern.matcher("");
      this.numGroups = Tann.countCharsInString('(', patternString) - Tann.countStringsInString("\\(", patternString);
   }

   @Override
   protected final boolean nameValid(String name) {
      return this.matcher.reset(name).matches();
   }

   private String[] groups(String input) {
      this.matcher.reset(input).find();
      if (this.matcher.groupCount() != this.numGroups) {
         return null;
      } else {
         String[] result = new String[this.matcher.groupCount()];

         for (int i = 0; i < result.length; i++) {
            result[i] = this.matcher.group(i + 1);
         }

         return result;
      }
   }

   @Override
   protected final T make(String name) {
      String[] groups = this.groups(name);
      if (groups == null) {
         return null;
      } else {
         return bad(groups) ? null : this.internalMake(this.groups(name));
      }
   }

   protected abstract T internalMake(String[] var1);

   @Override
   public String document() {
      return this.pattern.toString();
   }
}

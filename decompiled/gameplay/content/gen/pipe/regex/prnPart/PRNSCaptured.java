package com.tann.dice.gameplay.content.gen.pipe.regex.prnPart;

public class PRNSCaptured extends PRNPart {
   private final String name;

   public PRNSCaptured(String name) {
      this.name = name;
   }

   @Override
   public String regex() {
      return "(.+)";
   }

   @Override
   protected String describe() {
      return this.name;
   }
}

package com.tann.dice.util.ui.choice;

public class CDChoice {
   final String name;
   final Runnable onClick;

   public CDChoice(String name, Runnable onClick) {
      this.name = name;
      this.onClick = onClick;
   }
}

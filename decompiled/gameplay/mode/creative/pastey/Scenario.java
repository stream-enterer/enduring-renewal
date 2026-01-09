package com.tann.dice.gameplay.mode.creative.pastey;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Scenario {
   private String title;
   private String content;

   public Scenario() {
   }

   public Scenario(String title, String content) {
      this.title = title;
      this.content = content;
   }

   public String getTitle() {
      return this.title;
   }

   public String getContent() {
      return this.content;
   }

   public static List<Scenario> getDefault() {
      List<Scenario> result = new ArrayList<>();
      result.addAll(
         Arrays.asList(
            new Scenario(
               "[orange]ludii",
               "`{v:2060i,d:{n:19,p:{h:[ludus,ludus,ludus,ludus,ludus,ludus]},l:{m:[hexia,demon]},sl:19},s:01221334,p:[4The ludii face off against their arch-rival,3]}`"
            ),
            new Scenario(
               "[orange]cascade",
               "`{v:2012i,d:{n:1,p:{h:[Juggler,Juggler,Dancer,Juggler,Juggler]},l:{m:[Thorn,Thorn,Thorn,Thorn,Thorn,Thorn,Thorn]}},s:030500411,p:[4The troupe meets a prickly end,3]}`"
            ),
            new Scenario(
               "[blue]magi",
               "`{v:2012i,d:{n:19,p:{h:[Weaver,Sparky,Cultist,Glacia,Chronos,Ace]},l:{m:[Dragon,Dragon]}},s:030500411,p:[4The magi bite off more than they can chew,3]}`"
            )
         )
      );
      return result;
   }

   public static Scenario makeLethalPuzzle(int index, String author, String content) {
      content = content.replace("p:[", "p:[\"4Lethal Puzzle - defeat all enemies this turn[n2][n2][grey]Submitted by " + author + "\",");
      String title = index == 0 ? "[grey]lethal puzzle 1 (lp1)" : "[grey]lp" + (index + 1);
      return new Scenario(title, content);
   }
}

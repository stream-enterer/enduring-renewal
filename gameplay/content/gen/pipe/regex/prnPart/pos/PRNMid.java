package com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.pos;

import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.PRNS;

public class PRNMid extends PRNS {
   public PRNMid(String s) {
      super("\\." + s + "\\.");
   }
}

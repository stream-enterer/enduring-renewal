package com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.pos;

import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.PRNS;

public class PRNSuff extends PRNS {
   public PRNSuff(String s) {
      super("\\." + s);
   }
}

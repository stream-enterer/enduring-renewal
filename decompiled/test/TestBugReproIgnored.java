package com.tann.dice.test;

import com.tann.dice.gameplay.fightLog.FightLog;
import com.tann.dice.test.util.Skip;
import com.tann.dice.test.util.Test;
import com.tann.dice.test.util.TestUtils;
import com.tann.dice.util.Tann;

public class TestBugReproIgnored {
   @Test
   @Skip
   public static void undyingSoulLink() {
      FightLog f = TestUtils.loadFromString(
         "`{v:2028i,d:{n:1,p:{h:[Fencer,Veteran,Valkyrie,Wraith,Artificer~Soul Link]},m:[Sandstorm++++],l:{m:[Imp,Hexia,Imp]},sl:5},c:[16Z3c,2050,21Z5,22Z5,4,1790,19Z0,308,308,2073,2172,22Z1,2362,2464,4],s:3212450020,p:[1997]}`"
      );
      Tann.assertEquals(
         "Should be 2 living heroes because undying should save soulink?", 2, f.getSnapshot(FightLog.Temporality.Future).getStates(true, false).size()
      );
   }
}

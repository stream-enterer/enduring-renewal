package com.tann.dice.test;

import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.fightLog.FightLog;
import com.tann.dice.test.util.Test;
import com.tann.dice.test.util.TestUtils;
import com.tann.dice.util.Tann;
import java.util.ArrayList;
import java.util.List;

public class TestStates {
   @Test
   public static void test2hpStates() {
      List<String> bad = new ArrayList<>();

      for (String s : new String[]{
         "`{v:3018r,d:{n:7,p:{h:[Rogue,Sinew,Buckle,Splint,Fiend],e:[Glass Helm,Static Tome,Longbow]},l:{m:[Militia,Snake,Goblin,Militia]},sl:7},c:[1403c,18Z4,32Z,302,2074,2171,2280,2351,4],s:410134154,p:[12]}`",
         "`{v:3018r,d:{n:16,p:{h:[Roulette,Brute,Warden,Wraith,Ace],e:[Anchor,Terrarium,Longbow,Splitting Arrows,Early Grave,Crescent Shield,Dragon Pipe]},l:{m:[Spider,Tarantus,Spider,Spider]},sl:16},c:[1420c,1410,2044,2140,2264,2374,4,1401c,1411,2055,2152,22Z3,2351,4],s:523114303,p:[12]}`",
         "`{v:3018r,d:{n:20,p:{h:[Assassin~Overflowing Chalice,Curator~Botany~Singularity,Valkyrie,Doctor~Karma~Syringe,Sorcerer],e:[Friendship Bracelet,Treasure Chest,Iron Heart,Hissing Ring]},l:{m:[Dragon,Caw,Archer]},sl:20},c:[17Z4c,1422,1310,2053,2141,2263,4,17Z1c,1312,2032,2145,2245,4],s:25524111,p:[12]}`"
      }) {
         FightLog state = TestUtils.loadFromString(s);

         for (boolean hero : Tann.BOTH) {
            int num2hp = 0;

            for (EntState entState : state.getSnapshot(FightLog.Temporality.Present).getStates(hero, false)) {
               if (entState.getHp() == 2) {
                  num2hp++;
               }
            }

            if (num2hp != 1) {
               bad.add(s);
            }
         }
      }

      Tann.assertBads(bad);
   }
}

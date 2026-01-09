package com.tann.dice.gameplay.progress.stats;

import com.tann.dice.gameplay.context.DungeonContext;
import com.tann.dice.gameplay.fightLog.EntSideState;
import com.tann.dice.gameplay.progress.StatSnapshot;
import com.tann.dice.gameplay.progress.stats.stat.Stat;
import java.util.List;
import java.util.Map;

public interface StatUpdate {
   void updateAfterCommand(StatSnapshot var1, Map<String, Stat> var2);

   void updateEndOfRound(StatSnapshot var1);

   void updateAllDiceLanded(List<EntSideState> var1);

   void updateDiceRolled(int var1);

   void endOfFight(StatSnapshot var1, boolean var2);

   void endOfRun(DungeonContext var1, boolean var2, boolean var3);
}

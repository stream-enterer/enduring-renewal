package com.tann.dice.gameplay.fightLog.listener;

import com.tann.dice.gameplay.fightLog.FightLog;
import com.tann.dice.gameplay.fightLog.Snapshot;

public interface SnapshotChangeListener {
   void snapshotChanged(FightLog.Temporality var1, Snapshot var2);
}

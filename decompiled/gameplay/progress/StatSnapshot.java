package com.tann.dice.gameplay.progress;

import com.tann.dice.gameplay.context.DungeonContext;
import com.tann.dice.gameplay.fightLog.Snapshot;
import com.tann.dice.gameplay.fightLog.command.Command;
import java.util.List;

public class StatSnapshot {
   public final Command origin;
   public final List<Command> pastCommands;
   public final List<Command> futureCommands;
   public final Snapshot startOfTurn;
   public final Snapshot beforeCommand;
   public final Snapshot afterCommand;
   public final Snapshot future;
   public final Snapshot previousFuture;
   public final DungeonContext context;

   public StatSnapshot(
      Command origin,
      List<Command> pastCommands,
      List<Command> futureCommands,
      Snapshot startOfTurn,
      Snapshot beforeCommand,
      Snapshot afterCommand,
      Snapshot future,
      Snapshot previousFuture,
      DungeonContext dungeonContext
   ) {
      this.origin = origin;
      this.pastCommands = pastCommands;
      this.futureCommands = futureCommands;
      this.startOfTurn = startOfTurn;
      this.beforeCommand = beforeCommand;
      this.afterCommand = afterCommand;
      this.future = future;
      this.previousFuture = previousFuture;
      this.context = dungeonContext;
   }
}

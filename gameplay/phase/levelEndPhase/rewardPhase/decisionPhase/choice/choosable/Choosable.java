package com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.choosable;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.context.DungeonContext;
import com.tann.dice.gameplay.progress.stats.stat.Stat;
import java.util.Map;

public interface Choosable {
   boolean isPositive();

   Color getColour();

   String getSaveString();

   ChoosableType getType();

   void onChoose(DungeonContext var1, int var2);

   void onReject(DungeonContext var1);

   Actor makeChoosableActor(boolean var1, int var2);

   int getTier();

   float getModTier();

   String describe();

   float chance();

   String getTierString();

   String getName();

   boolean encountered(Map<String, Stat> var1);

   int getPicks(Map<String, Stat> var1, boolean var2);

   long getCollisionBits();
}

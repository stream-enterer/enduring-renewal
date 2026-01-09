package com.tann.dice.gameplay.effect.eff.conditionalBonus.conditionalRequirement;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.effect.eff.Eff;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.fightLog.Snapshot;

public interface ConditionalRequirement {
   boolean isValid(Snapshot var1, EntState var2, EntState var3, Eff var4);

   boolean preCalculate();

   String getInvalidString(Eff var1);

   String describe(Eff var1);

   String getBasicString();

   Actor getRestrictionActor();

   boolean isPlural();
}

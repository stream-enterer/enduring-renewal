package com.tann.dice.gameplay.content.ent.die;

import com.badlogic.gdx.math.Vector2;

public interface DieContainer {
   Vector2 getDieHolderLocation(boolean var1);

   void startLockingDie();

   void lockDie();

   void unlockDie();
}

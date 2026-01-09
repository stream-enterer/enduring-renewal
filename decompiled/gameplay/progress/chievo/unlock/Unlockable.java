package com.tann.dice.gameplay.progress.chievo.unlock;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;

public interface Unlockable {
   Actor makeUnlockActor(boolean var1);

   TextureRegion getAchievementIcon();

   String getAchievementIconString();
}

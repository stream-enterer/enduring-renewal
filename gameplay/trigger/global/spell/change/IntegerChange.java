package com.tann.dice.gameplay.trigger.global.spell.change;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;

public abstract class IntegerChange {
   public abstract int affect(int var1);

   public abstract String describe();

   public abstract Actor makeActor(TextureRegion var1);
}

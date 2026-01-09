package com.tann.dice.gameplay.effect.targetable.ability;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.effect.eff.Eff;
import com.tann.dice.gameplay.effect.targetable.Targetable;
import com.tann.dice.gameplay.fightLog.Snapshot;
import com.tann.dice.gameplay.trigger.global.Global;
import com.tann.dice.util.Colours;
import java.util.List;

public abstract class Ability implements Targetable {
   private Color col = Colours.purple;
   protected final Eff effect;
   protected final String title;
   protected final TextureRegion image;
   protected final float pw;

   protected Ability(Eff effect, String title, TextureRegion image) {
      this(effect, title, image, 1.0F);
   }

   protected Ability(Eff effect, String title, TextureRegion image, float pw) {
      this.effect = effect;
      this.title = title;
      this.image = image;
      this.pw = pw;
   }

   public Color getCol() {
      return this.col;
   }

   public void setCol(Color col) {
      this.col = col;
   }

   public TextureRegion getImage() {
      return this.image;
   }

   public String getTitle() {
      return this.title;
   }

   @Override
   public void afterUse(Snapshot snapshot, Eff preDerivedEffect, List<Integer> extraData) {
      snapshot.afterUseAbility(this);
   }

   @Override
   public final Eff getDerivedEffects(Snapshot snapshot) {
      return this.getDerivedEffects(snapshot.getGlobals());
   }

   public abstract Eff getDerivedEffects(List<Global> var1);

   public abstract List<Actor> getCostActors(Snapshot var1, int var2);

   public abstract String describe();

   public abstract Color getIdCol();

   public boolean useImage() {
      return true;
   }

   @Override
   public String toString() {
      return this.getTitle();
   }

   public abstract float getCostFactorInActual(int var1);

   public float getPowerMult() {
      return this.pw;
   }
}

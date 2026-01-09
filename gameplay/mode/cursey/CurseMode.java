package com.tann.dice.gameplay.mode.cursey;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.context.config.ContextConfig;
import com.tann.dice.gameplay.context.config.cursed.CurseConfig;
import com.tann.dice.gameplay.mode.Mode;
import com.tann.dice.gameplay.mode.meta.folder.FolderType;
import com.tann.dice.gameplay.modifier.ModifierPickContext;
import com.tann.dice.gameplay.trigger.Collision;
import com.tann.dice.gameplay.trigger.global.phase.addPhase.phaseGen.PhaseGenerator;
import com.tann.dice.gameplay.trigger.global.phase.addPhase.phaseGen.PhaseGeneratorModifierPick;
import com.tann.dice.util.Colours;
import com.tann.dice.util.ui.TextWriter;
import java.util.Arrays;
import java.util.List;

public class CurseMode extends Mode {
   public CurseMode() {
      super("Cursed");
   }

   @Override
   public String[] getDescriptionLines() {
      return new String[]{
         "infinite play until you lose!",
         "choose a [purple]t1 curse[cu] at the start and after each boss",
         "each loop, [purple]reset your party and items[cu] and gain a [green]t3 blessing"
      };
   }

   @Override
   protected List<ContextConfig> makeAllConfigs() {
      return Arrays.asList(new CurseConfig());
   }

   @Override
   public Color getColour() {
      return Colours.purple;
   }

   @Override
   public String getSaveKey() {
      return "curse3";
   }

   @Override
   public Actor makeWinsActor(ContextConfig config) {
      CurseConfig cc = (CurseConfig)this.getConfigs().get(0);
      int furthestReached = cc.getFurthestReached();
      return (Actor)(furthestReached <= 0 ? new Actor() : new TextWriter("[notranslate][yellow]Highscore: " + furthestReached, 5000, Colours.purple, 3));
   }

   public static PhaseGenerator makeBlessingPick() {
      return makeBlessingPick(1);
   }

   public static PhaseGenerator makeBlessingPick(int amt) {
      return makeBlessingPick(amt, 3);
   }

   public static PhaseGenerator makeBlessingPick(int amt, int tier) {
      return new PhaseGeneratorModifierPick(3 * amt, amt, tier, true, ModifierPickContext.Cursed);
   }

   @Override
   public FolderType getFolderType() {
      return FolderType.cursed;
   }

   @Override
   public long getBannedCollisionBits() {
      return getCollisionBitStatic();
   }

   public static long getCollisionBitStatic() {
      return Collision.MODIFIER | Collision.CURSED_MODE;
   }
}

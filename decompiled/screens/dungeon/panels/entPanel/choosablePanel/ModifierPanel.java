package com.tann.dice.screens.dungeon.panels.entPanel.choosablePanel;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.effect.eff.Eff;
import com.tann.dice.gameplay.effect.eff.keyword.Keyword;
import com.tann.dice.gameplay.modifier.Modifier;
import com.tann.dice.gameplay.phase.Phase;
import com.tann.dice.gameplay.phase.PhaseManager;
import com.tann.dice.gameplay.trigger.global.Global;
import com.tann.dice.util.ui.TextWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ModifierPanel extends ConcisePanel {
   public ModifierPanel(Modifier modifier, boolean big) {
      super(modifier, big);
   }

   private Modifier getModifier() {
      return (Modifier)this.choosable;
   }

   @Override
   protected boolean hasKeywords() {
      return this.getModifier().getReferencedKeywords().size() > 0;
   }

   @Override
   protected ConcisePanel makeCopy(boolean big) {
      return new ModifierPanel(this.getModifier(), big);
   }

   @Override
   protected String getFullDescription() {
      Modifier m = this.getModifier();
      return m.getFullDescription();
   }

   @Override
   protected Color getBorderColour() {
      return this.getModifier().getBorderColour();
   }

   @Override
   protected List<Keyword> getReferencedKeywords() {
      return this.getModifier().getReferencedKeywords();
   }

   @Override
   protected List<Actor> getMiddleActors(boolean big) {
      List<Actor> actors = getMiddleActors(new ArrayList<>(this.getModifier().getGlobals()), big);
      Phase p = PhaseManager.get().getPhase();
      return actors;
   }

   @Override
   protected String getTitle() {
      return TextWriter.rebracketTags(this.getModifier().getName(true));
   }

   @Override
   protected List<Actor> getExtraTopActors() {
      return Arrays.asList(new TextWriter(this.getModifier().getTierString(), 999, this.getBorderColour(), 2));
   }

   @Override
   protected TextureRegion getDescriptionImage() {
      for (Global g : this.getModifier().getGlobals()) {
         if (g.getSpecialImage() != null) {
            return g.getSpecialImage();
         }
      }

      return super.getDescriptionImage();
   }

   @Override
   protected Eff getSingleEffOrNull(Keyword k) {
      return this.getModifier().getSingleEffOrNull();
   }
}

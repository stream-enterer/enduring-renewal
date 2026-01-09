package com.tann.dice.screens.dungeon.panels.entPanel.choosablePanel;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.content.ent.die.side.EntSide;
import com.tann.dice.gameplay.content.item.Item;
import com.tann.dice.gameplay.effect.eff.Eff;
import com.tann.dice.gameplay.effect.eff.keyword.Keyword;
import com.tann.dice.gameplay.trigger.Trigger;
import com.tann.dice.gameplay.trigger.personal.Personal;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.AffectSides;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.AffectSideEffect;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.ReplaceWith;
import com.tann.dice.gameplay.trigger.personal.linked.TriggerPersonalToGlobal;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Pixl;
import com.tann.dice.util.ui.TextWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ItemPanel extends ConcisePanel {
   public ItemPanel(Item item, boolean big) {
      super(item, big);
      if (item == null) {
         throw new RuntimeException("hm>");
      }
   }

   public ItemPanel(Item item) {
      this(item, false);
   }

   public Item getItem() {
      return (Item)this.choosable;
   }

   @Override
   protected boolean hasKeywords() {
      return this.getItem().getReferencedKeywords().size() > 0;
   }

   @Override
   protected ConcisePanel makeCopy(boolean big) {
      return new ItemPanel(this.getItem(), big);
   }

   @Override
   protected String getFullDescription() {
      return Trigger.describeTriggers(new ArrayList<>(this.getItem().getPersonals()));
   }

   @Override
   protected Color getBorderColour() {
      return Colours.grey;
   }

   @Override
   protected List<Keyword> getReferencedKeywords() {
      return this.getItem().getReferencedKeywords();
   }

   @Override
   protected List<Actor> getMiddleActors(boolean big) {
      return getMiddleActors(new ArrayList<>(this.getItem().getPersonals()), big);
   }

   @Override
   protected String getTitle() {
      return "[notranslate]" + TextWriter.rebracketTags(this.getItem().getName(true));
   }

   @Override
   protected List<Actor> getExtraTopActors() {
      Actor image = new Pixl(0, 1).border(Colours.dark, Colours.grey, 1).actor(this.getItem().makeImageActor()).pix();
      Actor tier = new TextWriter(this.getItem().getTierString(), 999, Colours.grey, 2);
      return Arrays.asList(image, tier);
   }

   @Override
   protected Eff getSingleEffOrNull(Keyword k) {
      for (Personal pt : this.getItem().getPersonals()) {
         Eff e = pt.getSingleEffOrNull();
         if (e != null) {
            return e;
         }

         if (pt instanceof AffectSides) {
            AffectSides tas = (AffectSides)pt;

            for (AffectSideEffect ase : tas.getEffects()) {
               if (ase instanceof ReplaceWith) {
                  ReplaceWith rw = (ReplaceWith)ase;

                  for (EntSide es : rw.getReplaceSides()) {
                     if (es.getBaseEffect().hasKeyword(k)) {
                        return es.getBaseEffect();
                     }
                  }
               }
            }
         }

         if (pt instanceof TriggerPersonalToGlobal) {
            e = pt.getGlobalFromPersonalTrigger().getSingleEffOrNull();
            if (e != null) {
               return e;
            }
         }
      }

      return null;
   }

   @Override
   protected TextureRegion getDescriptionImage() {
      for (Personal p : this.getItem().getPersonals()) {
         if (p.getSpecialImage() != null) {
            return p.getSpecialImage();
         }
      }

      return super.getDescriptionImage();
   }
}

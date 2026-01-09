package com.tann.dice.screens.dungeon.panels.Explanel;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.Array.ArrayIterator;
import com.tann.dice.gameplay.content.ent.Ent;
import com.tann.dice.gameplay.content.ent.die.side.EntSide;
import com.tann.dice.gameplay.effect.Buff;
import com.tann.dice.gameplay.effect.Trait;
import com.tann.dice.gameplay.effect.eff.Eff;
import com.tann.dice.gameplay.effect.eff.keyword.KUtils;
import com.tann.dice.gameplay.effect.eff.keyword.Keyword;
import com.tann.dice.gameplay.effect.targetable.Targetable;
import com.tann.dice.gameplay.effect.targetable.ability.Ability;
import com.tann.dice.gameplay.effect.targetable.ability.spell.SpellUtils;
import com.tann.dice.gameplay.fightLog.EntSideState;
import com.tann.dice.gameplay.fightLog.FightLog;
import com.tann.dice.gameplay.trigger.personal.Personal;
import com.tann.dice.screens.dungeon.DungeonScreen;
import com.tann.dice.screens.dungeon.TargetingManager;
import com.tann.dice.screens.dungeon.panels.DieSidePanel;
import com.tann.dice.screens.dungeon.panels.ExplanelReposition;
import com.tann.dice.screens.dungeon.panels.entPanel.EntPanelCombat;
import com.tann.dice.util.AlternativePop;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Draw;
import com.tann.dice.util.Extendobar;
import com.tann.dice.util.Pixl;
import com.tann.dice.util.PostPop;
import com.tann.dice.util.Tann;
import com.tann.dice.util.ui.Button;
import com.tann.dice.util.ui.TextWriter;
import java.util.ArrayList;
import java.util.List;

public class Explanel extends InfoPanel implements PostPop, AlternativePop {
   private Object showing;
   Color border = Colours.light;
   NinePatch ninePatch;
   List<Actor> belowActors = new ArrayList<>();
   List<Actor> extras = new ArrayList<>();

   public Explanel() {
      this.setTransform(false);
   }

   public Explanel(EntSide side, Ent source) {
      this();
      int sideTextWidth = 85;
      this.showing = side;
      this.border = source == null ? Colours.purple : source.getColour();
      int gap = 3;
      Eff e = side.getBaseEffect();
      if (source != null) {
         EntSideState ess = side.findState(FightLog.Temporality.Visual, source);
         if (ess != null) {
            e = ess.getCalculatedEffect();
         }
      }

      String dsc = "[text]" + side.toString(source);
      TextWriter tx = new TextWriter(dsc, 85);
      if (tx.getWidth() > 60.0F && tx.getHeight() < 8.0F) {
         tx = new TextWriter(dsc, 51);
      }

      Tann.become(this, new Pixl(gap, gap).actor(new DieSidePanel(side, source)).actor(tx).pix());
      this.addKeywords(e.getKeywordsForDisplay(true), e);
   }

   public Explanel(Ability ability, boolean showKeywords) {
      this();
      this.showing = ability;
      this.ninePatch = Button.ninePatchAbility;
      this.border = ability.getCol();
      int TOP_GAP_L = 6;
      int TOP_GAP_R = 7;
      Pixl p = new Pixl(3, 2);
      if (ability.useImage()) {
         p.image(ability.getImage()).gap(6);
      }

      p.text(TextWriter.getTag(ability.getIdCol()) + ability.getTitle()).gap(7);
      p.actor(SpellUtils.makeAbilityCostActor(ability));
      p.row(2).actor(new Extendobar(this.border)).row(1);
      p.actor(new TextWriter("[text]" + ability.describe(), 85)).row(0);
      Tann.become(this, p.pix());
      if (showKeywords) {
         this.addKeywords(ability.getDerivedEffects());
      }
   }

   public Explanel(Trait trait, Ent ent, float width) {
      this(trait.personal, ent, false, width);
   }

   public Explanel(Personal personal, Ent ent, Boolean incoming, float width) {
      this();
      this.showing = personal;
      int borderSize = 2;
      String text = "[notranslateall]";
      TextureRegion triggerImage = personal.getImage();
      if (personal.hasImage() && personal.showInDiePanel() && personal.showImageInDiePanelTitle()) {
         text = text + TextWriter.getTag(personal.getImageCol()) + "[image][cu][h]: ";
      }

      text = text + "[text]" + com.tann.dice.Main.t(personal.describeForTriggerPanel());
      text = text + com.tann.dice.Main.t(this.getIncomingText(incoming, personal));
      if (!personal.hasImage()) {
         triggerImage = personal.getSpecialImage();
      }

      TextWriter tw = new TextWriter(text, (int)width - borderSize * 2, ent.getColour(), borderSize, triggerImage);
      tw.setWidth(width);
      this.addActor(tw);
      this.setSize(tw.getWidth(), tw.getHeight());
      if (Tann.notNullOrEmpty(personal.getReferencedKeywords())) {
         this.addKeywords(personal.getReferencedKeywords(), null);
      }
   }

   private String getIncomingText(Boolean incoming, Personal pt) {
      Buff b = pt.buff;
      if (incoming == null) {
         if (b == null) {
            return "Hm!?? incoming null and buff null";
         } else {
            return !b.isInfinite() ? " [yellow](incoming)[cu]" : " [yellow](partly-incoming)[cu]";
         }
      } else {
         return incoming ? " [yellow](incoming)[cu]" : "";
      }
   }

   private void addKeywords(Eff e) {
      this.addKeywords(e.getKeywordsForDisplay(true), e);
   }

   private void addKeywords(List<Keyword> keywords, Eff source) {
      int y = -2;

      for (int i = 0; i < keywords.size(); i++) {
         Keyword k = keywords.get(i);
         Actor a = KUtils.makeActor(k, source);
         this.addActor(a);
         y = (int)(y - (a.getHeight() - 1.0F));
         a.setPosition((int)(this.getWidth() / 2.0F - a.getWidth() / 2.0F), y);
         this.extras.add(a);
         this.belowActors.add(a);
      }
   }

   public void draw(Batch batch, float parentAlpha) {
      if (this.border != null) {
         if (this.ninePatch != null) {
            Draw.fillActor(batch, this, Colours.dark, 1);
            batch.setColor(this.border);
            this.ninePatch.draw(batch, this.getX(), this.getY(), this.getWidth(), this.getHeight());
         } else {
            Draw.fillActor(batch, this, Colours.dark, this.border, 1);
         }
      }

      super.draw(batch, parentAlpha);
   }

   @Override
   public void postPop() {
      if (DungeonScreen.get() != null) {
         TargetingManager targetingManager = DungeonScreen.get().targetingManager;
         Targetable targetable = targetingManager.getSelectedTargetable();
         if (targetable != null) {
            Ent source = targetable.getSource();
            if (source != null) {
               source.getEntPanel().slideBack();
            }
         }

         targetingManager.deselectTargetable();
         Actor top = com.tann.dice.Main.getCurrentScreen().getTopPushedActor();
         if (top == null || !(top instanceof EntPanelInventory)) {
            targetingManager.hideTargetingArrows();
         }
      }
   }

   public boolean isShowing(Object o) {
      return o == this.showing;
   }

   public void reposition() {
      Actor a = com.tann.dice.Main.getCurrentScreen().getTopPushedActor();
      if (!(a instanceof ExplanelReposition)) {
         a = com.tann.dice.Main.getCurrentScreen();
      }

      if (a instanceof ExplanelReposition) {
         ((ExplanelReposition)a).repositionExplanel(this);
      }
   }

   public void addDialog(String noTargetsString, boolean above) {
      this.addDialog(new TextWriter(noTargetsString, Integer.MAX_VALUE, Colours.purple, 2), above);
   }

   public void addDialog(Actor actor, boolean above) {
      int yPos = 0;
      if (!above) {
         yPos = (int)(-actor.getHeight()) - 1;
      } else {
         yPos = (int)this.getHeight() + 1;
      }

      ArrayIterator var4 = this.getChildren().iterator();

      while (var4.hasNext()) {
         Actor a = (Actor)var4.next();
         if (above) {
            yPos = (int)Math.max(a.getY() + a.getHeight(), (float)yPos);
         } else {
            yPos = (int)Math.min(a.getY() - actor.getHeight(), (float)yPos);
         }
      }

      actor.setPosition((int)(this.getWidth() / 2.0F - actor.getWidth() / 2.0F), yPos);
      this.extras.add(actor);
      this.addActor(actor);
   }

   public int getFullHeight() {
      return (int)(this.getHeight() + this.getExtraBelowExtent());
   }

   public int getExtraBelowExtent() {
      int botY = 0;
      ArrayIterator var2 = this.getChildren().iterator();

      while (var2.hasNext()) {
         Actor a = (Actor)var2.next();
         botY = (int)Math.min((float)botY, a.getY());
      }

      return -botY;
   }

   public float getMaxWidth() {
      float width = this.getWidth();
      ArrayIterator var2 = this.getChildren().iterator();

      while (var2.hasNext()) {
         Actor a = (Actor)var2.next();
         width = Math.max(a.getWidth(), width);
      }

      return width;
   }

   public void setBorder(Color border) {
      this.border = border;
   }

   public void addPassives(Ent ent) {
      int numTraits = 0;
      Pixl p = new Pixl(0);

      for (Personal t : ent.getState(FightLog.Temporality.Visual).getActivePersonals()) {
         if (t.showInDiePanel()) {
            numTraits++;
            if (t.getTrait() != null) {
               p.actor(new Explanel(t.getTrait(), ent, Math.min(this.getWidth(), 100.0F)));
            } else {
               p.actor(new Explanel(t, ent, false, Math.min(this.getWidth(), 100.0F)));
            }

            p.row(-1);
         }
      }

      if (numTraits > 0) {
         EntPanelCombat ep = ent.getEntPanel();
         Vector2 explanelPos = Tann.getAbsoluteCoordinates(this).cpy();
         Vector2 panelPos = Tann.getAbsoluteCoordinates(ep).cpy();
         Vector2 relative = panelPos.sub(explanelPos);
         Group allTraits = p.pix();
         int xPos = (int)(relative.x + ep.getWidth() / 2.0F - allTraits.getWidth() / 2.0F);
         xPos = (int)Math.min((float)xPos, com.tann.dice.Main.width - allTraits.getWidth() - explanelPos.x);
         int yPos = (int)(relative.y - allTraits.getHeight() + 1.0F);
         allTraits.setPosition(xPos, yPos);
         this.addActor(allTraits);
      }
   }

   public Group treatExtrasAsMain() {
      Pixl p = new Pixl(1);

      for (Actor a : this.extras) {
         a.remove();
         p.actor(a).row();
      }

      return p.pix();
   }

   @Override
   public boolean alternativePop() {
      for (Actor a : this.extras) {
         a.remove();
      }

      return false;
   }
}

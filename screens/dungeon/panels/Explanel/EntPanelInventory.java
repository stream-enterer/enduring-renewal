package com.tann.dice.screens.dungeon.panels.Explanel;

import com.badlogic.gdx.Input.TextInputListener;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.tann.dice.gameplay.content.ent.Ent;
import com.tann.dice.gameplay.content.ent.EntSize;
import com.tann.dice.gameplay.content.ent.Hero;
import com.tann.dice.gameplay.content.ent.die.side.EntSide;
import com.tann.dice.gameplay.content.ent.type.HeroType;
import com.tann.dice.gameplay.content.ent.type.lib.HeroTypeLib;
import com.tann.dice.gameplay.content.ent.type.lib.HeroTypeUtils;
import com.tann.dice.gameplay.content.gen.pipe.entity.hero.PipeHeroReplica;
import com.tann.dice.gameplay.effect.Trait;
import com.tann.dice.gameplay.effect.targetable.ability.Ability;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.fightLog.FightLog;
import com.tann.dice.gameplay.mode.Mode;
import com.tann.dice.gameplay.mode.creative.WishMode;
import com.tann.dice.gameplay.save.settings.option.OptionLib;
import com.tann.dice.gameplay.save.settings.option.OptionUtils;
import com.tann.dice.gameplay.trigger.personal.Personal;
import com.tann.dice.screens.dungeon.DungeonScreen;
import com.tann.dice.screens.dungeon.panels.DieSidePanel;
import com.tann.dice.screens.dungeon.panels.ExplanelReposition;
import com.tann.dice.screens.dungeon.panels.book.page.stuffPage.APIUtils;
import com.tann.dice.screens.dungeon.panels.entPanel.EntPanelCombat;
import com.tann.dice.screens.dungeon.panels.entPanel.choosablePanel.ConcisePanel;
import com.tann.dice.screens.dungeon.panels.threeD.DieSpinner;
import com.tann.dice.screens.generalPanels.PartyManagementPanel;
import com.tann.dice.statics.Images;
import com.tann.dice.statics.sound.Sounds;
import com.tann.dice.util.AlternativePop;
import com.tann.dice.util.Chrono;
import com.tann.dice.util.Colours;
import com.tann.dice.util.CopyButtonHolder;
import com.tann.dice.util.ImageActor;
import com.tann.dice.util.Pixl;
import com.tann.dice.util.PostPop;
import com.tann.dice.util.Rectactor;
import com.tann.dice.util.Render3D;
import com.tann.dice.util.Tann;
import com.tann.dice.util.lang.Words;
import com.tann.dice.util.listener.TannListener;
import com.tann.dice.util.ui.Flasher;
import com.tann.dice.util.ui.Glowverlay;
import com.tann.dice.util.ui.TextWriter;
import com.tann.dice.util.ui.resolver.HeroTypeResolver;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EntPanelInventory extends InfoPanel implements PostPop, ExplanelReposition, AlternativePop, CopyButtonHolder, Render3D {
   public Ent ent;
   Group hpWriter;
   NetPanel netPanel;
   Group explanelsGroup;
   final boolean showPassives;
   private static final int gap = 3;
   List<Actor> traitActors = new ArrayList<>();
   Group portraitGroup;
   DieSpinner dieSpinner;
   static final int TEXT_HEIGHT = 10;
   static final int SPINNER_GAP = 4;
   private boolean dirty = false;
   boolean traitsVisible = true;

   public EntPanelInventory(Ent ent) {
      this(ent, true);
   }

   public EntPanelInventory(Ent ent, boolean showPassives) {
      this.showPassives = showPassives;
      this.ent = ent;
      this.addListener(new TannListener() {
         @Override
         public boolean action(int button, int pointer, float x, float y) {
            boolean top = y / EntPanelInventory.this.getHeight() > 0.4;
            if (com.tann.dice.Main.getCurrentScreen().getTopPushedActor() instanceof Explanel) {
               com.tann.dice.Main.getCurrentScreen().popSingleLight();
               return true;
            } else {
               if (com.tann.dice.Main.getCurrentScreen().popAllLight()) {
                  Sounds.playSound(Sounds.pop);
               }

               return false;
            }
         }
      });
      this.layout();
   }

   public void act(float delta) {
      super.act(delta);
   }

   public void layout() {
      DungeonScreen ds = DungeonScreen.get();
      this.layout(PartyManagementPanel.isTinyPanels() && ds != null && ds.partyManagementPanel != null && ds.partyManagementPanel.hasParent());
   }

   public void layout(boolean tiny) {
      tiny |= OptionLib.DIE_PANEL_TINY.c() | OptionLib.HIDE_SPINNERS.c();
      this.dirty = false;
      this.traitActors.clear();
      this.clearChildren();
      EntState visualState = this.ent.getState(FightLog.Temporality.Visual);
      if (visualState == null) {
         visualState = this.ent.getBlankState();
      }

      if (this.ent.isPlayer() && this.ent.getSize() == EntSize.reg) {
         this.playerLayout(visualState, tiny);
      } else {
         this.monsterLayout(visualState);
      }

      int y = 0;
      ArrayList<Actor> actors = new ArrayList<>();
      if (this.showPassives) {
         List<Actor> potentialTraitActors = makeTraitActors((int)this.getWidth(), visualState, this.ent);
         if (Tann.totalHeight(potentialTraitActors) > 50) {
            List<Actor> tmp = makeTraitActors((int)Math.min(com.tann.dice.Main.width * 0.9F, this.getWidth() * 2.0F), visualState, this.ent);
            if (Tann.totalHeight(tmp) < Tann.totalHeight(potentialTraitActors)) {
               potentialTraitActors = tmp;
            }
         }

         this.traitActors.addAll(potentialTraitActors);
         actors.addAll(potentialTraitActors);
      }

      this.explanelsGroup = Tann.makeGroup();

      for (Actor a : actors) {
         this.explanelsGroup.addActor(a);
         y = (int)(y - (a.getHeight() - 1.0F));
         a.setPosition((int)(this.getWidth() / 2.0F - a.getWidth() / 2.0F), y);
      }

      this.addActor(this.explanelsGroup);
      this.updateTraitVisibility();
      if (OptionUtils.shouldShowCopy()) {
         this.addCopyButton();
      }

      if (shouldAddSidesButton(this.ent)) {
         this.addSidesButton();
      }
   }

   private static boolean shouldAddSidesButton(Ent ent) {
      return ent.name.contains("facade");
   }

   @Override
   public void addCopyButton() {
      APIUtils.addCopyButton(this, this.ent.getName(false));
   }

   private void addSidesButton() {
      ImageActor a = new ImageActor(Images.copyButton, true);
      this.addActor(a);
      a.setX(this.getWidth() - a.getWidth());
      a.addListener(new TannListener() {
         @Override
         public boolean action(int button, int pointer, float x, float y) {
            if (x - y < 0.0F) {
               return false;
            } else {
               Actor ax = EntPanelInventory.this.makeSidesActor();
               com.tann.dice.Main.getCurrentScreen().pushAndCenter(ax, 0.5F);
               return true;
            }
         }
      });
   }

   private Actor makeSidesActor() {
      int secGap = 3;
      Pixl p = new Pixl(-1, 3).border(Colours.dark);
      p.actor(new EntPanelInventory(this.ent.entType.makeEnt(), false)).row(3);
      Set<String> hashes = new HashSet<>();
      EntSide[] sides = PipeHeroReplica.sidesFromHero(this.ent);
      int index = 0;

      for (EntSide side : sides) {
         EntSide testSide = side.withValue(1);
         String desc = testSide.getBaseEffect().describe();
         String hash = desc + testSide.getTexture().hashCode();
         if (!hashes.contains(hash) && !desc.equals("blank")) {
            hashes.add(hash);
            Explanel ep = new Explanel(side, this.ent);
            ep.alternativePop();
            p.actor(ep);
            if (++index % 2 == 0) {
               p.row();
            }
         }
      }

      List<Actor> extras = new ArrayList<>();
      int mw = 150;

      for (Trait trait : this.ent.traits) {
         if (NetPanel.showTraitInNet(trait)) {
            Ability a = trait.personal.getAbility();
            if (a != null) {
               extras.add(new Explanel(a, false));
            } else {
               extras.add(new Explanel(trait, this.ent, 75.0F));
            }
         }
      }

      if (extras.size() > 0) {
         p.row(3).listActor(extras, 3, 150);
      }

      return p.pix();
   }

   private static List<Actor> makeTraitActors(int width, EntState visualState, Ent ent) {
      List<Actor> actors = new ArrayList<>();

      for (Personal t : ent.getEntPanel().getAllDescribableTriggers()) {
         if (t.showInDiePanel()) {
            if (t.getTrait() == null) {
               Boolean incoming = Personal.treatAsIncoming(t, visualState.getActivePersonals());
               if (incoming == null || !incoming || t.showAsIncoming()) {
                  Explanel e = new Explanel(t, ent, incoming, width);
                  actors.add(e);
               }
            } else if (t.getTrait().visible) {
               Explanel e = new Explanel(t.getTrait(), ent, width);
               actors.add(e);
            }
         }
      }

      return actors;
   }

   public Actor getFullActor() {
      if (this.traitsVisible && this.traitActors.size() != 0) {
         Pixl p = new Pixl(-1).actor(this);

         for (Actor a : this.traitActors) {
            p.row().actor(a);
         }

         return p.pix();
      } else {
         return this;
      }
   }

   private void playerLayout(EntState visualState, boolean tiny) {
      this.setSize(tiny ? 71.0F : 96.0F, 61.0F);
      Rectactor.fill(this, this.ent.getColour());
      TextureRegion portrait = this.ent.getPortrait();
      int pWidth = 0;
      this.portraitGroup = new Pixl(1, 2).border(Colours.dark, this.ent.getColour(), 1).image(portrait).pix();
      this.addActor(this.portraitGroup);
      if (tiny) {
         this.portraitGroup.setPosition(this.getWidth() - this.portraitGroup.getWidth(), this.getHeight() - this.portraitGroup.getHeight());
      } else {
         this.portraitGroup.setPosition(0.0F, this.getHeight() - this.portraitGroup.getHeight());
         pWidth = (int)this.portraitGroup.getWidth();
      }

      if (DungeonScreen.isWish()) {
         this.addWishListener(this.portraitGroup);
      }

      Actor levelTag = makeLevelTag((Hero)this.ent);
      this.addActor(levelTag);
      levelTag.setPosition(pWidth - (tiny ? 0 : 1), this.getHeight() - 10.0F);
      int textOffset = (int)(levelTag.getWidth() - 1.0F);
      int textboxWidth = (int)(this.getWidth() - pWidth + (tiny ? 0 : 1) - textOffset);
      if (((Hero)this.ent).isDiedLastRound()) {
         ImageActor died = new ImageActor(Images.eq_skullWhite, Colours.withAlpha(Colours.grey, 0.6F).cpy());
         this.addActor(died);
         int skullGap = 1;
         died.setPosition(levelTag.getX() + 1.0F + 1.0F, levelTag.getY() - died.getHeight() - 1.0F);
         died.addListener(new TannListener() {
            @Override
            public boolean info(int button, float x, float y) {
               Actor a = new Pixl(3, 3).border(Colours.grey).text("Heroes defeated last fight return with half hp", 85).pix();
               com.tann.dice.Main.getCurrentScreen().pushAndCenter(a);
               return true;
            }
         });
      }

      Group textBox = Tann.makeGroup(textboxWidth, 10);
      Rectactor.fill(textBox, this.ent.getColour());
      int hpw = tiny ? (int)(this.getWidth() - this.portraitGroup.getWidth() - levelTag.getWidth()) : this.getHpWidth();
      this.hpWriter = this.getTitle(getHpString(visualState, tiny), hpw, !tiny);
      textBox.addActor(this.hpWriter);
      Tann.center(this.hpWriter);
      if (tiny) {
         this.hpWriter.setX((int)(this.hpWriter.getX() - this.portraitGroup.getWidth() / 2.0F + 1.0F));
      }

      this.addActor(textBox);
      textBox.setPosition(this.getWidth() - textboxWidth, this.getHeight() - textBox.getHeight());
      this.addNameChangeListener(textBox);
      if (!tiny) {
         float av = this.getHeight() - this.portraitGroup.getHeight() + 1.0F;
         this.dieSpinner = new DieSpinner(this.ent.getDie(), this.ent.getSize().getPixels() * 1.5F);
         this.addActor(this.dieSpinner);
         this.dieSpinner.setPosition(4.0F, (int)(av / 2.0F - this.dieSpinner.getHeight() / 2.0F));
      }

      this.netPanel = new NetPanel(this.ent, false);
      this.addActor(this.netPanel);
      float hAv = this.getHeight() - textBox.getHeight() + 1.0F;
      this.netPanel
         .setPosition(tiny ? 5.0F : (int)(this.dieSpinner.getX() + this.dieSpinner.getWidth() + 4.0F), (int)(hAv / 2.0F - this.netPanel.getHeight() / 2.0F));
      if (tiny) {
         this.portraitGroup.setZIndex(textBox.getZIndex() + 1);
         String hps = getHpString(visualState, tiny);
         Group a = new Pixl(0, 3).border(this.ent.getColour()).text(hps).pix();
         this.hpWriter = a;
         a.setTouchable(Touchable.disabled);
         this.addActor(a);
         a.setX(this.getWidth() - a.getWidth());
         this.netPanel.toFront();
      }
   }

   private void addWishListener(final Group g) {
      g.addListener(new TannListener() {
         @Override
         public boolean action(int button, int pointer, float x, float y) {
            if (!Tann.hasParent(g, PartyManagementPanel.class)) {
               return false;
            } else {
               (new HeroTypeResolver() {
                  public void resolve(HeroType heroType) {
                     if (heroType != null && !heroType.isMissingno()) {
                        DungeonScreen ds = DungeonScreen.get();
                        if (ds != null) {
                           Hero h = (Hero)EntPanelInventory.this.ent;
                           h.levelUpTo(heroType, DungeonScreen.getCurrentContextIfInGame());
                           ds.save();
                           WishMode.makeWishSound();
                        }
                     } else {
                        Sounds.playSound(Sounds.error);
                     }
                  }
               }).activate(Mode.WISH.wishFor("levelup", Colours.yellow));
               return true;
            }
         }
      });
   }

   private void addNameChangeListener(final Group g) {
      g.addListener(new TannListener() {
         @Override
         public boolean action(int button, int pointer, float x, float y) {
            if (!Tann.hasParent(g, PartyManagementPanel.class)) {
               return false;
            } else {
               com.tann.dice.Main.self().control.textInput(new TextInputListener() {
                  public void input(String text) {
                     if (text != null && !text.isEmpty()) {
                        DungeonScreen ds = DungeonScreen.get();
                        if (ds != null) {
                           Hero h = (Hero)EntPanelInventory.this.ent;
                           text = Tann.makeEllipses(text, 100);
                           String safeText = HeroTypeUtils.safeHeroName(text);
                           String oldName = h.getName(false);
                           oldName = oldName.replaceAll("\\.i\\.name tag\\.n\\.[^.]*", "");
                           HeroType newHero = HeroTypeLib.byName(oldName + ".i.name tag.n." + safeText);
                           if (newHero.isMissingno()) {
                              return;
                           }

                           h.levelUpTo(newHero, null);
                           ds.save();
                        }
                     }
                  }

                  public void canceled() {
                  }
               }, "rename", null, null);
               return true;
            }
         }
      });
   }

   private int getHpWidth() {
      int bonus = 6;
      if (this.ent instanceof Hero) {
         bonus += (int)makeLevelTag((Hero)this.ent).getWidth();
      }

      int pnw;
      switch (this.ent.getSize()) {
         case small:
            pnw = 72;
            break;
         case reg:
            pnw = 94;
            break;
         case big:
            pnw = 91;
            break;
         case huge:
            pnw = 115;
            break;
         default:
            throw new RuntimeException("unset size: " + this.ent.getSize());
      }

      return pnw - this.ent.getPortrait().getRegionWidth() - bonus;
   }

   public static Actor makeLevelTag(Hero ent) {
      return makeLevelTag(ent.getLevel(), ent.getHeroCol().col);
   }

   public static Actor makeLevelTag(int tier, Color col) {
      String levelText = TextWriter.getTag(col) + Words.getTierString(tier);
      TextWriter tw = new TextWriter(levelText, 999);
      Rectactor ra = new Rectactor((int)tw.getWidth() + 4, 10, col);
      Group g = Tann.makeGroup(ra);
      g.addActor(tw);
      Tann.center(tw);
      return g;
   }

   private Group getTitle(String hpString, int width, boolean showHp) {
      TextWriter hp = new TextWriter(hpString);
      Actor left = ConcisePanel.makeTitle(
         "[notranslate]" + ConcisePanel.entName(this.ent), this.ent.getColour(), (int)(width - (showHp ? hp.getWidth() : 0.0F)), false
      );
      int maxHpGap = 5;
      return new Pixl(0)
         .actor(left)
         .actor(showHp ? new Pixl().gap(Math.max(0, Math.min(5, (int)(width - left.getWidth() - hp.getWidth())))).actor(hp) : null)
         .pix();
   }

   private void monsterLayout(EntState visualState) {
      ImageActor portraitImage = new ImageActor(this.ent.getPortrait());
      portraitImage.setXFlipped(true);
      Group portrait = new Pixl(1, 2).border(Colours.dark, this.ent.getColour(), 1).actor(portraitImage).pix();
      Pixl p = new Pixl(3, 2);
      this.hpWriter = new Pixl().actor(this.getTitle(getHpString(visualState, false), this.getHpWidth(), true)).gap((int)portrait.getWidth()).pix();
      p.actor(this.hpWriter);
      p.row(4);
      if (this.ent.getSize() == EntSize.small || this.ent.getSize() == EntSize.reg) {
         this.dieSpinner = new DieSpinner(this.ent.getDie(), this.ent.getSize().getPixels() * 1.5F);
         p.actor(this.dieSpinner);
      }

      p.actor(this.netPanel = new NetPanel(this.ent, false));
      Tann.become(this, p.pix());
      Color c = this.ent.getColour();
      if (c == null) {
         c = Colours.pink;
      }

      Rectactor ra = new Rectactor((int)this.getWidth(), (int)this.hpWriter.getHeight() + 4, c);
      this.addActor(ra);
      ra.setY(this.getHeight() - ra.getHeight());
      this.setTransform(true);
      ra.toBack();
      this.addActor(portrait);
      portrait.setPosition(this.getWidth() - portrait.getWidth(), this.getHeight() - portrait.getHeight());
      portrait.setZIndex(ra.getZIndex() + 1);
      Rectactor.fill(this, c);
   }

   private static String getHpString(EntState visualState, boolean tiny) {
      if (tiny && visualState.getHp() >= 10) {
         return (visualState.isAtMaxHp() ? "[red]" : "[purple]") + visualState.getMaxHp();
      } else {
         return visualState.isAtMaxHp()
            ? "[red]" + visualState.getMaxHp()
            : "[red]" + visualState.getHp() + "[grey]/[cu][purple]" + visualState.getMaxHp() + "[cu]";
      }
   }

   public void setDirty() {
      this.dirty = true;
   }

   public void draw(Batch batch, float parentAlpha) {
      if (this.dirty) {
         this.layout();
      }

      super.draw(batch, parentAlpha);
   }

   @Override
   public void postPop() {
      this.ent.getEntPanel().setArrowIntensity(0.0F, 0.0F);
   }

   @Override
   public void repositionExplanel(Explanel exp) {
      Vector2 pos = Tann.getAbsoluteCoordinates(this);
      if (pos.y - exp.getFullHeight() < 0.0F) {
         exp.setPosition((int)(com.tann.dice.Main.width / 2 - exp.getWidth() / 2.0F), (int)(com.tann.dice.Main.height / 2 - exp.getHeight() / 2.0F));
      } else {
         switch (this.ent.getSize()) {
            case small:
            case reg:
               exp.getFullHeight();
               Vector2 local = Tann.getAbsoluteCoordinates(this);
               exp.setPosition((int)(local.x + this.getWidth() / 2.0F - exp.getWidth() / 2.0F), (int)(local.y - exp.getHeight() + 1.0F));
               break;
            case big:
            case huge:
               exp.setPosition((int)(com.tann.dice.Main.width / 2 - exp.getWidth() / 2.0F), (int)(com.tann.dice.Main.height / 2 - exp.getHeight() / 2.0F));
         }
      }
   }

   @Override
   public boolean alternativePop() {
      if (com.tann.dice.Main.getCurrentScreen() instanceof DungeonScreen && DungeonScreen.checkActive(this.ent)) {
         this.clearActions();
         EntPanelCombat ePan = this.ent.getEntPanel();
         Vector2 coord = Tann.getAbsoluteCoordinates(ePan);
         this.addAction(
            Actions.sequence(
               Actions.parallel(
                  Actions.scaleTo(0.0F, 0.0F, 0.3F),
                  Actions.moveTo(coord.x + (ePan.ent.isPlayer() ? ePan.getWidth() : 0.0F), coord.y + ePan.getHeight() / 2.0F, 0.3F)
               ),
               Actions.removeActor()
            )
         );
         return true;
      } else {
         this.withoutDice();
         return false;
      }
   }

   public void flash(int prevMaxHp, int newMaxHp, int prevHp, int newHp, int[] prevHashes, int[] newHashes) {
      if (newMaxHp != prevMaxHp || prevHp != newHp) {
         this.flashHp();
      }

      for (int i = 0; i < prevHashes.length; i++) {
         if (newHashes[i] != prevHashes[i]) {
            this.netPanel.flashSide(i);
         }
      }
   }

   public void flashHp() {
      this.hpWriter.addActor(new Flasher(this.hpWriter, DieSidePanel.EQUIP_BONUS_FLASH_COLOUR, 0.4F));
   }

   public void show() {
      this.layout();
      Sounds.playSound(Sounds.pip);
      EntPanelInventory pan = this.ent.getDiePanel();
      DungeonScreen.get().push(pan, false, true, false, 0.0F);
      pan.setScale(0.0F);
      EntPanelCombat ePan = this.ent.getEntPanel();
      Vector2 coord = Tann.getAbsoluteCoordinates(ePan);
      pan.setPosition(coord.x + ePan.getWidth() * (this.ent.isPlayer() ? 0.8F : 0.2F), coord.y + ePan.getHeight() / 2.0F);
      float dur = 0.4F;
      Interpolation terp = Chrono.i;
      pan.clearActions();
      pan.addAction(Actions.parallel(Actions.scaleTo(1.0F, 1.0F, dur * 0.8F, terp), Actions.moveTo(pan.getNiceX(), pan.getNiceY(), dur, terp)));
      this.ent.getEntPanel().setArrowIntensity(1.0F, 0.0F);
   }

   private void updateTraitVisibility() {
      for (Actor a : this.traitActors) {
         a.setVisible(this.traitsVisible);
      }
   }

   public void setTraitsVisible(boolean visible) {
      this.traitsVisible = visible;
      this.updateTraitVisibility();
   }

   public void addPortraitFlasher() {
      Glowverlay go = new Glowverlay(Colours.light);
      this.portraitGroup.toFront();
      this.portraitGroup.addActor(go);
   }

   public void removeDice() {
      if (this.dieSpinner != null) {
         this.dieSpinner.remove();
      }
   }

   public EntPanelInventory withoutDice() {
      this.removeDice();
      this.setTransform(false);
      return this;
   }

   @Override
   public void makeSafeForScrollpane() {
      this.withoutDice();
   }
}

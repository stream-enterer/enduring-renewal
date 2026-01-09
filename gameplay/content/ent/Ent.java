package com.tann.dice.gameplay.content.ent;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.tann.dice.gameplay.content.ent.die.EntDie;
import com.tann.dice.gameplay.content.ent.die.side.EntSide;
import com.tann.dice.gameplay.content.ent.type.EntType;
import com.tann.dice.gameplay.content.item.Item;
import com.tann.dice.gameplay.effect.Trait;
import com.tann.dice.gameplay.fightLog.EntSideState;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.fightLog.FightLog;
import com.tann.dice.gameplay.trigger.personal.Personal;
import com.tann.dice.screens.dungeon.DungeonScreen;
import com.tann.dice.screens.dungeon.panels.Explanel.EntPanelInventory;
import com.tann.dice.screens.dungeon.panels.entPanel.EntPanelCombat;
import com.tann.dice.statics.ImageUtils;
import com.tann.dice.statics.bullet.BulletStuff;
import com.tann.dice.util.Colours;
import com.tann.dice.util.ui.TextWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Ent {
   protected EntDie die;
   protected EntSide[] sides;
   protected Color col = Colours.purple;
   protected TextureRegion lapel;
   protected TextureRegion lapel2d;
   private EntPanelCombat ep;
   public String name;
   public EntType entType;
   private Item[] items;
   public Trait[] traits;
   EntSize size;
   Ent summonedBy;
   FightLog fightLog;
   private Map<FightLog.Temporality, EntState> stateMap = new HashMap<>();
   private EntPanelInventory panel;
   private static final int BASE_MAX_ITEM_SIZE = 2;
   List<Item> itemAsList;
   EntState blankState = null;

   public Ent(EntType type) {
      this.entType = type;
      this.name = type.getName(false);
      this.traits = new Trait[type.traits.size()];

      for (int i = 0; i < this.traits.length; i++) {
         this.traits[i] = type.traits.get(i).copy();
      }

      this.size = type.size;
      this.setSides(this.entType.sides);
      this.setupLapels(0);
      this.refreshItemSlots();
   }

   public List<Item> refreshItemSlots() {
      this.fightlogUpdate();
      int slots = this.getNumberItemSlots();
      if (this.items == null) {
         this.items = new Item[slots];
      }

      List<Item> unequipped = new ArrayList<>();

      for (int i = slots; i < this.items.length; i++) {
         if (this.items[i] != null) {
            unequipped.add(this.items[i]);
            this.items[i] = null;
         }
      }

      for (int ix = 0; ix < this.items.length; ix++) {
         if (this.items[ix] != null) {
            Item e = this.items[ix];

            for (Personal pt : e.getPersonals()) {
               if (pt.unequip(this)) {
                  unequipped.add(e);
                  this.items[ix] = null;
               }
            }
         }
      }

      this.itemAsList = null;
      if (this.items.length != slots) {
         Item[] newEq = new Item[slots];
         System.arraycopy(this.items, 0, newEq, 0, Math.min(newEq.length, this.items.length));
         this.items = newEq;
      }

      return unequipped;
   }

   public void setRealFightLog(FightLog fightlog) {
      this.fightLog = fightlog;
   }

   protected void setSides(EntSide[] sides) {
      this.sides = EntSide.copy(sides);
   }

   public void somethingChangedDiePanel() {
      this.getDiePanel().setDirty();
   }

   public void stopped() {
      if (!BulletStuff.isSimulating()) {
         this.getState(FightLog.Temporality.Present).dieStoppedOn(this.getState(FightLog.Temporality.Present).getSideState(this.die.getCurrentSide()));
      }
   }

   public EntSide[] getSides() {
      return this.sides;
   }

   public abstract boolean isPlayer();

   public EntDie getDie() {
      if (this.die == null) {
         this.die = new EntDie(this);
      }

      return this.die;
   }

   public Color getColour() {
      return this.col;
   }

   public void setColour(Color col) {
      this.col = col;
   }

   public String getName(boolean forDisplay) {
      return this.getName(forDisplay, forDisplay);
   }

   public String getName(boolean forDisplay, boolean andCol) {
      return this.entType.getName(forDisplay, andCol);
   }

   public EntPanelCombat getEntPanel() {
      if (this.ep == null) {
         this.ep = new EntPanelCombat(this);
         this.ep.layout();
      }

      return this.ep;
   }

   public TextureRegion getLapel() {
      return this.lapel;
   }

   public TextureRegion get2DLapel() {
      return this.lapel2d;
   }

   public EntPanelInventory getDiePanel() {
      if (this.panel == null) {
         this.panel = new EntPanelInventory(this);
      }

      return this.panel;
   }

   public abstract int getPixelSize();

   public void locked() {
      this.getState(FightLog.Temporality.Present).dieLocked(this.getState(FightLog.Temporality.Present).getSideState(this.die.getCurrentSide()));
   }

   public int getNumberItemSlots() {
      if (this.items == null) {
         return 2;
      } else {
         int maxItemSize = 2;

         for (Personal pt : this.getState(FightLog.Temporality.Present).getActivePersonals()) {
            maxItemSize = pt.affectItemSlots(maxItemSize);
         }

         return Math.max(0, maxItemSize);
      }
   }

   public void forceItems(List<Item> deserialise) {
      this.items = new Item[Math.max(2, deserialise.size())];

      for (int i = 0; i < this.items.length && i < deserialise.size(); i++) {
         this.items[i] = deserialise.get(i);
      }
   }

   public boolean addItem(Item i) {
      Integer slot = this.getAvailableItemSlot();
      if (slot == null) {
         return false;
      } else {
         this.addItem(i, slot);
         return true;
      }
   }

   private Integer getAvailableItemSlot() {
      for (int i = 0; i < this.items.length; i++) {
         if (this.items[i] == null) {
            return i;
         }
      }

      return null;
   }

   public Item addItem(Item e, int slot) {
      Item replaced = null;
      if (slot < this.items.length) {
         if (this.items[slot] != null) {
            replaced = this.items[slot];
         }

         this.items[slot] = e;
         this.itemAsList = null;
         return replaced;
      } else {
         return e;
      }
   }

   private void fightlogUpdate() {
      this.blankState = null;
      if (this.getFightLog() != null) {
         this.getFightLog().updateOutOfCombat();
      }
   }

   public int removeItem(Item e) {
      Integer slot = this.getItemIndex(e);
      if (slot == null) {
         return -1;
      } else {
         this.items[slot] = null;
         this.itemAsList = null;
         return slot;
      }
   }

   public Integer getItemIndex(Item e) {
      for (int i = 0; i < this.items.length; i++) {
         if (this.items[i] == e) {
            return i;
         }
      }

      return null;
   }

   public String getColourTag() {
      return TextWriter.getTag(this.getColour());
   }

   public Ent getSummonedBy() {
      return this.summonedBy;
   }

   public List<Item> getItems() {
      if (this.itemAsList == null) {
         this.itemAsList = new ArrayList<>();

         for (Item e : this.items) {
            if (e != null) {
               this.itemAsList.add(e);
            }
         }
      }

      return this.itemAsList;
   }

   public Item getItems(int index) {
      return index >= this.items.length ? null : this.items[index];
   }

   public int[] getSideHashes() {
      int[] results = new int[6];
      EntState blank = this.getState(FightLog.Temporality.Present);

      for (int i = 0; i < this.getSides().length; i++) {
         EntSideState ess = blank.getSideState(i);
         results[i] = ess.getCalculatedEffect().hashEff();
         TextureRegion tr = ess.getCalculatedTexture();
         if (tr instanceof AtlasRegion) {
            results[i] += ((AtlasRegion)tr).name.hashCode();
         }
      }

      return results;
   }

   public abstract void deathSound();

   public int getPortraitWidth(boolean accountForOffset) {
      return this.getPortrait() == null
         ? 0
         : this.getPortrait().getRegionWidth() + this.getPortraitOffsetX() * (accountForOffset ? 1 : 0) * (this.isPlayer() ? 1 : -1);
   }

   public int getPortraitOffsetY() {
      return this.entType.offsets.get("U") - this.entType.offsets.get("D");
   }

   public int getPortraitShift() {
      return this.entType.offsets.get("S");
   }

   public int getPortraitOffsetX() {
      return this.isPlayer() ? this.entType.offsets.get("R") - this.entType.offsets.get("L") : this.entType.offsets.get("L") - this.entType.offsets.get("R");
   }

   public TextureRegion getPortrait() {
      return this.entType.portrait;
   }

   public EntSize getSize() {
      return this.size;
   }

   public void setupLapels(int level) {
      level = Math.min(3, --level);
      level = Math.max(0, level);
      this.lapel = ImageUtils.loadExt3dNull(this.size + "/lapel/" + level);
      if (this.lapel == null) {
         this.lapel = ImageUtils.loadExt3d(this.size + "/lapel/0");
      }

      this.lapel2d = ImageUtils.get2DIfPossible(this.lapel);
   }

   public void setSummonedBy(Ent summoner) {
      this.summonedBy = summoner;
   }

   public EntState getBlankState() {
      if (this.blankState == null) {
         if (DungeonScreen.get() == null || !DungeonScreen.checkActive(this) && !this.allowContextInjection()) {
            this.blankState = new EntState(this, null);
         } else {
            DungeonScreen ds = DungeonScreen.get();
            this.blankState = new EntState(
               this, ds.getFightLog().getSnapshot(FightLog.Temporality.Present), ds.getDungeonContext().getModifierGlobalsIncludingLinked()
            );
         }
      }

      return this.blankState;
   }

   protected boolean allowContextInjection() {
      return false;
   }

   public FightLog getFightLog() {
      return this.fightLog;
   }

   public void updateOutOfCombat() {
      this.fightlogUpdate();
      if (DungeonScreen.get() != null) {
         this.getEntPanel().updatePanelStateCache();
         this.getEntPanel().layout();
         this.getDiePanel().layout();
      }
   }

   public void setState(FightLog.Temporality temporality, EntState state) {
      this.blankState = null;
      this.stateMap.put(temporality, state);
      this.somethingChangedDiePanel();
      if (temporality == FightLog.Temporality.Visual) {
         this.getDie().clearTextureCache();
      }
   }

   public EntState getState(FightLog.Temporality temporality) {
      EntState es = this.stateMap.get(temporality);
      return es != null ? es : this.getBlankState();
   }

   @Override
   public String toString() {
      return this.name + "(" + this.hashCode() + ")";
   }

   public EntType getEntType() {
      return this.entType;
   }

   public List<Item> getPotions(EntState es) {
      List<Item> result = new ArrayList<>();

      for (Item i : this.getItems()) {
         if (!es.ignoreItem(i) && i.isPotion()) {
            result.add(i);
         }
      }

      return result;
   }

   public boolean isMissingno() {
      return this.entType.isMissingno();
   }
}

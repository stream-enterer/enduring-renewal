package com.tann.dice.gameplay.content.item;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.tann.dice.gameplay.content.ent.Ent;
import com.tann.dice.gameplay.content.ent.Hero;
import com.tann.dice.gameplay.content.gen.pipe.item.PipeItem;
import com.tann.dice.gameplay.content.gen.pipe.regex.meta.PipeMetaIndexed;
import com.tann.dice.gameplay.context.DungeonContext;
import com.tann.dice.gameplay.effect.eff.keyword.Keyword;
import com.tann.dice.gameplay.effect.targetable.ability.Ability;
import com.tann.dice.gameplay.modifier.modBal.TierUtils;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.choosable.Choosable;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.choosable.ChoosableType;
import com.tann.dice.gameplay.progress.chievo.unlock.Unlockable;
import com.tann.dice.gameplay.progress.stats.stat.Stat;
import com.tann.dice.gameplay.progress.stats.stat.pickRate.PickStat;
import com.tann.dice.gameplay.trigger.Collision;
import com.tann.dice.gameplay.trigger.Trigger;
import com.tann.dice.gameplay.trigger.global.Global;
import com.tann.dice.gameplay.trigger.global.chance.GlobalRarity;
import com.tann.dice.gameplay.trigger.personal.Personal;
import com.tann.dice.screens.dungeon.DungeonScreen;
import com.tann.dice.screens.dungeon.panels.book.views.ItemLedgerView;
import com.tann.dice.screens.dungeon.panels.entPanel.choosablePanel.ItemPanel;
import com.tann.dice.statics.Images;
import com.tann.dice.statics.sound.Sounds;
import com.tann.dice.util.Colours;
import com.tann.dice.util.ImageActor;
import com.tann.dice.util.Tann;
import com.tann.dice.util.lang.Words;
import com.tann.dice.util.ui.TextWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Item implements Unlockable, Choosable {
   public static final int UNRATED_TIER = -69;
   private final int tier;
   private final String name;
   private final String description;
   private final List<Personal> personals;
   private final boolean hidden;
   private final float chance;
   private final TextureRegion image;
   boolean isNew = false;
   final long ignoredBits = Collision.PLAYER_KEYWORD | Collision.PHYSICAL_DAMAGE;

   public Item(int tier, String name, TextureRegion image, List<Personal> personals, String description, boolean hidden) {
      this.tier = tier;
      this.name = name;
      this.personals = personals;
      this.hidden = hidden;
      this.description = description;
      this.image = image;
      this.chance = this.calculateChance();
   }

   public String getImagePath() {
      return "item/" + this.getImageName();
   }

   public String getImageName() {
      return this.name.replaceAll("[ ':]", "-").toLowerCase();
   }

   public boolean usableBy(Ent ent) {
      return ent instanceof Hero;
   }

   public boolean isNew() {
      return this.isNew;
   }

   public void setNew(boolean isNew) {
      this.isNew = isNew;
   }

   public boolean isForceEquip() {
      for (Personal t : this.getPersonals()) {
         if (t.forceEquip()) {
            return true;
         }
      }

      return false;
   }

   @Override
   public Actor makeUnlockActor(boolean big) {
      if (!big) {
         ItemLedgerView iav = new ItemLedgerView(this, ItemLedgerView.EquipSeenState.Seen);
         iav.addBasicListener();
         return iav;
      } else {
         return new ItemPanel(this, false);
      }
   }

   @Override
   public TextureRegion getAchievementIcon() {
      return this.image;
   }

   @Override
   public String getAchievementIconString() {
      return "[grey]I";
   }

   @Override
   public String toString() {
      return this.name;
   }

   @Override
   public String getTierString() {
      if (this.tier == -69) {
         return "[grey]/[cu]";
      } else {
         String col = this.tier < 0 ? "[purple]" : "[grey]";
         return col + Words.getTierString(Math.abs(this.getTier())) + "[cu]";
      }
   }

   @Override
   public ChoosableType getType() {
      return ChoosableType.Item;
   }

   @Override
   public void onChoose(DungeonContext dc, int index) {
      if (!dc.allowInventory()) {
         Sounds.playSound(Sounds.error);
      } else {
         dc.getParty().addItem(this);
         dc.getStatsManager().pickDelta(this, true);
         dc.setCheckedItems(false);
         this.setNew(true);
      }
   }

   @Override
   public void onReject(DungeonContext dc) {
      dc.getStatsManager().pickDelta(this, false);
   }

   @Override
   public Actor makeChoosableActor(boolean big, int index) {
      return new ItemPanel(this, big);
   }

   @Override
   public String describe() {
      return "item";
   }

   private float calculateChance() {
      List<Global> globals = new ArrayList<>();

      for (Personal pt : this.personals) {
         Global gt = pt.getGlobalFromPersonalTrigger();
         if (gt != null) {
            globals.add(gt);
         }
      }

      return GlobalRarity.listChance(globals);
   }

   @Override
   public float chance() {
      return this.chance;
   }

   public boolean hasBadEquipArt() {
      for (Personal pt : this.getPersonals()) {
         Actor b = pt.makePanelActor(true);
         if (b == null) {
            return true;
         }
      }

      return false;
   }

   @Override
   public int getTier() {
      return this.tier;
   }

   public boolean hasTier() {
      return this.tier != -69;
   }

   @Override
   public float getModTier() {
      return TierUtils.itemModTier(this.getTier());
   }

   public List<Personal> getPersonals() {
      return this.personals;
   }

   @Override
   public String getName() {
      return this.getName(false);
   }

   public String getName(boolean display) {
      if (!display) {
         return this.name;
      } else {
         String originalName = this.name;
         String tmp = originalName;

         for (int i = 0; i < this.personals.size(); i++) {
            tmp = this.personals.get(i).affectItemName(tmp);
         }

         if (originalName.equals(tmp) && !originalName.contains(".")) {
            if (tmp.contains("#")) {
               String[] parts = tmp.split("#", 2);
               tmp = com.tann.dice.Main.t(parts[0]) + "#" + com.tann.dice.Main.t(parts[1]);
            } else {
               tmp = com.tann.dice.Main.t(tmp);
            }
         }

         return tmp;
      }
   }

   public String getDescription() {
      return this.description;
   }

   public boolean isHidden() {
      return this.hidden;
   }

   public TextureRegion getImage() {
      return this.image;
   }

   public static Color getIdCol() {
      return Colours.grey;
   }

   public List<Keyword> getReferencedKeywords() {
      List<Keyword> result = new ArrayList<>();

      for (Personal pt : this.personals) {
         result.addAll(pt.getReferencedKeywords());
      }

      Tann.uniquify(result);
      return result;
   }

   @Override
   public boolean encountered(Map<String, Stat> allMergedStats) {
      return this.getPicks(allMergedStats, false) + this.getPicks(allMergedStats, true) > 0;
   }

   @Override
   public int getPicks(Map<String, Stat> allMergedStats, boolean reject) {
      String tag = PickStat.nameFor(this);
      Stat s = allMergedStats.get(tag);
      return s == null ? 0 : PickStat.val(s, reject);
   }

   @Override
   public long getCollisionBits() {
      long result = this.getCollisionBitsBasic();
      return result - (result & this.ignoredBits);
   }

   public long getCollisionBitsIncludingGenericTransformed() {
      long result = 0L;

      for (int j = 0; j < this.personals.size(); j++) {
         result |= this.personals.get(j).getCollisionBits(true);
      }

      if ((result & Collision.GENERIC_ALL_SIDES_HERO) != 0L) {
         result |= Collision.ALL_SIDES_HERO_COMPOSITE;
      }

      return result;
   }

   private long getCollisionBitsBasic() {
      long result = 0L;

      for (int j = 0; j < this.personals.size(); j++) {
         result |= this.personals.get(j).getCollisionBits(true);
      }

      if (this.tier < -1) {
         result |= Collision.VERY_BAD_ITEM;
      }

      return result;
   }

   public boolean isMissingno() {
      return this == PipeItem.getMissingno();
   }

   @Override
   public String getSaveString() {
      if (DungeonScreen.tinyPasting) {
         String tiny = PipeMetaIndexed.tinyName(this);
         if (tiny != null && tiny.length() <= this.getName(false).length()) {
            return tiny;
         }
      }

      return this.getName(false);
   }

   @Override
   public boolean isPositive() {
      return this.tier >= 0;
   }

   @Override
   public Color getColour() {
      return getIdCol();
   }

   public Ability getAbility() {
      List<Personal> personals = this.getPersonals();
      int i = 0;

      for (int triggersSize = personals.size(); i < triggersSize; i++) {
         Personal pt = personals.get(i);
         if (pt.getAbility() != null) {
            return pt.getAbility();
         }
      }

      return null;
   }

   public Actor makeImageActor() {
      return (Actor)(this.getAbility() == null ? new ImageActor(this.image) : makeAbilityItemActor(this.getAbility()));
   }

   public static Actor makeAbilityItemActor(Ability ability) {
      Group g = Tann.makeGroup(14, 14);
      Actor center;
      if (ability.useImage()) {
         center = new ImageActor(ability.getImage());
      } else {
         center = new TextWriter(TextWriter.getTag(ability.getIdCol()) + ability.getTitle().charAt(0));
      }

      g.addActor(center);
      Tann.center(center);
      ImageActor border = new ImageActor(Images.itemAbilityBorder, ability.getIdCol());
      g.addActor(border);
      return g;
   }

   public boolean canEquip(Hero h) {
      if (h == null) {
         return false;
      } else {
         for (Personal pt : this.getPersonals()) {
            if (pt.unequip(h)) {
               return false;
            }
         }

         return true;
      }
   }

   public boolean isPotion() {
      return this.getName(false).toLowerCase().contains("potion");
   }

   public boolean isMultiplable(boolean liberal) {
      return Trigger.checkMultiplability(this.personals, liberal);
   }

   public Personal getSinglePersonalOrNull() {
      Personal result = null;
      List<Personal> globs = this.getPersonals();

      for (int i = 0; i < globs.size(); i++) {
         Personal g = globs.get(i);
         if (!g.metaOnly()) {
            if (result != null) {
               return null;
            }

            result = g;
         }
      }

      return result;
   }
}

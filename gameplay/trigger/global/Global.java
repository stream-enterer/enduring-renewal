package com.tann.dice.gameplay.trigger.global;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.tann.dice.gameplay.battleTest.Zone;
import com.tann.dice.gameplay.content.ent.Ent;
import com.tann.dice.gameplay.content.ent.Monster;
import com.tann.dice.gameplay.content.ent.type.HeroCol;
import com.tann.dice.gameplay.content.ent.type.HeroType;
import com.tann.dice.gameplay.content.ent.type.MonsterType;
import com.tann.dice.gameplay.content.item.Item;
import com.tann.dice.gameplay.context.DungeonContext;
import com.tann.dice.gameplay.effect.eff.Eff;
import com.tann.dice.gameplay.effect.targetable.ability.Ability;
import com.tann.dice.gameplay.effect.targetable.ability.spell.Spell;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.fightLog.FightLog;
import com.tann.dice.gameplay.fightLog.Snapshot;
import com.tann.dice.gameplay.phase.Phase;
import com.tann.dice.gameplay.progress.StatSnapshot;
import com.tann.dice.gameplay.trigger.Trigger;
import com.tann.dice.gameplay.trigger.global.heroLevelupAffect.HeroGenType;
import com.tann.dice.gameplay.trigger.personal.Personal;
import com.tann.dice.screens.dungeon.DungeonScreen;
import com.tann.dice.screens.dungeon.panels.hourglass.HourglassTime;
import java.util.ArrayList;
import java.util.List;

public abstract class Global extends Trigger {
   public int affectGlobalLootQuality(int quality) {
      return quality;
   }

   public void startOfTurnGeneral(Snapshot snapshot, int turn) {
   }

   public void endOfTurnGeneral(Snapshot snapshot, int turn) {
   }

   public int affectMaxRerolls(int max, int turn) {
      return max;
   }

   public int affectMaxMana(int max) {
      return max;
   }

   public List<Item> getStartingItems(DungeonContext dc) {
      return null;
   }

   public int affectSpellCost(Spell s, int cost, Snapshot snapshot) {
      return cost;
   }

   public void onPick(DungeonContext context) {
   }

   public Spell getGlobalSpell() {
      return null;
   }

   public String getRollError(List<Ent> entitiesToRoll, int size) {
      return null;
   }

   public Personal getLinkedPersonal(EntState entState) {
      return null;
   }

   public void affectStartMonsters(List<Monster> monsters) {
   }

   public boolean canUseAbility(Ability ability, Snapshot snapshot) {
      return true;
   }

   public void affectSpell(String title, Eff result) {
   }

   public List<Phase> getPhases(DungeonContext dungeonContext) {
      return new ArrayList<>();
   }

   public int affectFinalMana(int mana) {
      return mana;
   }

   public int affectFinalRerolls(int rerolls) {
      return rerolls;
   }

   public int getMaxLevel(int level) {
      return level;
   }

   public int getLevelNumberForGameplay(int level) {
      return level;
   }

   public boolean allowDuplicateHeroLevelups() {
      return false;
   }

   public HeroGenType generateHeroes() {
      return null;
   }

   public String describeForMode() {
      return null;
   }

   public int affectReinforcements(int amt) {
      return amt;
   }

   public boolean flee(Snapshot snapshot) {
      return false;
   }

   public float chance() {
      return 1.0F;
   }

   public Global getLinkedGlobal(DungeonContext context, int turn) {
      return null;
   }

   public boolean allowToggleLock(boolean currentlyLocked, List<EntState> states) {
      return true;
   }

   public TextureRegion getSpecialImage() {
      return null;
   }

   public void affectPhasesPost(int currentLevel, DungeonContext context, List<Phase> result) {
   }

   protected void resetFightLogOnPick() {
      if (DungeonScreen.get() != null) {
         FightLog f = DungeonScreen.get().getFightLog();
         f.resetDueToFiddling();
      }
   }

   public List<Global> getLinkedGlobalList(int level, DungeonContext context, int turn) {
      return null;
   }

   public void statSnapshotCheck(StatSnapshot ss) {
   }

   @Override
   public Eff getSingleEffOrNull() {
      return null;
   }

   public void levelEndAfterShortWait(DungeonContext context) {
   }

   public String describeForHourglass() {
      return this.describeForSelfBuff();
   }

   public boolean isDescribedAsBeforeFight() {
      return false;
   }

   public boolean keepRerolls() {
      return false;
   }

   public boolean allowDeadAbilities() {
      return false;
   }

   public HourglassTime getHourglassTime() {
      return HourglassTime.DURING;
   }

   public List<HeroType> getExtraLevelupOptions(HeroCol col, Integer newTier) {
      return null;
   }

   public int affectLootQuantity(int amt) {
      return amt;
   }

   public int affectLevelupChoices(int amt) {
      return amt;
   }

   public Zone getOverrideZone() {
      return null;
   }

   public void affectLevelupOptions(List<HeroType> results) {
   }

   public boolean canWish() {
      return false;
   }

   @Override
   public String hyphenTag() {
      return null;
   }

   public String overrideDisplayName(String name) {
      return name;
   }

   public boolean afterItems() {
      return false;
   }

   public void affectItemOptions(List<Item> results, int itemTier) {
   }

   public boolean skipNotifyRandomReveal() {
      return false;
   }

   public boolean avoidFlee(Snapshot snapshot) {
      return false;
   }

   public boolean isHidden() {
      return false;
   }

   public void affectMonsterPool(List<MonsterType> results) {
   }

   public int affectTotalLength(int base) {
      return base;
   }
}

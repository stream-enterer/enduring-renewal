package com.tann.dice.test;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.tann.dice.gameplay.battleTest.Difficulty;
import com.tann.dice.gameplay.battleTest.Zone;
import com.tann.dice.gameplay.content.ent.EntSize;
import com.tann.dice.gameplay.content.ent.die.side.EntSide;
import com.tann.dice.gameplay.content.ent.die.side.EntSidesLib;
import com.tann.dice.gameplay.content.ent.die.side.blob.ESB;
import com.tann.dice.gameplay.content.ent.die.side.blob.EntSidesBlobBig;
import com.tann.dice.gameplay.content.ent.group.Party;
import com.tann.dice.gameplay.content.ent.type.EntType;
import com.tann.dice.gameplay.content.ent.type.HeroCol;
import com.tann.dice.gameplay.content.ent.type.HeroType;
import com.tann.dice.gameplay.content.ent.type.MonsterType;
import com.tann.dice.gameplay.content.ent.type.lib.EntTypeUtils;
import com.tann.dice.gameplay.content.ent.type.lib.HeroTypeLib;
import com.tann.dice.gameplay.content.ent.type.lib.HeroTypeUtils;
import com.tann.dice.gameplay.content.ent.type.lib.MonsterTypeLib;
import com.tann.dice.gameplay.content.gen.pipe.Pipe;
import com.tann.dice.gameplay.content.gen.pipe.PipeMaster;
import com.tann.dice.gameplay.content.gen.pipe.entity.hero.generate.PipeHeroGenerated;
import com.tann.dice.gameplay.content.gen.pipe.entity.monster.PipeMonsterJinx;
import com.tann.dice.gameplay.content.gen.pipe.item.facade.FacadeUtils;
import com.tann.dice.gameplay.content.gen.pipe.item.sideReally.PipeItemSticker;
import com.tann.dice.gameplay.content.gen.pipe.mod.PipeMod;
import com.tann.dice.gameplay.content.item.Item;
import com.tann.dice.gameplay.content.item.ItemLib;
import com.tann.dice.gameplay.context.DungeonContext;
import com.tann.dice.gameplay.context.config.ContextConfig;
import com.tann.dice.gameplay.context.config.EventUtils;
import com.tann.dice.gameplay.context.config.cursed.CurseConfig;
import com.tann.dice.gameplay.context.config.difficultyConfig.ClassicConfig;
import com.tann.dice.gameplay.context.config.difficultyConfig.LootConfig;
import com.tann.dice.gameplay.context.config.event.EventGenerator;
import com.tann.dice.gameplay.effect.Trait;
import com.tann.dice.gameplay.effect.eff.Eff;
import com.tann.dice.gameplay.effect.eff.EffType;
import com.tann.dice.gameplay.effect.eff.TargetingType;
import com.tann.dice.gameplay.effect.eff.VisualEffectType;
import com.tann.dice.gameplay.effect.eff.keyword.KUtils;
import com.tann.dice.gameplay.effect.eff.keyword.Keyword;
import com.tann.dice.gameplay.effect.targetable.ability.Ability;
import com.tann.dice.gameplay.effect.targetable.ability.AbilityUtils;
import com.tann.dice.gameplay.effect.targetable.ability.spell.Spell;
import com.tann.dice.gameplay.effect.targetable.ability.spell.SpellLib;
import com.tann.dice.gameplay.effect.targetable.ability.spell.SpellUtils;
import com.tann.dice.gameplay.level.Level;
import com.tann.dice.gameplay.mode.Mode;
import com.tann.dice.gameplay.mode.creative.custom.CustomPreset;
import com.tann.dice.gameplay.modifier.Modifier;
import com.tann.dice.gameplay.modifier.ModifierLib;
import com.tann.dice.gameplay.modifier.modBal.ModTierUtils;
import com.tann.dice.gameplay.phase.Phase;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.ChoicePhase;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.choosable.Choosable;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.choosable.ChoosableUtils;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.choosable.special.ReplaceChoosable;
import com.tann.dice.gameplay.progress.UnlockManager;
import com.tann.dice.gameplay.progress.chievo.AchLib;
import com.tann.dice.gameplay.progress.chievo.Achievement;
import com.tann.dice.gameplay.progress.chievo.achievementTypes.debug.PlaceholderAchievement;
import com.tann.dice.gameplay.progress.chievo.achievementTypes.timing.equip.EquipAchievement;
import com.tann.dice.gameplay.progress.chievo.unlock.Unlockable;
import com.tann.dice.gameplay.progress.stats.stat.Stat;
import com.tann.dice.gameplay.progress.stats.stat.StatLib;
import com.tann.dice.gameplay.save.SaveState;
import com.tann.dice.gameplay.save.settings.option.BOption;
import com.tann.dice.gameplay.save.settings.option.Option;
import com.tann.dice.gameplay.save.settings.option.OptionLib;
import com.tann.dice.gameplay.save.settings.option.OptionUtils;
import com.tann.dice.gameplay.trigger.Collision;
import com.tann.dice.gameplay.trigger.Trigger;
import com.tann.dice.gameplay.trigger.global.Global;
import com.tann.dice.gameplay.trigger.global.chance.GlobalRarity;
import com.tann.dice.gameplay.trigger.global.item.GlobalStartWithItem;
import com.tann.dice.gameplay.trigger.global.linked.GlobalLinked;
import com.tann.dice.gameplay.trigger.global.linked.all.GlobalAllEntities;
import com.tann.dice.gameplay.trigger.global.linked.all.GlobalHeroes;
import com.tann.dice.gameplay.trigger.global.phase.addPhase.GlobalAddPhase;
import com.tann.dice.gameplay.trigger.global.phase.addPhase.phaseGen.PhaseGeneratorDifficulty;
import com.tann.dice.gameplay.trigger.global.scaffolding.levelRequirement.LevelRequirement;
import com.tann.dice.gameplay.trigger.global.scaffolding.levelRequirement.LevelRequirementHash;
import com.tann.dice.gameplay.trigger.global.scaffolding.turnRequirement.GlobalTurnRequirement;
import com.tann.dice.gameplay.trigger.global.spell.GlobalLearnSpell;
import com.tann.dice.gameplay.trigger.personal.AvoidDeathPenalty;
import com.tann.dice.gameplay.trigger.personal.ForceEquip;
import com.tann.dice.gameplay.trigger.personal.IncomingEffBonus;
import com.tann.dice.gameplay.trigger.personal.KeepShields;
import com.tann.dice.gameplay.trigger.personal.LostOnDeath;
import com.tann.dice.gameplay.trigger.personal.MultiplyDamageTaken;
import com.tann.dice.gameplay.trigger.personal.OnOverheal;
import com.tann.dice.gameplay.trigger.personal.OnRescue;
import com.tann.dice.gameplay.trigger.personal.Permadeath;
import com.tann.dice.gameplay.trigger.personal.Personal;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.AffectSides;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.condition.SpecificSidesCondition;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.condition.SpecificSidesType;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.AddKeyword;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.AffectByIndex;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.AffectSideEffect;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.FlatBonus;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.ReplaceWithEnt;
import com.tann.dice.gameplay.trigger.personal.death.DamageAdjacentsOnDeath;
import com.tann.dice.gameplay.trigger.personal.death.OnDeathEffect;
import com.tann.dice.gameplay.trigger.personal.death.OtherDeathEffect;
import com.tann.dice.gameplay.trigger.personal.eff.AfterUseDiceEffect;
import com.tann.dice.gameplay.trigger.personal.eff.EndOfTurnEff;
import com.tann.dice.gameplay.trigger.personal.eff.EndOfTurnMana;
import com.tann.dice.gameplay.trigger.personal.eff.StartOfCombat;
import com.tann.dice.gameplay.trigger.personal.eff.StartOfTurnSelf;
import com.tann.dice.gameplay.trigger.personal.hp.EmptyMaxHp;
import com.tann.dice.gameplay.trigger.personal.hp.MaxHP;
import com.tann.dice.gameplay.trigger.personal.immunity.DamageImmunity;
import com.tann.dice.gameplay.trigger.personal.immunity.HealImmunity;
import com.tann.dice.gameplay.trigger.personal.immunity.ShieldImmunity;
import com.tann.dice.gameplay.trigger.personal.item.ItemSlots;
import com.tann.dice.gameplay.trigger.personal.linked.PersonalTurnRequirement;
import com.tann.dice.gameplay.trigger.personal.linked.TriggerPersonalToGlobal;
import com.tann.dice.gameplay.trigger.personal.specialPips.GhostHP;
import com.tann.dice.gameplay.trigger.personal.specialPips.SpecialHp;
import com.tann.dice.gameplay.trigger.personal.specialPips.resistive.StoneSpecialHp;
import com.tann.dice.gameplay.trigger.personal.spell.AfterUseAbility;
import com.tann.dice.gameplay.trigger.personal.spell.learn.LearnSpell;
import com.tann.dice.gameplay.trigger.personal.spell.learn.LearnTactic;
import com.tann.dice.gameplay.trigger.personal.startBuffed.StartPetrified;
import com.tann.dice.gameplay.trigger.personal.startBuffed.StartPoisoned;
import com.tann.dice.gameplay.trigger.personal.startBuffed.StartRegenned;
import com.tann.dice.gameplay.trigger.personal.weird.KeepName;
import com.tann.dice.gameplay.trigger.personal.weird.LevelUpInto;
import com.tann.dice.screens.dungeon.panels.book.page.stuffPage.PipeType;
import com.tann.dice.screens.dungeon.panels.entPanel.EntPanelCombat;
import com.tann.dice.screens.dungeon.panels.entPanel.choosablePanel.ItemPanel;
import com.tann.dice.screens.dungeon.panels.entPanel.choosablePanel.ModifierPanel;
import com.tann.dice.screens.dungeon.panels.tutorial.TutorialManager;
import com.tann.dice.test.util.Skip;
import com.tann.dice.test.util.SkipNonTann;
import com.tann.dice.test.util.Slow;
import com.tann.dice.test.util.Test;
import com.tann.dice.test.util.TestRunner;
import com.tann.dice.util.Colours;
import com.tann.dice.util.DebugUtilsUseful;
import com.tann.dice.util.Tann;
import com.tann.dice.util.TannLog;
import com.tann.dice.util.TannStageUtils;
import com.tann.dice.util.tp.TP;
import com.tann.dice.util.ui.TextMarquee;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TestValidation {
   @Test
   public static void basicModifierValidation() {
      List<String> names = new ArrayList<>();
      List<Modifier> duplicateName = new ArrayList<>();

      for (Modifier m : ModifierLib.getAll()) {
         if (names.contains(m.getName())) {
            duplicateName.add(m);
         }

         names.add(m.getName());
      }

      Tann.assertTrue("Should be no modifiers with duplicate names: " + duplicateName, duplicateName.size() == 0);
   }

   @Test
   public static void checkForNullItems() {
      DungeonContext dummy = DebugUtilsUseful.dummyContext();
      List<Modifier> bad = new ArrayList<>();

      for (Modifier b : ModifierLib.getAll()) {
         if (b.getGlobals().size() != 0) {
            Global gt = b.getGlobals().get(0);
            if (gt instanceof GlobalStartWithItem) {
               List<Item> list = gt.getStartingItems(dummy);
               if (list != null) {
                  for (Item e : list) {
                     if (e.isMissingno()) {
                        bad.add(b);
                     }
                  }
               }
            }
         }
      }

      Tann.assertTrue("Should be no modifiers providing null items: " + bad, bad.size() == 0);
   }

   @Test
   public static void validateSpells() {
      List<Spell> badSpells = new ArrayList<>();
      List<String> excl = Arrays.asList("Zcx");

      for (Spell s : SpellLib.makeAllSpellsList(false)) {
         if (!excl.contains(s.getTitle()) && !validateSpell(s)) {
            badSpells.add(s);
         }
      }

      Tann.assertTrue("Should be no bad spells: " + badSpells, badSpells.size() == 0);
   }

   private static boolean validateSpell(Spell s) {
      if (s.getBaseEffect() != null && s.getTitle() != null && s.getImage() != null && s.getBaseCost() > 0) {
         boolean anyVisual = false;
         Eff e = s.getBaseEffect();
         if (e.getType() == EffType.Or) {
            for (boolean b : Tann.BOTH) {
               anyVisual |= e.getOr(b).getVisual() != VisualEffectType.None;
            }
         }

         anyVisual |= e.getVisual() != VisualEffectType.None;
         anyVisual |= e.getSound() != null;

         for (Keyword k : e.getKeywords()) {
            if (!SpellUtils.allowAddingKeyword(k)) {
               return false;
            }
         }

         return anyVisual;
      } else {
         return false;
      }
   }

   @Test
   public static void checkEquipmentArtSize() {
      List<String> fails = new ArrayList<>();
      int expectedSize = 14;

      for (Item i : ItemLib.getMasterCopy()) {
         if (!i.isMissingno() && i.getAbility() == null) {
            TextureRegion tr = i.getImage();
            if (tr.getRegionWidth() != 14 || tr.getRegionHeight() != 14) {
               fails.add(i.getName(false));
            }
         }
      }

      Tann.assertEquals("should be no fails " + fails, 0, fails.size());
   }

   @Test
   public static void noItemsWithTriggerAllEntities() {
      List<Item> broken = new ArrayList<>();

      for (Item i : ItemLib.getMasterCopy()) {
         if (!i.getName(false).contains("Cart") && !i.getName(false).contains("Wine")) {
            for (Personal pt : i.getPersonals()) {
               Global gt1 = pt.getGlobalFromPersonalTrigger();
               if (gt1 instanceof GlobalAllEntities) {
                  broken.add(i);
               }
            }
         }
      }

      Tann.assertTrue("Should be no all items " + broken, broken.size() == 0);
   }

   @Test
   public static void noGreenTriggerPips() {
      List<MonsterType> types = new ArrayList<>();

      for (MonsterType mt : MonsterTypeLib.getMasterCopy()) {
         for (Trait t : mt.traits) {
            if (t.personal instanceof SpecialHp) {
               SpecialHp tp = (SpecialHp)t.personal;
               TP<TextureRegion, Color> tannp = tp.getPipTannple(false);
               if (tannp.b == Colours.green) {
                  types.add(mt);
               }
            }
         }
      }

      Tann.assertTrue("Should be no bad monsters: " + types, types.size() == 0);
   }

   @Test
   public static void noDuplicateModifiers() {
      Map<String, Modifier> map = new HashMap<>();
      List<Modifier> fails = new ArrayList<>();

      for (Modifier m : ModifierLib.getAll()) {
         String desc = m.getFullDescription();
         boolean skip = false;

         for (Global gt : m.getGlobals()) {
            skip |= gt instanceof GlobalLearnSpell;
            skip |= gt instanceof GlobalStartWithItem;
         }

         if (!skip) {
            Modifier existing = map.get(desc);
            if (existing != null) {
               fails.add(existing);
               fails.add(m);
            } else {
               map.put(desc, m);
            }
         }
      }

      Tann.assertTrue("Should be no dupe mods: " + fails, fails.size() == 0);
   }

   @Test
   public static void detectItemsThatShouldBeModifiers() {
      List<Item> bad = new ArrayList<>();

      for (Item i : ItemLib.getMasterCopy()) {
         boolean hasPersonal = false;
         if (i.getPersonals().size() != 0) {
            for (Personal pt : i.getPersonals()) {
               if (!(pt instanceof TriggerPersonalToGlobal)
                  && (!(pt instanceof StartOfCombat) || ((StartOfCombat)pt).eff.getTargetingType() == TargetingType.Self)) {
                  hasPersonal = true;
               }
            }

            if (!hasPersonal) {
               bad.add(i);
            }
         }
      }

      Tann.assertTrue("Should be no items with only globals: " + bad, bad.size() == 0);
   }

   @Test
   public static void validateChallenges() {
      List<Achievement> defaultDifficulty = new ArrayList<>();
      List<Achievement> noUnlockable = new ArrayList<>();

      for (Achievement a : AchLib.getChallenges()) {
         if (a.getDifficulty() == -1.0F) {
            defaultDifficulty.add(a);
            TannLog.log(a + " should have difficulty", TannLog.Severity.error);
         }

         if (a.getUnlockables() == null || a.getUnlockables().length == 0) {
            noUnlockable.add(a);
         }
      }

      Tann.assertTrue("Should be no challenges without unlockable: " + noUnlockable, noUnlockable.size() == 0);
      Tann.assertTrue("Should be no challenges with default difficulty: " + defaultDifficulty, defaultDifficulty.size() == 0);
   }

   @Test
   public static void validateSecrets() {
      List<Achievement> unlockableSecrets = new ArrayList<>();

      for (Achievement a : AchLib.getSecrets()) {
         if (a.getUnlockables() != null && a.getUnlockables().length > 0) {
            unlockableSecrets.add(a);
         }
      }

      Tann.assertTrue("Should be secrets with unlockables: " + unlockableSecrets, unlockableSecrets.size() == 0);
   }

   @Test
   public static void checkForAchievementDuplicates() {
      List<String> names = new ArrayList<>();
      List<Unlockable> unlocks = new ArrayList<>();
      List<Unlockable> badUs = new ArrayList<>();
      List<Achievement> badAs = new ArrayList<>();

      for (Achievement a : AchLib.getAll()) {
         if (names.contains(a.getName())) {
            badAs.add(a);
         }

         names.add(a.getName());

         for (Unlockable unlockable : a.getUnlockables()) {
            if (unlockable != null) {
               if (unlocks.contains(unlockable)) {
                  badUs.add(unlockable);
               }

               if (Tann.isMissingno(unlockable)) {
                  badAs.add(a);
               }

               unlocks.add(unlockable);
            }
         }
      }

      Tann.assertTrue("Should be no bad achs or unls: " + badAs + "," + badUs, badAs.size() + badUs.size() == 0);
   }

   @Test
   public static void checkForAchievementMissingno() {
      List<Achievement> missingnoChievos = new ArrayList<>();
      List<Unlockable> missingnos = new ArrayList<>();
      missingnos.add(MonsterTypeLib.byName("doirgtj"));
      missingnos.add(HeroTypeUtils.byName("doirgtj"));
      missingnos.add(ItemLib.byName("doirgtj"));
      missingnos.add(ModifierLib.byName("doirgtj"));

      for (Achievement a : AchLib.getAll()) {
         for (Unlockable unlockable : a.getUnlockables()) {
            if (unlockable != null && missingnos.contains(unlockable)) {
               missingnoChievos.add(a);
               break;
            }
         }
      }

      Tann.assertTrue("no missingno chiev: " + missingnoChievos, missingnoChievos.size() == 0);
   }

   @Test
   @Slow
   public static void ensureGeneratedHeroesUseAllSides() {
      List<String> bad = new ArrayList<>();

      for (HeroCol col : HeroCol.basics()) {
         List<TextureRegion> list = new ArrayList<>();

         for (EntSide es : HeroTypeUtils.getSidesWithColour(col, false, false)) {
            TextureRegion tr = es.getTexture();
            if (!list.contains(tr)) {
               list.add(tr);
            }
         }

         list.remove(ESB.swordRoulette.val(1).getTexture());

         for (int attempt = 0; attempt < 1000; attempt++) {
            for (int tier = 1; tier <= 3; tier++) {
               HeroType ht = PipeHeroGenerated.generate(col, tier);

               for (EntSide esx : ht.sides) {
                  list.remove(esx.getTexture());
               }
            }

            if (list.size() == 0) {
               break;
            }
         }

         for (TextureRegion tr : list) {
            bad.add(col.colName + "-" + ((AtlasRegion)tr).name);
         }
      }

      Tann.assertTrue("no bads: " + bad, bad.size() <= 1);
   }

   @Test
   public static void testSpellUniqueness() {
      List<String> descriptions = new ArrayList<>();
      List<String> allSpellNames = new ArrayList<>();
      List<String> nameCollisions = new ArrayList<>();
      List<String> descCollisions = new ArrayList<>();

      for (HeroType ht : HeroTypeLib.getMasterCopy()) {
         for (Trait t : ht.traits) {
            Ability s = t.personal.getAbility();
            if (s != null) {
               String name = s.getTitle();
               if (allSpellNames.contains(name)) {
                  nameCollisions.add(name);
               } else {
                  allSpellNames.add(name);
               }

               String description = s.getClass().getSimpleName() + " " + s.describe();
               if (descriptions.contains(description)) {
                  descCollisions.add(description);
               } else {
                  descriptions.add(description);
               }
            }
         }
      }

      Tann.assertTrue("Name collisions should be empty: " + nameCollisions, nameCollisions.size() == 0);
      Tann.assertTrue("Desc collisions should be empty: " + descCollisions, descCollisions.size() == 0);
   }

   @Test
   public static void testSideDifference() {
      Map<Integer, EntSide> map = new HashMap<>();
      List<EntSide> fails = new ArrayList<>();

      for (HeroType h : HeroTypeLib.getMasterCopy()) {
         if (h.size == EntSize.reg && h.heroCol != HeroCol.green) {
            for (EntSide es : h.sides) {
               int hash = EntSide.badHash(es.getBaseEffect());
               if (es.getBaseEffect().getType() != EffType.Blank && hash != -1) {
                  if (map.get(hash) == null) {
                     map.put(hash, es);
                  } else if (!map.get(hash).getTexture().equals(es.getTexture())) {
                     fails.add(es);
                  }
               }
            }
         }
      }

      TestRunner.assertTrue("No bad sides " + fails, fails.size() == 0);
   }

   @Test
   public static void test6Sides() {
      for (HeroType ht : HeroTypeLib.getMasterCopy()) {
         if (ht.sides.length != 6) {
            TestRunner.assertEquals(ht.getName(false) + " should have 6 sides", 6, ht.sides.length);
         }
      }

      for (MonsterType mt : MonsterTypeLib.getMasterCopy()) {
         if (mt.sides.length != 6) {
            TestRunner.assertEquals(mt.getName(false) + " should have 6 sides", 6, mt.sides.length);
         }
      }
   }

   @Test
   public static void testMonsterStats() {
      for (MonsterType mt : MonsterTypeLib.getMasterCopy()) {
         TestRunner.assertEquals("nan effective hp: " + mt.getName(false), false, Float.isNaN(mt.getEffectiveHp()));
         TestRunner.assertEquals("nan avg dmg: " + mt.getName(false), false, Float.isNaN(mt.getAvgEffectTier()));
      }
   }

   @Test
   public static void nameCollision() {
      DungeonContext dummy = DebugUtilsUseful.dummyContext();
      List<String> itemNames = new ArrayList<>();

      for (Item e : ItemLib.getMasterCopy()) {
         if (itemNames.contains(e.getName(false))) {
            throw new RuntimeException("duplicate item name: " + e.getName(false));
         }

         itemNames.add(e.getName(false).toLowerCase());
      }

      List<String> entNames = new ArrayList<>();

      for (EntType ht : EntTypeUtils.getAll()) {
         if (entNames.contains(ht.getName(false))) {
            throw new RuntimeException("duplicate hero name: " + ht.getName(false));
         }

         entNames.add(ht.getName(false).toLowerCase());
      }

      List<String> modifierNames = new ArrayList<>();

      for (Modifier m : ModifierLib.getAll()) {
         boolean skip = false;

         for (Global t : m.getGlobals()) {
            if (t.getStartingItems(dummy) != null) {
               skip = true;
            }
         }

         if (!skip) {
            if (modifierNames.contains(m.getName())) {
               throw new RuntimeException("duplicate modifier name: " + m.getName());
            }

            modifierNames.add(m.getName().toLowerCase());
         }
      }

      List<String> keywordNames = new ArrayList<>();

      for (Keyword k : Keyword.values()) {
         keywordNames.add(k.getName().toLowerCase());
      }

      for (String s : keywordNames) {
         if (entNames.contains(s)) {
            throw new RuntimeException("hero/keyword collsision: " + s);
         }

         if (modifierNames.contains(s)) {
            throw new RuntimeException("modifier/keyword collsision: " + s);
         }

         if (itemNames.contains(s)) {
            throw new RuntimeException("item/keyword collsision: " + s);
         }
      }

      for (String s : itemNames) {
         if (entNames.contains(s)) {
            throw new RuntimeException("hero/item collsision: " + s);
         }

         if (modifierNames.contains(s)) {
            throw new RuntimeException("modifier/item collsision: " + s);
         }
      }

      for (String s : entNames) {
         if (modifierNames.contains(s)) {
            throw new RuntimeException("hero/modifier collision: " + s);
         }
      }
   }

   @Test
   public static void indexPipeTest() {
      DungeonContext dummy = DebugUtilsUseful.dummyContext();
      List<String> itemNames = new ArrayList<>();

      for (Item e : ItemLib.getMasterCopy()) {
         if (itemNames.contains(e.getName(false))) {
            throw new RuntimeException("duplicate item name: " + e.getName(false));
         }

         itemNames.add(e.getName(false).toLowerCase());
      }

      List<String> entNames = new ArrayList<>();

      for (EntType ht : EntTypeUtils.getAll()) {
         if (entNames.contains(ht.getName(false))) {
            throw new RuntimeException("duplicate hero name: " + ht.getName(false));
         }

         entNames.add(ht.getName(false).toLowerCase());
      }

      List<String> modifierNames = new ArrayList<>();

      for (Modifier m : ModifierLib.getAll()) {
         boolean skip = false;

         for (Global t : m.getGlobals()) {
            if (t.getStartingItems(dummy) != null) {
               skip = true;
            }
         }

         if (!skip) {
            if (modifierNames.contains(m.getName())) {
               throw new RuntimeException("duplicate modifier name: " + m.getName());
            }

            modifierNames.add(m.getName().toLowerCase());
         }
      }

      List<String> keywordNames = new ArrayList<>();

      for (Keyword k : Keyword.values()) {
         keywordNames.add(k.getName().toLowerCase());
      }

      List<String> all = new ArrayList<>();
      all.addAll(keywordNames);
      all.addAll(itemNames);
      all.addAll(entNames);
      all.addAll(modifierNames);
      List<String> bad = new ArrayList<>();

      for (String s : all) {
         if (s.length() <= 2 && Tann.contains("zjuq".toCharArray(), s.charAt(0))) {
            bad.add(s);
         }
      }

      Tann.assertBads(bad);
   }

   @Test
   public static void checkItemTriggerPriority() {
      List<Class<? extends Trigger>> exclusions = Arrays.asList(
         MaxHP.class,
         IncomingEffBonus.class,
         OnOverheal.class,
         OnRescue.class,
         HealImmunity.class,
         EmptyMaxHp.class,
         LearnSpell.class,
         LearnTactic.class,
         DamageAdjacentsOnDeath.class,
         StartOfCombat.class,
         TriggerPersonalToGlobal.class,
         StartPoisoned.class,
         StartPetrified.class,
         StartRegenned.class,
         AvoidDeathPenalty.class,
         AfterUseAbility.class,
         DamageImmunity.class,
         OnDeathEffect.class,
         EndOfTurnMana.class,
         KeepShields.class,
         ForceEquip.class,
         StartOfTurnSelf.class,
         ShieldImmunity.class,
         ItemSlots.class,
         SpecialHp.class,
         StoneSpecialHp.class,
         OtherDeathEffect.class,
         EndOfTurnEff.class,
         Permadeath.class,
         LostOnDeath.class,
         MultiplyDamageTaken.class,
         AfterUseDiceEffect.class,
         GhostHP.class,
         LevelUpInto.class,
         KeepName.class
      );
      Set<String> bads = new HashSet<>();

      for (Item e : ItemLib.getMasterCopy()) {
         for (Trigger t : e.getPersonals()) {
            if (!exclusions.contains(t.getClass()) && t.getPriority() != -10.0F) {
               bads.add(t.getClass().getSimpleName());
            }
         }
      }

      Tann.assertTrue("No bad triggers: " + bads, bads.size() == 0);
   }

   @Test
   @Skip
   public static void testBlankSides() {
      List<EntSide> sides = EntSidesLib.getAllSidesWithValue();
      List<EntSide> bads = new ArrayList<>();

      for (EntSide es : sides) {
         Eff e = es.getBaseEffect();
         if (e.getType() == EffType.Blank
            && !e.describe().contains("exert")
            && e.getKeywords().isEmpty()
            && es.size.getBlank() != es
            && es != ESB.blankPetrified
            && es != EntSidesBlobBig.blank
            && es != ESB.blankSingleUsed
            && es != ESB.blankExerted
            && !e.hasKeyword(Keyword.cleanse)
            && es != ESB.blankUnset
            && es != ESB.blankItem
            && es != ESB.blankCurse
            && es != ESB.blankFumble
            && !((AtlasRegion)es.getTexture()).name.contains("/generic")) {
            bads.add(es);
         }
      }

      if (bads.size() > 0) {
         System.out.println(bads);
      }

      TestRunner.assertTrue("should be no bads " + bads, bads.size() == 0);
   }

   @Test
   @Skip
   public static void testItemsInOrder() {
      List<Item> eqs = ItemLib.getMasterCopy();
      List<Item> failed = new ArrayList<>();
      int prevTier = -99;

      for (Item e : eqs) {
         if (e.getTier() > 0 && e.getTier() < prevTier) {
            failed.add(e);
         }

         prevTier = e.getTier();
      }

      TestRunner.assertEquals("Should be no bad items: " + failed, 0, failed.size());
   }

   @Test
   @Skip
   public static void testAllItemsDescribed() {
      List<Item> failed = new ArrayList<>();

      for (Item e : ItemLib.getMasterCopy()) {
         for (Trigger t : e.getPersonals()) {
            if (t.makePanelActor(true) == null) {
               failed.add(e);
            }
         }
      }

      Tann.assertEquals("Should be no failed " + failed, 0, failed.size());
   }

   @Test
   public static void checkOmniCollisions() {
      List<String> exceptions = Arrays.asList();
      List<String> dupes = new ArrayList<>();
      List<String> all = new ArrayList<>();

      for (String s : getAllStrings()) {
         s = s.replaceAll("\\s+", "");
         registerString(s, dupes, all);
      }

      dupes.removeAll(exceptions);
      Tann.assertEquals("Should be no dupe names: " + dupes, 0, dupes.size());
   }

   private static List<String> getAllStrings() {
      return getAllStrings(true, true, true, true, true);
   }

   private static List<String> getAllStrings(boolean keyword, boolean spell, boolean item, boolean entity, boolean modifiers) {
      List<String> result = new ArrayList<>();
      if (keyword) {
         for (Keyword k : Keyword.values()) {
            result.add(k.getName());
         }
      }

      if (spell) {
         for (Ability s : AbilityUtils.getAll()) {
            result.add(s.getTitle());
         }
      }

      if (item) {
         for (Item e : ItemLib.getMasterCopy()) {
            result.add(e.getName(false));
         }
      }

      if (entity) {
         for (EntType et : EntTypeUtils.getAll()) {
            result.add(et.getName(false));
         }
      }

      if (modifiers) {
         for (Modifier m : ModifierLib.getAll()) {
            boolean itemCurse = false;

            for (Global gt : m.getGlobals()) {
               if (gt instanceof GlobalStartWithItem) {
                  itemCurse = true;
               }
            }

            if (!itemCurse) {
               result.add(m.getName());
            }
         }
      }

      return result;
   }

   private static void registerString(String s, List<String> dupes, List<String> all) {
      s = s.toLowerCase();
      if (all.contains(s)) {
         dupes.add(s);
      } else {
         all.add(s);
      }
   }

   @Test
   public static void checkBlight() {
      for (Modifier c : PipeMonsterJinx.makeAllCurses()) {
         TestRunner.assertTrue("Should be no null accursed curses", c != null);
         TestRunner.assertTrue("Should be no missingno accursed curses", c != PipeMod.getMissingno());
      }

      PipeMonsterJinx pmj = new PipeMonsterJinx();

      for (int i = 0; i < PipeMonsterJinx.makeAllCurses().size(); i++) {
         MonsterType mt = pmj.makeIndexed(i);
         TestRunner.assertTrue("Should be no null accursed curse descriptions", !mt.traits.get(0).personal.describeForSelfBuff().contains(" random "));
      }
   }

   @Test
   public static void metaOrderingTest() {
      List<Modifier> metaFirst = new ArrayList<>();

      for (Modifier m : ModifierLib.getAll()) {
         if (!m.getName().equalsIgnoreCase("skip") && m.getGlobals().get(0).metaOnly()) {
            metaFirst.add(m);
         }
      }

      Tann.assertTrue("Should be no metas first: " + metaFirst, metaFirst.size() == 0);
   }

   @Test
   public static void sideSize() {
      List<EntType> bad = new ArrayList<>();

      for (EntType et : EntTypeUtils.getAll()) {
         EntSize sz = et.size;

         for (EntSide es : et.sides) {
            if (es.size != sz) {
               bad.add(et);
            }
         }
      }

      Tann.assertTrue("Should be no bad size: " + bad, bad.size() == 0);
   }

   @Test
   public static void chievoTypes() {
      UnlockManager um = com.tann.dice.Main.self().masterStats.getUnlockManager();
      List<Achievement> all = um.getAllAchievements();
      List<Achievement> types = um.getAllTypedAchievements();
      Comparator<Achievement> comp = new Comparator<Achievement>() {
         public int compare(Achievement o1, Achievement o2) {
            return o2.getName().compareTo(o1.getName());
         }
      };
      Collections.sort(all, comp);
      Collections.sort(types, comp);
      List<Achievement> tmp = new ArrayList<>(all);
      tmp.removeAll(types);
      Tann.assertTrue("All should contain none not in types: " + tmp, tmp.isEmpty());
      Tann.assertEquals("Achievement lists should be the same size", all.size(), types.size());
      Tann.assertTrue("Achievement lists should be identical", all.equals(types));
   }

   @Test
   public static void chievoMissingno() {
      EquipAchievement.testMissingno();
   }

   @Test
   public static void unusedPortraits() {
      List<AtlasRegion> used = new ArrayList<>();

      for (MonsterType mt : MonsterTypeLib.getMasterCopy()) {
         used.add(mt.portrait);
      }

      for (HeroType ht : HeroTypeLib.getMasterCopy()) {
         used.add(ht.portrait);
      }

      List<AtlasRegion> bads = new ArrayList<>();

      for (AtlasRegion ar : Tann.getRegionsStartingWith(com.tann.dice.Main.atlas, "portrait")) {
         String tName = ar.name.toLowerCase();
         if (!tName.contains("unused/")
            && !tName.contains("placeholder/")
            && !tName.contains("old/")
            && !tName.contains("to")
            && !tName.contains("/rnd/")
            && !tName.contains("Tw1n".toLowerCase())
            && !tName.contains("error")
            && !tName.contains("special/")
            && !used.contains(ar)) {
            bads.add(ar);
         }
      }

      Tann.assertTrue("Should be no unused portraits: " + bads, bads.size() == 0);
   }

   @Test
   public static void validateMonsterSideOrdering() {
      List<MonsterType> badTypes = new ArrayList<>();
      List<TP<Integer, Integer>> tests = Arrays.asList(new TP<>(0, 1), new TP<>(1, 4), new TP<>(4, 5), new TP<>(2, 5), new TP<>(3, 5));
      List<MonsterType> list = MonsterTypeLib.getMasterCopy();
      list.removeAll(
         Arrays.asList(MonsterTypeLib.byName("sudul"), MonsterTypeLib.byName("chaos orb"), MonsterTypeLib.byName("the hand"), MonsterTypeLib.byName("golem"))
      );

      for (MonsterType mt : list) {
         EntSide[] sides = mt.getNiceSides();

         for (TP<Integer, Integer> t : tests) {
            if (sides[t.a].getBaseEffect().getValue() < sides[t.b].getBaseEffect().getValue()) {
               badTypes.add(mt);
               break;
            }
         }
      }

      Tann.assertTrue("bad types is empty: " + badTypes, badTypes.isEmpty());
   }

   @Test
   public static void duplicateStatNames() {
      List<Stat> allStats = StatLib.makeAllStats();
      Set<String> names = new HashSet<>();
      List<String> badNames = new ArrayList<>();

      for (Stat s : allStats) {
         String name = s.getName();
         if (names.contains(name)) {
            badNames.add(name);
         } else {
            names.add(name);
         }
      }

      Tann.assertTrue("no bad names: " + badNames, badNames.size() == 0);
   }

   @Test
   @SkipNonTann
   public static void checkBoptia() {
      List<Option> fields = new ArrayList<>();

      for (Field f : OptionLib.class.getDeclaredFields()) {
         if (java.lang.reflect.Modifier.isStatic(f.getModifiers()) && Option.class.isAssignableFrom(f.getType())) {
            try {
               fields.add((Option)f.get(null));
            } catch (IllegalAccessException var6) {
               var6.printStackTrace();
            }
         }
      }

      List<Option> method = OptionUtils.getAll();
      List<BOption> skipped = Arrays.asList(OptionLib.LANDSCAPE_LOCK);
      fields.removeAll(skipped);
      method.removeAll(skipped);
      List<Option> cpy = new ArrayList<>(method);
      cpy.removeAll(fields);
      Tann.assertBads(cpy);
      cpy.clear();
      cpy.addAll(fields);
      cpy.removeAll(method);
      Tann.assertBads(cpy);
   }

   @Test
   public static void noPlaceholderWhenPrepping() {
      for (Achievement a : AchLib.getAll()) {
         if (a instanceof PlaceholderAchievement) {
            throw new RuntimeException("placeholder achievement found");
         }
      }
   }

   @Test
   public static void generateValidationOfDesignedHeroes() {
      List<HeroType> bads = new ArrayList<>();
      List<String> excl = Arrays.asList("statue", "Twin", "sidey", "sculpture");

      for (HeroType ht : HeroTypeLib.getMasterCopy()) {
         if (!excl.contains(ht.getName(false).toLowerCase()) && !PipeHeroGenerated.finalChecks(ht)) {
            bads.add(ht);
         }
      }

      Tann.assertTrue("Should be no bads: " + bads, bads.isEmpty());
   }

   @Test
   public static void ensureLootModeCannotGenerateDeepPockets() {
      DungeonContext dc = new LootConfig(Difficulty.Heaven).makeContext();
      int ATTEMPTS = 20;

      for (int i = 0; i < 20; i++) {
         List<Modifier> mods = PhaseGeneratorDifficulty.getModifiersForChoiceDebug(Difficulty.Heaven, dc);
         Tann.assertTrue("no dps", !mods.contains(ModifierLib.byName("Deep Pockets")));
         Tann.assertTrue("no Better Items", !mods.contains(ModifierLib.byName("Better Items")));
      }
   }

   @Test
   public static void ensureCollisionBitsDiffer() {
      Class clazz = Collision.class;
      long bits = 0L;
      List<String> bads = new ArrayList<>();

      for (Field f : clazz.getFields()) {
         if (f.getType() == long.class) {
            try {
               long val = (Long)f.get(null);
               if ((bits & val) > 0L && !f.getName().contains("WIDE")) {
                  bads.add(f.getName());
               }

               bits |= val;
            } catch (IllegalAccessException var10) {
               throw new RuntimeException(var10);
            }
         }
      }

      Tann.assertTrue("no bads: " + bads, bads.isEmpty());
   }

   @Test
   public static void hoarderCollectorCurator() {
      List<TextureRegion> allRegions = new ArrayList<>();

      for (String s : new String[]{"hoarder", "collector", "curator"}) {
         HeroType ht = HeroTypeLib.byName(s);
         Tann.assertTrue(!ht.isMissingno());

         for (EntSide es : ht.sides) {
            allRegions.add(es.getTexture());
         }
      }

      Tann.uniquify(allRegions);
      Tann.assertEquals("Should be 18 unique sides", 18, allRegions.size());
   }

   @Test
   public static void noUnnecessaryAlls() {
      List<Item> fails = new ArrayList<>();

      for (Item item : ItemLib.getMasterCopy()) {
         for (Personal personalTrigger : item.getPersonals()) {
            if (personalTrigger instanceof AffectSides) {
               AffectSides as = (AffectSides)personalTrigger;
               if (as.getEffects().size() != 0) {
                  AffectSideEffect first = as.getEffects().get(0);
                  if (!(first instanceof AffectByIndex)
                     && !(first instanceof ReplaceWithEnt)
                     && as.getConditions().size() == 1
                     && as.getConditions().get(0) instanceof SpecificSidesCondition
                     && ((SpecificSidesCondition)as.getConditions().get(0)).specificSidesType == SpecificSidesType.All) {
                     fails.add(item);
                  }
               }
            }
         }
      }

      Tann.assertTrue("Should be no fails: " + fails, fails.isEmpty());
   }

   @Test
   public static void checkSST() {
      List<String> shorts = new ArrayList<>();

      for (SpecificSidesType value : SpecificSidesType.values()) {
         shorts.add(value.getShortName());
      }

      int size = shorts.size();
      Tann.uniquify(shorts);
      Tann.assertEquals("Should be no dupe shorts", size, shorts.size());
   }

   @Test
   public static void bannedCharactersInSpellsHeroesModifiersItems() {
      List<String> banned = new ArrayList<>(Arrays.asList(":", "add ", "summon "));
      String alsoBanned = "-+'.+#$£*&%!():;@?,<>\\\"[]{}`¬";

      for (char c : alsoBanned.toCharArray()) {
         banned.add("" + c);
      }

      List<String> bads = new ArrayList<>();

      for (String s : getAllStrings()) {
         for (String b : banned) {
            if (s.contains(b)) {
               bads.add(s);
            }
         }
      }

      Tann.assertEquals("Should be no bads: " + bads, 0, bads.size());
   }

   @Test
   public static void specificModeAddPhaseCheck() {
      List<String> bads = new ArrayList<>();

      for (Mode m : Mode.getAllModes()) {
         List<ContextConfig> confs = m.getConfigs();
         if (confs.size() != 0) {
            ContextConfig c = confs.get(0);

            for (Global g : c.getSpecificModeGlobals()) {
               if (g instanceof GlobalAddPhase) {
                  bads.add(m.getName() + ":" + g.getClass().getSimpleName());
               }
            }
         }
      }

      Tann.assertEquals("Should be no bads: " + bads, 0, bads.size());
   }

   @Test
   public static void checkBoptionDuplication() {
      List<Option> all = new ArrayList<>();

      for (OptionUtils.EscBopType value : OptionUtils.EscBopType.values()) {
         all.addAll(value.getOptions());
      }

      int amt = all.size();
      Tann.uniquify(all);
      int newAmt = all.size();
      Tann.assertEquals("should be no dupebopt", amt, newAmt);
   }

   @Test
   @Skip
   public static void checkModifiersForMergeableAllEntities() {
      List<Modifier> bads = new ArrayList<>();

      for (Modifier modifier : ModifierLib.getAll()) {
         GlobalAllEntities prev = null;

         for (Global global : modifier.getGlobals()) {
            if (global instanceof GlobalAllEntities) {
               GlobalAllEntities nw = (GlobalAllEntities)global;
               if (prev != null && prev.getPlayer() == nw.getPlayer()) {
                  bads.add(modifier);
               }

               prev = nw;
            }
         }
      }

      Tann.assertTrue("should be no bads: " + bads, bads.isEmpty());
   }

   @Test
   public static void allShouldReturnSelf() {
      List<Object> bads = new ArrayList<>();

      for (Item item : ItemLib.getMasterCopy()) {
         if (ItemLib.byName(item.getName(false)) != item) {
            bads.add(item);
         }
      }

      for (HeroType ht : HeroTypeLib.getMasterCopy()) {
         if (HeroTypeLib.byName(ht.getName(false)) != ht) {
            bads.add(ht);
         }
      }

      for (Modifier m : ModifierLib.getAll()) {
         if (ModifierLib.byName(m.getName()) != m) {
            bads.add(m);
         }
      }

      for (MonsterType mt : MonsterTypeLib.getMasterCopy()) {
         if (MonsterTypeLib.byName(mt.getName(false)) != mt) {
            bads.add(mt);
         }
      }

      Tann.assertTrue("should be no bads: " + bads, bads.isEmpty());
   }

   @Test
   public static void noZoneMissingno() {
      List<Zone> bads = new ArrayList<>();

      for (Zone z : Zone.values()) {
         for (MonsterType validMonster : z.validMonsters) {
            if (validMonster.isMissingno()) {
               bads.add(z);
            }
         }
      }

      Tann.assertTrue("should be no bads: " + bads, bads.isEmpty());
   }

   @Test
   @Skip
   public static void noSpecialCharacterItems() {
      List<Character> isp = getUsedSpecialChars(true, false, false, false);
      Tann.assertTrue("Should be no item spch: " + isp, isp.isEmpty());
   }

   @Test
   @Skip
   public static void noSpecialCharacterHeroMonster() {
      List<Character> isp = getUsedSpecialChars(false, true, true, false);
      Tann.assertTrue("Should be no heromon spch: " + isp, isp.isEmpty());
   }

   @Test
   @Skip
   public static void noSpecialCharacterHeroModifier() {
      List<Character> isp = getUsedSpecialChars(false, false, false, true);
      Tann.assertTrue("Should be no mod spch: " + isp, isp.isEmpty());
   }

   public static List<Character> getUsedSpecialChars(boolean items, boolean heroes, boolean monsters, boolean modifiers) {
      String regex = "[a-zA-Z0-9' ]";
      List<Character> result = new ArrayList<>();
      if (items) {
         for (Item item : ItemLib.getMasterCopy()) {
            if (item.getAbility() == null) {
               String s = item.getName().replaceAll(regex, "");

               for (char c : s.toCharArray()) {
                  result.add(c);
               }
            }
         }
      }

      if (heroes) {
         for (HeroType type : HeroTypeLib.getMasterCopy()) {
            String s = type.getName().replaceAll(regex, "");

            for (char c : s.toCharArray()) {
               result.add(c);
            }
         }
      }

      if (monsters) {
         for (MonsterType type : MonsterTypeLib.getMasterCopy()) {
            String s = type.getName().replaceAll(regex, "");

            for (char c : s.toCharArray()) {
               result.add(c);
            }
         }
      }

      if (modifiers) {
         for (Modifier type : ModifierLib.getAll()) {
            String s = type.getName().replaceAll(regex, "");

            for (char c : s.toCharArray()) {
               result.add(c);
            }
         }
      }

      Tann.uniquify(result);
      return result;
   }

   @Test
   public static void roundtripGenerated() {
      roundtripTest(false);
   }

   @Test
   public static void roundtripWild() {
      roundtripTest(true);
   }

   private static void roundtripTest(boolean wild) {
      List<Modifier> mods = PipeMod.makeGenerated(50, null, wild);
      List<Modifier> bads = new ArrayList<>();

      for (Modifier mod : mods) {
         String mn = mod.getName();
         if (!mn.equalsIgnoreCase(ModifierLib.byName(mn).getName())) {
            bads.add(mod);
         }
      }

      Tann.assertTrue("no bad roundtrip mods: " + bads, bads.isEmpty());
   }

   @Test
   public static void sstValToOne() {
      Tann.assertEquals("should add up to 1", 1.0F, ModTierUtils.keywordToSides(SpecificSidesType.All, 1.0F));
   }

   private static List<Modifier> makeAllList(int genAmt) {
      List<Modifier> result = new ArrayList<>(ModifierLib.getAll());

      for (boolean b : Tann.BOTH) {
         result.addAll(PipeMod.makeGenerated(genAmt, null, b));
      }

      return result;
   }

   @Test
   public static void noDoubleRarity() {
      List<Modifier> bads = new ArrayList<>();

      for (Modifier modifier : makeAllList(200)) {
         int amt = 0;

         for (Global global : modifier.getGlobals()) {
            if (global instanceof GlobalRarity) {
               amt++;
            }
         }

         if (amt > 1) {
            bads.add(modifier);
         }
      }

      Tann.assertTrue("Should be no bads: " + bads, bads.isEmpty());
   }

   @Test
   @Slow
   public static void itemOfferShouldNotCollide() {
      int attempts = 10;
      List<String> bads = new ArrayList<>();

      for (int attemptIndex = 0; attemptIndex < 10; attemptIndex++) {
         for (List<Choosable> makeReward : DebugUtilsUseful.makeRewards(20)) {
            for (int i = 0; i < makeReward.size(); i++) {
               for (int j = i + 1; j < makeReward.size(); j++) {
                  if (ChoosableUtils.collides(makeReward.get(i), makeReward.get(j))) {
                     bads.add(makeReward.get(i) + ":" + makeReward.get(j));
                  }
               }
            }
         }
      }

      Tann.assertTrue("No choice coll: " + bads, bads.isEmpty());
   }

   @Test
   public static void noDesignedItemsShouldNeedMarquee() {
      List<String> bads = new ArrayList<>();

      for (Item item : ItemLib.getMasterCopy()) {
         Group cp = new ItemPanel(item, false);
         if (TannStageUtils.hasActor(cp, TextMarquee.class)) {
            bads.add(item.getName());
         }
      }

      for (Modifier mod : ModifierLib.getAll()) {
         Group cp = new ModifierPanel(mod, false);
         if (TannStageUtils.hasActor(cp, TextMarquee.class)) {
            bads.add(mod.getName());
         }
      }

      for (EntType e : EntTypeUtils.getAll()) {
         EntPanelCombat cp = new EntPanelCombat(e.makeEnt());
         cp.layout();
         if (TannStageUtils.hasActor(cp, TextMarquee.class)) {
            bads.add(e.getName());
         }
      }

      for (int i = bads.size() - 1; i >= 0; i--) {
         String s = bads.get(i);
         if (s.contains("enduring") || s.contains("inflict")) {
            bads.remove(i);
         }
      }

      Tann.assertTrue("No designed marquees: " + bads, bads.isEmpty());
   }

   @Test
   public static void pipeType() {
      List<String> bads = new ArrayList<>();
      List<String> all = new ArrayList<>();

      for (PipeType value : PipeType.values()) {
         for (Pipe content : value.contents) {
            if (!content.isHiddenAPI()
               && !(content instanceof PipeMaster)
               && !content.getClass().getSimpleName().contains("Meta")
               && !(content instanceof PipeItemSticker)) {
               String cn = content.getClass().getSimpleName();
               List<Object> os = content.examples(3);
               if (os.size() == 0 || os.get(0) == null) {
                  bads.add(cn + "(failed)");
               }

               if (!cn.equalsIgnoreCase("PipeModAllItem") && !cn.equalsIgnoreCase("PipeModAllKeyword")) {
                  if (all.contains(cn)) {
                     bads.add(cn + "(dupe)");
                  }

                  all.add(cn);
               }
            }
         }
      }

      Tann.assertTrue("no pipe stuff in wrong section: " + bads, bads.isEmpty());
   }

   @Test
   public static void checkPresetMissingno() {
      List<String> bads = new ArrayList<>();

      for (CustomPreset customPreset : CustomPreset.getDefault()) {
         for (Modifier contentAsModifier : customPreset.getContentAsModifiers()) {
            if (contentAsModifier.isMissingno()) {
               bads.add(customPreset.getTitle());
               break;
            }
         }
      }

      Tann.assertTrue("no buggy presets: " + bads, bads.isEmpty());
   }

   @Test
   public static void checkSideKeywords() {
      List<String> bads = new ArrayList<>();

      for (EntSide entSide : EntSidesLib.getAllSidesWithValue()) {
         Eff e = entSide.getBaseEffect();
         if (e.getType() != EffType.Blank) {
            Eff base = e.copy();
            base.clearKeywords();

            for (Keyword keyword : e.getKeywords()) {
               if (!KUtils.allowAddingKeyword(keyword, base)) {
                  bads.add(entSide.getTexture().toString());
               }
            }
         }
      }

      Tann.assertTrue("no buggy side keywords: " + bads, bads.isEmpty());
   }

   @Test
   @Slow
   public static void nameCapitalisation() {
      List<String> bads = new ArrayList<>();
      List<String> blob = new ArrayList<>();

      for (Item item : ItemLib.getMasterCopy()) {
         blob.add(item.getName());
      }

      for (EntType entType : EntTypeUtils.getAll()) {
         blob.add(entType.getName());
      }

      for (String s : blob) {
         if (!s.startsWith("spell") && !s.startsWith("tactic")) {
            char first = s.charAt(0);
            if (!Character.isUpperCase(first) && !Tann.isInt(first + "")) {
               bads.add(s);
            }
         }
      }

      Tann.assertTrue("no uncap: " + bads, bads.isEmpty());
   }

   @Test
   public static void noGeneratedWithRealRarity() {
      testModifierRarityTypes(true);
   }

   private static void testModifierRarityTypes(boolean generated) {
      List<String> bads = new ArrayList<>();
      List<Modifier> mods = new ArrayList<>();
      if (generated) {
         mods.addAll(PipeMod.makeGenerated(100, null, false));
         mods.addAll(PipeMod.makeGenerated(100, null, true));
      } else {
         mods.addAll(ModifierLib.getAll());
      }

      for (Modifier m : mods) {
         for (Global global : m.getGlobals()) {
            if (global instanceof GlobalRarity) {
               bads.add(m.getName());
            }
         }
      }

      Tann.assertTrue("no rarity mismatch: " + bads, bads.isEmpty());
   }

   @Test
   public static void allBlueRedsShouldHaveSpells() {
      List<String> bads = new ArrayList<>();
      List<HeroType> types = new ArrayList<>();
      types.addAll(HeroTypeUtils.getFilteredTypes(HeroCol.blue, null, true));
      types.addAll(HeroTypeUtils.getFilteredTypes(HeroCol.red, null, true));

      for (HeroType type : types) {
         if (type.getSpell() == null) {
            bads.add(type.getName());
         }
      }

      Tann.assertTrue("No bads: " + bads, bads.isEmpty());
   }

   @Test
   @Skip
   public static void doublePlusBad() {
      List<String> bads = new ArrayList<>();

      for (Modifier modifier : ModifierLib.getAll()) {
         if (Tann.countCharsInString('+', modifier.getName()) > 1) {
            bads.add(modifier.getName());
         }
      }

      Tann.assertTrue("No bads: " + bads, bads.isEmpty());
   }

   @Test
   @Slow
   public static void noDupesBetweenWildAndGen() {
      List<String> lgen = new ArrayList<>();
      List<String> lwild = new ArrayList<>();
      int amt = 10;
      int tries = 1000;

      for (boolean wild : Tann.BOTH) {
         List<String> ta = wild ? lwild : lgen;

         for (int i = 0; i < 1000; i++) {
            for (Modifier modifier : PipeMod.makeGenerated(10, null, wild)) {
               ta.add(modifier.getName());
            }
         }
      }

      Tann.uniquify(lgen);
      Tann.uniquify(lwild);
      List<String> bad = Tann.getSharedItems(lgen, lwild);
      Tann.assertTrue("No shared items " + bad, bad.isEmpty());
   }

   @Test
   public static void affectSidesEffectOrdering() {
      List<String> bads = new ArrayList<>();

      for (Item item : ItemLib.getMasterCopy()) {
         for (Personal personalTrigger : item.getPersonals()) {
            if (personalTrigger instanceof AffectSides && badAffectSide((AffectSides)personalTrigger)) {
               bads.add(item.getName());
            }
         }
      }

      Tann.assertTrue("no bads; " + bads, bads.isEmpty());
   }

   private static boolean badAffectSide(AffectSides as) {
      boolean flatBonusFound = false;

      for (AffectSideEffect effect : as.getEffects()) {
         flatBonusFound |= effect instanceof FlatBonus;
         if (flatBonusFound && effect instanceof AddKeyword) {
            return true;
         }
      }

      return false;
   }

   @Test
   public static void mandatoryItems() {
      List<Item> bads = new ArrayList<>();

      for (Item item : ItemLib.getMasterCopy()) {
         boolean mandatory = false;

         for (Personal personalTrigger : item.getPersonals()) {
            if (personalTrigger instanceof ForceEquip) {
               mandatory = true;
            }
         }

         if (item.getTier() > 0 && mandatory || item.getTier() < 0 && !mandatory) {
            bads.add(item);
         }
      }

      Tann.assertBads(bads);
   }

   @Test
   @Skip
   public static void badHeroSides() {
      List<HeroType> bads = new ArrayList<>();

      for (HeroType heroType : HeroTypeLib.getMasterCopy()) {
         for (int i = 0; i < heroType.sides.length; i++) {
            EntSide sd = heroType.sides[i];
            List<EntSide> allTex = new ArrayList<>();
            allTex.add(sd);

            for (int j = 0; j < heroType.sides.length; j++) {
               if (i != j) {
                  EntSide sdj = heroType.sides[j];
                  if (sd.getTexture() == sdj.getTexture()) {
                     allTex.add(sdj);
                  }
               }
            }

            if (allTex.size() == 2 && Math.abs(allTex.get(0).getBaseEffect().getValue() - allTex.get(1).getBaseEffect().getValue()) == 1) {
               bads.add(heroType);
               break;
            }
         }
      }

      Tann.assertBads(bads);
   }

   @Test
   public static void testModifierEssenceCollision() {
      List<String> bads = new ArrayList<>();

      for (Modifier m1 : ModifierLib.getAll()) {
         String essence1 = m1.getEssence();
         if (essence1 != null) {
            for (Modifier m2 : ModifierLib.getAll()) {
               String essence2 = m2.getEssence();
               if (essence2 != null && essence1.equalsIgnoreCase(essence2) && !Collision.collides(m1.getCollisionBits(), m2.getCollisionBits())) {
                  bads.add(m1 + ":" + m2);
               }
            }
         }
      }

      Tann.assertBads(bads);
   }

   @Test
   public static void afterItemModifiers() {
      List<Modifier> bads = new ArrayList<>();

      for (Modifier modifier : ModifierLib.getAll()) {
         Global g = modifier.getSingleGlobalOrNull();
         if (g != null && g instanceof GlobalTurnRequirement) {
            GlobalTurnRequirement gtr = (GlobalTurnRequirement)g;
            Global linked = gtr.debugLinked();
            if (linked instanceof GlobalHeroes) {
               GlobalAllEntities gae = (GlobalAllEntities)linked;
               Personal p = gae.personal;
               if (p instanceof AffectSides) {
                  AffectSides as = (AffectSides)p;
                  if (as.getPriority() != 0.0F) {
                     bads.add(modifier);
                  }
               }
            }
         }
      }

      Tann.assertBads(bads);
   }

   @Test
   public static void noGlobalLinkedWithPTR() {
      List<Modifier> bads = new ArrayList<>();

      for (Modifier modifier : ModifierLib.getAll()) {
         for (Global global : modifier.getGlobals()) {
            if (global instanceof GlobalLinked) {
               GlobalLinked gl = (GlobalLinked)global;
               Trigger t = gl.linkDebug();
               if (t instanceof PersonalTurnRequirement) {
                  bads.add(modifier);
               }
            }
         }
      }

      Tann.assertBads(bads);
   }

   @Test
   public static void specialEventsLevelOne() {
      List<Object> bad = new ArrayList<>();

      for (EventGenerator evg : EventUtils.makeEvents()) {
         LevelRequirement lr = evg.lr;
         if (lr instanceof LevelRequirementHash) {
            LevelRequirementHash lrh = (LevelRequirementHash)lr;
            if (lrh.minLevel <= 1) {
               bad.add(evg);
            }
         }
      }

      Tann.assertBads(bad);
   }

   @Test
   public static void testTutorialPresets() {
      for (Difficulty difficulty : new Difficulty[]{Difficulty.Easy, Difficulty.Hard}) {
         String s = TutorialManager.getTutOverride(new ClassicConfig(difficulty));
         SaveState result = SaveState.loadPasteModeString(s, false);
         String phase = result.phases.get(0);
         String repd = Phase.deserialise(phase).serialise();
         if (repd.contains(ModifierLib.getMissingno().getName())) {
            throw new RuntimeException("Bad tut: " + s);
         }
      }
   }

   @Test
   @Slow
   public static void anotherCurseAttempt() {
      List<String> bads = new ArrayList<>();

      for (int runs = 0; runs < 2; runs++) {
         DungeonContext dc = new DungeonContext(new CurseConfig(), Party.generate(0));

         for (int i = 0; i < 100; i++) {
            List<Phase> phases = new ArrayList<>();
            dc.addPhasesFromCurrentLevel(phases);

            for (Phase phase : phases) {
               if (phase instanceof ChoicePhase) {
                  ChoicePhase cp = (ChoicePhase)phase;
                  List<Choosable> options = cp.getOptions();
                  if (options.get(0) instanceof Modifier || options.get(0) instanceof ReplaceChoosable) {
                     boolean v = options.get(0).getTier() > 0;

                     for (Choosable option : options) {
                        if (v != option.getTier() > 0) {
                           bads.add("Different valence: " + options + ":" + phases);
                        }
                     }
                  }

                  Choosable ch = options.get(0);
                  ch.onChoose(dc, 0);
               }
            }

            if (i > 0 && i % 20 == 0) {
               dc.clearForLoop();
            }

            dc.nextLevel();
            Level l = dc.getCurrentLevel();
            if (l.hasMissingno()) {
               bads.add("missingno level?");
            }
         }

         List<Modifier> currentModifiers = dc.getCurrentModifiers();
         List<Modifier> cpy = new ArrayList<>(currentModifiers);
         Tann.uniquify(cpy);
         if (currentModifiers.size() != cpy.size()) {
            bads.add("Duplicate modifiers: " + currentModifiers);
         }

         for (int i = 0; i < currentModifiers.size(); i++) {
            Modifier a = currentModifiers.get(i);
            if (a.isMissingno()) {
               bads.add("missingno");
            }

            if (a.getEssence() != null) {
               for (int j = i + 1; j < currentModifiers.size(); j++) {
                  Modifier b = currentModifiers.get(j);
                  if (a.getEssence().equalsIgnoreCase(b.getEssence())) {
                     bads.add("same essence: " + a + ":" + b);
                  }
               }
            }
         }
      }

      Tann.assertBads(bads);
   }

   @Test
   @Slow
   public static void testRunsActivatePhases() {
      List<String> bads = new ArrayList<>();

      for (int runs = 0; runs < 10; runs++) {
         DungeonContext dc = new DungeonContext(new ClassicConfig(Difficulty.Normal), Party.generate(0));

         for (int i = 0; i < 20; i++) {
            List<Phase> phases = new ArrayList<>();
            dc.addPhasesFromCurrentLevel(phases);

            for (int j = 0; j < phases.size(); j++) {
               Phase phase = phases.get(j);

               try {
                  phase.activate();
                  phase.deactivate();
                  phase.reactivate();
               } catch (Exception var8) {
                  bads.add(var8.getClass().getSimpleName() + ":" + phase.getClass().getSimpleName());
               }
            }

            dc.nextLevel();
         }
      }

      Tann.assertBads(bads);
   }

   @Test
   @Skip
   public static void noBadDebugSpells() {
      List<Spell> bad = new ArrayList<>();

      for (Spell spell : SpellLib.makeAllSpellsList(true, true, true)) {
         for (Keyword k : spell.getBaseEffect().getKeywords()) {
            if (!SpellUtils.allowAddingKeyword(k)) {
               bad.add(spell);
            }
         }
      }

      Tann.assertBads(bad);
   }

   @Test
   public static void testFacadeCollision() {
      Set<String> st = new HashSet<>();
      List<String> bads = new ArrayList<>();

      for (String folder : FacadeUtils.folderNames()) {
         String thr = folder.substring(0, 3);
         if (st.contains(thr) || FacadeUtils.indexedFullData(thr, 0) == null) {
            bads.add(thr);
         }

         st.add(thr);
      }

      Tann.assertBads(bads);
   }
}

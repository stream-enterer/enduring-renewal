package com.tann.dice.gameplay.trigger.personal;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.tann.dice.gameplay.content.ent.Ent;
import com.tann.dice.gameplay.content.ent.Hero;
import com.tann.dice.gameplay.content.ent.die.side.EntSide;
import com.tann.dice.gameplay.content.ent.type.EntType;
import com.tann.dice.gameplay.content.ent.type.HeroType;
import com.tann.dice.gameplay.content.ent.type.MonsterType;
import com.tann.dice.gameplay.content.item.Item;
import com.tann.dice.gameplay.context.DungeonContext;
import com.tann.dice.gameplay.effect.Buff;
import com.tann.dice.gameplay.effect.Trait;
import com.tann.dice.gameplay.effect.eff.Eff;
import com.tann.dice.gameplay.effect.targetable.Targetable;
import com.tann.dice.gameplay.effect.targetable.ability.Ability;
import com.tann.dice.gameplay.effect.targetable.ability.spell.Spell;
import com.tann.dice.gameplay.fightLog.EntSideState;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.fightLog.Snapshot;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.choosable.Choosable;
import com.tann.dice.gameplay.trigger.Trigger;
import com.tann.dice.gameplay.trigger.global.Global;
import com.tann.dice.gameplay.trigger.personal.util.CalcStats;
import com.tann.dice.screens.dungeon.panels.entPanel.EntPanelCombat;
import com.tann.dice.statics.ImageUtils;
import com.tann.dice.statics.Images;
import com.tann.dice.util.Colours;
import com.tann.dice.util.ImageActor;
import com.tann.dice.util.Tann;
import com.tann.dice.util.TannLog;
import com.tann.dice.util.lang.Words;
import com.tann.dice.util.tp.TP;
import java.util.List;
import java.util.Objects;

public abstract class Personal extends Trigger {
   public static final String UNSET_IMAGE = "unset";
   private transient TextureRegion image;
   public transient Buff buff;
   Boolean overrideShow;
   private String lastCachedDescriptionLang;
   private String cachedDescription;
   Trait trait;
   protected CalcStats calcStats;
   boolean showAsIncoming = true;

   private TextureRegion loadImage(String name) {
      return ImageUtils.loadExtNull("trigger/" + name);
   }

   public int bonusEmptyMaxHp(Integer maxHp, int empties) {
      return 0;
   }

   public int getBonusMaxHp(int maxHp, EntState state) {
      return 0;
   }

   public void endOfTurn(EntState entState) {
   }

   public Integer alterTakenDamage(int damage, Eff eff, Snapshot snapshot, EntState self, Targetable targetable) {
      return damage;
   }

   public Integer getPoisonDamage() {
      return 0;
   }

   public boolean backRow() {
      return false;
   }

   public boolean onRescue(EntState saved) {
      return false;
   }

   public void onDeath(EntState self, Snapshot snapshot) {
   }

   public Integer getRegen() {
      return 0;
   }

   public void startOfTurn(EntState self, int turn) {
   }

   public void affectSide(EntSideState sideState, EntState owner, int triggerIndex) {
   }

   public boolean keepShields() {
      return false;
   }

   public boolean poisonSpecificImmunity() {
      return false;
   }

   public boolean stopResurrect() {
      return false;
   }

   public String getImageName() {
      return "unset";
   }

   public final boolean hasImage() {
      return this.getImage() != null && !Objects.equals(this.getImageName(), "unset");
   }

   public void afterUse(EntState entState, EntSide side) {
   }

   public void drawOnPanel(Batch batch, EntPanelCombat entPanelCombat) {
   }

   public String[] getSound() {
      return null;
   }

   public boolean preventAction() {
      return false;
   }

   public boolean dodgeAttack() {
      return false;
   }

   public boolean showInDiePanel() {
      return this.showInEntPanel();
   }

   public Personal overrideShow(boolean b) {
      this.overrideShow = b;
      return this;
   }

   protected boolean showInEntPanelInternal() {
      return false;
   }

   public final boolean showInEntPanel() {
      return this.overrideShow != null ? this.overrideShow : this.showInEntPanelInternal();
   }

   public Cleansed.CleanseType getCleanseType() {
      return null;
   }

   public final TextureRegion getImage() {
      TextureRegion override = this.overrideImage();
      if (override != null) {
         return override;
      } else {
         if (this.image == null) {
            this.image = this.loadImage(this.getImageName());
         }

         return this.image;
      }
   }

   protected TextureRegion overrideImage() {
      return null;
   }

   public String describeForTriggerPanel() {
      if (this.cachedDescription == null || !Objects.equals(com.tann.dice.Main.self().translator.getLanguageCode(), this.lastCachedDescriptionLang)) {
         String described;
         try {
            described = this.describeForSelfBuff();
         } catch (Exception var3) {
            TannLog.error(var3, "seefgfrew");
            described = var3.getClass().getSimpleName();
         }

         if (this.buff == null) {
            this.cachedDescription = described;
         } else {
            this.cachedDescription = described + this.buff.getTurnsString();
         }

         this.lastCachedDescriptionLang = com.tann.dice.Main.self().translator.getLanguageCode();
      }

      return this.cachedDescription;
   }

   public void clearDescCache() {
      this.cachedDescription = null;
   }

   public String describeForGiveBuff(Eff source) {
      String prefix = "";
      switch (source.getTargetingType()) {
         case Single:
            if (source.isFriendlyForce()) {
               prefix = "Target " + Words.entName(true, source.isFriendlyForce(), null) + ":[n]";
            } else {
               prefix = "Give ";
            }
            break;
         case Group:
            if (source.isFriendlyForce()) {
               prefix = "All " + Words.entName(source, true) + ":[n]";
            } else {
               prefix = "All " + Words.entName(source, true) + ":[n]";
            }
      }

      if (this.removeGainsFromGiveText()) {
         prefix = prefix.replaceAll("gains ", "").replaceAll("gain ", "");
      }

      if (this.removeGiveFromGiveText()) {
         prefix = prefix.replaceAll("Give ", "");
      }

      return prefix.contains("ains")
         ? prefix + "'" + this.describeForSelfBuff().toLowerCase() + "'"
         : prefix + Words.capitaliseFirst(this.describeForSelfBuff().toLowerCase());
   }

   public boolean allowDeath(EntState state) {
      return true;
   }

   public void damageTaken(
      EntState source, EntState self, Snapshot snapshot, int damage, int damageTakenThisTurn, Eff sourceEff, Targetable targetable, int minTriggerPipHp
   ) {
   }

   protected boolean removeGainsFromGiveText() {
      return false;
   }

   protected boolean removeGiveFromGiveText() {
      return false;
   }

   public boolean isRecommended(EntState sourceState, EntState targetPresent, EntState targetFuture) {
      return true;
   }

   public void startOfCombat(Snapshot snapshot, EntState entState) {
   }

   public Ability getAbility() {
      return null;
   }

   public boolean avoidDeathPenalty() {
      return false;
   }

   public void setTrait(Trait trait) {
      this.trait = trait;
   }

   public Trait getTrait() {
      return this.trait;
   }

   public int affectHealing(int hp) {
      return hp;
   }

   public int affectShields(int shield) {
      return shield;
   }

   public int calcBackRowTurn() {
      return -1;
   }

   public float getEffectTier(int pips, int tier) {
      return Float.NaN;
   }

   public void setCalcStats(CalcStats calcStats) {
      this.calcStats = calcStats;
   }

   public float affectStrengthCalc(float total, float avgRawValue, EntType type) {
      return this.skipCalc() ? total : Float.NaN;
   }

   public final float getStrengthCalc(float total, float avgRawValue, EntType type) {
      return this.calcStats != null ? total + this.calcStats.getDamage() : this.affectStrengthCalc(total, avgRawValue, type);
   }

   public String affectItemName(String current) {
      return current;
   }

   public boolean skipCalc() {
      return false;
   }

   public final float getTotalHpCalc(float hp, EntType entType) {
      return this.calcStats != null ? hp + this.calcStats.getHp() : this.affectTotalHpCalc(hp, entType);
   }

   public float affectTotalHpCalc(float hp, EntType entType) {
      return this.skipCalc() ? hp : Float.NaN;
   }

   public void overHeal(EntState entState, int overHeal) {
   }

   public boolean allowOverheal() {
      return false;
   }

   public void afterUseAbility(Snapshot snapshot, Ability ability, EntState entState) {
   }

   public boolean forceEquip() {
      return false;
   }

   public List<Personal> getLinkedPersonalsNoSnapshot(EntState entState) {
      return null;
   }

   public List<Personal> getLinkedPersonals(Snapshot snapshot, EntState entState) {
      return null;
   }

   public boolean showAsIncoming() {
      return this.showAsIncoming;
   }

   public Global getGlobalFromPersonalTrigger() {
      return null;
   }

   public boolean immuneToAbilities() {
      return false;
   }

   public void onSpendAbilityCost(int amtGained, Snapshot snapshot, EntState es) {
   }

   public boolean immuneToHealing() {
      return false;
   }

   public boolean immuneToShields() {
      return false;
   }

   public boolean ignoreItem(Item item) {
      return false;
   }

   public void endOfLevel(EntState entState, Snapshot snapshot) {
   }

   public int affectItemSlots(int amt) {
      return amt;
   }

   public int affectFinalShields(int shields) {
      return shields;
   }

   public void affectSideFinal(EntSideState entSideState, EntState entState) {
   }

   public boolean allowTraits() {
      return true;
   }

   public void dieStoppedOn(EntSideState currentSide, EntState entState) {
   }

   public boolean skipEquipScreen() {
      return false;
   }

   public Hero getExtraHero() {
      return null;
   }

   public boolean unequip(Ent ent) {
      return false;
   }

   public boolean bannedFromLateStart() {
      return false;
   }

   @Override
   public Actor makePanelActorI(boolean big) {
      if (this.hasImage()) {
         Actor ia = new ImageActor(this.getImage());
         Group g = Tann.makeGroup(new ImageActor(Images.panelNaked));
         g.addActor(ia);
         Tann.center(ia);
         return g;
      } else {
         return super.makePanelActorI(big);
      }
   }

   public boolean isGenerated() {
      return false;
   }

   public boolean autoLockLite() {
      return false;
   }

   public TextureRegion getSpecialImage() {
      return null;
   }

   public boolean immuneToDamage(boolean poison) {
      return false;
   }

   public boolean canBeAddedTo(EntState entState) {
      return true;
   }

   public TP<Integer, Boolean> cleanseBy(int cleanseAmt) {
      return null;
   }

   public int getCleanseAmt() {
      return 0;
   }

   public static Boolean treatAsIncoming(Personal t, List<Personal> visualTriggers) {
      boolean classMatch = false;
      String desc = t.describeForTriggerPanel();
      Class<? extends Personal> clazz = (Class<? extends Personal>)t.getClass();
      if (visualTriggers.contains(t)) {
         return false;
      } else {
         for (int i = 0; i < visualTriggers.size(); i++) {
            Personal test = visualTriggers.get(i);
            if (desc != null && desc.equals(test.describeForTriggerPanel())) {
               return false;
            }

            classMatch |= test.getClass() == clazz;
         }

         return classMatch ? null : true;
      }
   }

   public Personal transformForBuff() {
      return this;
   }

   public boolean skipTraitPanel() {
      return false;
   }

   public boolean canLevelUp() {
      return true;
   }

   public String getDisplayName(String name) {
      return null;
   }

   public boolean persistThroughDeathBuff() {
      return false;
   }

   public Float getOverridePowerEstimate(MonsterType monsterType) {
      return null;
   }

   public Integer limitHp(int maxHp) {
      return maxHp;
   }

   public Color getImageCol() {
      return Colours.z_white;
   }

   public boolean showImageInDiePanelTitle() {
      return true;
   }

   public void onKill(EntState entState, Ent killed) {
   }

   public void onOtherDeath(Snapshot snapshot, EntState dead, EntState self) {
   }

   public int affectStartingHp(int hp) {
      return hp;
   }

   public Personal genMult(int mult) {
      return null;
   }

   public void targetGainsShield(EntState me, EntState shieldGainer) {
   }

   public void dieLocked(EntSideState currentSide, EntState entState) {
   }

   public void onChoose(DungeonContext dc, Choosable source) {
   }

   public final Spell getSpell() {
      return this.getAbility() instanceof Spell ? (Spell)this.getAbility() : null;
   }

   public Actor getTraitActor() {
      return (Actor)(this.hasImage() ? new ImageActor(this.getImage()) : this.makePanelActor(false));
   }

   public String transformChat(String chatText, EntState vis) {
      return chatText;
   }

   public boolean skipNetAndIcon() {
      return false;
   }

   public HeroType affectLevelup(HeroType from, HeroType to) {
      return null;
   }

   public boolean singular() {
      return false;
   }
}

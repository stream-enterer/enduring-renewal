package com.tann.dice.gameplay.fightLog.event.entState;

import com.tann.dice.gameplay.effect.eff.keyword.Keyword;
import com.tann.dice.screens.dungeon.panels.entPanel.EntPanelCombat;

public class TextEvent extends StateEvent {
   public static final TextEvent Redirected = new TextEvent("[grey]redirected");
   public static final TextEvent Undying = new TextEvent("undying");
   public static final TextEvent ImmuneToDebuff = new TextEvent("cleansed");
   public static final TextEvent ImmuneToPoison = new TextEvent("[green]immune");
   public static final TextEvent ImmuneToDamage = new TextEvent("immune");
   public static final TextEvent Immune = new TextEvent("immune");
   public static final TextEvent ALREADY_MAX = new TextEvent("[red]full");
   public static final TextEvent Dodge = new TextEvent("dodged");
   public static final TextEvent Petrify = new TextEvent("[grey]petrified");
   public static final TextEvent ImmuneToSpells = new TextEvent("[blue]immune");
   public static final TextEvent Corrupted = new TextEvent("[purple]mana-burn");
   public static final TextEvent RAMPAGE_EVENT = new TextEvent(Keyword.rampage.getColourTaggedString());
   public static final TextEvent RESCUE_EVENT = new TextEvent(Keyword.rescue.getColourTaggedString());
   public static final TextEvent DISCARD_EVENT = new TextEvent(Keyword.potion.getColourTaggedString());
   public static final TextEvent PLAGUE = new TextEvent("[purple]plague");
   public static final TextEvent GONG = new TextEvent("[grey]GONG");
   public static final TextEvent CHIP = new TextEvent("[pink]chip");
   final String text;

   public TextEvent(String text) {
      this.text = text;
   }

   @Override
   public void act(EntPanelCombat panel) {
      panel.addMessage(this.text);
   }
}

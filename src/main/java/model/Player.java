package model;

import java.util.ArrayList;
import java.util.List;

public class Player {
    private List<String> inventory;
    private int maxSkill;
    private int currentSkill;
    private int maxStamina;
    private int currentStamina;
    private int maxFear;
    private int currentFear;
    private int maxLuck;
    private int currentLuck;

    public Player(int skill, int stamina, int luck) {
        this.maxSkill = skill;
        this.currentSkill = skill;
        this.maxStamina = stamina;
        this.currentStamina = stamina;
        this.maxFear = 10;
        this.currentFear = 0;
        this.maxLuck = luck;
        this.currentLuck = luck;
        this.inventory = new ArrayList<>();
    }

    // Getters
    public List<String> getInventory() { return new ArrayList<>(inventory); }
    public int getMaxSkill() { return maxSkill; }
    public int getCurrentSkill() { return currentSkill; }
    public int getMaxStamina() { return maxStamina; }
    public int getCurrentStamina() { return currentStamina; }
    public int getMaxFear() { return maxFear; }
    public int getCurrentFear() { return currentFear; }
    public int getMaxLuck() { return maxLuck; }
    public int getLuck() { return currentLuck; }

    // Inventory methods
    public boolean hasItem(String item) {
        return inventory.contains(item);
    }

    public void addItem(String item) {
        if (!inventory.contains(item)) {
            inventory.add(item);
        }
    }

    public boolean removeItem(String item) {
        return inventory.remove(item);
    }

    // Stat modification methods
    public void modifyStamina(int change) {
        currentStamina = Math.min(maxStamina, Math.max(0, currentStamina + change));
    }

    public void modifySkill(int change) {
        currentSkill = Math.min(maxSkill, Math.max(0, currentSkill + change));
        // When fear is high, skill is reduced
        if (currentFear >= maxFear * 0.8) {
            currentSkill = Math.max(0, currentSkill - 1);
        }
    }

    public void modifyFear(int change) {
        int previousFear = currentFear;
        currentFear = Math.min(maxFear, Math.max(0, currentFear + change));
        
        // If fear reaches certain thresholds, it affects other stats
        if (currentFear >= maxFear * 0.8 && previousFear < maxFear * 0.8) {
            modifySkill(-1); // Reduce skill when fear is very high
        }
        if (currentFear >= maxFear * 0.6 && previousFear < maxFear * 0.6) {
            modifyLuck(-1); // Reduce luck when fear is moderately high
        }
    }

    public void modifyLuck(int change) {
        currentLuck = Math.min(maxLuck, Math.max(0, currentLuck + change));
    }

    public boolean isAlive() {
        return currentStamina > 0;
    }
}
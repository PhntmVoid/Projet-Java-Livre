package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Player implements Serializable {
    private List<String> inventory;
    private Map<String, Integer> weapons;
    private int maxSkill;
    private int currentSkill;
    private int baseSkill; // Skill without weapon bonuses
    private int maxStamina;
    private int currentStamina;
    private int maxFear;
    private int currentFear;
    private int maxLuck;
    private int currentLuck;
    private transient Random random;

    public Player(int skill, int stamina, int luck) {
        this.random = new Random();
        this.maxSkill = skill;
        this.currentSkill = skill - 3; // Initial -3 penalty for no weapon
        this.baseSkill = currentSkill;
        this.maxStamina = stamina;
        this.currentStamina = stamina;
        this.maxFear = random.nextInt(6) + 1 + 6;
        this.currentFear = 0;
        this.maxLuck = luck;
        this.currentLuck = luck;
        this.inventory = new ArrayList<>();
        this.weapons = new HashMap<>();
    }

    // Getters
    public List<String> getInventory() { return new ArrayList<>(inventory); }
    public Map<String, Integer> getWeapons() { return new HashMap<>(weapons); }
    public int getMaxSkill() { return maxSkill; }
    public int getCurrentSkill() { return currentSkill; }
    public int getBaseSkill() { return baseSkill; }
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

    // Weapon methods
    public void addWeapon(String weapon, int bonus) {
        weapons.put(weapon, bonus);
        recalculateSkill();
    }

    public void removeWeapon(String weapon) {
        weapons.remove(weapon);
        recalculateSkill();
    }

    private void recalculateSkill() {
        currentSkill = baseSkill;
        for (int bonus : weapons.values()) {
            currentSkill += bonus;
        }
        currentSkill = Math.min(currentSkill, maxSkill);
    }

    // Stat modification methods
    public void modifyStamina(int change) {
        currentStamina = Math.min(maxStamina, Math.max(0, currentStamina + change));
    }

    public void modifySkill(int change) {
        baseSkill = Math.min(maxSkill, Math.max(0, baseSkill + change));
        recalculateSkill();
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
            modifySkill(-1);
        }
        if (currentFear >= maxFear * 0.6 && previousFear < maxFear * 0.6) {
            modifyLuck(-1);
        }
    }

    public void modifyLuck(int change) {
        currentLuck = Math.min(maxLuck, Math.max(0, currentLuck + change));
    }

    public boolean isAlive() {
        return currentStamina > 0 && currentFear < maxFear;
    }

    // Handle deserialization of Random
    private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.random = new Random();
    }
}
package model;

import java.util.ArrayList;
import java.util.List;

public class Player {
    private List<String> inventory;
    private int skill;
    int actualSkill;
    private int stamina;
    int actualStamina;
    int maxFear;
    int actualFear;

    public Player(int skill, int stamina) {
        this.skill = skill;
        this.stamina = stamina;
        this.inventory = new ArrayList<>();
    }

    // Getters et setters
    public List<String> getInventory() { return inventory; }
    public int getSkill() { return skill; }
    public int getStamina() { return stamina; }

    public void addItem(String item) {
        inventory.add(item);
    }

    public void removeItem(String item) {
        inventory.remove(item);
    }

    public void modifyStamina(int change) {
        stamina += change;
    }
}
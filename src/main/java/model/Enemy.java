package model;

public class Enemy {
    private String name;
    private int skill;
    private int endurance;

    public Enemy(String name, int skill, int endurance) {
        this.name = name;
        this.skill = skill;
        this.endurance = endurance;
    }

    public String getName() { return name; }
    public int getSkill() { return skill; }
    public int getEndurance() { return endurance; }

    public void takeDamage(int damage) {
        this.endurance = Math.max(0, this.endurance - damage);
    }
}
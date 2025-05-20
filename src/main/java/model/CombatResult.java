package model;

public class CombatResult {
    private boolean playerWonRound;
    private boolean combatOver;
    private int damage;
    private String message;

    public CombatResult(boolean playerWonRound, boolean combatOver, int damage, String message) {
        this.playerWonRound = playerWonRound;
        this.combatOver = combatOver;
        this.damage = damage;
        this.message = message;
    }

    public boolean isPlayerWonRound() { return playerWonRound; }
    public boolean isCombatOver() { return combatOver; }
    public int getDamage() { return damage; }
    public String getMessage() { return message; }
}
package model;

import java.util.Random;

public class Combat {
    private Player player;
    private Enemy enemy;
    private int roundCount;
    private Random random;

    public Combat(Player player, Enemy enemy) {
        this.player = player;
        this.enemy = enemy;
        this.roundCount = 0;
        this.random = new Random();
    }

    public CombatResult executeRound() {
        roundCount++;
        
        // Calculate attack strengths
        int playerAttackStrength = rollDice(2) + player.getCurrentSkill();
        int enemyAttackStrength = rollDice(2) + enemy.getSkill();

        // Determine who wins the round
        if (playerAttackStrength > enemyAttackStrength) {
            // Player wins round
            enemy.takeDamage(2);
            return new CombatResult(true, false, 2, 
                String.format("Vous blessez votre adversaire! (%d contre %d)", 
                    playerAttackStrength, enemyAttackStrength));
        } else if (enemyAttackStrength > playerAttackStrength) {
            // Enemy wins round
            player.modifyStamina(-2);
            return new CombatResult(false, false, 2, 
                String.format("Votre adversaire vous blesse! (%d contre %d)", 
                    playerAttackStrength, enemyAttackStrength));
        }

        // It's a draw
        return new CombatResult(false, false, 0, 
            String.format("Vos armes s'entrechoquent! (%d contre %d)", 
                playerAttackStrength, enemyAttackStrength));
    }

    public boolean isOver() {
        return !player.isAlive() || enemy.getEndurance() <= 0;
    }

    public boolean playerWon() {
        return player.isAlive() && enemy.getEndurance() <= 0;
    }

    private int rollDice(int numberOfDice) {
        int total = 0;
        for (int i = 0; i < numberOfDice; i++) {
            total += random.nextInt(6) + 1;
        }
        return total;
    }

    public Enemy getEnemy() {
        return enemy;
    }
}
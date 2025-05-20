package model;

public class Combat {
    private Player player;
    private Enemy enemy;
    private int roundCount;

    public Combat(Player player, Enemy enemy) {
        this.player = player;
        this.enemy = enemy;
        this.roundCount = 0;
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
            return new CombatResult(true, false, 2, "Vous blessez votre adversaire!");
        } else if (enemyAttackStrength > playerAttackStrength) {
            // Enemy wins round
            player.modifyStamina(-2);
            return new CombatResult(false, false, 2, "Votre adversaire vous blesse!");
        }

        // It's a draw
        return new CombatResult(false, false, 0, "Vos armes s'entrechoquent!");
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
            total += (int)(Math.random() * 6) + 1;
        }
        return total;
    }
}
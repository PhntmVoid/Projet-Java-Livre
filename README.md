# Projet-Java-Livre: Le Manoir de l'Enfer

## Description

Un jeu de fiction interactive en Java, "Le Manoir de l'Enfer", où le joueur fait des choix pour progresser dans une histoire. Le jeu comprend un scénario chargé depuis un fichier JSON, des statistiques de joueur (habileté, endurance, chance, peur), des combats et des tests de chance. Il propose une interface graphique (GUI) basée sur Swing et une interface textuelle (TUI).

## Fonctionnalités

- **Chargement de Scénario:** Charge les scénarios de jeu depuis des fichiers JSON
- **Statistiques du Joueur:** Gère les attributs du joueur (habileté, endurance, chance, peur)
- **Système de Choix:** Présente aux joueurs des choix qui influencent l'histoire
- **Système de Combat:** Implémente un système de combat avec lancers de dés
- **Tests de Chance:** Intègre des tests de chance qui peuvent affecter le résultat des événements
- **Inventaire:** Permet aux joueurs de collecter et gérer des objets
- **Interface Graphique:** Interface utilisateur graphique avec Swing
- **Interface Textuelle:** Interface en mode console

## Interfaces et Fonctionnalités Détaillées

### Menu Principal
- **Titre du Jeu:** Affiché en grand format
- **Boutons:**
  - "Commencer l'aventure": Lance la création du personnage
  - "Règles du jeu": Affiche les règles dans une fenêtre dédiée
  - "Quitter": Ferme l'application

### Création du Personnage
- **Statistiques Initiales:**
  - HABILETÉ (1d6 + 6): Capacité au combat
  - ENDURANCE (2d6 + 12): Points de vie
  - CHANCE (1d6 + 6): Pour les tests de chance
- **Options:**
  - "Relancer les dés": Régénère les statistiques
  - "Commencer l'aventure": Démarre avec les stats actuelles

### Interface de Jeu Principale
- **Panneau des Statistiques (Gauche):**
  - Barre d'HABILETÉ (verte)
  - Barre d'ENDURANCE (rouge)
  - Barre de CHANCE (jaune)
  - Barre de PEUR (violette)
  - Section Inventaire
- **Zone de Jeu (Centre):**
  - Texte du chapitre actuel
  - Boutons de choix en bas

### Système de Combat
- **Fenêtre de Combat:**
  - Statistiques de l'adversaire
  - Journal de combat
  - Bouton d'attaque
- **Mécanique:**
  - Force d'Attaque = HABILETÉ + 2d6
  - 2 points d'ENDURANCE perdus par coup
  - Combat termine à 0 ENDURANCE

### Tests de Chance
- **Déclenchement:** Sur certains choix
- **Mécanique:**
  - Lance 2d6
  - Compare avec CHANCE actuelle
  - CHANCE -1 après chaque test
- **Résultats:** Affichés dans le texte

## Prérequis

- Kit de Développement Java (JDK) version 17 ou supérieure

## Installation

1. **Cloner le dépôt:**
   ```bash
   git clone https://github.com/PhntmVoid/Projet-Java-Livre.git
   cd Projet-Java-Livre
   ```

2. **Compiler le code:**
   ```bash
   javac src/Main.java
   ```

3. **Lancer le jeu:**
   - Interface graphique:
     ```bash
     java Main
     ```
   - Interface textuelle:
     ```bash
     java Main --text
     ```

## Structure du Projet

```
Projet-Java-Livre/
├── .gradle/                # Fichiers Gradle
├── .idea/                  # Fichiers projet IntelliJ IDEA
├── src/
│   ├── controller/        # Contrôleurs
│   ├── model/            # Modèles de jeu
│   ├── view/             # Interfaces utilisateur
│   ├── Main.java         # Classe principale
│   └── resources/        # Données du jeu
├── build.gradle          # Configuration Gradle
└── README.md            # Ce fichier
```

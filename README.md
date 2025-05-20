# Le Manoir de l'Enfer

## Table des Matières

1. [Introduction](#introduction)
2. [Architecture](#architecture)
3. [Composants du Système](#composants-du-système)
4. [Flux de Données](#flux-de-données)
5. [Interfaces Utilisateur](#interfaces-utilisateur)
6. [Systèmes de Jeu](#systèmes-de-jeu)
7. [Gestion des Données](#gestion-des-données)
8. [Configuration et Déploiement](#configuration-et-déploiement)
9. [Tests et Qualité](#tests-et-qualité)
10. [Évolutions Futures](#évolutions-futures)

## Introduction

Le Manoir de l'Enfer est un jeu de fiction interactive développé en Java, offrant une expérience de jeu immersive où les choix du joueur influencent directement le déroulement de l'histoire. Cette documentation technique détaille l'architecture, les composants et les mécanismes du système.

### Objectifs du Projet
- Créer une expérience de jeu narrative interactive
- Implémenter des mécaniques de jeu de rôle (stats, combats, tests)
- Offrir une interface utilisateur intuitive et engageante
- Assurer une architecture modulaire et extensible

## Architecture

### Vue d'Ensemble
Le projet suit une architecture MVC (Modèle-Vue-Contrôleur) stricte pour garantir :
- Séparation claire des responsabilités
- Maintenance facilitée
- Extensibilité du système
- Tests unitaires simplifiés

### Structure des Packages
```
src/
├── main/
│   ├── java/
│   │   ├── controller/    # Logique de contrôle
│   │   ├── model/        # Modèles de données
│   │   ├── view/         # Interfaces utilisateur
│   │   └── Main.java     # Point d'entrée
│   └── resources/        # Ressources (JSON, images)
└── test/                # Tests unitaires
```

## Composants du Système

### Modèle (Model)

#### Player
- Gestion des statistiques (HABILETÉ, ENDURANCE, CHANCE, PEUR)
- Inventaire dynamique
- États du personnage
- Méthodes de modification des stats avec validation

#### Chapter
- Contenu narratif
- Choix disponibles
- Modificateurs d'état
- Conditions de combat et tests

#### Combat
- Système de résolution des combats
- Calcul des dégâts
- Gestion des tours
- États de combat

### Contrôleur (Controller)

#### GameController
- Gestion du flux de jeu
- Traitement des choix
- Coordination modèle-vue
- Sauvegarde/Chargement

#### ScenarioLoader
- Chargement des scénarios JSON
- Validation des données
- Gestion des erreurs
- Construction des objets de jeu

### Vue (View)

#### Interface Graphique (SwingUI)
- Composants Swing personnalisés
- Gestion des événements
- Animations et effets visuels
- Mise à jour dynamique

##### Composants Principaux
- Menu principal
- Écran de jeu
- Interface de combat
- Inventaire
- Statistiques du joueur

#### Interface Textuelle (TextUI)
- Mode console
- Commandes textuelles
- Affichage formaté
- Navigation simplifiée

## Flux de Données

### Chargement du Jeu
1. Initialisation du système
2. Chargement du scénario
3. Création du joueur
4. Configuration de l'interface

### Cycle de Jeu
1. Affichage du chapitre
2. Attente de l'action du joueur
3. Traitement de l'action
4. Mise à jour de l'état
5. Retour à l'étape 1

## Systèmes de Jeu

### Combat
- Calcul de la force d'attaque
- Résolution des rounds
- Application des dégâts
- Gestion des états spéciaux

### Tests de Chance
- Lancer de dés virtuel
- Comparaison avec stat CHANCE
- Application des effets
- Réduction de la CHANCE

## Gestion des Données

### Format de Sauvegarde
- Structure JSON
- États du joueur
- Progression du jeu
- Historique des choix

### Scénarios
- Format JSON structuré
- Validation des données
- Gestion des erreurs
- Extensibilité

### Installation
1. Cloner le dépôt
2. Compiler avec Gradle
3. Exécuter le jeu


# Projet-Java-Livre: Le Manoir de l'Enfer

## Description

This is a Java-based interactive fiction game, "Le Manoir de l'Enfer" (The House of Hell), where the player makes choices to navigate through a story. The game features a scenario loaded from a JSON file, player stats (skill, stamina, luck, fear), combat, and luck tests. It offers both a Swing-based graphical user interface (GUI) and a text-based user interface (TUI).

## Features

-   **Scenario Loading:** Loads game scenarios from JSON files.
-   **Player Stats:** Manages player attributes such as skill, stamina, luck, and fear.
-   **Choices:** Presents players with choices that drive the narrative.
-   **Combat System:** Implements a combat system with dice rolls and stat comparisons.
-   **Luck Tests:** Incorporates luck tests that can affect the outcome of events.
-   **Inventory:** Allows players to collect and manage items.
-   **GUI:** Provides a graphical user interface using Swing.
-   **TUI:** Offers a text-based user interface for playing in the console.

## Getting Started

### Prerequisites

-   Java Development Kit (JDK) version 17 or higher

### How to Run

1.  **Clone the repository:**

    ```bash
    git clone <repository_url>
    cd Projet-Java-Livre
    ```

2.  **Compile the code:**

    ```bash
    javac src/Main.java
    ```

3.  **Run the game:**

    -   **GUI:**

        ```bash
        java Main
        ```

    -   **Text UI:**

        ```bash
        java Main --text
        ```

## Project Structure

```
Projet-Java-Livre/
├── .gradle/                # Gradle files
├── .idea/                  # IntelliJ IDEA project files
├── src/                    # Source code
│   ├── controller/         # Controllers (GameController, ScenarioLoader)
│   ├── model/              # Game models (Chapter, Choice, Player, Scenario, etc.)
│   ├── view/               # User interfaces (SwingUI, TextUI)
│   ├── Main.java           # Main class to start the game
│   └── resources/          # Game data (scenarios)
│       └── manoir_enfer.json # Scenario file
├── build.gradle            # Gradle build file
├── Projet-Java-Livre.iml   # IntelliJ IDEA module file
└── README.md               # This file
```

## Dependencies

-   org.json:json:20231013

## Usage

The game starts with a main menu where you can start a new adventure, view the game rules, or quit. When starting a new adventure, you can roll for initial stats (Habileté, Endurance, Chance). The game presents you with choices at each chapter, and your decisions affect the story's outcome.

## Contributing

We welcome contributions to improve Le Manoir de l'Enfer! Here's how you can help:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Make your changes
4. Commit your changes (`git commit -m 'Add amazing feature'`)
5. Push to the branch (`git push origin feature/amazing-feature`)
6. Open a Pull Request

Please make sure to:
- Follow the existing code style
- Add comments for new features
- Update documentation as needed
- Test your changes thoroughly

## License

MIT License

Copyright (c) 2025

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
# Don't Starve Alone
**Don't Starve Alone** is a school project for Epitech Nancy, heavily inspired by the popular game *Don't Starve Together*. This project explores core principles of procedural generation, resource management, and multiplayer game mechanics within an open-world sandbox environment.

In *Don't Starve Alone*, players enter a harsh, procedurally generated world where the main objective is simple: survive. Gather resources, craft tools, and overcome the many challenges presented by a dynamic, evolving environment.

## 🌎 Game Overview
In *Don't Starve Alone*, players enter an expansive sandbox world generated uniquely for each session. Set in a harsh, mysterious environment, the objective is simple: survive for as long as possible. Whether playing alone or with friends, players will need to manage resources, explore the environment, and fend off dangers to endure as long as they can.

### Key Features
- **Procedural World Generation**: Every world is unique! Terrain, resources, and points of interest are dynamically generated for endless exploration.
- **Survival Mechanics**: Hunger, weather, and dangerous wildlife are just some of the elements to navigate as you struggle to stay alive.
- **Resource Gathering and Crafting**: Collect materials to build shelter, craft tools, and create items necessary for survival.
- **Single-player and Multiplayer Modes**: Choose to venture alone or team up with friends to increase your chances of survival.

## 🚀 Getting Started
### Prerequisites
To run *Don't Starve Alone*, make sure you have the following installed:

- **Java Development Kit (JDK)**: Version 17 or above is recommended.
- **Git**: (for cloning the repository)
### Installation
Clone the repository:
```bash
git clone https://github.com/yourusername/DontStarveAlone.git
```
Navigate into the project directory:
```bash
cd DontStarveAlone
```
Compile the project:
```bash
javac -d out/production/DontStarveAlone src/**/*.java
```
Run the game:
```bash
java -classpath out/production/DontStarveAlone src.main.GameLauncher

```
### Project Structure
The main components of the project are organized as follows:

```bash
src
├── game
│   ├── constants         # Game configuration and constants
│   ├── data              # Save/load functionality and data management
│   ├── entities          # Game entities like Player, World objects, etc.
│   ├── network           # Multiplayer networking components
│   ├── ui                # User interface components
│   └── world             # World generation, terrain, and tile management
└── main
    ├── GamePanel.java    # Main game panel, handles game logic and rendering
    └── GameLauncher.java # Entry point for launching the game
```
## 🎮 How to Play
1. **Launch the Game**: Start the game from the main menu. You can choose to play in Single Player or Multiplayer mode.

2. **Explore the World**: Navigate through randomly generated biomes, each with unique resources and dangers.

3. **Gather Resources**: Collect essential items like wood, stone, and food to survive and craft tools.

4. **Craft and Build**: Use gathered resources to craft tools, shelter, and items that improve your chances of survival.

5. **Survive**: Manage your hunger and health while avoiding or fighting off wildlife. Surviving through the night brings additional challenges.

6. **Multiplayer**: Host a game to allow friends to join, or discover available servers to team up and share the survival experience.

## 🌐 Multiplayer Features
### Hosting a Game
1. From the **Multiplayer Menu**, select **Host Game**.
2. Enter a player name and start a session. Other players can discover your game and 
join via LAN.
### Joining a Game
1. From the **Multiplayer Menu**, select **Join Game**.
2. Browse available servers or manually enter an IP address if connecting over LAN.
3. Enter a player name and join the session.
## 🛠 Configuration
### Game Settings
Adjust game parameters, such as:

- **Resolution and Display Mode**: Customize windowed or fullscreen options.
- **Controls**: Key bindings for movement, interaction, and inventory management.
- **Audio Levels**: Volume controls for background music and sound effects.
These settings can be accessed through the **Options Menu** from the main menu.

## 📂 Saving and Loading
Your world data is saved automatically, allowing you to pick up right where you left off. Save files are stored in the `saves` directory within the project folder. To load a saved game:

1. Go to **Single Player Menu** > **Load Game**.
2. Select a saved world to resume your progress.
## 📚 Documentation
For more detailed information on each class, its responsibilities, and its methods, please refer to our Documentation. Here you'll find in-depth descriptions of each module and guidelines for expanding or modifying the code.

## 🌟 Contributing
Contributions are welcome! To contribute:

1. Fork the project.
2. Create a new branch 
```bash 
git checkout -b feature/YourFeature
```
3. Commit your changes 
```bash
git commit -m 'Add Your Feature'
```
4. Push to the branch 
```bash
git push origin feature/YourFeature
```
5. Create a pull request for review.
## ⚠️ Troubleshooting
### Common Issues
- **Gray Screen on Startup**: Ensure all dependencies are properly loaded and the project is compiled correctly.
- **Multiplayer Connection Issues**: Verify network settings and ensure that necessary ports are open.
- **Player Movement Issues**: Check key bindings and verify that the `GamePanel` is receiving focus for input.
For other issues, please check the documentation or open a GitHub issue.

## 📄 License
This project is licensed under the MIT License - see the LICENSE file for details.

---
Don't Starve Alone is an adventure where survival hinges on your resourcefulness and bravery. Dive in, adapt, and see how long you can survive in this unforgiving world!

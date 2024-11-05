# 🌌 Our Last Light

**Our Last Light** is a survival adventure game built in Java for a school project at Epitech Nancy. Set in a procedurally generated world, players must explore, gather resources, build shelters, and survive in a hostile environment filled with dynamic challenges. The game leverages an **Entity-Component-System (ECS)** architecture to efficiently manage game entities and systems, making the game structure modular and easy to expand.

## 📜 Table of Contents

- [Project Overview](#project-overview)
- [Features](#features)
- [Getting Started](#getting-started)
- [Gameplay](#gameplay)
- [ECS Architecture](#ecs-architecture)
  - [Entity](#entity)
  - [Component](#component)
  - [System](#system)
  - [World](#world)
- [Procedural Generation](#procedural-generation)
- [Contributing](#contributing)
- [License](#license)

## 🌍 Project Overview

**Our Last Light** is designed to immerse players in a mysterious, procedurally generated world where survival depends on skill, resourcefulness, and adaptability. Built as part of a Java programming project, this game emphasizes modular design through the **Entity-Component-System (ECS)** architecture, allowing for scalable gameplay and clean separation of data and logic.

## ✨ Features

- **Procedurally Generated World**: Dynamic biomes, resources, and landmarks appear randomly, offering a unique experience each time.
- **Survival Mechanics**: Manage hunger, thirst, health, and stamina while fending off environmental hazards.
- **Modular Character Abilities**: Different classes, each with unique abilities and perks.
- **Day/Night Cycle**: Distinct gameplay experiences during the day and night, affecting visibility, resource availability, and danger levels.
- **Camp Building**: Build and upgrade your camp to increase safety and expand survival options.

## 🚀 Getting Started

### Prerequisites

- **Java 8** or higher
- A Java IDE or compiler (e.g., IntelliJ IDEA, Eclipse, or command-line `javac`)

### Installation

1. Clone the repository:
   ```bash
   git clone [https://github.com/LittleMoonie/our-last-light.git](https://github.com/LittleMoonie/Our-Last-Light.git)
   cd our-last-light
```
Compile the Java files:

```bash
javac -d bin src/*.java
```
Run the game:

```bash
java -cp bin GameLauncher
```

## 🎲 Gameplay
In Our Last Light, players navigate a procedurally generated world filled with dangers and mysteries. Survive by managing your resources, crafting tools, and building a safe camp. Each class has unique strengths and weaknesses, so strategy and planning are crucial to survival.

## 🧩 ECS Architecture
Our Last Light is structured using an Entity-Component-System (ECS) design pattern, which breaks down game elements into modular components, making the game easier to expand and maintain.

### Entity
The Entity class represents any object in the game world, such as characters, items, and environmental elements. Entities contain only components (data), not behavior.

### Component
Components are pure data holders that define the attributes of entities. Examples include:

- PositionComponent: Stores an entity's position in the world.
- HealthComponent: Tracks health points.
- VelocityComponent: Contains movement speed and direction.

### System
Systems contain the logic of the game and operate on entities with specific components. Examples of systems include:

- MovementSystem: Updates positions based on velocity.
- HealthSystem: Manages health status and conditions.
- ProceduralGenerationSystem: Handles the generation of biomes, resources, and terrain.

### World
The World class acts as the main orchestrator, managing entities and systems. It updates each system in the game loop and handles entity lifecycle events.

```java
public class World {
    private List<Entity> entities;
    private List<System> systems;

    public void update(float deltaTime) {
        for (System system : systems) {
            system.update(deltaTime);
        }
    }
}
```

## 🗺️ Procedural Generation
The **Procedural Generation System** is responsible for creating the game world. It generates various biomes, places resources, and populates the environment with enemies and items, ensuring a unique layout each time the game is played. The system allows for adjustable parameters, making it easy to tweak the difficulty and density of resources.

## 🤝 Contributing
Contributions are welcome! If you'd like to contribute to the development of Our Last Light, please follow these steps:

🔗 Links
Project Repository: GitHub Repository
Documentation: Coming soon
Our Last Light is a collaborative project by students at Epitech Nancy as a demonstration of advanced programming techniques and game development principles. We hope you enjoy exploring the mysteries of our world and surviving its dangers!

Note: This is a school project. Please feel free to reach out to the developers for questions, feedback, or collaboration opportunities!

---

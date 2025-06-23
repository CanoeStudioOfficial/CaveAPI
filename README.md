# CaveAPI - Minecraft 1.12.2 Cave Generation API Mod

Ports cave generation structures from Minecraft 1.17+ to version 1.12.2, while providing mod developers with an easy-to-use cave generation API.

---

## Project Overview

CaveAPI is a mod designed for Minecraft 1.12.2 that brings the diverse cave generation structures introduced in modern versions back to this classic edition. Through this mod, players can experience richer and more natural cave ecosystems, while mod developers gain access to a powerful API for customizing and extending cave generation logic.

---

## Key Features

- **Cave Structure Porting**: Fully ports large caves, underground canyons, cave vegetation, and other structures from 1.17+ to 1.12.2
- **Biome Adaptation**: Automatically generates depth-adapted underground biomes (e.g., underground jungles, underground deserts)
- **Cave Type Diversity**: Supports multiple cave types including rounded caves, canyons, crevices, etc.
- **Optimized Resource Distribution**: Improves ore and resource generation logic for more natural distribution patterns
- **High Configurability**: Provides detailed configuration files for customizing cave generation parameters
- **Developer-Friendly API**:
    - Event listener interfaces for cave generation
    - Custom cave type registration support
    - Decoration placement interfaces
    - Underground biome variant definition support

---

## Installation Guide

### For Players:
1. Ensure [Java 8](https://www.oracle.com/java/technologies/javase/javase-jdk8-downloads.html) or newer is installed
2. Install [Forge 1.12.2](https://files.minecraftforge.net/net/minecraftforge/forge/index_1.12.2.html) (Recommended: v14.23.5.2847+)
3. Download [CaveAPI mod file](https://github.com/CanoeStudioOfficial/CaveAPI/releases)
4. Place the downloaded `.jar` in your Minecraft `mods` folder
5. Launch the game and enjoy enhanced cave systems

### For Developers:
1. Clone repository: `git clone https://github.com/CanoeStudioOfficial/CaveAPI.git`
2. Import into IDE (IntelliJ IDEA recommended)
3. Ensure [Minecraft Forge MDK](https://mcforge.readthedocs.io/en/latest/gettingstarted/) is installed
4. Run `gradlew setupDecompWorkspace` to initialize environment
5. Build project with `gradlew build`

---



![CurseForge](http://cf.way2muchnoise.eu/980149.svg)
![Modrinth](https://img.shields.io/modrinth/dt/tetrachord-lib?logo=modrinth&style=flat&color=242629&labelColor=5ca424&logoColor=1c1c1c)

# Introduction

Numerous Minecraft mods have integrated functional features within both 2D and 3D environments[1][2][3], yet brute-force methods frequently prove inefficient. In this regard, we introduce Tetrachord Lib, a lightweight library modification designed to optimize diverse data structures.

# Related Works

## Server-side Optimization Mods

jellysquid3_[4] introduced Lithium, a groundbreaking optimization mod that enhances various facets of Minecraft servers. Employing precise algorithms to minimize chunk and block access, this mod enhances not only world generation but also AI for mobs, collision detection, discovery of points of interests (poi), and other crucial aspects to boost computational efficiency and decrease milliseconds per tick (mspt) of servers.

Spottedstar[5] introduced Starlight, an innovative modification that redefines the light engine of Minecraft to improve performance in chunk generation, block placement and destruction. In contrast to Phosphor[6], Starlight does not alter the vanilla light engine system - instead, it revolutionizes it by introducing a new algorithm.

## Data Structures

The KD Tree[7] is a data structure utilized in high-dimensional spaces for the storage and organization of k-dimensional data points. Functioning as a binary tree, each node within the KD Tree represents a k-dimensional data point and is partitioned according to the feature values (dimensions) of the data point. KD trees are particularly well-suited for conducting range queries and nearest neighbor searches within high-dimensional datasets, and they can also contribute to optimizing data retrieval within the Minecraft world.

The Segment Tree[8] is a data structure utilized for handling range updates and queries. It divides a segment into smaller subsections and organizes them hierarchically in a tree structure. Segment trees are efficient for updating and querying data ranges. In the context of a Minecraft world represented as a 3D tensor, employing a segment tree can greatly improve performance when frequent range updates and queries are involved.

# Experiments

## Evaluation

Spark[9] is a lightweight performance profiler mod, which shows detailed time costs of mods, memory and CPU usage, milliseconds per tick (mspt) and other metrics of performance of clients and servers. We utilized Spark and chose time costs and mspt as metrics to evaluate the capability of optimization of Tetrachord Lib.

## Benchmarks

We implemented the aforementioned data structures and conducted experiments using the following benchmarks:

### No Hostiles around Campfire

In a specific scenario, when players light campfires, the surrounding areas within a radius of several blocks will no longer spawn monsters. The original mod[1] offers a brute-force method that involves multiple block accesses, leading to a significant increase in mspt on the server and slowing down the game program. We chose to utilize KD Tree to implement this requirement. We utilized Spark profiler to record 1 minute of normal game progress under different parameter setting, and the experimental results are shown in the following figure:

![Effectiveness of Optimization on "No Hostiles Around Campfire" Benchmark](https://media.forgecdn.net/attachments/815/341/figure_1.png)

### Ore Blocks near Beacon Increase Xp Drop

Considering the scenario where players mine blocks close to a beacon, they can receive an additional XP reward based on the number of ore blocks surrounding the mined block. The table below illustrates the response area and "ore score" associated with each type of ore block:

Type of Ore | Copper | Iron | Gold | Diamond | Netherite
----|----|----|----|----|----
Response Area | 5x5 | 9x9 | 13x13 | 17x17 | 19x19
Ore Score | 1 | 1 | 1 | 2 | 3

We utilized Spark profiler to record 32 ore block placing and 32 block mining under different parameter setting, and the experimental results are shown in the following figure:

![Effectiveness of Optimization on "Ore Blocks Near Beacon Increase Xp Drop" Benchmark](https://media.forgecdn.net/attachments/815/342/figure_2.png)

# Conclusion

Experiments show that Tetrachord lib plays an emphatic role in different situation, and provides a highly efficient way to implement some requirements on 2D and 3D space updates and queries.

Tetrachord Lib mod is currently working as a library, so developers can use it as a dependency to improve their mod performance.

# References

[1] Serilum. [No Hostiles Around Campfire](https://legacy.curseforge.com/minecraft/mc-mods/no-hostiles-around-campfire). *CurseForge* 2019.

[2] Serilum. [Healing Campfire](https://legacy.curseforge.com/minecraft/mc-mods/healing-campfire). *CurseForge* 2019.

[3] Mikul, chubbymomo, skinnymomo. [Cold Sweat](https://legacy.curseforge.com/minecraft/mc-mods/cold-sweat). *CurseForge* 2021.

[4] jellysquid3_. [Lithium](https://legacy.curseforge.com/minecraft/mc-mods/lithium-forge). *CurseForge* 2020.

[5] Spottedstar. [Starlight](https://legacy.curseforge.com/minecraft/mc-mods/starlight-forge). *CurseForge* 2021.

[6] jellysquid3_. [Phosphor](https://legacy.curseforge.com/minecraft/mc-mods/phosphor-forge). *CurseForge* 2019.

[7] Jon Louis Bentley. Multidimensional binary search trees used for associative searching. *Commun. ACM 18, 9 (Sept. 1975)*, 509–517.

[8] Jon Louis Bentley. Solutions to Klee's rectangle problems, *Unpublished Manuscript*, 1977.

[9] Iucko. [Spark](https://legacy.curseforge.com/minecraft/mc-mods/spark). *CurseForge* 2020.

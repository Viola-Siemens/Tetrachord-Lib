# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 项目概述

Tetrachord Lib 是一个 NeoForge Minecraft 库模组，为 2D/3D 空间查询与更新提供高性能数据结构：KD Tree（最近/最远点查询）、Segment Tree（区间编辑与查询）、Fenwick Tree（前缀和/单点更新）。作为库被其他模组依赖使用，不包含独立的游戏内功能喵~

## 构建命令

使用 Gradle Wrapper，JDK 25：

```bash
# 编译项目
./gradlew build

# 运行所有测试
./gradlew test

# 运行单个测试类
./gradlew test --tests "com.hexagram2021.tetrachordlib.MainTest"

# 运行单个测试方法
./gradlew test --tests "com.hexagram2021.tetrachordlib.MainTest.testKDTree"

# 生成数据资源（datagen）
./gradlew runClientData

# 启动 Minecraft 客户端（用于手动调试）
./gradlew runClient

# 启动 Minecraft 服务端
./gradlew runServer

# 启动游戏测试服务端
./gradlew runGameTestServer
```

## 核心架构

### 包结构

```
com.hexagram2021.tetrachordlib
├── TetrachordLib.java              # @Mod 入口，定义 MODID = "tetrachordlib"
├── core
│   ├── algorithm/Algorithm.java    # 工具类：QuickSelect、lowbit/highbit、随机数
│   └── container/                  # 数据结构接口层
│       ├── IEditRule.java          # 线段树/树状数组的代数规则（combine/edit/update/subtract）
│       ├── IMultidimensional.java  # k 维坐标抽象（距离计算、算术运算）
│       ├── IVisitFunction.java     # 遍历回调函数接口
│       ├── KDTree.java             # KD Tree 接口 + 最近/最远点 DFS 搜索默认实现
│       ├── SegmentTree1D.java      # 一维线段树接口
│       ├── SegmentTree2D.java      # 二维线段树接口
│       ├── SegmentTree3D.java      # 三维线段树接口（无默认实现，需自行开发）
│       ├── FenwickTree1D.java      # 一维树状数组接口
│       ├── FenwickTree2D.java      # 二维树状数组接口
│       ├── VisitConsumer2D.java    # 二维遍历回调
│       └── impl/                   # 具体实现
│           ├── LinkedKDTree.java   # KD Tree 链表实现（含 α 平衡自重建）
│           ├── ArraySegmentTree1D.java
│           ├── ArrayQuadSegmentTree2D.java
│           ├── ArraySegmentTreeOfSegmentTrees2D.java
│           ├── ArrayFenwickTree1D.java
│           ├── ArrayFenwickTree2D.java
│           ├── EditRules.java      # IEditRule 工厂：Integer/Double/Boolean 的 SumAdd/MaxAdd/MinAdd/SumSet/MaxSet/MinSet
│           ├── IntPosition.java    # IMultidimensional<Integer> 实现
│           └── DoublePosition.java # IMultidimensional<Double> 实现
├── vanilla
│   └── MDUtils.java                # Minecraft 坐标 → IMultidimensional 转换工具
└── benchmark/                      # 性能基准测试（目前仅含 package-info.java）
```

### 关键设计模式

**接口-实现分离**：所有数据结构（KDTree、SegmentTree、FenwickTree）均以接口定义 API，通过接口上的 `static` 工厂方法创建实例。例如：
```java
KDTree<Integer, Double> kdt = KDTree.newLinkedKDTree(2);
SegmentTree1D<Integer> st = SegmentTree1D.newArraySegmentTree1D(length, EditRules.Integer.sumAdd(), Integer[]::new);
```

**IEditRule 代数系统**：`IEditRule<T>` 将线段树/树状数组的更新逻辑抽象为可插拔的代数规则，包含 `elementDefault`、`combine`、`edit`、`update`、`subtract` 等方法。`EditRules` 类提供常见规则的工厂方法：
- `SumAdd` / `MaxAdd` / `MinAdd`：加法增量 + 对应聚合
- `SumSet` / `MaxSet` / `MinSet`：覆盖设置 + 对应聚合
- 支持 `Integer`、`Double`、`Boolean`（计数）三种数据类型
- `zero()` 返回 `null` 表示"无操作"语义（用于 Set 系列），返回 `0` 表示"零增量"（用于 Add 系列）

**IMultidimensional 多维坐标**：KD Tree 通过 `IMultidimensional<T>` 抽象 k 维坐标，核心方法包括：
- `distanceWith(md)` — 欧几里得距离
- `lowerboundDistanceWith(max, min)` / `upperboundDistanceWith(max, min)` — 到超矩形的距离下界/上界（用于 KD Tree 剪枝搜索）
- `IntPosition` 和 `DoublePosition` 是两种内置实现

**KD Tree 的 α 平衡**：`LinkedKDTree` 实现为带 α 参数的自平衡 KD Tree（scapegoat tree 变体），`setAlpha(double)` 控制平衡阈值：查询频繁时取较小值（0.55~0.75），插入/删除频繁时取较大值（0.65~0.9）。`sepDim` 记录根节点的分割维度，用于 `build` → `clear` → 重建时维持一致性。

**线段树迭代构造**：2D 线段树提供两种实现——`ArrayQuadSegmentTree2D`（四叉分治）和 `ArraySegmentTreeOfSegmentTrees2D`（嵌套线段树）。

### 测试

测试文件 `src/test/java/.../MainTest.java`，使用 JUnit 4，包含：

| 测试方法 | 内容 |
|----------|------|
| `testSegmentTree` / `testSegmentTree1` | 二维线段树随机操作对比暴力算法 + 洛谷 P4514 数据集 |
| `testKDTree` / `testKDTree1` / `testKDTree2` | KD Tree 随机增删查 vs 暴力 + 洛谷 P6247 数据集 |
| `testKDTreeMaintainability` | KD Tree 子树大小维护正确性 |
| `testKDTreeTime` | KD Tree vs 暴力算法性能对比 |
| `testFenwickTree1D` / `testFenwickTree1D1` | 树状数组随机测试 + 洛谷 P3374 数据集 |
| `testFenwickTree2D` / `testFenwickTree2D1` | 二维树状数组随机测试 + LOJ 133 数据集 |

测试数据集位于 `src/test/resources/`（`.in` / `.out` 文件）。`main()` 方法可独立运行所有测试（设置 `debugOnly = true` 避免 JUnit 断言）。

### 版本与环境

- **Minecraft**：26.1.2（对应 NeoForge 1.21.x 系列）
- **NeoForge**：26.1.2.71
- **JDK**：25（`java.toolchain.languageVersion = JavaLanguageVersion.of(25)`）
- **Gradle**：使用 `net.neoforged.gradle.userdev` 7.1.36 插件
- **模组 ID**：`tetrachordlib`，Maven 坐标 `com.hexagram2021.tetrachordlib`
- **许可证**：LGPL v2.1
- **作者**：Liu Dongyu（刘冬煜）

### 代码规范要点

- 使用 Tab 缩进，K&R 大括号风格
- 公共接口/类须有 Javadoc
- 使用 `@Nullable`（`javax.annotation`）标注可能为空的值
- 使用 `@Contract(pure = true)` 标注纯函数
- 包名全小写下划线，类名大驼峰，方法/变量名小驼峰
- 提交信息格式：`type(scope): subject`（如 `feat(REQ-33133): add 3D segment tree`）

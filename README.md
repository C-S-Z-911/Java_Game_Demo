# Java_Game_Demo · 文字格斗游戏

一个用于 **学习 Java 面向对象语法与逻辑** 的练手项目：控制台（Console）文字回合制战斗游戏。

项目刻意使用「能跑通的最小闭环 + 预留扩展接口」的写法，重点不在于玩法多丰富，而在于把 **封装、继承、多态、抽象类、枚举、集合** 这些面向对象核心概念落到真实代码里。

> 📌 代码改进建议与待修复问题清单见 [CODE_REVIEW.md](CODE_REVIEW.md)

---

## 目录

- [项目简介](#项目简介)
- [技术栈与环境](#技术栈与环境)
- [快速开始](#快速开始)
- [项目结构](#项目结构)
- [类图与设计思路](#类图与设计思路)
- [面向对象知识点地图](#面向对象知识点地图)
- [游戏玩法](#游戏玩法)
- [已完成 / 未完成功能](#已完成--未完成功能)
- [扩展指南](#扩展指南)
- [常见问题](#常见问题)

---

## 项目简介

| 项目 | 说明 |
| --- | --- |
| 类型 | 控制台文字回合制战斗游戏（学习项目） |
| 语言 | Java |
| 构建方式 | 纯 `javac` / IDE 直接运行（无 Maven / Gradle） |
| 依赖 | 仅 JDK 标准库 |
| 目标 | 练习 Java 面向对象语法、包结构组织与业务逻辑拆分 |

游戏流程：

```
启动 → 登录/注册 → 登录成功 → 进入游戏 → 遭遇战（回合制） → 胜利/失败 → 再次遭遇战 …
```

---

## 技术栈与环境

- **JDK 8+**（代码中使用了 `switch` 表达式箭头语法 `case "1" -> ...`，实际需要 **JDK 14+**；实测环境为 JDK 25）
- **IntelliJ IDEA**（项目内含 `Game.iml` 与 `.idea/` 配置，可直接打开）
- 无第三方依赖，不需要联网拉包

---

## 快速开始

### 方式一：IntelliJ IDEA（推荐）

1. `File → Open`，选择项目根目录 `Game`
2. 确认 `Project Structure → Project SDK` 已配置为 JDK 14 及以上
3. 右键 `src/Main.java` → `Run 'Main.main()'`

### 方式二：命令行

**Windows PowerShell**

```powershell
# 1. 编译（把全部源文件输出到 out 目录）
New-Item -ItemType Directory -Force -Path out | Out-Null
javac -encoding UTF-8 -d out (Get-ChildItem -Recurse -Filter *.java src | Select-Object -ExpandProperty FullName)

# 2. 运行
java -cp out Main
```

**macOS / Linux**

```bash
mkdir -p out
javac -encoding UTF-8 -d out $(find src -name "*.java")
java -cp out Main
```

> ⚠️ 终端出现中文乱码时，Windows 下先执行 `chcp 65001` 切换到 UTF-8 代码页。

### 上手操作

```
[------------------------]
[    欢迎来到文字格斗游戏    ]
[------------------------]
请选择操作: 1登录 2注册 3退出
```

- **注册**：用户名 3~16 位、非纯数字、不可重复；密码 3~8 位、必须同时含字母和数字
- **登录**：用户名不存在 / 密码错误会有提示，**连续错误 3 次账号将被锁定**
- 登录成功后直接进入战斗循环

---

## 项目结构

```
Game/
├── Game.iml                      # IntelliJ 模块配置
├── README.md                     # 本文件：项目说明
├── CODE_REVIEW.md                # 代码审查：改进建议与问题清单
├── .idea/                        # IDE 配置
└── src/
    ├── Main.java                 # 程序入口
    └── com/csz/
        ├── enums/                # 枚举：固定取值集合
        │   ├── Status.java           # 账号状态：正常 / 死亡 / 锁定
        │   └── DemandAttribute.java  # 技能消耗的资源类型：HP / MP / 最大HP / 最大MP
        ├── model/                # 数据模型（领域对象）
        │   ├── User.java             # 用户（账号）：id、用户名、密码、状态
        │   ├── Role.java             # 【抽象类】角色：属性 + 技能表 + 战斗行为
        │   ├── Player.java           # 玩家角色，继承 Role
        │   ├── Enemy.java            # 敌人角色，继承 Role
        │   ├── Skill.java            # 【抽象类】技能：名称 + 消耗 + 抽象效果
        │   └── skillClass/           # 技能具体实现（多态）
        │       ├── CommonAttack.java # 普通攻击
        │       ├── HeavyAttack.java  # 重攻击（自损换高伤）
        │       └── GroupAttack.java  # 群体攻击
        ├── tool/                 # 工具类
        │   └── userTool.java         # ID 生成、用户名/密码校验、用户查找
        └── ui/                   # 界面（控制台 I/O）
            ├── LogIn.java            # 登录 / 注册 / 主菜单
            ├── Game.java             # 游戏主循环
            └── event/
                └── Battle.java       # 回合制战斗：回合调度 + 各类 UI
```

**分层思想**：`enums` → `model` → `tool` → `ui`，依赖方向自上而下单向流动，`model` 层不依赖 `ui` 层，保证模型可以在没有控制台的场景下复用。

---

## 类图与设计思路

### 继承体系

```
                    ┌──────────────┐
                    │   Role       │  «abstract»
                    │──────────────│
                    │ name         │
                    │ HP / maxHP   │
                    │ MP / maxMP   │
                    │ ATK / DEF    │
                    │ skillList    │
                    │──────────────│
                    │ useSkills()  │
                    │ isDeath()    │
                    └──────┬───────┘
                           │ extends
              ┌────────────┴────────────┐
              ▼                         ▼
      ┌──────────────┐          ┌──────────────┐
      │   Player     │          │   Enemy      │
      └──────────────┘          └──────────────┘

                    ┌──────────────┐
                    │   Skill      │  «abstract»
                    │──────────────│
                    │ skillName    │
                    │ demandValue  │
                    │ demandAttr   │
                    │──────────────│
                    │ ability()    │ «abstract»
                    └──────┬───────┘
                           │ extends
        ┌──────────────────┼──────────────────┐
        ▼                  ▼                  ▼
┌────────────────┐ ┌────────────────┐ ┌────────────────┐
│ CommonAttack   │ │ HeavyAttack    │ │ GroupAttack    │
└────────────────┘ └────────────────┘ └────────────────┘
```

### 设计要点

1. **抽象类 `Role`**：玩家和敌人共享全部属性与战斗行为，差异只在构造参数与少量判定上 → 用继承消除重复。
2. **抽象类 `Skill` + 抽象方法 `ability()`**：技能效果千差万别，父类只规定「接口」，具体伤害计算交给子类 → 这是**多态**的核心。
   `Role.useSkills()` 只写一次：
   ```java
   Skill skill = this.skillList.get(skillSerialNumber);
   consumptionMP(skill.getDemandValue());
   skill.ability(this, roles, target);   // 运行时决定调用哪个子类实现
   ```
   新增技能**不需要改动任何已有代码**，只需新增一个 `Skill` 子类并加入技能表。
3. **枚举替代魔法值**：`Status` / `DemandAttribute` 把散落的字符串常量收敛为类型安全的枚举。
4. **工具类与静态方法**：`userTool` 的校验逻辑与 I/O、模型都无关，抽成无状态的静态工具方法。
5. **UI 与逻辑分离**：`ui` 包只负责打印与读输入，`model` 包只负责数据与规则。

---

## 面向对象知识点地图

| 知识点 | 在本项目中的体现 | 位置 |
| --- | --- | --- |
| 封装 | 属性全部 `private`，通过 getter/setter 与 `consumptionXxx()` 方法暴露受控操作 | `Role`、`User`、`Skill` |
| 继承 | `Player` / `Enemy` extends `Role`；三个技能 extends `Skill` | `model/` |
| 多态 | 父类引用 `Role` 指向 `Player` / `Enemy` 实例；`Skill.ability()` 运行时绑定 | `Role.useSkills()`、`Battle.enemyTurn()` |
| 抽象类 | `Role`、`Skill` 不能被实例化，只提供公共实现与抽象契约 | `Role`、`Skill` |
| 抽象方法 | `Skill.ability()` 强制子类必须实现 | `Skill` |
| 方法重载 | `Role` 三个构造器；`Player` / `Enemy` 多个构造器 | `Role`、`Player`、`Enemy` |
| 构造器链 | `Player(User)` → `super(user.getUserName())`；`User(userName, password)` → `this()` | `Player`、`User` |
| 枚举 | `Status`、`DemandAttribute` 带字段和方法的枚举（枚举也是类） | `enums/` |
| 集合框架 | `ArrayList<Role>` 存角色、`ArrayList<Skill>` 存技能、`List.of()` 快速初始化 | `Battle`、`Game`、`Role` |
| 增强 for / 泛型 | `for (Skill skill : skillList)`，泛型保证类型安全 | `Role.getSkillNameList()` |
| `instanceof` | 区分「玩家」与「敌人」并输出不同样式的属性面板 | `Battle.roleAttributeUI()` |
| 异常处理 | `Integer.parseInt()` 配合 `try-catch NumberFormatException` 防非法输入崩溃 | `Battle.useSkillUI()` |
| `switch` 表达式 | 箭头语法 + `default` 分支处理菜单 | `LogIn.start()`、`Battle.playerTurnUI()` |
| `StringBuilder` | 循环拼接进度条与 ID，避免字符串常量池浪费 | `Battle.progressBarUI()`、`userTool.generateId()` |

---

## 游戏玩法

### 属性

| 属性 | 含义 |
| --- | --- |
| `HP` / `maxHP` | 当前血量 / 最大血量，`HP <= 0` 判定死亡 |
| `MP` / `maxMP` | 当前魔量 / 最大魔量，技能消耗资源 |
| `ATK` | 基础攻击力 |
| `DEF` | 基础防御力（**目前尚未参与伤害计算，为后续扩展预留**） |

### 数值

| 单位 | HP | maxHP | MP | maxMP | ATK | DEF |
| --- | --- | --- | --- | --- | --- | --- |
| 玩家 `Player` | 100 | 100 | 100 | 100 | 10 | 0 |
| 敌人 `Enemy` | 10 | 10 | 10 | 10 | 1 | 0 |

### 技能表

| 技能 | 消耗 | 效果 |
| --- | --- | --- |
| 普通攻击 `CommonAttack` | 5 MP | 对单体造成 `ATK + 10` 伤害 |
| 重攻击 `HeavyAttack` | 10 MP | 对单体造成 `ATK + 20` 伤害，自身损失 1 HP |
| 群体攻击 `GroupAttack` | 10 MP | 对场上所有角色造成 `ATK` 伤害 |

> 每个角色创建时默认自带「普通攻击」（在 `Role` 构造器中 `skillList.add(new CommonAttack())`）。
> 目前 `GroupAttack` / `HeavyAttack` 只被 `Game` 中的示例引用过，尚未真正装配到技能表上。

### 战斗流程

```
遭遇战开始
  └─ 循环
       ├─ 打印玩家属性面板
       ├─ 判断：敌人全灭 → 胜利
       ├─ 判断：玩家死亡 → 失败
       ├─ 打印所有敌人属性面板
       ├─ 玩家回合：选技能 → 选目标 → 结算
       ├─ 清理已死亡敌人
       └─ 敌人回合：每个敌人随机释放一个技能
  └─ 输出胜利/失败
```

### 资源消耗规则

- MP 不足时，`consumptionMP()` 会把 MP 归零，**溢出的差额从 HP 扣除**（相当于「燃血施法」）
- `HP` 可以降到负数，死亡由 `isDeath()` 统一判定

---

## 已完成 / 未完成功能

### ✅ 已完成

- [x] 用户注册（用户名 / 密码格式校验、用户名唯一性校验）
- [x] 用户登录（密码校验、3 次错误锁定账号、状态拦截）
- [x] 自动生成用户 ID（`csz` + 5 位随机数字）
- [x] 角色抽象体系（`Role` / `Player` / `Enemy`）
- [x] 技能抽象体系（`Skill` + 3 个具体技能，体现多态）
- [x] 回合制战斗循环、目标选择、胜负判定
- [x] 属性面板与控制台进度条 UI
- [x] HP / MP 扣除与溢出规则

### ⏳ 未完成（**有意留下的扩展接口**）

这些「空位」不是遗漏，而是刻意保留的**接缝（seam）**，方便后续按需补全：

| 预留点 | 位置 | 说明 |
| --- | --- | --- |
| 技能消耗类型未生效 | `Role.useSkills()` 只调用了 `consumptionMP()` | `DemandAttribute` 枚举已定义好 HP / MP / 最大 HP / 最大 MP 四种资源，等待接入统一分发 |
| `DEF` 未参与计算 | `Skill` 各子类的 `ability()` | 字段、getter/setter 已就绪，等待加入伤害公式 |
| 用户数据未持久化 | `LogIn.list` | 已有 `User` 完整模型，等待接入文件 / 数据库存储 |
| 账号状态未完全使用 | `Status.DEATH` | 枚举值已定义，等待「角色死亡后账号状态流转」逻辑 |
| 无成长与奖励 | `Game.gameBegins()` | 战斗后可获得经验 / 金币 / 掉落，等待扩展 |
| 无多关卡与 Boss | `Game.gameBegins()` | 目前每轮固定生成「小怪 1/2/3」 |
| `Battle.encounterBattle()` 返回值被忽略 | `Game.gameBegins()` | 已返回 `boolean victory`，等待用于分支走向（例如失败结算） |
| 无防御 / 道具 / 逃跑指令 | `Battle.playerTurnUI()` | 菜单目前只有 `[0]使用技能` 一个选项 |
| 装备与背包系统 | — | 全新模块，可复用 `Skill` 的抽象类套路 |

### 🗺️ 建议的推进顺序

1. **让技能消耗真正生效**（接管 `DemandAttribute`，改动小、收益大，能彻底理解「枚举 + 多态」）
2. **引入伤害公式**（`ATK - DEF` 之类，让 `DEF` 不再是死字段）
3. **数据持久化**（学 I/O 与集合的序列化）
4. **成长与关卡系统**（学复杂状态流转）
5. **拆分 `Battle` 类**（目前约 180 行且职责混杂，是练习「重构」的绝佳素材）

---

## 扩展指南

### 如何新增一个技能？

只需 3 步，**不用修改任何已有类**：

```java
// 1. 新建 src/com/csz/model/skillClass/HealSkill.java
package com.csz.model.skillClass;

import com.csz.enums.DemandAttribute;
import com.csz.model.Role;
import com.csz.model.Skill;

import java.util.ArrayList;

public class HealSkill extends Skill {

    public HealSkill() {
        super("治疗术", 8, DemandAttribute.MP);
    }

    @Override
    public void ability(Role master, ArrayList<Role> roles, int target) {
        master.setHP(master.getHP() + 30);
    }
}
```

```java
// 2. 装配给角色（例如玩家）
player.addSkillList(new HealSkill());

// 3. 完成 —— 战斗 UI 会自动列出新技能，无需改动 Battle 类
```

### 如何新增一个敌人类型？

```java
public class Boss extends Role {
    public Boss() {
        super("魔王", 300, 300, 200, 200, 35, 15);
        addSkillList(new HeavyAttack());
        addSkillList(new GroupAttack());
    }
}
```

### 如何新增一个账号状态？

在 `Status` 枚举中追加一个常量即可，`isLogin()` 决定该状态下能否登录：

```java
public enum Status {
    NORMAL("正常", true), DEATH("死亡", false), LOCKED("锁定", false),
    BANNED("封禁", false);   // 新增
    // ...
}
```

---

## 常见问题

**Q：为什么 `Game.gameBegins()` 里每轮都新建敌人列表？**
A：这是当前简化实现的临时做法 —— 每场战斗都重新生成一批相同的「小怪」。后续应在该方法内维护关卡进度与敌人配置。

**Q：为什么注册的账号重启程序后就没了？**
A：`LogIn.list` 是内存中的 `ArrayList`，进程结束即丢失。持久化是预留的扩展点之一。

**Q：为什么敌人也会被自己的「群体攻击」打到？**
A：这是当前的一个逻辑缺陷（`GroupAttack` 会波及列表内所有角色，包括施法者阵营），已在 [CODE_REVIEW.md](CODE_REVIEW.md) 中记录并给出修复方案。

**Q：`DEF` 字段完全没用到，是不是多余的？**
A：不是冗余，而是刻意预留。防御力是回合制游戏的必备属性，先定义好字段与访问器，伤害公式留待后续接入。

---

## 许可

个人学习项目，可自由参考与修改。

# 代码审查与改进建议

项目：`Java_Game_Demo`（文字格斗游戏 · Java 学习项目）
审查方式：全量阅读源码 + `javac` 编译验证（编译通过，问题均为**运行期逻辑缺陷**与**规范问题**）

> 本文档面向「学习用途」，因此每个问题都给出 **为什么错 → 怎么改 → 改完能学到什么**。
> 优先级：**逻辑 > 语法 > 命名**（与你的要求一致）。

---

## 0. 总览

| 编号 | 问题 | 类别 | 严重度 | 位置 |
| --- | --- | --- | --- | --- |
| [B1](#b1-技能消耗的资源类型完全没生效) | 技能消耗的资源类型完全没生效 | 逻辑 | 🔴 高 | `Role.useSkills()` |
| [B2](#b2-越界判断-off-by-one3-处) | 越界判断 Off-by-One（3 处） | 逻辑 | 🔴 高 | `Battle` |
| [B3](#b3-群体攻击把敌人自己的队友也打了) | 群体攻击打到施法者自己阵营 | 逻辑 | 🔴 高 | `GroupAttack` |
| [B4](#b4-成员死亡后仍会继续出手) | 成员死亡后仍会继续出手 | 逻辑 | 🔴 高 | `Battle.enemyTurn()` |
| [B5](#b5-hp--mp-显示顺序反了) | HP / MP 显示顺序反了 | 逻辑 | 🔴 高 | `Battle.roleAttributeUI()` |
| [B6](#b6-setmp-与-consumptionmp-符号规则相反) | `setMP()` 与 `consumptionMP()` 符号规则相反 | 逻辑 | 🟠 中 | `Role` |
| [B7](#b7-进度条可能崩溃或画歪) | 进度条可能崩溃 / 画歪 | 逻辑 | 🟠 中 | `Battle.progressBarUI()` |
| [B8](#b8-施法者可能被自己的技能反噬致死却仍判定成功) | 施法者可能被自己的技能反噬致死 | 逻辑 | 🟠 中 | `HeavyAttack` |
| [B9](#b9-已死亡目标仍可被选为攻击对象) | 已死亡目标仍可被选为攻击对象 | 逻辑 | 🟠 中 | `Role.useSkills()` |
| [S1](#s1-未使用的标签-outer) | 未使用的标签 `outer:` | 语法 | 🟡 低 | `LogIn.start()` |
| [S2](#s2-循环结构与-break-的语义不清晰) | 循环结构与 `break` 语义不清晰 | 语法 | 🟡 低 | `LogIn.register()` |
| [S3](#s3-变量未初始化且依赖-break-保证赋值) | 变量未初始化且依赖 `break` 保证赋值 | 语法 | 🟡 低 | `Battle.encounterBattle()` |
| [S4](#s4-重复读取-scanner-导致两次输入被误判) | 重复读取 Scanner 导致两次输入被误判 | 语法 | 🟡 低 | `LogIn.register()` |
| [S5](#s5-泛型与导入的冗余写法) | 泛型与导入冗余、格式瑕疵 | 语法 | 🟡 低 | 多处 |
| [N1](#n1-类名违反大驼峰userTool) | 类名违反大驼峰 `userTool` | 命名 | 🟠 中 | `tool/userTool.java` |
| [N2](#n2-包名不合规范skillclass--event) | 包名不合规范 `skillClass` / `event` | 命名 | 🟠 中 | `model/`、`ui/` |
| [N3](#n3-缩略语字段名全大写-atkdefhpmp) | 缩略语字段名全大写 `ATK`/`DEF`/`HP` | 命名 | 🟠 中 | `Role`、`Skill` |
| [N4](#n4-方法名语义不准或动词错误) | 方法名语义不准或动词错误 | 命名 | 🟠 中 | 多处 |
| [N5](#n5-类名与职责不符login--battle) | 类名与职责不符 `LogIn` / `Battle` | 命名 | 🟠 中 | `ui/` |
| [N6](#n6-魔法数字未提取为常量) | 魔法数字未提取为常量 | 命名 | 🟡 低 | 多处 |
| [N7](#n7-构造器参数命名前后不一致) | 构造器参数命名前后不一致 | 命名 | 🟡 低 | `Role(String, int, ...)` |
| [N8](#n8-javadoc-内容与实际不符) | Javadoc 内容与实际不符 | 命名/文档 | 🟡 低 | 多处 |

---

## 1. 逻辑问题（最高优先级）

### B1. 技能消耗的资源类型完全没生效

**位置**：`src/com/csz/model/Role.java:106-111`

```java
public void useSkills(int skillSerialNumber, ArrayList<Role> roles, int target) {
    Skill skill = this.skillList.get(skillSerialNumber);
    consumptionMP(skill.getDemandValue());   // ⚠️ 永远只扣 MP
    skill.ability(this, roles, target);
    System.out.println("[" + this.name + " - 使用技能: " + skill.getSkillName() + "]");
}
```

**为什么是问题**

`DemandAttribute` 枚举精心定义了四种消耗资源：

```java
HP("血量", false), MP("魔量", false), MAXHP("最大血量", true), MAXMP("最大魔量", true);
private final boolean isMax;   // ⚠️ 定义了却从没有人调用 isMax()
```

但 `useSkills()` 硬编码调用 `consumptionMP()`，导致：

1. `DemandAttribute` 这个枚举 **完全沦为装饰品**，`getDemandAttribute()`、`isMax()` 是死代码；
2. 一个配了 `DemandAttribute.HP` 的技能（比如「燃烧生命」），扣的却是 MP —— **静默的错误**，不会报错但行为完全不对；
3. `MAXHP` / `MAXMP` 两种消耗永远不会被触发。

**这不是「未完成」，而是「接口已就绪但接线未接」** —— 属于最该优先补上的逻辑闭环。

**怎么改**

```java
public void useSkills(int skillSerialNumber, ArrayList<Role> roles, int target) {
    if (skillSerialNumber < 0 || skillSerialNumber >= skillList.size()) {
        throw new IllegalArgumentException("技能序号非法: " + skillSerialNumber);
    }
    Skill skill = skillList.get(skillSerialNumber);
    payCost(skill);                              // ① 抽出消耗逻辑
    skill.ability(this, roles, target);
    System.out.println("[" + name + " - 使用技能: " + skill.getSkillName() + "]");
}

/** 统一资源扣除入口：新增资源类型时只改这里 */
private void payCost(Skill skill) {
    int demand = skill.getDemandValue();
    switch (skill.getDemandAttribute()) {
        case HP    -> consumptionHP(demand);
        case MP    -> consumptionMP(demand);
        case MAXHP -> consumptionMaxHP(demand);
        case MAXMP -> consumptionMaxMP(demand);
    }
}
```

**学到什么**：枚举 + `switch` 表达式做**策略分发**，是把「散落的 if-else」收敛成单一职责方法的经典手法。`DemandAttribute.isMax` 这个布尔字段其实就变成了冗余 —— 更好的设计是让枚举直接持有行为（枚举抽象方法）：

```java
public enum DemandAttribute {
    HP("血量") {
        @Override public void deduct(Role role, int value) { role.consumptionHP(value); }
    },
    MP("魔量") {
        @Override public void deduct(Role role, int value) { role.consumptionMP(value); }
    };
    // ...
    public abstract void deduct(Role role, int value);
}
```

> 这是「用多态消灭 switch」的进阶练习，推荐在完成基础版之后再尝试。

**附带问题**：`consumptionMP()` 扣 MP 不足时会把差额从 HP 扣（`Role.java:165-173`），这个「燃血施法」规则**没有任何注释说明设计意图**，建议补 Javadoc，否则后面自己都会怀疑是不是 bug。

---

### B2. 越界判断 Off-by-One（3 处）

**位置 1**：`src/com/csz/ui/event/Battle.java:109`

```java
int skillSerialNumber = Integer.parseInt(scanner.next());
if (skillSerialNumber >= 0 && skillSerialNumber <= skillList.size()) {   // ⚠️ 应为 <
```

**位置 2**：`src/com/csz/ui/event/Battle.java:119`

```java
int target = Integer.parseInt(scanner.next());
if (target >= 0 && target <= roleList.size()) {                          // ⚠️ 应为 <
```

**位置 3**：`src/com/csz/model/Role.java:107`（无任何校验）

```java
Skill skill = this.skillList.get(skillSerialNumber);   // ⚠️ 越界直接抛异常
```

**为什么是问题**

集合大小是 `size`，合法下标是 `0 .. size-1`。写 `<= size` 意味着**允许了恰好等于 size 的那个非法下标**，一旦玩家输入就等于「合法的最高值」：

```
技能面板:
[0]普通攻击        ← size = 1
输入 1
→ 通过校验（1 <= 1）
→ skillList.get(1)
→ 💥 IndexOutOfBoundsException: Index 1 out of bounds for length 1
```

更糟的是**目标选择**：`roleList` 只有 3 个敌人时输入 `3` 会通过校验，然后 `roles.get(3)` 崩在技能内部（栈更深，更难定位）。

**怎么改**

```java
if (skillSerialNumber >= 0 && skillSerialNumber < skillList.size()) {
```

并且**在模型层也加一道防线**（见 B1 的 `useSkills` 版本）—— UI 层校验是为了给用户友好提示，模型层校验是为了保证「不管谁调用都不会崩」。这叫**防御式编程**，两层各司其职。

**建议**：`Battle` 里重复出现「读一个整数 + 校验范围」的模式（技能序号、目标序号、菜单选项共 3 处），抽成一个方法会立刻消除这类复制粘贴 bug：

```java
/**
 * 读取一个 [min, max] 闭区间内的整数，非法输入会重新提示
 */
private int readIntInRange(String prompt, int min, int max) {
    while (true) {
        System.out.print(prompt);
        try {
            int value = Integer.parseInt(scanner.next());
            if (value >= min && value <= max) {
                return value;
            }
            System.out.println("\n[++++请输入 " + min + "~" + max + " 之间的数字++++]");
        } catch (NumberFormatException e) {
            System.out.println("\n[++++请输入正确数据++++]");
        }
    }
}
```

原本 `useSkillUI()` 里两层嵌套 `do-while` + `try-catch`（约 35 行）可压缩到 10 行以内。

---

### B3. 群体攻击把敌人自己的队友也打了

**位置**：`src/com/csz/model/skillClass/GroupAttack.java:18-22`，配合 `Battle.java:66-72`

```java
@Override
public void ability(Role master, ArrayList<Role> roles, int target) {
    for (Role role : roles) {
        role.consumptionHP(master.getATK());      // ⚠️ 无差别攻击所有人
    }
}
```

`Battle.enemyTurn()` 的调用方式：

```java
role.useSkills(skillSerialNumber, new ArrayList<Role>(List.of(player)), 0);
```

**为什么是问题**

关键在 `roles` 这个参数在不同调用方下**含义不一致**：

| 调用方 | 传入的 `roles` | 期望的语义 |
| --- | --- | --- |
| 玩家出手 | `roleList`（**敌人列表**） | 我打的人 = 全体敌人 ✅ 恰好正确 |
| 敌人出手 | `List.of(player)`（**只有玩家**） | 我打的人 = 玩家 ✅ 单目标正确 |

所以玩家用 `GroupAttack` 时**碰巧是对的**（因为 `roles` 里只有敌人）；但一旦敌人也装备了 `GroupAttack`，由于敌人传进来的是「只有玩家」的列表，**它只会打到玩家，打不到队友** —— 反过来，如果哪天把敌人的 `roles` 改成「全体角色」，它就会**把同伴一起轰了**。

问题的本质是：**`ability()` 只拿到「一份角色列表」，无法区分敌我**。这个设计缺陷会在任何 AOE / 治疗 / 增益技能出现时集中爆发（比如「治疗术」该治疗谁？`roles` 里全是敌人）。

**怎么改（推荐：引入阵营概念）**

方案 A —— 最小改动：让 `ability` 拿到「施法者的敌人列表」，由调用方负责筛选。

```java
/**
 * @param enemies 施法者的敌对目标列表（不含自己与队友）
 */
public abstract void ability(Role master, ArrayList<Role> enemies, int target);
```

调用方：
```java
// 玩家出手：敌人全体
player.useSkills(skillSerialNumber, enemyList, target);

// 敌人出手：把玩家当成「它的敌人列表」
enemy.useSkills(skillSerialNumber, new ArrayList<>(List.of(player)), 0);
```

方案 B —— 根治（推荐后续重构）：给 `Role` 加阵营字段，让技能自己判断。

```java
public enum Camp { PLAYER, ENEMY }

// Role 中新增
private Camp camp;
public boolean isHostileTo(Role other) { return this.camp != other.camp; }

// GroupAttack 中
for (Role role : allRoles) {
    if (master.isHostileTo(role)) {
        role.consumptionHP(master.getATK());
    }
}
```

方案 B 需要把**全部参战单位放在同一个列表**里管理，是更规范的回合制战斗架构（`Battle` 只维护一个 `ArrayList<Role> allRoles`，而不是「玩家字段 + 敌人列表」双轨制）。

**注意**：顺手给 `GroupAttack` 加上 `target` 未使用的说明 —— 目前 `GroupAttack` 完全忽略 `target` 参数，这是**正常的**（群体技能不需要指定目标），但建议在 Javadoc 里注明，否则读者会以为漏写了。

---

### B4. 成员死亡后仍会继续出手

**位置**：`src/com/csz/ui/event/Battle.java:45-49, 66-72, 134-141`

```java
// 回合开头
for (Role enemy : roleList) {
    roleAttributeUI(enemy);
}
playerTurnUI();
enemyTurn();          // ⚠️ 里面遍历 roleList 全体，不检查死活
```

清理死亡单位的代码**只存在于 `useSkillUI()` 末尾**：

```java
for (int i = 0; i < roleList.size(); ) {
    if (roleList.get(i).isDeath()) {
        System.out.println("[" + roleList.get(i).getName() + ": 死亡]");
        roleList.remove(i);
    } else {
        i++;
    }
}
```

**为什么是问题**

1. `useSkillUI()` 是**玩家技能选择**流程的一部分。虽然玩家回合结束后恰好会清理，但语义上「清理死亡单位」属于**战斗回合调度**，不属于「选择技能 UI」—— 职责放错了层；
2. 如果将来新增「玩家逃跑」「使用道具」「防御」等指令，这些路径都不会清理死亡单位，**尸体就会继续行动**；
3. 更直接的漏洞：如果 `GroupAttack` 被修复成真正的 AOE，或者引入「持续伤害（中毒）」这类在**敌人回合中**造成死亡的效果，`enemyTurn()` 的 `for-each` 遍历 `roleList` 时**已死单位仍会出手**，甚至触发 `ConcurrentModificationException`（如果边遍历边 `remove`）。

**怎么改**

把清理逻辑提到回合调度层，并加上死亡检查：

```java
/** 移除已阵亡单位，返回被移除的名单 */
private List<Role> cleanUpDead() {
    List<Role> dead = new ArrayList<>();
    Iterator<Role> it = roleList.iterator();
    while (it.hasNext()) {
        Role role = it.next();
        if (role.isDeath()) {
            dead.add(role);
            it.remove();          // ✅ 用迭代器删除，安全
        }
    }
    for (Role role : dead) {
        System.out.println("[" + role.getName() + ": 死亡]");
    }
    return dead;
}

public void enemyTurn() {
    for (Role role : roleList) {
        if (role.isDeath()) {     // ✅ 死者不出手
            continue;
        }
        int skillSerialNumber = random.nextInt(role.getSkillNameList().size());
        role.useSkills(skillSerialNumber, new ArrayList<>(List.of(player)), 0);
    }
}
```

回合主循环改为：

```java
playerTurnUI();
cleanUpDead();      // ✅ 玩家回合后清理
if (roleList.isEmpty()) { victory = true; break; }
enemyTurn();
cleanUpDead();      // ✅ 敌人回合后也清理
if (player.isDeath()) { victory = false; break; }
```

**学到什么**：`for-each` 遍历时删除元素会抛 `ConcurrentModificationException`，正确姿势是 `Iterator.remove()`。当前代码用「手动维护下标 `i` 且只在非删除分支 `i++`」绕过了这个问题，属于**能跑但不易懂**的写法（很多初学者会在这里写错），用 `Iterator` 或 `removeIf` 更清晰：

```java
roleList.removeIf(Role::isDeath);   // 一行，但没法打印死亡日志
```

---

### B5. HP / MP 显示顺序反了

**位置**：`src/com/csz/ui/event/Battle.java:155-156`

```java
System.out.println("HP: " + role.getMaxHP() + "/" + role.getHP() + "\t" + progressBarUI(role.getMaxHP(), role.getHP()));
System.out.println("MP: " + role.getMaxMP() + "/" + role.getMP() + "\t" + progressBarUI(role.getMaxMP(), role.getMP()));
```

**为什么是问题**

游戏界面的通用约定是 **`当前值 / 最大值`**（例如 `HP: 30/100` 表示还剩 30 点血）。现在打印的是 `最大值 / 当前值`，一个满血敌人显示成：

```
HP: 10/10   ← 看似正常（因为满血时两者相等）
```

一旦受伤就变成：

```
HP: 10/3    ← 误导：读起来像「10 点里还是 10 点」或「上限只有 3」
```

而 `progressBarUI(int max, int min)` 的**参数命名也把语义带偏了**：形参叫 `max` / `min`（大小关系），而实际传入的是 `maxHP`（上限）与 `HP`（当前值）。**命名误导了调用方**，两个参数还很容易传反 —— 事实上这里就传反了：进度条应该反映**当前值占上限的比例**。

**怎么改**

```java
// 形参改名，语义自解释
public static String progressBarUI(int current, int max) {
    ...
}

// 调用处：当前值在前
System.out.printf("HP: %d/%d\t%s%n", role.getHP(), role.getMaxHP(),
                  progressBarUI(role.getHP(), role.getMaxHP()));
System.out.printf("MP: %d/%d\t%s%n", role.getMP(), role.getMaxMP(),
                  progressBarUI(role.getMP(), role.getMaxMP()));
```

**学到什么**：**形参名就是契约**。`progressBarUI(int max, int min)` 这种命名会让调用方（包括未来的自己）产生「参数是个区间」的误解 —— 改成 `(int current, int max)` 后，参数顺序错误会在阅读时立刻暴露。

---

### B6. `setMP()` 与 `consumptionMP()` 符号规则相反

**位置**：`src/com/csz/model/Role.java:154-173`

```java
public void setMP(int MP) {
    if (MP >= 0) {
        this.MP = MP;
    } else {
        this.HP -= MP;        // ⚠️ MP 为负 → HP 反而「增加」
    }
}

public void consumptionMP(int demand) {
    int remainingMP = this.MP - demand;
    if (remainingMP >= 0) {
        this.MP -= demand;
    } else {
        this.MP = 0;
        this.HP += remainingMP;   // ✅ 差值为负 → HP 减少
    }
}
```

**为什么是问题**

同样是「MP 不够，用血补」，两个方法用了**相反的符号约定**：

- `consumptionMP(5)`，MP = 3 → 差值 `-2` → `HP += (-2)` → HP 减 2 ✅
- `setMP(-2)` → `HP -= (-2)` → HP **加 2** ❌

也就是说 `setMP(-2)` 会让角色**回血**。这是典型的**符号约定不统一**缺陷，且 `setMP` 的负数分支语义极其隐晦（一个 setter 偷偷改了另一个字段，还带副作用）。

**怎么改**

setter 应该只做「赋值 + 合法性保护」，不承担跨字段业务逻辑：

```java
public void setMP(int mp) {
    this.MP = Math.max(0, Math.min(mp, maxMP));   // 只做区间钳制
}
```

「燃血施法」是**战斗规则**，应该只保留在 `consumptionMP()` 一处：

```java
/**
 * 消耗魔量。MP 不足时差额从 HP 扣除（燃血施法），MP 归零。
 */
public void consumptionMP(int demand) {
    int remaining = this.MP - demand;
    if (remaining >= 0) {
        this.MP = remaining;
    } else {
        this.MP = 0;
        consumptionHP(-remaining);   // ✅ 复用已有方法，语义统一：正数表示扣除
    }
}
```

同样的不一致也出现在血量上：

```java
public void consumptionHP(int demand) { this.HP -= demand; }        // 正数=扣血 ✅
public void setHP(int HP) { this.HP = HP; }                          // 直接赋值，可传负数 ⚠️
```

建议统一为：**`consumptionXxx(正数)` 表示扣除，`setXxx()` 只做钳制**。

---

### B7. 进度条可能崩溃或画歪

**位置**：`src/com/csz/ui/event/Battle.java:164-183`

```java
public static String progressBarUI(int max, int min) {
    int result = min * 10 / max;        // ⚠️ max 为 0 时 ArithmeticException
    StringBuilder stringBuilder = new StringBuilder("[");
    for (int i = 0; i < result; i++) {
        if (i < 10) {
            stringBuilder.append("■");
        } else {
            stringBuilder.append("◊");   // ⚠️ 死代码：循环条件保证 i < result，此处才判 i < 10
        }
    }
    if (min * 10 % max > 0) {            // ⚠️ max 为 0 时再次除零
        stringBuilder.append("▪");
        result++;
    }
    for (int i = 0; i < 10 - result; i++) {   // ⚠️ result > 10 时循环不执行，条会超过 10 格
        stringBuilder.append("□");
    }
    ...
}
```

**为什么是问题**

1. **除零风险**：`max == 0` 时直接抛 `ArithmeticException`。而 `Role.consumptionMaxHP()` 会把 `maxHP` 一直减到 0 甚至负数：

```java
public void consumptionMaxHP(int demand) {
    int remainingMaxMP = this.maxMP - demand;
    if (remainingMaxMP >= 0) {
        this.maxMP -= demand;     // ⚠️ 可以减到 0
    }
}
```

只要接入 `DemandAttribute.MAXHP` 消耗（B1 修复后立刻会发生），进度条就会崩。

2. **`result > 10` 时条会画歪**：如果 `HP > maxHP`（例如治疗超过上限、或 maxHP 被扣减后 HP 没同步），`result` 可能大于 10，此时 `10 - result` 为负 → 填充循环不执行 → 进度条长度超过 10 格。

3. **`i < 10` 是死代码**：`else` 分支的 `◊` 永远不会被追加。作者本意可能是「超过 10 格用另一种符号」，但由于 `result ≤ 10` 的隐含假设，这个分支没有意义。

4. **细节**：`min * 10` 在极端数值下可能**整数溢出**（`min` 很大时）。

**怎么改**

```java
/**
 * 生成 10 格宽的进度条
 *
 * @param current 当前值
 * @param max     上限，为 0 或负数时返回空条
 */
public static String progressBarUI(int current, int max) {
    final int CELLS = 10;
    if (max <= 0) {
        return "[" + "□".repeat(CELLS) + "]";
    }
    // 用 long 运算避免溢出，并把占比钳制到 [0, 10]
    int filled = (int) Math.max(0, Math.min(CELLS, (long) current * CELLS / max));
    return "[" + "■".repeat(filled) + "□".repeat(CELLS - filled) + "]";
}
```

**学到什么**：`String.repeat(n)`（JDK 11+）让循环拼接变得极简；`Math.clamp` 式的钳制是防御式编程的标准手段。同时注意**每个除法都要先问「分母会不会是 0」**。

---

### B8. 施法者可能被自己的技能反噬致死却仍判定成功

**位置**：`src/com/csz/model/skillClass/HeavyAttack.java:18-21`

```java
@Override
public void ability(Role master, ArrayList<Role> roles, int target) {
    roles.get(target).consumptionHP(master.getATK() + 20);
    master.consumptionHP(1);      // ⚠️ 可能把施法者自己打死
}
```

**为什么是问题**

`HeavyAttack` 是「自损 1 点换高伤」，但**没有检查自损后施法者是否还活着**。若玩家只剩 1 点血时使用重攻击，结果是：玩家死亡 + 敌人掉血，但技能被判定为「成功释放」，战斗循环要到**下一轮开头**才会发现玩家已死。

更微妙的是：`Role.consumptionHP()` 允许 HP 变成负数（`this.HP -= demand`），而 `progressBarUI` 拿到负的 `current` 会怎样？按修复前的写法 `result` 为负 → 填充循环不执行 → 显示空条（勉强可用）；但如果有别的地方用 `HP` 做除法就会出问题。

**怎么改**

```java
@Override
public void ability(Role master, ArrayList<Role> roles, int target) {
    Role victim = roles.get(target);
    victim.consumptionHP(master.getATK() + 20);
    master.consumptionHP(1);
    if (master.isDeath()) {
        System.out.println("[" + master.getName() + " 因反噬力竭倒下]");   // ✅ 立刻反馈
    }
}
```

更好的做法是把「能否释放」前置为**消耗校验**（这也正是 `DemandAttribute.HP` 该派上用场的场景）—— 让重攻击的消耗声明为 `DemandAttribute.HP, 1`，这样「血不够」会在 B1 的 `payCost()` 阶段就被统一拦截，技能根本不会释放出去。

顺带修一下 `consumptionHP`，让 HP 不越界为负：

```java
public void consumptionHP(int demand) {
    this.HP = Math.max(0, this.HP - demand);
}
```

**学到什么**：一个「改动多个单位状态」的方法，要考虑**所有被改动方**的合法性，而不是只关心主目标。

---

### B9. 已死亡目标仍可被选为攻击对象

**位置**：`src/com/csz/ui/event/Battle.java:112-124`（无死亡校验）

**为什么是问题**

目标选择界面列出的是 `roleList` 全体现有成员。在 B4 修复前，死亡单位虽然会被清理，但清理发生在**玩家回合之后**；也就是说，如果一次攻击造成多个目标死亡（例如修复后的 AOE），玩家在**同一次选目标时**仍可能选中一个已经空血的对象，造成「鞭尸」并浪费一次出手。

同时 `Role.useSkills()` 与 `Skill.ability()` 都没有「施法者是否已死」「目标是否已死」的前置校验，把合法性完全托付给了 UI 层。

**怎么改**

在目标选择 UI 中过滤，并在模型层兜底：

```java
// 模型层
public void useSkills(int skillSerialNumber, ArrayList<Role> roles, int target) {
    if (isDeath()) {
        System.out.println("[" + name + " 已阵亡，无法行动]");
        return;
    }
    Role victim = roles.get(target);
    if (victim.isDeath()) {
        System.out.println("[" + victim.getName() + " 已阵亡，请重新选择目标]");
        return;
    }
    // ...
}
```

**学到什么**：**校验要放在最靠近数据的地方**（模型层），UI 层的校验只是「体验优化」。这是分层架构中很关键的一条原则。

---

## 2. 语法与代码结构问题

### S1. 未使用的标签 `outer:`

**位置**：`src/com/csz/ui/LogIn.java:19-20`

```java
outer:                       // ⚠️ 定义了却从未被 break/continue 引用
do {
    loginMenu();
    switch (scanner.next()) {
```

**为什么是问题**

`outer:` 是**带标签的循环**，用于从嵌套循环中跳出。但 `start()` 里没有任何 `break outer;` / `continue outer;`，且退出路径用的是 `return`（`LogIn.java:27`）。这是纯遗留代码 —— 推测最初写成 `while(true)` + `break outer`，改成 `do-while` + `return` 后忘了删标签。

**怎么改**：直接删除 `outer:`。

```java
public void start() {
    while (true) {
        loginMenu();
        switch (scanner.next()) {
            case "1" -> login(list);
            case "2" -> register(list);
            case "3" -> {
                exitMenu();
                return;          // ✅ 语义清晰：退出即离开方法
            }
            default -> System.out.println("\n[++++请输入正确选项++++]");
        }
    }
}
```

> 补充：既然 `return` 已经表达了「退出」，用 `do-while(true)` 就没有意义了 —— `do-while` 的价值在于「至少执行一次且条件可判定」，而这里条件恒为 `true`。改成 `while (true)` 更贴合意图。

**顺带**：`login(list)` / `register(list)` 把成员变量 `private final ArrayList<User> list` 又当参数传了一遍（`LogIn.java:13, 23-24`）—— **成员变量和方法参数同名重影**，是常见的困惑源。既然 `list` 是实例字段，方法内部直接访问即可，删掉这两个参数更干净。

---

### S2. 循环结构与 `break` 的语义不清晰

**位置**：`src/com/csz/ui/LogIn.java:102-119`

```java
do {
    do {
        System.out.print("请输入密码: ");
        password = scanner.next();
        if (userTool.verifyPassword(password)) {
            break;                    // 跳出内层
        } else {
            System.out.println("密码不符合规范");
            System.out.println("长度在3~8位, 数字加字母\n");
        }
    } while (true);
    System.out.print("请再次输入密码: ");
    if (password.equals(scanner.next())) {
        break;                        // 跳出外层
    } else {
        System.out.println("再次输入密码错误\n");
    }
} while (true);                       // ⚠️ 嵌套 do-while(true) + 裸 break，控制流难追踪
```

**为什么是问题**

三层嵌套的 `do-while(true)` 配合裸 `break`，读者必须**逐层数大括号**才能确定每个 `break` 跳到哪。这是可读性问题，也是新手最容易写错的结构（加一层循环就全乱）。

另外逻辑上有一点小瑕疵：**重新输入密码时不会再次校验格式**（因为走的是直接比较），虽然此处 `password` 已通过校验所以没问题，但一旦有人重构这段代码就容易踩坑。

**怎么改**：用「抽取方法 + `return`」替代嵌套跳出 —— **一个方法只做一件事**。

```java
private void register() {
    System.out.println("\n[====注册操作中====]");
    String userName = readValidUserName();
    String password = readValidPassword();
    list.add(new User(userName, password));
    System.out.println("\n[====注册成功====]");
}

/** 读取一个合法且未被占用的用户名 */
private String readValidUserName() {
    while (true) {
        System.out.print("请输入用户名: ");
        String userName = scanner.next();
        if (userTool.verifyUserName(userName) && userTool.usernameExists(list, userName) == null) {
            return userName;
        }
        System.out.println("用户名不符合规范");
        System.out.println("必须唯一且长度在3~16位, 非纯数字\n");
    }
}

/** 读取两次一致且合法的密码 */
private String readValidPassword() {
    while (true) {
        String password = readOnceValidPassword();
        System.out.print("请再次输入密码: ");
        if (password.equals(scanner.next())) {
            return password;
        }
        System.out.println("再次输入密码错误\n");
    }
}

private String readOnceValidPassword() {
    while (true) {
        System.out.print("请输入密码: ");
        String password = scanner.next();
        if (userTool.verifyPassword(password)) {
            return password;
        }
        System.out.println("密码不符合规范");
        System.out.println("长度在3~8位, 数字加字母\n");
    }
}
```

**学到什么**：**用「方法边界」代替「循环跳出」**。`return` 的语义永远比 `break` 明确，而且顺带完成了「注册流程」与「输入校验」的职责分离 —— 这是最实用的重构技巧之一。

---

### S3. 变量未初始化且依赖 `break` 保证赋值

**位置**：`src/com/csz/ui/event/Battle.java:32-64`

```java
public boolean encounterBattle() {
    System.out.println("\n[====遭 遇 战====]");
    boolean victory;                  // ⚠️ 未初始化
    do {
        // ...
        if (roleList.size() == 0) { victory = true;  break; }
        if (player.isDeath())     { victory = false; break; }
        // ...
    } while (true);
    if (victory) { ... }              // 编译器能通过，只因「所有出口都赋了值」
```

**为什么是问题**

`victory` 未初始化，编译器之所以放行，是因为**它证明了 `do-while(true)` 的所有出口都先赋值**。这是「能编译但脆弱」的写法：只要将来有人加一条 `break` 而忘记赋值，立刻变成 `variable victory might not have been initialized` 编译错误（还算幸运），或者更糟 —— 逻辑上走了不该走的分支。

另外这段代码的**控制流是用 `break` 模拟 `return`**：

```java
if (roleList.size() == 0) { victory = true; break; }
```

既然 `encounterBattle()` 本来就返回 `boolean`，直接 `return true;` 更直接。用 `break` 只是为了让方法尾部能统一打印「胜利/失败」文案，但这段文案完全可以抽成方法。

**怎么改**

```java
public boolean encounterBattle() {
    System.out.println("\n[====遭 遇 战====]");

    boolean victory = false;                     // ✅ 给一个安全的默认值
    while (true) {
        showAllRoles();
        if (roleList.isEmpty()) {                // ✅ 用 isEmpty() 表达「空」
            victory = true;
            break;
        }
        if (player.isDeath()) {
            break;                               // 默认 false，无需重复赋值
        }
        playerTurnUI();
        cleanUpDead();
        if (roleList.isEmpty()) {
            victory = true;
            break;
        }
        enemyTurn();
        cleanUpDead();
    }

    printResult(victory);
    return victory;
}

private void printResult(boolean victory) {
    System.out.println(victory ? "[====胜 利====]" : "[====失 败====]");
    System.out.println("[输入任意键继续: ]");
    scanner.next();
}
```

**学到什么**：
- 变量声明时**即赋初值**，是最便宜的防御手段；
- `list.size() == 0` 优先写成 `list.isEmpty()`（语义更明确，某些实现还是 O(1)）；
- 一段代码如果用 `break` + 标志变量绕路，通常说明**这个方法承担了两件事**（战斗循环 + 结果展示），拆开即可。

---

### S4. 重复读取 Scanner 导致两次输入被误判

**位置**：`src/com/csz/ui/LogIn.java:113-114`

```java
System.out.print("请再次输入密码: ");
if (password.equals(scanner.next())) {
```

**为什么是问题**

这里 `scanner.next()` 的参数被直接当实参传进 `equals()`，没有中间变量 —— 本身合法，但在**整体 I/O 设计**上有个更值得注意的隐患：

项目里同时存在**两个 `Scanner(System.in)` 实例**：

```java
// LogIn.java:12
private final Scanner scanner = new Scanner(System.in);
// Battle.java:15
private final Scanner scanner = new Scanner(System.in);
```

`Scanner` 是**带缓冲**的：它会一次性从 `System.in` 读取一大块数据到内部缓冲区。两个 Scanner 同时包装同一个 `System.in` 时，A 读走的字节 B 拿不到，可能出现「输入被吞掉 / 卡住不响应 / 需要多按一次回车」等现象。当前流程恰好是**串行**的（登录结束后才进入战斗，不会交错读），所以侥幸没暴露，但这属于**随时会炸的地雷**。

**怎么改（推荐）**：全局共享一个 `Scanner`。

```java
// 新建 src/com/csz/tool/ConsoleTool.java
public final class ConsoleTool {
    private static final Scanner SCANNER = new Scanner(System.in);

    private ConsoleTool() { }        // 工具类禁止实例化

    public static Scanner scanner() {
        return SCANNER;
    }
}
```

`LogIn` 与 `Battle` 都改用 `ConsoleTool.scanner()`，问题从根上消除。

**同时建议**：`next()` 与 `nextLine()` **不要混用**（`next()` 不消费行尾换行符，会让后续 `nextLine()` 立刻返回空串）。当前代码统一用 `next()`，暂时安全；但一旦想改用 `nextLine()` 读整行（例如支持带空格的用户名），必须配合 `scanner.nextLine()` 吃掉残留换行。

---

### S5. 泛型与导入的冗余写法

**(1) 显式类型参数可省略（菱形运算符）**

```java
// Role.java:95
ArrayList<String> skillNameList = new ArrayList<String>();   // JDK 7+ 可写 new ArrayList<>()
```

**(2) 未使用的 import**

```java
// Enemy.java:3
import java.util.ArrayList;      // ⚠️ Enemy.java 里根本没用到 ArrayList

// Game.java:6
import com.csz.model.User;       // ⚠️ 未使用
// Game.java:7-8
import com.csz.model.skillClass.GroupAttack;   // ⚠️ 未使用
import com.csz.model.skillClass.HeavyAttack;   // ⚠️ 未使用
// Game.java:5
import com.csz.model.Role;       // ⚠️ 仅通过 ArrayList<Role> 间接使用，视风格可留
```

建议在 IDEA 中执行 `Ctrl+Alt+O`（Optimize Imports），并开启未使用导入的实时提示。

**(3) 声明为接口类型而非实现类型**

```java
// 现状：字段与参数都写死了具体实现 ArrayList
private ArrayList<Skill> skillList = new ArrayList<>();
public ArrayList<String> getSkillNameList() { ... }
public void setSkillList(ArrayList<Skill> skillList) { ... }

// 推荐：对外暴露接口，内部才用实现
private List<Skill> skillList = new ArrayList<>();
public List<String> getSkillNameList() { ... }
public void setSkillList(List<Skill> skillList) { ... }
```

**为什么**：接口作为类型，将来想换成 `LinkedList` / `CopyOnWriteArrayList` 时**不需要改动任何调用方**。这是「面向接口编程」最基础的一条实践。

> 注：`Battle` 里已经用了 `List.of(...)`（`Battle.java:70`、`Game.java:24`），说明作者有接口意识，只是字段声明没统一。

**(4) 格式瑕疵**

```java
// Enemy.java:7 —— Javadoc 结尾多余的空格和星号缩进不齐
/**
 * 敌人类
* */          // ⚠️ 应为 " */"
```

```java
// skillClass/*.java —— 类注释结尾风格不统一
/**
 * 普通攻击技能
 * */       // ⚠️ 项目里两种写法混用：" */"
```

建议统一为 ` */`（一个空格 + 两个字符），并让 IDEA 的格式化（`Ctrl+Alt+L`）自动处理。

**(5) 导入顺序**

`LogIn.java:3-6` 的导入顺序为 `model` → `enums` → `tool`，而同项目的其他类顺序不一。建议统一按 **① JDK 标准库 ② 第三方库 ③ 本项目包** 分组，组内按字母序 —— 这是 Java 社区通行约定，也是大多数公司的 Checkstyle 规则内容。

---

## 3. 命名习惯问题

> Java 命名规范（Oracle Code Conventions / Google Java Style）速查：
> **类名 `UpperCamelCase`｜方法名与变量名 `lowerCamelCase`｜常量 `UPPER_SNAKE_CASE`｜包名全小写｜缩略语也用小写驼峰（`userId` 而非 `userID`）**

### N1. 类名违反大驼峰 `userTool`

**位置**：`src/com/csz/tool/userTool.java:8`

```java
public class userTool {          // ❌ 类名必须首字母大写
```

**怎么改**

| 现在 | 建议 | 理由 |
| --- | --- | --- |
| `userTool` | `UserTool` | 类名大驼峰；且它是工具类，可进一步命名为 `UserValidator`（职责是校验）+ `IdGenerator` |
| 包名 `tool` | `util` | Java 生态惯例是 `util`（如 `java.util`），`tool` 不是错误但不通用 |

**注意**：IDEA 的「Rename」（`Shift+F6`）会**自动同步**所有引用与文件名，不需要手动搜索替换。

**更深一层**：`userTool` 目前混装了三种职责：

1. **ID 生成** —— `generateId()`
2. **格式校验** —— `verifyUserName()` / `verifyPassword()` / `nonPureNumber()` / `letterPlusNumbers()`
3. **集合查询** —— `usernameExists()`

理想拆分：
- `IdGenerator.generateUserId()`
- `UserValidator.verifyUserName()` / `verifyPassword()`
- 「查找用户」其实属于**仓储（Repository）职责**，更适合放到一个 `UserRepository` 里（未来持久化后，这里就是读写文件/数据库的位置）

**含义**：`userTool` 这类「万能工具箱」是新手项目的通病。工具类本身没问题，但**一个工具类只服务一个领域**才可持续。

---

### N2. 包名不合规范 `skillClass` / `event`

**位置**：`src/com/csz/model/skillClass/`、`src/com/csz/ui/event/`

```java
package com.csz.model.skillClass;   // ❌ 包名不应含大写字母
package com.csz.ui.event;           // ⚠️ 合法但不达意
```

**为什么是问题**

包名约定**全小写**（避免在不区分大小写的文件系统上冲突，如 Windows）。`skillClass` 里的 `Class` 更是冗余 —— 包里的东西本来就是类。

**怎么改**

| 现在 | 建议 | 说明 |
| --- | --- | --- |
| `com.csz.model.skillClass` | `com.csz.model.skill` | 简洁、达意 |
| `com.csz.tool` | `com.csz.util` | 见 N1 |
| `com.csz.ui.event` | `com.csz.ui.battle` 或 `com.csz.event` | `event` 太笼统，且 `Battle` 是「战斗流程」不是「事件」 |

**顺带**：包结构可以更「业务化」。如果项目继续长大，推荐改成按**功能**而非按**技术层**分包：

```
com.csz.game.user      （User, UserValidator, UserRepository）
com.csz.game.battle    （Battle, BattleTurn, DamageCalculator）
com.csz.game.role      （Role, Player, Enemy）
com.csz.game.skill     （Skill, CommonAttack, ...）
com.csz.game.ui        （LogIn, GameMenu）
```

> 关于 `Skill` 该放 `model` 还是 `model.skill`：目前 `Skill` 在 `model`，子类在 `model.skillClass`，导致 `Skill` 要 `import com.csz.model.skillClass.CommonAttack`（`Role.java:3`）—— **父包 import 子包**，方向尴尬。把 `Skill` 也移入 `skill` 包即可解耦。

---

### N3. 缩略语字段名全大写 `ATK` / `DEF` / `HP` / `MP`

**位置**：`src/com/csz/model/Role.java:22-42`、`src/com/csz/model/Skill.java`

```java
private int HP;        // ❌ 若视为变量，应 lowerCamelCase
private int maxHP;
private int MP;
private int maxMP;
private int ATK;       // ❌
private int DEF;       // ❌

public int getHP()  { return HP; }        // ❌ getter 应为 getHp()
public void setATK(int ATK) { this.ATK = ATK; }
```

**为什么是问题**

全大写（`UPPER_SNAKE_CASE`）在 Java 中**专属于 `static final` 常量**。看到 `HP` 时，有经验的 Java 程序员第一反应是「这是个常量」—— 这与事实矛盾，会造成阅读误导。

规范做法是**缩略语按普通单词处理**（只保留首字母大写）：

| 现在 | 建议 |
| --- | --- |
| `HP` / `maxHP` | `hp` / `maxHp` |
| `MP` / `maxMP` | `mp` / `maxMp` |
| `ATK` | `atk` |
| `DEF` | `def` |
| `getHP()` / `setHP()` | `getHp()` / `setHp()` |
| `getMaxHP()` / `setMaxMP()` | `getMaxHp()` / `setMaxMp()` |
| `getATK()` / `getDEF()` | `getAtk()` / `getDef()` |

**反例说明**：JDK 自身也遵循此规则 —— `java.net.URL` 是**类名**（缩略语在类名中全大写可接受），但字段/方法里是 `getUrl()`、`openConnection()`。参考 `java.time` 中的 `getDayOfMonth()`。

**注意**：`Role(String name, int HP, int maxHP, ...)` 这种构造器参数全大写同样要改（见 N7）。

这是本文档中**最值得改的命名问题**，因为 `getHP()` 这类方法在项目里被调用了几十次，正是统一改名的好时机（IDEA `Shift+F6` 一键完成）。

---

### N4. 方法名语义不准或动词错误

| 位置 | 现在 | 问题 | 建议 |
| --- | --- | --- | --- |
| `Role.java:94` | `getSkillNameList()` | 返回技能**名称**列表，但方法名读起来像「获取技能列表」 | `getSkillNames()` |
| `Role.java:106` | `useSkills(...)` | 一次只用**一个**技能，却用复数 | `useSkill(...)` |
| `Role.java:116` | `addSkillList(Skill)` | 动词 `add` + 名词 `List`，读起来像「添加一个列表」；实际是「往列表里加一个技能」 | `addSkill(Skill)` / `learnSkill(Skill)` |
| `Role.java:131,146,165,188` | `consumptionHP(int)` 等 | `consumption` 是**名词**，方法名必须用**动词**；且它实际是「扣除/消耗」 | `consumeHp(int)` / `deductHp(int)` / `reduceHp(int)` / `takeDamage(int)` |
| `Role.java:216` | `isDeath()` | 语义反了：`isDeath` 读作「是死亡」，而它判断的是「已经死亡」 | `isDead()` |
| `Skill.java:58` | `ability(...)` | 名词，且含义模糊（能力？） | `cast(...)` / `applyEffect(...)` / `execute(...)` |
| `Status.java:21` | `isLogin()` | 它返回的是「该状态**允许**登录」，不是「是否已登录」—— 极易误解 | `canLogin()` / `isLoginAllowed()` |
| `Battle.java:32` | `encounterBattle()` | 「遭遇战」是名词；方法应描述**动作** | `startBattle()` / `fight()`（返回值是 `boolean`，也可以考虑 `isVictory()` 命名风格，但保留动作动词更好） |
| `Battle.java:78,97` | `playerTurnUI()` / `useSkillUI()` | 方法名带 `UI` 后缀 = 该方法**职责就是画界面**，暗示它没在干正事 | `playerTurn()` / `chooseSkill()`；界面渲染抽到独立的 `BattleView` 类 |
| `Game.java:18` | `gameBegins()` | 主谓倒装（应为 `game begins`），动词应在前 | `startGame()` / `run()` |
| `userTool.java:40` | `nonPureNumber(...)` | 以 `non` 开头的方法返回 `boolean`，读起来别扭 | `containsLetter(...)`（正向语义更好读） |
| `LogIn.java` | 类名 `LogIn` | 动词短语做类名；且它同时管登录、注册、菜单 | `LoginUI`，或拆为 `App`（主循环）+ `AuthService`（登录注册） |

**核心原则**：

1. **方法名 = 动词（+ 宾语）**：`consumeHp`、`addSkill`、`startBattle`
2. **布尔方法用 `is` / `has` / `can` / `should` 开头，且语义为「真」**：
   - ❌ `isDeath()` → 返回「死了吗」
   - ✅ `isDead()`
   - ❌ `nonPureNumber()` → 双重否定
   - ✅ `containsLetter()`
3. **避免 `...UI` 这类实现细节后缀**：方法名应描述**意图**，界面怎么画是实现细节。若一个方法既算逻辑又打印，说明该拆成两个方法（或两个类）。

---

### N5. 类名与职责不符 `LogIn` / `Battle`

这是命名问题的**深层形态**：名字不对，往往是因为**职责划分不对**。

**(1) `LogIn` 名不副实**

```java
public class LogIn {
    public void start()        // 主菜单循环（登录/注册/退出）
    private void login(...)    // 登录
    private void register(...) // 注册
    private void gameStartup() // 启动游戏  ⚠️ 越界了
    private void loginMenu()   // 打印菜单
}
```

一个叫「登录」的类，管着注册、主菜单循环、甚至创建 `Game` 和 `Player` 并启动游戏。**启动游戏根本不是登录该管的事**。

**建议拆分**：

```java
public class Application {        // 主循环（原 LogIn.start）
    public void run() { ... }
}

public class AuthService {        // 登录/注册的规则（可脱离控制台复用）
    public User login(String name, String password);
    public User register(String name, String password);
}

public class LoginUI {            // 只负责登录/注册的输入输出（原 LogIn 的 UI 部分）
}
```

**收益**：`AuthService` 不依赖 `Scanner`，将来换成图形界面或加单元测试时可以直接复用 —— 这就是「UI 与逻辑分离」的实际价值。

**(2) `Battle` 既打仗又画界面**

`Battle`（184 行）目前同时承担：

| 职责 | 方法 |
| --- | --- |
| 回合调度 | `encounterBattle()` |
| 敌方 AI | `enemyTurn()` |
| 玩家指令输入 | `playerTurnUI()` / `useSkillUI()` |
| 战果结算与清理 | `useSkillUI()` 末尾的死亡清理 |
| **界面渲染** | `roleAttributeUI()` / `progressBarUI()` |
| **控制台 I/O** | 持有 `Scanner` 字段 |

**建议**：把「画界面」的部分抽成 `BattleView`（纯静态方法 + 只接收数据）：

```java
public class BattleView {
    public static void printRole(Role role);
    public static void printSkillMenu(List<String> skillNames);
    public static void printTargetMenu(List<Role> targets);
    public static String progressBar(int current, int max);
}
```

`Battle` 只保留「算什么 / 打谁 / 什么时候结束」的逻辑。这样 `progressBar()` 这类纯函数还能**单独写单元测试**（当前它是个 `public static`，位置很合适，只是被埋在 UI 职责里了）。

**学到什么**：判断一个类是否职责过多的实用技巧 —— **试着用一句话描述它，如果句子里出现「并且」，就该拆了**。

---

### N6. 魔法数字未提取为常量

| 位置 | 魔法值 | 含义 | 建议常量 |
| --- | --- | --- | --- |
| `userTool.java:15` | `"csz"` | ID 前缀 | `private static final String ID_PREFIX = "csz";` |
| `userTool.java:17` | `5` | ID 数字位数 | `private static final int ID_DIGIT_COUNT = 5;` |
| `userTool.java:29` | `3` / `16` | 用户名长度范围 | `USERNAME_MIN_LENGTH = 3` / `USERNAME_MAX_LENGTH = 16` |
| `userTool.java:69` | `3` / `8` | 密码长度范围 | `PASSWORD_MIN_LENGTH = 3` / `PASSWORD_MAX_LENGTH = 8` |
| `LogIn.java:46` | `3` | 最大登录尝试次数 | `MAX_LOGIN_ATTEMPTS = 3` |
| `LogIn.java:98,110` | 提示文案里的 `3~16` / `3~8` | 与校验逻辑**重复硬编码** | 用字符串拼接引用常量，避免改一处忘一处 |
| `Role.java:45-52` | `50` / `5` | NPC 默认属性 | 见下方「数据与逻辑分离」 |
| `Role.java:55-63` | `100` / `10` | 玩家默认属性 | 同上 |
| `Enemy.java:10` | `10, 10, 10, 10, 1, 0` | 小怪属性 | 同上 |
| `CommonAttack.java:15,20` | `5` / `10` | MP 消耗 / 伤害加成 | `MP_COST` / `BONUS_DAMAGE` |
| `HeavyAttack.java:15,19,20` | `10` / `20` / `1` | 同上 + 自损值 | `MP_COST` / `BONUS_DAMAGE` / `SELF_DAMAGE` |
| `Battle.java:165,174,178` | `10` | 进度条格数 | `PROGRESS_BAR_CELLS = 10` |

**为什么重要**

以用户名长度为例，**同一个 `3~16` 规则在代码里出现了两处**（`userTool.java:29` 的判定 + `LogIn.java:98` 的提示文案）。某天要改成 `4~12`，改了判定忘了改文案，玩家就会看到「提示说 3~16，实际 4~12 才通过」的经典 bug。

```java
// 常量定义 + 提示文案统一引用
public static final int USERNAME_MIN_LENGTH = 3;
public static final int USERNAME_MAX_LENGTH = 16;

// LogIn.java 中
System.out.println("必须唯一且长度在" + UserTool.USERNAME_MIN_LENGTH
                 + "~" + UserTool.USERNAME_MAX_LENGTH + "位, 非纯数字\n");
```

**数据与逻辑分离（进阶建议）**

`Role` 的三个构造器把「默认数值」硬编码在**逻辑代码**里：

```java
public Role() {
    this.name = "NPC";
    this.HP = 50;  this.maxHP = 50;
    this.MP = 50;  this.maxMP = 50;
    this.ATK = 5;  this.DEF = 0;
}
```

`Player` / `Enemy` 的默认值又散落在这两个子类里。更好的做法是把角色数值当成**配置数据**：

```java
// 简单版：常量集中管理
public final class RoleConfig {
    public static final int NPC_HP = 50, NPC_ATK = 5;
    public static final int PLAYER_HP = 100, PLAYER_ATK = 10;
    public static final int ENEMY_HP = 10, ENEMY_ATK = 1;
    private RoleConfig() { }
}
```

```java
// 进阶版：枚举 + 工厂，把「角色模板」数据化
public enum RoleTemplate {
    PLAYER("玩家", 100, 100, 100, 100, 10, 0),
    SMALL_MONSTER("小怪", 10, 10, 10, 10, 1, 0),
    BOSS("魔王", 300, 300, 200, 200, 35, 15);
    // ...
}
```

这样新增敌人类型只需**加一个枚举常量**，不用新建类、不用改构造器 —— 数值平衡也能在一处集中调整。

---

### N7. 构造器参数命名前后不一致

**位置**：`src/com/csz/model/Role.java:66`、`Enemy.java:13`、`Player.java:12`

```java
public Role(String name, int HP, int maxHP, int MP, int maxMP, int ATK, int DEF) {
```

问题有三：

1. **大驼峰/全大写当参数名**（`HP`、`ATK`、`DEF`）—— 违反 `lowerCamelCase`（同 N3）
2. **风格混用**：`HP`（全大写）与 `maxHP`（小驼峰）出现在**同一个参数列表**里 —— 明显是写到一半改了风格留下的一致性缺口
3. **`Enemy` 的两个构造器参数含义易混**：

```java
public Enemy(String name) {
    super(name, 10, 10, 10, 10, 1, 0);      // ⚠️ 6 个裸数字，读者无法知道哪个是哪个
}

public Enemy(String name, int HP, int maxHP, int MP, int maxMP, int ATK, int DEF) {
    super(name, HP, maxHP, MP, maxMP, ATK, DEF);
}
```

`super(name, 10, 10, 10, 10, 1, 0)` 这种写法**完全依赖位置对应**，一旦父类构造器参数顺序调整（比如把 `maxHP` 和 `HP` 换个位置），这里会**静默地传错数**——编译器不会报错，因为类型全是 `int`。

**怎么改**

```java
public Role(String name, int hp, int maxHp, int mp, int maxMp, int atk, int def) {
```

**更好的做法**：当参数超过 4~5 个时，考虑**构造器重载 + 链式调用**，或者引入一个配置对象：

```java
// 用枚举模板（同 N6 进阶版）
public Enemy(String name) {
    super(name, RoleTemplate.SMALL_MONSTER);
}
```

或者至少加**命名清晰的静态工厂方法**，让调用点自解释：

```java
public static Enemy smallMonster(String name) {
    return new Enemy(name, 10, 10, 10, 10, 1, 0);
}

public static Enemy boss(String name) {
    return new Enemy(name, 300, 300, 200, 200, 35, 15);
}

// 调用点
new Enemy = Enemy.smallMonster("小怪1");   // ✅ 一眼看懂
```

**学到什么**：**「相同类型的长参数列表」是 bug 温床**。防御手段就是「减少参数个数」或「让调用点自解释」。这也是《Effective Java》里「遇到多个构造器参数时要考虑用构建器」的建议背景。

---

### N8. Javadoc 内容与实际不符

| 位置 | 现在 | 问题 | 建议 |
| --- | --- | --- | --- |
| `Role.java:89-93` | `@return ArrayList<String>` | 写的不是类型而是泛型字符串；且用了全限定感 | `@return 技能名称列表` |
| `Role.java:131` | `/** 消耗最大血量 */` 贴在 `consumptionHP()` 上 | **注释与方法完全错位** —— 描述「消耗最大血量」的方法实际在 146 行 | 把注释移到 `consumptionMaxHP()` |
| `Role.java:143-146` | `/** 消耗血量 */` 贴在 `consumptionMaxHP()` 上 | 同上，两个方法的注释**互相交换了** | 对调 |
| `userTool.java:23-27` | `@return 返回 布尔` | 缺失 `@param`；`@return` 描述太笼统 | `@param userName 待校验的用户名` / `@return true 表示长度合法且非纯数字` |
| `userTool.java:35-39,49-53,63-67` | 全部缺失 `@param` | 参数未文档化 | 补齐 |
| `Skill.java:55-57` | `/** 能力效果 */` | 没有说明 `master` / `roles` / `target` 三个参数的含义 —— **这是本项目最需要文档的方法**（B3 的歧义正源于此） | 明确写出「roles 指施法者的敌方目标列表」 |
| `DemandAttribute.java` | 无类注释 | 四种资源类型的含义与用法无说明 | 补充，并说明 `isMax` 的含义 |
| `Battle.java:66` | `public void enemyTurn()` 无 Javadoc | 与相邻方法风格不一致 | 补齐 |

**重点提醒**：`Role.java:131` 和 `146` 的注释**互换**了 —— 这类错误比没有注释更危险，因为它会主动误导读者。修复：

```java
/**
 * 消耗当前血量
 *
 * @param demand 扣除量，传入正数
 */
public void consumptionHP(int demand) { ... }

/**
 * 消耗最大血量上限
 *
 * @param demand 扣除量，传入正数
 */
public void consumptionMaxHP(int demand) { ... }
```

**方法参数文档的重要性**：B3（群体攻击误伤）根本原因就是 `Skill.ability(Role master, ArrayList<Role> roles, int target)` 这**三个参数没有任何说明**。补上 Javadoc 后：

```java
/**
 * 释放技能的效果。
 *
 * @param master 施法者本人
 * @param targets 施法者的敌对目标列表（**不含施法者与其友方**）
 * @param target  单目标技能的选中下标；群体技能应忽略此参数
 */
public abstract void ability(Role master, ArrayList<Role> targets, int target);
```

写这段话的时候，你会自然发现「`roles` 里到底装着谁」这个问题必须先想清楚 —— **好的文档促使好的设计**。

---

## 4. 类设计层面的建议（重构方向）

前面都是「点」上的问题，这一节谈「面」上的架构。按**建议实施的顺序**排列。

### 4.1 伤害计算应当抽成独立方法

目前伤害公式散落在三个技能类里，且**完全没有考虑 `DEF`**：

```java
roles.get(target).consumptionHP(master.getATK() + 10);   // CommonAttack
roles.get(target).consumptionHP(master.getATK() + 20);   // HeavyAttack
role.consumptionHP(master.getATK());                     // GroupAttack
```

**建议**：

```java
// 新建 src/com/csz/game/DamageCalculator.java
public final class DamageCalculator {
    private static final double DEF_REDUCTION = 0.5;

    private DamageCalculator() { }

    /**
     * 计算实际伤害：至少造成 1 点，防御力按比例减伤
     */
    public static int physical(int atk, int bonus, int def) {
        int raw = atk + bonus - (int) (def * DEF_REDUCTION);
        return Math.max(1, raw);        // 保底 1 点，避免高防单位完全免疫
    }
}
```

这样：
- `DEF` 终于有了用处；
- 改平衡只改一处；
- 公式可以**单独写单元测试**（纯函数，无副作用）；
- 技能类退化为「声明参数」的角色，职责更纯粹。

### 4.2 引入统一的「战斗上下文」

`Battle` 目前用「`player` 字段 + `roleList` 字段」双轨制表示参战单位，这直接催生了 B3 的敌我歧义。规范做法是**一个列表管所有单位**：

```java
public class Battle {
    private final List<Role> allRoles = new ArrayList<>();   // 玩家 + 全部敌人
    private final Role player;

    public List<Role> enemiesOf(Role role) {
        return allRoles.stream()
                       .filter(r -> r.isHostileTo(role) && !r.isDead())
                       .toList();
    }
}
```

配合 `Role` 上的 `Camp` 阵营字段（见 B3 方案 B），`GroupAttack` / 治疗 / 增益技能都能**准确找到正确目标**。

### 4.3 把「资源消耗」与「技能效果」的先后顺序写明确

当前 `useSkills()` 的顺序是「先扣费，后生效」：

```java
consumptionMP(skill.getDemandValue());   // 先扣
skill.ability(this, roles, target);      // 后打
```

这带来一个**未定义行为**：如果目标已经死亡（B9），技能效果落空，但 MP **已经扣掉了**。应该改为「先校验 → 再扣费 → 后生效」：

```java
public void useSkill(int index, List<Role> targets, int target) {
    Skill skill = skillList.get(index);
    if (!canAfford(skill)) {
        System.out.println("[MP 不足，无法释放 " + skill.getSkillName() + "]");
        return;                                     // ✅ 付不起就不放技能
    }
    if (target < 0 || target >= targets.size() || targets.get(target).isDead()) {
        return;
    }
    payCost(skill);
    skill.ability(this, targets, target);
}

private boolean canAfford(Skill skill) {
    return MP >= skill.getDemandValue();            // 若允许燃血施法，这里就判 HP 是否够
}
```

**这条建议会同时修掉 B1、B2、B9 三个问题**，是性价比最高的一次重构。

### 4.4 `Battle.encounterBattle()` 的返回值应当被使用

```java
// Game.java:23-27
while (!player.isDeath()){
    ArrayList<Role> roles = new ArrayList<>(List.of(new Enemy("小怪1"), ...));
    Battle battle = new Battle(player, roles);
    battle.encounterBattle();      // ⚠️ 返回值被丢弃
}
```

方法签名声明了 `boolean`，调用方却忽略 —— 要么用起来（分支走向 / 结算奖励 / 累计战绩），要么把返回类型改成 `void`。**「声明了却不用」比「没声明」更糟**，因为它让读者以为这里有逻辑。

顺便，`Game.gameBegins()` 目前每轮**重新创建敌人列表**，意味着战斗将无限进行且毫无变化。建议加入关卡/波次概念：

```java
public void gameBegins(Player player) {
    int wave = 0;
    while (!player.isDeath()) {
        wave++;
        List<Role> enemies = BattleFactory.createWave(wave);   // 按波次生成不同敌人
        Battle battle = new Battle(player, enemies);
        if (battle.encounterBattle()) {
            System.out.println("[第 " + wave + " 波通关]");
            player.recoverAfterBattle();      // ✅ 战间恢复，为成长系统留接口
        }
    }
    System.out.println("[游戏结束，共通过 " + (wave - 1) + " 波]");
}
```

### 4.5 `Battle` 的 setter 与构造器重复

```java
public Battle(Player player, ArrayList<Role> roleList) { ... }
public void setRoleList(ArrayList<Role> roleList) { this.roleList = roleList; }   // ⚠️ 从未被调用
```

`setRoleList` 全项目无调用点，且与构造器注入**功能重叠**。建议删除（需要「换一批敌人」时，用新方法 `nextWave(List<Role>)` 语义更清晰）。**原则：不要为「将来可能用得上」预留 setter** —— 那正是版本控制存在的意义。

### 4.6 常量类与工具类应禁止实例化

```java
public class userTool {
    // ⚠️ 隐式 public 无参构造器 → 可以被 new userTool()，毫无意义
}
```

规范写法：

```java
public final class UserTool {
    private UserTool() {
        throw new AssertionError("工具类不应被实例化");
    }
    // ...
}
```

`final` 防止被继承，`private` 构造器防止被实例化 —— 这是 JDK 与主流库（如 Guava）的一致做法。

---

## 5. 修复优先级建议

### 第一优先级 —— 会崩溃或严重错误表达（建议立刻修）

| 编号 | 问题 | 预估工作量 |
| --- | --- | --- |
| B2 | Off-by-One 越界（输入 `size` 就崩） | 10 分钟 |
| B5 | HP/MP 显示顺序反了 | 5 分钟 |
| B4 | 死亡单位仍出手 + 清理逻辑位置错误 | 30 分钟 |
| B3 | 群体攻击敌我歧义 | 1 小时（含阵营设计） |

### 第二优先级 —— 逻辑闭环（让「预留接口」真正接上线）

| 编号 | 问题 |
| --- | --- |
| B1 | 让 `DemandAttribute` 生效（配 4.3 一起做，收益最大） |
| B9 | 死亡/非法目标校验 |
| B6 | `setMP` 符号统一 |
| B8 | 反噬致死反馈 |
| B7 | 进度条除零保护 |
| 4.1 | 抽出 `DamageCalculator`，让 `DEF` 生效 |

### 第三优先级 —— 命名与结构（用 IDEA 批量重命名，风险低收益高）

| 编号 | 问题 |
| --- | --- |
| N3 | `HP`/`MP`/`ATK`/`DEF` → `hp`/`mp`/`atk`/`def`（含 `getHP()` → `getHp()`） |
| N1 | `userTool` → `UserTool`；包 `tool` → `util` |
| N2 | 包 `skillClass` → `skill`；`Skill` 移入该包 |
| N4 | 方法名动词化（`consumptionHP` → `consumeHp`，`isDeath` → `isDead` 等） |
| N8 | 修正 `Role` 中互相错位的两条 Javadoc |

### 第四优先级 —— 可读性与扩展性（有时间再做）

| 编号 | 问题 |
| --- | --- |
| S1–S5 | 循环结构、标签、Scanner 共享、泛型与导入清理 |
| N6 | 魔法数字提取为常量 + 角色数值配置化 |
| N7 | 构造器参数命名与「长参数列表」的替代方案 |
| N5 / 4.5 | 拆分 `LogIn` 与 `Battle` 的职责 |
| 4.2 / 4.4 | 统一战斗上下文、使用战斗返回值、引入波次系统 |

---

## 6. 值得肯定的地方

作为学习项目，以下几点做得比多数初学者作业好：

1. **分层意识清晰** —— `enums` / `model` / `tool` / `ui` 包划分合理，依赖方向单向，没有出现「model 里 import UI」的常见错误。
2. **抽象类用在了正确的位置** —— `Role` 与 `Skill` 都是「有共享实现 + 有差异化行为」的场景，不是为了让代码看起来高级而硬套继承。`Skill` + `ability()` 的写法是**教科书式的多态范例**，新增技能零改动已有代码。
3. **枚举带字段与方法** —— `Status("正常", true)` 这种写法说明理解了「枚举也是类」，比用一堆 `static final String` 高明得多。
4. **构造器链使用正确** —— `User(userName, password)` 用 `this()` 复用无参构造器（`User.java:22`），`Player(User)` 用 `super(user.getUserName())` 委托父类，避免了重复初始化代码。
5. **基础防御意识到了** —— `Integer.parseInt()` 外面套了 `try-catch NumberFormatException`（`Battle.java:107-131`），`setMP`/`setMaxMP` 里有范围校验，说明有「不信任输入」的直觉。
6. **UI 细节有心思** —— 进度条、`■□▪` 字符、玩家用 `---` 敌人用 `+++` 的区分、统一的 `[++++提示++++]` 文案格式，说明在认真对待输出体验。
7. **有意识地预留扩展接口** —— 这正是本次审查的重点：`DemandAttribute` 的四种资源、`DEF` 字段、`Battle` 的 `victory` 返回值、`Status.DEATH`，全都是「接口已就绪、等待接线」的状态。**先定义契约、后补实现**是正确的工程习惯，本文档的多数建议本质上是「把已经画好的线接上」。

---

## 7. 一句话总结

> **问题集中在两类**：一是「**接口定义好了但没接线**」（`DemandAttribute` 未被使用、`DEF` 未参与计算、`victory` 返回值被丢弃）—— 这类属于**完成度**问题，按 B1 / 4.1 / 4.4 补上即可；
> 二是「**边界与语义不严谨**」（`<= size` 越界、HP/MP 显示顺序、符号约定相反、敌我目标歧义）—— 这类是**编程习惯**问题，是本次最需要刻意练习的部分。
>
> 建议的练习方式：**先只改 B2 / B5 两个 10 分钟内能完成的问题，跑一遍游戏亲眼看到崩溃与修复的对比**，再逐步推进到 B1 + 4.3 那次「性价比最高的重构」。命名问题（N3 / N1 / N2）留到最后用 IDEA 批量重命名一次做完 —— 拖得越久，改动成本越高。

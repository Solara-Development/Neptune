<div align="center">

<img src="https://github.com/user-attachments/assets/76ecdb11-ead6-4598-9d0c-4327503df5ec" alt="Neptune Practice Core" width="100%"/>

# 🅿️ Placeholders

**Reference for all available Neptune placeholders.**

[![Discord](https://img.shields.io/badge/Discord-Join%20Server-7289da?style=for-the-badge&logo=discord&logoColor=white)](https://discord.gg/f6rUtpy6y4)
[![BuiltByBit](https://img.shields.io/badge/BuiltByBit-Purchase-7289da?style=for-the-badge&logo=builtbybit&logoColor=white)](https://builtbybit.com/resources/neptune-practice-core.44588/)

</div>

---

> [!NOTE]
> [PlaceholderAPI](https://placeholderapi.com) is required to use `%neptune_..%` placeholders.

> [!WARNING]
> This page may not always be fully up-to-date.

---

## 🌐 Globally Available

| Plugin Placeholder | PlaceholderAPI | Description |
|---|---|---|
| `<online>` | — `*` | Online player count (use server expansion) |
| `<queued>` | `%neptune_queued%` | Number of players currently in queue |
| `<in-match>` | `%neptune_matches%` | Number of players currently in matches |
| `<player>` | — `*` | Name of the player |
| `<ping>` | `%neptune_ping%` | Player's ping in milliseconds |
| `<wins>` | `%neptune_wins%` | Total wins accumulated |
| `<losses>` | `%neptune_losses%` | Total losses accumulated |
| `<kills>` | `%neptune_kills%` | Total kills accumulated |
| `<deaths>` | `%neptune_deaths%` | Total deaths accumulated |
| `<current-win-streak>` | `%neptune_current_win_streak%` | Player's current win streak |
| `<best-win-streak>` | `%neptune_best_win_streak%` | Player's all-time best win streak |
| `<division>` | `%neptune_division%` | Player's global division name |
| — | `%neptune_kit_<kit>_<stat>%` | Kit-specific stat — replace `<stat>` with: `name`, `elo`, `division`, `rounds`, `current_win_streak`, `best_win_streak`, `wins`, `losses`, `kills`, `deaths`, `queued`, `in_match` |
| — | `%neptune_recent_match_<num>_<field>%` | Recent match details — replace `<field>` with: `opponent`, `arena`, `kit`, `date`, `time`, `unix_timestamp` *(seconds, not ms)* |
| — | `%neptune_state%` | Current state of the player |

---

## ⏳ In Queue

| Plugin Placeholder | PlaceholderAPI | Description |
|---|---|---|
| `<kit>` | `%neptune_kit_name%` | Display name of the kit being queued for |
| `<kit-division>` | `%neptune_kit_division%` | Division for the kit |
| `<max-ping>` | `%neptune_max_ping%` | Max ping allowed per player settings |
| `<time>` | `%neptune_time%` | Time spent queueing (mm:ss) |
| — | `%neptune_kit_<kit>_<stat>%` | Kit-specific stats *(see above)* |

---

## 🎒 Kit Editor

| Plugin Placeholder | PlaceholderAPI | Description |
|---|---|---|
| `<kit>` | `%neptune_kit_name%` | Display name of the kit being edited |
| — | `%neptune_kit_<kit>_<stat>%` | Kit-specific stats *(see above)* |

---

## 🎉 Party

| Plugin Placeholder | PlaceholderAPI | Description |
|---|---|---|
| `<leader>` | `%neptune_leader%` | Name of the party leader |
| `<size>` | `%neptune_size%` | Current number of party members |
| `<party-max>` | `%neptune_party-max%` | Maximum allowed party size |

---

## ⚔️ Any Match

| Plugin Placeholder | PlaceholderAPI | Description |
|---|---|---|
| `<red-bed-broken>` `**` | `%neptune_red_bed_broken%` `$` | Whether the red team's bed is broken |
| `<blue-bed-broken>` `**` | `%neptune_blue_bed_broken%` `$` | Whether the blue team's bed is broken |
| `<time>` | `%neptune_time%` | Duration the match has been active |
| `<rounds>` | `%neptune_roundss%` | Total number of rounds in the match |
| `<kit>` | `%neptune_kit%` | Display name of the kit being played |
| `<arena>` | `%neptune_arena%` | Display name of the arena |
| — | `%neptune_kit_<kit>_<stat>%` | Kit-specific stats *(see above)* |

---

## 🧍 Solo Match

| Plugin Placeholder | PlaceholderAPI | Description |
|---|---|---|
| `<opponent>` | `%neptune_opponent%` | Opponent's name |
| `<opponent-ping>` | `%neptune_opponent_ping%` | Opponent's ping in milliseconds |
| `<red-hits>` | — | Hits landed by the red player |
| `<blue-hits>` | — | Hits landed by the blue player |
| `<red-combo>` | — | Combo of the red player |
| `<blue-combo>` | — | Combo of the blue player |
| `<red-points>` | — | Points of the red player |
| `<blue-points>` | — | Points of the blue player |
| `<red-hit-difference>` | — | Hit difference for the red player |
| `<blue-hit-difference>` | — | Hit difference for the blue player |
| `<combo>` | `%neptune_combo%` | Player's current combo against opponent |
| `<opponent-combo>` | `%neptune_opponent_combo%` | Opponent's current combo against player |
| `<opponent-elo>` | `%neptune_opponent_elo%` | Opponent's global ELO |
| `<opponent-kit-elo>` | `%neptune_opponent_kit_elo%` | Opponent's kit-specific ELO |
| `<hits>` | `%neptune_hits%` | Times player has hit opponent |
| `<opponent-hits>` | `%neptune_opponent_hits%` | Times opponent has hit player |
| `<hit-difference>` | `%neptune_difference%` | Hit count difference between both players |
| `<red-name>` | `%neptune_red_name%` | Name of the red team player |
| `<blue-name>` | `%neptune_blue_name%` | Name of the blue team player |
| `<red-ping>` | `%neptune_red_ping%` | Ping of the red team player |
| `<blue-ping>` | `%neptune_blue_ping%` | Ping of the blue team player |
| `<red-elo>` | `%neptune_red_elo%` | Global ELO of the red team player |
| `<blue-elo>` | `%neptune_blue_elo%` | Global ELO of the blue team player |
| `<red-kit-elo>` | `%neptune_red_kit_elo%` | Kit ELO of the red team player |
| `<blue-kit-elo>` | `%neptune_blue_kit_elo%` | Kit ELO of the blue team player |
| `<bed-broken>` `**` | `%neptune_bed_broken%` `$` | Whether the player's bed is broken |
| `<opponent-bed-broken>` `**` | `%neptune_opponent_bed_broken%` `$` | Whether the opponent's bed is broken |
| `<points>` | `%neptune_points%` | Rounds won by the player's team |
| `<opponent-points>` | `%neptune_opponent_points%` | Rounds won by the opponent's team |

---

## 👥 Team Match

| Plugin Placeholder | PlaceholderAPI | Description |
|---|---|---|
| `<alive>` | `%neptune_alive%` | Players alive on the player's team |
| `<max>` | `%neptune_max%` | Total players on the player's team |
| `<alive-opponent>` | `%neptune_opponent_alive%` | Players alive on the opposing team |
| `<max-opponent>` | `%neptune_opponent_max%` | Total players on the opposing team |
| `<team-bed-broken>` `**` | `%neptune_bed_broken%` `$` | Whether the player's team bed is broken |
| `<opponent-team-bed-broken>` `**` | `%neptune_opponent_bed_broken%` `$` | Whether the opponent's team bed is broken |
| `<red-alive>` | `%neptune_red_alive%` | Players alive on the red team |
| `<blue-alive>` | `%neptune_blue_alive%` | Players alive on the blue team |
| `<red-total>` | `%neptune_red_max%` | Total players on the red team |
| `<blue-total>` | `%neptune_blue_max%` | Total players on the blue team |
| `<points>` | `%neptune_points%` | Rounds won by the player's team |
| `<opponent-points>` | `%neptune_opponent_points%` | Rounds won by the opponent's team |

---

## 🎊 Party Match

| Plugin Placeholder | PlaceholderAPI | Description |
|---|---|---|
| `<alive>` | `%neptune_alive%` | Players alive in the match |
| `<max>` | `%neptune_max%` | Total players that entered the match |

---

## 🏆 Leaderboards

| PlaceholderAPI | Description |
|---|---|
| `%neptune_<STAT>_<kit>_<1-10>_name%` | Player name at the given rank for the stat and kit |
| `%neptune_<STAT>_<kit>_<1-10>_value%` | Stat value of the player at the given rank |

`<STAT>` must be one of: `KILLS` · `BEST_WIN_STREAK` · `DEATHS` · `ELO` · `WINS` · `LOSSES`

**Examples:**

```
%neptune_KILLS_Axe_1_name%    →  Lrxh_   (player with most kills in Axe, rank #1)
%neptune_KILLS_Axe_1_value%   →  100     (their kill count)
```

---

## 📌 Footnotes

| Symbol | Meaning |
|---|---|
| `*` | No placeholder needed — use a standard PAPI server expansion instead |
| `**` | Only available in **BedWars** kits |
| `$` | Returns `"true"` or `"false"` as a string |
| `—` | No plugin placeholder equivalent |

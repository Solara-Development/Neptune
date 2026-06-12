<img width="2000" height="1000" alt="image(6)" src="https://github.com/user-attachments/assets/76ecdb11-ead6-4598-9d0c-4327503df5ec" />

---

# Placeholders

> [!NOTE]
> [PlaceholderAPI](https://placeholderapi.com) is required for placeholders.
>
> The expansion identifier is `neptune`. All placeholders use the format `%neptune_<placeholder>%`.

---

## Global

| Plugin        | PlaceholderAPI               | Description                            |
|---------------|------------------------------|----------------------------------------|
| \<online>     | None<sup>*</sup>             | Use server expansion for PAPI          |
| \<queued>     | %neptune_queued%             | Number of players in queue             |
| \<in-match>   | %neptune_matches%            | Number of active matches               |
| \<player>     | None<sup>*</sup>             | The name of the player                 |
| \<ping>       | %neptune_ping%             | The ping of the player in milliseconds |

## Player Stats

| Plugin                | PlaceholderAPI                   | Description                          |
|-----------------------|----------------------------------|--------------------------------------|
| \<wins>               | %neptune_wins%                   | Total wins                           |
| \<losses>             | %neptune_losses%                 | Total losses                         |
| \<kills>              | %neptune_kills%                  | Total kills                          |
| \<deaths>             | %neptune_deaths%                 | Total deaths                         |
| \<elo>                | %neptune_elo%                    | Global elo                           |
| \<kdr>                | %neptune_kdr%                    | Kill/death ratio                     |
| \<played>             | %neptune_played%                 | Total matches played (wins + losses) |
| \<win-loss-ratio>     | %neptune_win_loss_ratio%         | Win/loss ratio                       |
| \<current-win-streak> | %neptune_current_win_streak%     | Current win streak                   |
| \<best-win-streak>    | %neptune_best_win_streak%        | Best win streak                      |
| \<division>           | %neptune_division%               | Global division name                 |
| None                  | %neptune_state%                  | Current player state                 |
| None                  | %neptune_lastKit%                | Last played kit name                 |

## Player Settings

| Plugin            | PlaceholderAPI               | Description                         |
|-------------------|------------------------------|-------------------------------------|
| \<max-ping>       | %neptune_max_ping%           | Max ping setting                    |
| \<kill-effect>    | %neptune_kill_effect%        | Equipped kill effect                |
| \<kill-message>   | %neptune_kill_message%       | Equipped kill message package       |
| \<armor-trim>     | %neptune_armor_trim%         | Equipped armor trim package         |
| \<shield-pattern> | %neptune_shield_pattern%     | Equipped shield pattern package     |

## Kit Stats

Kit placeholders support two formats:
- **Context-aware** (uses the kit from the player's current state): `%neptune_kit_<type>%`
- **Specific kit**: `%neptune_kit_<kit name>_<type>%`

| Plugin                  | PlaceholderAPI                            | Description            |
|-------------------------|-------------------------------------------|------------------------|
| \<kit>                  | %neptune_kit_name%                        | Kit display name       |
| \<kit-division>         | %neptune_kit_division%                    | Kit division           |
| \<rounds>               | %neptune_kit_rounds%                      | Rounds required to win the match (1–9) |
| \<kit-current-win-streak> | %neptune_kit_current_win_streak%        | Kit current win streak |
| \<kit-best-win-streak>  | %neptune_kit_best_win_streak%             | Kit best win streak    |
| \<kit-wins>             | %neptune_kit_wins%                        | Kit wins               |
| \<kit-losses>           | %neptune_kit_losses%                      | Kit losses             |
| \<kit-kills>            | %neptune_kit_kills%                       | Kit kills              |
| \<kit-deaths>           | %neptune_kit_deaths%                      | Kit deaths             |
| \<kit-elo>              | %neptune_kit_elo%                         | Kit elo                |
| None                    | %neptune_kit_queued%                      | Players queued for kit |
| None                    | %neptune_kit_in_match%                    | Players in match for kit |

**Specific kit example:** `%neptune_kit_Boxing_wins%` — returns the player's wins in the Boxing kit.

## Queue

| Plugin  | PlaceholderAPI   | Description                         |
|---------|------------------|-------------------------------------|
| \<time> | %neptune_time%   | Time spent queuing (mm:ss)          |

## Party

| Plugin          | PlaceholderAPI        | Description                |
|-----------------|-----------------------|----------------------------|
| \<party-leader> | %neptune_party_leader%| Party leader name          |
| \<party-size>   | %neptune_party_size%  | Number of party members    |
| \<party-max>    | %neptune_party_max%   | Maximum party size         |

## Match (Any)

| Plugin    | PlaceholderAPI       | Description                        |
|-----------|----------------------|------------------------------------|
| \<arena>  | %neptune_arena%      | Arena display name                 |
| \<winner> | %neptune_winner%     | Match winner name                  |
| \<loser>  | %neptune_loser%      | Match loser name                   |
| \<round>  | %neptune_round%      | Current round number               |
| \<time>   | %neptune_time%       | Match duration (mm:ss)             |
| None      | %neptune_color%      | Player's team color                |
| None      | %neptune_opponent_color% | Opponent's team color          |

## Solo Match

| Plugin                 | PlaceholderAPI                 | Description                              |
|------------------------|-------------------------------|------------------------------------------|
| \<opponent>            | %neptune_opponent%          | Opponent name                            |
| \<opponent-ping>       | %neptune_opponent_ping%       | Opponent ping                            |
| \<opponent-elo>        | %neptune_opponent_elo%        | Opponent global elo                      |
| \<opponent-division>   | %neptune_opponent_division%   | Opponent global division                 |
| \<opponent-kit-elo>    | %neptune_opponent_kit_elo%  | Opponent kit elo                         |
| \<combo>               | %neptune_combo%               | Player combo                             |
| \<opponent-combo>      | %neptune_opponent_combo%      | Opponent combo                           |
| \<hits>                | %neptune_hits%                | Player hits                              |
| \<opponent-hits>       | %neptune_opponent_hits%       | Opponent hits                            |
| \<hit-difference>      | %neptune_hit_difference%      | Player hit difference                    |
| \<opponent-hit-difference> | %neptune_opponent_hit_difference% | Opponent hit difference          |
| \<longest-combo>       | %neptune_longest_combo%       | Player longest combo                     |
| \<opponent-longest-combo> | %neptune_opponent_longest_combo% | Opponent longest combo            |
| \<points>              | %neptune_points%              | Player points/rounds won                 |
| \<opponent-points>     | %neptune_opponent_points%     | Opponent points/rounds won               |
| \<red-name>            | %neptune_red_name%            | Red player name                          |
| \<blue-name>           | %neptune_blue_name%           | Blue player name                         |
| \<red-ping>            | %neptune_red_ping%            | Red player ping                          |
| \<blue-ping>           | %neptune_blue_ping%           | Blue player ping                         |
| \<red-elo>             | %neptune_red_elo%             | Red player global elo                    |
| \<red-division>        | %neptune_red_division%        | Red player global division               |
| \<red-kit-elo>         | %neptune_red_kit_elo%         | Red player kit elo                       |
| \<blue-elo>            | %neptune_blue_elo%            | Blue player global elo                   |
| \<blue-division>       | %neptune_blue_division%       | Blue player global division              |
| \<blue-kit-elo>        | %neptune_blue_kit_elo%        | Blue player kit elo                      |
| \<red-combo>           | %neptune_red_combo%           | Red player combo                         |
| \<blue-combo>          | %neptune_blue_combo%          | Blue player combo                        |
| \<red-hits>            | %neptune_red_hits%            | Red player hits                          |
| \<blue-hits>           | %neptune_blue_hits%           | Blue player hits                         |
| \<red-hit-difference>  | %neptune_red_hit_difference%  | Red player hit difference                |
| \<blue-hit-difference> | %neptune_blue_hit_difference% | Blue player hit difference               |
| \<red-longest-combo>   | %neptune_red_longest_combo%   | Red player longest combo                 |
| \<blue-longest-combo>  | %neptune_blue_longest_combo%  | Blue player longest combo                |
| \<red-points>          | %neptune_red_points%          | Red player points                        |
| \<blue-points>         | %neptune_blue_points%         | Blue player points                       |

### BedWars (Solo)

| Plugin                  | PlaceholderAPI                    | Description                    |
|-------------------------|-----------------------------------|--------------------------------|
| \<bed-broken>           | %neptune_bed_broken%<sup>$</sup>  | Player's bed broken status     |
| \<opponent-bed-broken>  | %neptune_opponent_bed_broken%<sup>$</sup> | Opponent's bed broken status |
| \<red-bed-broken>       | %neptune_red_bed_broken%<sup>$</sup>      | Red player bed status    |
| \<blue-bed-broken>      | %neptune_blue_bed_broken%<sup>$</sup>     | Blue player bed status   |

## Team Match

| Plugin              | PlaceholderAPI              | Description                           |
|---------------------|----------------------------|-----------------------------------------|
| \<team-players>     | %neptune_team_players%     | Player's team member names             |
| \<opponent-players> | %neptune_opponent_players% | Opponent team member names             |
| \<team-alive>       | %neptune_team_alive%       | Players alive on player's team         |
| \<team-dead>        | %neptune_team_dead%        | Players dead on player's team          |
| \<team-total>       | %neptune_team_total%       | Total players on player's team         |
| \<team-points>      | %neptune_team_points%      | Player's team points                   |
| \<opponent-alive>   | %neptune_opponent_alive%   | Players alive on opponent team         |
| \<opponent-dead>    | %neptune_opponent_dead%    | Players dead on opponent team          |
| \<opponent-total>   | %neptune_opponent_total%   | Total players on opponent team         |
| \<opponent-points>  | %neptune_opponent_points%  | Opponent team points                   |
| \<red-players>      | %neptune_red_players%      | Red team member names                  |
| \<blue-players>     | %neptune_blue_players%     | Blue team member names                 |
| \<red-alive>        | %neptune_red_alive%        | Players alive on red team              |
| \<red-dead>         | %neptune_red_dead%         | Players dead on red team               |
| \<red-total>        | %neptune_red_total%        | Total players on red team              |
| \<red-points>       | %neptune_red_points%       | Red team points                        |
| \<blue-alive>       | %neptune_blue_alive%       | Players alive on blue team             |
| \<blue-dead>        | %neptune_blue_dead%        | Players dead on blue team              |
| \<blue-total>       | %neptune_blue_total%       | Total players on blue team             |
| \<blue-points>      | %neptune_blue_points%      | Blue team points                       |

### BedWars (Team)

| Plugin                  | PlaceholderAPI                             | Description                  |
|-------------------------|-------------------------------------------|------------------------------|
| \<team-bed-broken>      | %neptune_bed_broken%<sup>$</sup>          | Player's team bed status     |
| \<opponent-bed-broken>  | %neptune_opponent_bed_broken%<sup>$</sup> | Opponent team bed status     |
| \<red-bed-broken>       | %neptune_red_bed_broken%<sup>$</sup>      | Red team bed status          |
| \<blue-bed-broken>      | %neptune_blue_bed_broken%<sup>$</sup>     | Blue team bed status         |

## FFA Match

| Plugin    | PlaceholderAPI       | Description                        |
|-----------|----------------------|------------------------------------|
| \<alive>  | %neptune_alive%      | Players alive                      |
| \<max>    | %neptune_max%        | Total players in match             |
| \<dead>   | %neptune_dead%       | Players eliminated                 |
| None      | %neptune_is_dead%    | Whether the player is dead (true/false) |

## Event

Available during an active event (LMS, Tournament, KOTH, Sumo).

| Plugin | PlaceholderAPI              | Description                                      |
|--------|-----------------------------|--------------------------------------------------|
| None   | %neptune_event-type%        | Event type name (e.g. `LMS`, `TOURNAMENT`)       |
| None   | %neptune_event-players%     | Total participants in the event                  |
| None   | %neptune_event-alive%       | Players currently alive (in-game) in the event   |
| None   | %neptune_event-round%       | Current event round number                       |

## Recent Match History

Format: `%neptune_recent_match_<number>_<type>%`

| PlaceholderAPI                            | Description                              |
|-------------------------------------------|------------------------------------------|
| %neptune_recent_match_1_opponent%         | Opponent name of the Nth recent match    |
| %neptune_recent_match_1_kit%             | Kit name of the Nth recent match         |
| %neptune_recent_match_1_arena%           | Arena name of the Nth recent match       |
| %neptune_recent_match_1_date%            | Date of the Nth recent match             |
| %neptune_recent_match_1_time%            | Time of the Nth recent match             |
| %neptune_recent_match_1_unix_timestamp%  | Unix timestamp (seconds) of the Nth match|

Replace `1` with any match number (1 = most recent).

## Leaderboards

Format: `%neptune_<stat>_<kit name>_<position>_<name|value>%`

**Available stats:** `KILLS`, `BEST_WIN_STREAK`, `DEATHS`, `ELO`

**Position:** `1` to `10`

| PlaceholderAPI                         | Description                                          |
|----------------------------------------|------------------------------------------------------|
| %neptune_KILLS_Axe_1_name%            | Player name with the most kills in Axe kit (rank 1)  |
| %neptune_KILLS_Axe_1_value%           | Kill count of the #1 player in Axe kit               |
| %neptune_ELO_Boxing_3_name%           | Player name ranked #3 in elo for Boxing kit          |
| %neptune_BEST_WIN_STREAK_Sword_1_value% | Highest win streak value in Sword kit              |

---

## Notes

\* The placeholder is not needed since you can use other PAPI expansions (e.g., Player expansion for name/ping, Server expansion for online count).

\$ The PlaceholderAPI version returns `"true"` or `"false"` as a string.
